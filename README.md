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
* Install Node (and NPM) for example by [installing NVM](https://github.com/nvm-sh/nvm#installing-and-updating).

```
poetry install
npm install
```

# Modifying the source LinkML

Edit the files in [/v1/linkml-schemas/](./v1/linkml-schemas/)

## Generating JSON Schemas

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
npx @11ty/eleventy --serve
```

## Deploy

```
./scripts/deploy
```
