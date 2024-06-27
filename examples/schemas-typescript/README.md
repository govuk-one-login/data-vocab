Example Node.js project consuming JSON schemas
==================================================

This Node.js project consumes the [Digital Identity Vocab](https://github.com/govuk-one-login/data-vocab) TypeScript types.

In the `package.json`, note the `@govuk-one-login/data-vocab-schemas` dependency:

```json
"@govuk-one-login/data-vocab-schemas": "1.7.2"
```

> **Note**
> See [releases](https://github.com/govuk-one-login/data-vocab/releases) for the latest version.

### Build the project

##### Prerequisites

Ensure you have authenticated to the `@govuk-one-login` npm scope in GitHub Packages and selected `write:packages` scope when creating personal token:

```shell
npm login --scope=@govuk-one-login --auth-type=legacy --registry=https://npm.pkg.github.com
```

> See [Authenticating to GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-npm-registry#authenticating-to-github-packages) documentation.

> **Note**
> For instructions on using the package from GitHub Actions, see the [main README](https://github.com/govuk-one-login/data-vocab#setting-permissions).

##### Steps

Install dependencies:

```shell
npm ci
```

Build the project using:

```shell
npm run build
```

This runs the TypeScript transpiler and outputs to the `dist` directory.

### Run the project

Once you have built the project, run it with:

```shell
npm start
```

You should see message `Payload complies with the schema` if the payload complies with the provided schema or error object if it doesn't.
