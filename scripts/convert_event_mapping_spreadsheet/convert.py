import json
import os

def snake_to_pascal(snake_str):
    """Convert a snake_case string to PascalCase."""
    components = snake_str.split('_')
    return ''.join(x.title() for x in components)

def snake_to_kebab(snake_str):
    """Convert a snake_case string to kebab-case."""
    return snake_str.replace('_', '-').lower()

def create_files_from_json(json_filename):
    """Creates event class files and a slots.yaml file based on the JSON data."""
    
    with open(json_filename, 'r') as json_file:
        data_list = json.load(json_file)

    # Ensure the outputs directory exists
    if not os.path.exists('./outputs'):
        os.makedirs('./outputs')

    for data in data_list:
        slots = set()
        classes = {}

        def extract_slots(item, parent=None):
            """Recursively extract non-object fields from the JSON object."""
            if isinstance(item, dict):
                for key, value in item.items():
                    if isinstance(value, dict):
                        class_name = snake_to_pascal(key)
                        if parent:
                            class_name = f"{parent}{class_name}"
                        classes[class_name] = (key, value)  # Store original key and its attributes
                        extract_slots(value, class_name)
                    else:
                        slots.add(key)

        extract_slots(data)

        audit_event_type = data["AuditEventType"]
        kebab_case_name = snake_to_kebab(audit_event_type)
        pascal_case_name = snake_to_pascal(audit_event_type)
        filename = f"./outputs/{pascal_case_name}AuditEvent.yaml"
        with open(filename, 'w') as file:
            file.write(f"""id: https://vocab.account.gov.uk/linkml/{kebab_case_name}
name: {kebab_case_name}
description: >-
  Description for audit event {audit_event_type}
prefixes:
  linkml: https://w3id.org/linkml/
  di_vocab: https://vocab.account.gov.uk/v1/

imports:
  - ./slots

default_curi_maps:
  - semweb_context  
default_prefix: di_vocab
default_range: string

classes:
  {pascal_case_name}AuditEventClass:
    slots:\n""")
            for key in data.keys():
                if key == "AuditEventType":
                    file.write(f"      - event_name\n")
                if key != "AuditEventType":
                    file.write(f"      - {key}\n")
            for class_name, (original_key, attributes) in classes.items():
                file.write(f"""
  {pascal_case_name}{class_name}Class:
    slots:\n""")
                for key in attributes.keys():
                    file.write(f"      - {key}\n")

            file.write("\nslots:\n")
            for class_name, (original_key, attributes) in classes.items():
                print(original_key)
                file.write(f"  {original_key}:\n")
                file.write(f"    range: {pascal_case_name}{class_name}Class\n")
                file.write(f"    description: Description for {snake_to_kebab(original_key)}\n")

        # Write to the slots.yaml file
        with open('./outputs/slots.yaml', 'w') as file:
            # Add the specified header
            file.write("""id: https://vocab.account.gov.uk/linkml/audit-slots
name: audit-slots
description: >-
prefixes:
  linkml: https://w3id.org/linkml/
  di_vocab: https://vocab.account.gov.uk/v1/

imports:
  - linkml:types
                       
default_curi_maps:
  - semweb_context  
default_prefix: di_vocab
default_range: string

slots:
  event_name:
    description: EVENT_NAME                       
""")
        
            for slot in slots:
                file.write(f"  {slot}:\n")
                file.write(f"    description: Description for {slot}\n")

# Usage
create_files_from_json('cleaned_sample.json')


