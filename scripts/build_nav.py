"""

Build the nav section (site navbar config) in the mkdocs.yml

https://www.mkdocs.org/user-guide/configuration/#nav

"""

import os
import ruamel.yaml

DOCS_DIR = "./docs"

CUSTOM_NAMES = {
    "audit-events/json-schemas/index.md": "JSON Schema files",
    "index.md": "1. GOV.UK One Login Vocabulary",
    "v1/_Home/index.md": "credentials",
    "v1/json-schemas/index.md": "JSON Schema files",
}

CUSTOM_CATEGORY_NAMES = {
    "slots": "Slots",
    "_Home": "Home",
    "types": "Types",
    "classes": "Classes",
    "enums": "Enums",
    "json-schemas": "JSON Schemas",
    "audit-events": "3. Audit Events",
    "v1": "2. Physical Data Model",
}


def pascal_to_upper_snake(pascal_str):
    """Convert a PascalCase string to UPPER_SNAKE_CASE."""
    return (
        "".join(["_" + i.lower() if i.isupper() else i for i in pascal_str])
        .lstrip("_")
        .upper()
    )


def is_redirect(file_path):
    """Check if the given file path is a redirect."""
    with open(file_path, "r", encoding="utf-8") as file:
        return file.readline().strip() == "# Redirect"


def add_to_structure(path_parts, nav_list, file_path):
    """Recursively process and add the given path to the navigation structure."""
    if not path_parts:
        return nav_list

    part = path_parts.pop(0)
    part = CUSTOM_CATEGORY_NAMES.get(part, part)

    for item in nav_list:
        if part in item:
            break
    else:
        item = {part: []}
        nav_list.append(item)

    if not path_parts:
        item[part] = file_path
        return

    add_to_structure(path_parts, item[part], file_path)


def sort_nav_structure(nav_list):
    """
    Sort the given navigation structure.

    - Home appears on top
    - Everything else appears alphabetically
    """

    home_item = [item for item in nav_list if "Home" in item]
    other_items = [item for item in nav_list if "Home" not in item]

    sorted_other_items = sorted(
        other_items, key=lambda item: list(item.keys())[0].lower()
    )

    for item in sorted_other_items:
        for key, value in item.items():
            if isinstance(value, list):
                item[key] = sort_nav_structure(value)

    sorted_nav_list = home_item + sorted_other_items

    return sorted_nav_list


def generate_navigation_structure():
    """Generate and return the navigation structure based on the DOCS_DIR."""
    nav_structure = []

    for root, dirs, files in os.walk(DOCS_DIR, followlinks=True):
        for file in files:
            if file.endswith(".md"):
                full_path = os.path.join(root, file)
                print(full_path)
                if is_redirect(full_path):
                    continue
                rel_path = os.path.relpath(full_path, DOCS_DIR)
                path_parts = rel_path.split(os.sep)

                # Custom logic for audit events
                # Only show the root audit event class for each event in the nav
                if (
                    "audit-events" in path_parts
                    and "classes" in path_parts
                    and "AuditEventClass.md" not in file
                ):
                    continue

                # Replace class names with event names in the nav bar
                if "audit-events" in path_parts and "classes" in path_parts:
                    path_parts[1] = "Events"
                    path_parts[-1] = pascal_to_upper_snake(
                        path_parts[-1].replace("AuditEventClass.md", "")
                    )

                # Don't add slots which have been updated in slot_usage within class files to the Nav
                if (
                    "audit-events" in path_parts
                    and "slots" in path_parts
                    and "AuditEventClass" in file
                ):
                    continue

                if rel_path in CUSTOM_NAMES:
                    path_parts[-1] = CUSTOM_NAMES[rel_path]
                else:
                    path_parts[-1] = path_parts[-1].replace(".md", "")
                add_to_structure(path_parts, nav_structure, rel_path)

    return sort_nav_structure(nav_structure)


def update_mkdocs_config(nav_structure):
    """Update the mkdocs.yml file with the given navigation structure."""
    yaml = ruamel.yaml.YAML()
    yaml.indent(mapping=2, sequence=4, offset=2)
    with open("mkdocs.yml", "r") as file:
        mkdocs_data = yaml.load(file)
    mkdocs_data["nav"] = nav_structure
    with open("mkdocs.yml", "w") as file:
        yaml.dump(mkdocs_data, file)


def main():
    nav_structure = generate_navigation_structure()
    update_mkdocs_config(nav_structure)


if __name__ == "__main__":
    main()
