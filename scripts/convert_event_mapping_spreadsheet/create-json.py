import csv
import json

def nested_set(dic, keys, value):
    """Set a value in a nested dictionary structure."""
    for key in keys[:-1]:
        if key not in dic:
            dic[key] = {}
        elif not isinstance(dic[key], dict):
            # Handle the conflict by renaming the terminal key
            dic[key + "_value"] = dic[key]
            dic[key] = {}
        dic = dic[key]
    dic[keys[-1]] = value

def process_data(data):
    """Convert flat keys with dots to nested dictionaries, remove '{}', and replace empty or 'N/A' with False."""
    processed_data = {}
    for key, value in data.items():
        # Replace empty or 'N/A' values with False
        if not value or value == "N/A":
            value = False
        if value == "TRUE":
            value = True

        # Split the key by '.' and remove '{}'
        keys = [k.replace('{}', '') for k in key.split('.')]
        nested_set(processed_data, keys, value)
    return processed_data

def csv_to_json_nested(csv_filename):
    """Converts a CSV file to a nested JSON structure and returns it."""
    data_list = []

    with open(csv_filename, 'r') as csv_file:
        csv_reader = csv.DictReader(csv_file)
        for row in csv_reader:
            data_list.append(process_data(row))

    return data_list

def remove_false_fields(data, output_filename):
    """Removes fields with a value of False from the data and writes the result to an output file."""
    
    # Remove fields with a value of False and empty dictionaries
    def clean_data(item):
        if isinstance(item, dict):
            cleaned_dict = {k: clean_data(v) for k, v in item.items() if v is not False}
            return {k: v for k, v in cleaned_dict.items() if v != {}}
        elif isinstance(item, list):
            return [clean_data(v) for v in item if v is not False and v != {}]
        else:
            return item

    cleaned_data = clean_data(data)

    with open(output_filename, 'w') as output_file:
        json.dump(cleaned_data, output_file, indent=4)

# Usage
data = csv_to_json_nested('sample.csv')
remove_false_fields(data, 'cleaned_sample.json')


