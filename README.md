# Digital Identity Vocabulary Specification

## About

This specification intends to describe a Linked Data vocabulary for asserting Verifiable Credentials related to identity proofing and verification.

Further development could include:

* JSON Schemas
* Automated tests
* Documentation
* JSON-LD
* OpenAPI description for our various OAuth providers

## Getting started

* Install Python for example by [installing pyenv](https://briansunter.com/blog/python-setup-pyenv-poetry/#initial-setup);
* Install [poetry](https://python-poetry.org/docs/) so that you can install and run this project's Python depdenencies;
* Install Node (and NPM) for example by [installing NVM](https://github.com/nvm-sh/nvm#installing-and-updating);
* Install some build and test command line tools we require globally (or into your environment, somehow)

```
poetry install
npm install

npm install -g ajv-cli@5.0.0 ajv-formats@2.1.1

npm install -g sass  # or your local equivalent per https://sass-lang.com/install

```

# Modifying the source LinkML

Edit the files in [/v1/linkml-schemas/](./v1/linkml-schemas/)

>:exclamation: Class names are all post-fixed with `Class`.
This is because otherwise, when generating documentation on MacOS, classes whose names clash with slot names (eg `Name` and `name`) will be lost, see
[https://github.com/linkml/linkml/issues/632](LinkML Issue 632).

## Generating JSON Schemas and compile the documentation site

```
./scripts/build
```

## Run tests

Test the example files against the JSON schemas using an independent JSON-Schema library:

```
./scripts/test
```

# Run a local server

```
poetry run mkdocs serve
```

## Deploy

```
./scripts/deploy
```
