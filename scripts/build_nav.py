import os
import ruamel.yaml


custom_names = {
    "v1/_Home/index.md": "credentials",
    "v1/json-schemas/index.md": "JSON Schema files"
}

custom_category_names = {
    "slots": "Slots",
    "_Home": "-Home",
    "types": "Types",
    "classes": "Classes",
    "enums": "Enums",
    "json-schemas": "JSON Schemas"
}


# Define the directory to search
dir_to_search = "./docs"

# Initialize an empty list to hold the nav structure
nav_structure = []

def sort_nav_structure(nav_list):
    sorted_nav_list = sorted(nav_list, key=lambda item: list(item.keys())[0].lower())
    for item in sorted_nav_list:
        for key, value in item.items():
            if isinstance(value,list):
                item[key] = sort_nav_structure(value)

    return sorted_nav_list



def add_to_structure(path_parts, nav_list, file_path):
    # Base case: if the path_parts list is empty, return
    if not path_parts:
        return nav_list

    # Get the first part of the path
    part = path_parts.pop(0)

    part = custom_category_names.get(part, part)
    
    # Find the dictionary for this part, or create a new one
    for item in nav_list:
        if part in item:
            break
    else:
        item = {part: []}
        nav_list.append(item)
    
    # If this is the last part of the path, it's a file
    if not path_parts:
        item[part] = file_path
        return
    
    # Recurse to process the rest of the path
    add_to_structure(path_parts, item[part], file_path)

def is_redirect(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        first_line = file.readline().strip()
        return first_line == "# Redirect"

# followlinks=True for walking the symbolic links
for root, dirs, files in os.walk(dir_to_search, followlinks=True):
    for file in files:
        if file.endswith('.md'):
            full_path = os.path.join(root, file)
            if is_redirect(full_path):
                continue
            rel_path = os.path.relpath(full_path, dir_to_search)
            path_parts = rel_path.split(os.sep)
            #print(rel_path)

            #Check for custom name
            custom_name_key = rel_path
            if custom_name_key in custom_names:
                path_parts[-1] = custom_names[custom_name_key]
            else:
                path_parts[-1] = path_parts[-1].replace('.md', '')

            add_to_structure(path_parts, nav_structure, rel_path)


nav_structure = sort_nav_structure(nav_structure)


yaml = ruamel.yaml.YAML()
yaml.indent(mapping=2, sequence=4, offset=2)
with open('mkdocs.yml', 'r') as file:
    mkdocs_data = yaml.load(file)

mkdocs_data['nav'] = nav_structure

with open('mkdocs.yml', 'w') as file:
    yaml.dump(mkdocs_data, file)


