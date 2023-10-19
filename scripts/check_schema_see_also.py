import yaml
import argparse
import sys


parser = argparse.ArgumentParser(
    description='Check that the specified LinkML class definition includes a link to the provided JSON Schema file.'
)
parser.add_argument('linkml_schema_file')
parser.add_argument('class_name')
parser.add_argument('json_schema_file')
args = parser.parse_args()

# Attempting to automatically write these as 'see_also' links was much harder than it looked!

with open(args.linkml_schema_file) as f:
    y = yaml.safe_load(f)
    related_refs = y['classes'][args.class_name].get('see_also', list())
    if args.json_schema_file not in related_refs:
        print(f"Missing 'see_also' reference to '{args.json_schema_file}' in {args.linkml_schema_file}/{args.class_name}", file=sys.stderr)
        sys.exit(1)
    description = y['classes'][args.class_name].get('description', '')
    if f"]({args.json_schema_file}" not in description:
        print(f"Missing reference to '{args.json_schema_file}' in description for {args.linkml_schema_file}/{args.class_name}", file=sys.stderr)
        sys.exit(1)
