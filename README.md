# Digital Identity Vocabulary Specification

## About

This specification intends to describe a Linked Data vocabulary for asserting Verifiable Credentials related to identity proofing and verification.

This repository produces the following artifacts:

* [Vocabulary documentation](https://govuk-one-login.github.io/data-vocab/)
* [JSON Schemas](https://github.com/govuk-one-login/data-vocab/releases)
* [TypeScript types (npm module)](https://github.com/govuk-one-login/data-vocab/pkgs/npm/data-vocab)

Further development could include:

* Automated tests
* Documentation
* JSON-LD
* OpenAPI description for our various OAuth providers

## Getting started

When checking out, ensure you update/initialise submodules recursively, these are used for certain dependencies, e.g.
```bash
git submodule update --init --recursive
```

### Manually

* Install Python for example by [installing pyenv](https://briansunter.com/blog/python-setup-pyenv-poetry/#initial-setup);
* Install [poetry](https://python-poetry.org/docs/) so that you can install and run this project's Python depdenencies;
* Install Node (and NPM) for example by [installing NVM](https://github.com/nvm-sh/nvm#installing-and-updating);
* Install some build and test command line tools we require globally (or into your environment, somehow)

```bash
poetry install
npm install

npm install -g ajv-cli@5.0.0 ajv-formats@2.1.1

npm install -g sass  # or your local equivalent per https://sass-lang.com/install

If the above dependencies/versions need to change, please update the [devcontainer configuration](.devcontainer/devcontainer.json) also.

```

### Dev Container

Open the repo in a development container in vscode, and the pre-requisites will be pre-installed

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

## Run a local server

```
poetry run mkdocs serve
```

If the styling appears off, you may need to run the build script first

## Deploy

```
./scripts/deploy
```

---

# Using the TypeScript types

Type definition files (`.d.ts`) are published with each tagged version of this repository. You can add these as a dependency in your TypeScript/Node.js project.

These are published as [an npm module](https://github.com/govuk-one-login/data-vocab/pkgs/npm/data-vocab) to GitHub Packages.

> **Note**
> Check out the [sample project](./examples/typescript-nodejs) for an example.

## Add the dependency

You can add it as a dependency using npm:

```shell
npm install @govuk-one-login/data-vocab@1.4.2
```

Or in your `package.json` dependencies:

```json
"@govuk-one-login/data-vocab": "1.4.2"
```

> **Note**
> See [releases](https://github.com/govuk-one-login/data-vocab/releases) for the latest version.

## Setting permissions

### Working locally

If you are working locally, ensure you have authenticated to the `@govuk-one-login` npm scope in GitHub Packages:

```shell
npm login --scope=@govuk-one-login --auth-type=legacy --registry=https://npm.pkg.github.com
```

> See [Authenticating to GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-npm-registry#authenticating-to-github-packages) documentation.

### Using the package in GitHub Actions 

If you are building your project in a GitHub Actions workflow, grant access to your repository from the [package settings](https://github.com/orgs/govuk-one-login/packages/npm/data-vocab/settings) page.

In your workflow file, set the following permission for the GitHub token:

```yaml
permissions:
  packages: read
```

In your `setup-node` step, set the registry URL:

```yaml
registry-url: "https://npm.pkg.github.com"
```

<details>
<summary>Full setup-node step example</summary>

Here's an example of the step with the registry configured:
```yaml
steps: 
- name: Setup node and npm
  uses: actions/setup-node@v3
  with:
    node-version: 18
    cache: npm
    registry-url: "https://npm.pkg.github.com"
```
</details>

In the step where you run `npm install`, set the `NODE_AUTH_TOKEN` environment variable:

```yaml
NODE_AUTH_TOKEN: ${{ github.token }}
```

<details>
<summary>Full npm install example</summary>

Here's an example of the step with the `NODE_AUTH_TOKEN` configured:
```yaml
- name: Install npm dependencies
  run: "npm ci --ignore-scripts"
  env:
    NODE_AUTH_TOKEN: ${{ github.token }}
```
</details>
