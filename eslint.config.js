const js = require('@eslint/js');
const importXPlugin = require('eslint-plugin-import-x');
const jestPlugin = require('eslint-plugin-jest');
const globals = require('globals');

module.exports = [
  {
    ignores: ['site/**', 'docs/v1/**', 'docs/css/**', 'docs/js/**', 'node_modules/**'],
  },
  js.configs.recommended,
  importXPlugin.flatConfigs.recommended,
  {
    ...jestPlugin.configs['flat/recommended'],
    files: ['**/*.test.js', '**/__tests__/**/*.js'],
  },
  {
    files: ['**/*.js'],
    languageOptions: {
      ecmaVersion: 2021,
      sourceType: 'commonjs',
      globals: {
        ...globals.browser,
        ...globals.commonjs,
      },
    },
    rules: {
      'no-console': 'off',
      'no-unused-vars': 'off',
    },
  },
  {
    files: ['**/*.mjs'],
    languageOptions: {
      ecmaVersion: 'latest',
      sourceType: 'module',
      globals: {
        ...globals.node,
      },
    },
    rules: {
      'no-console': 'off',
    },
  },
];
