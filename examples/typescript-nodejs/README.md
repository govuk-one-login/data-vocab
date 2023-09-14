Example Node.js project consuming TypeScript types
==================================================

This Node.js project consumes the [Digital Identity Vocab](https://github.com/alphagov/di-identity-vocab) TypeScript types.

In the `package.json`, note the `@alphagov/di-identity-vocab` dependency:

```json
"@alphagov/di-identity-vocab": "1.3.0"
```

> **Note**
> See [releases](https://github.com/alphagov/di-identity-vocab/releases) for the latest version.

### Build the project

##### Prerequisities

This project uses TypeScript and expects the `tsc` command to be available.

Ensure you have authenticated to the `@alphagov` npm scope in GitHub Packages:

```shell
npm login --scope=@alphagov --auth-type=legacy --registry=https://npm.pkg.github.com
```

> See [Authenticating to GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-npm-registry#authenticating-to-github-packages) documentation.

> **Note**
> For instructions on using the package from GitHub Actions, see the [main README](https://github.com/alphagov/di-identity-vocab#setting-permissions).

##### Steps

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

You should see a JSON-formatted console entry of a sample `Credentials` object.
