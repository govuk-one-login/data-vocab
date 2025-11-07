import click

from linkml.generators.jsonschemagen import JsonSchemaGenerator, json_schema_types
from linkml.generators.common.lifecycle import TClass, TSchema
from linkml.utils.generator import shared_arguments

from linkml_runtime import SchemaView
from linkml_runtime.linkml_model.meta import ClassDefinition

# Add a URI type
json_schema_types["uri"] = ("string", "uri")

class JsonSchemaGeneratorWrapper(JsonSchemaGenerator):
    def __init__(self, yamlfile, nonstandard_extends, *args, **kwargs):
        super().__init__(yamlfile, *args, **kwargs)
        self.nonstandard_extends = nonstandard_extends
        # By default this will include null as a valid value for all non-required properties
        self.include_null = False

    def after_generate_class(self, cls: TClass, sv: SchemaView) -> TClass:
        # By setting the non-standard 'extends' property
        # jsonschema2pojo can generate Java classes with sensible inheritance hierarchy
        if self.nonstandard_extends and cls.source.is_a is not None:
            cls.schema_['extends'] = {
                '$ref': f'#/$defs/{cls.source.is_a}'
            }
        return cls

    def after_generate_schema(self, schema: TSchema, sv: SchemaView) -> TSchema:
        # jsonschemagen includes metadata which don't match the json-schema spec
        del schema.schema_['metamodel_version']
        del schema.schema_['version']
        return schema

@shared_arguments(JsonSchemaGenerator)
@click.command(name="json-schema")
@click.option(
    "-i",
    "--inline",
    is_flag=True,
    help="""
Generate references to types rather than inlining them.
Note that declaring a slot as inlined: true will always inline the class
""",
)
@click.option(
    "-t",
    "--top-class",
    help="""
Top level class; slots of this class will become top level properties in the json-schema
""",
)
@click.option(
    "--not-closed/--closed",
    default=True,
    show_default=True,
    help="""
Set additionalProperties=False if closed otherwise true if not closed at the global level
""",
)
@click.option(
    "--include-range-class-descendants/--no-range-class-descendants",
    default=False,
    show_default=False,
    help="""
When handling range constraints, include all descendants of the range class instead of just the range class
""",
)
@click.option(
    "--indent",
    default=4,
    show_default=True,
    help="""
If this is a positive number the resulting JSON will be pretty-printed with that indent level. Set to 0 to
disable pretty-printing and return the most compact JSON representation
""",
)
@click.option(
    "--title-from",
    type=click.Choice(["name", "title"], case_sensitive=False),
    default="name",
    help="""
Specify from which slot are JSON Schema 'title' annotations generated.
""",
)
@click.option(
    "-d",
    "--include",
    help="""
Include LinkML Schema outside of imports mechanism.  Helpful in including deprecated classes and slots in a separate
YAML, and including it when necessary but not by default (e.g. in documentation or for backwards compatibility)
""",
)
@click.option(
    "--materialize-patterns/--no-materialize-patterns",
    default=True,  # Default set to True
    show_default=True,
    help="If set, patterns will be materialized in the generated JSON Schema.",
)
@click.option(
    "--preserve-names/--normalize-names",
    default=False,
    show_default=True,
    help="Preserve original LinkML names in JSON Schema output (e.g., for $defs, properties, $ref targets).",
)
@click.option(
    "--nonstandard-extends",
    is_flag=True,
    help="Generate schema using an inheritance hierarchy, using the non-standard extends property",
)
def cli(yamlfile, **kwargs):
    """Generate JSON Schema representation of a LinkML model"""
    print(JsonSchemaGeneratorWrapper(yamlfile, **kwargs).serialize(**kwargs))

if __name__ == "__main__":
    cli()
