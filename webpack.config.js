var webpack = require("webpack");

const path = require('path');

module.exports = {
  entry: {
    'mkdocs-govuk': './mkdocs-govuk/src/js/application.js'
  },
  output: {
    filename: 'bundle.js',
    path: path.resolve(__dirname, 'docs/js'),
  },
  devtool: "source-map",
  plugins: [
    new webpack.ProvidePlugin({
      $: "jquery",
      jQuery: "jquery"
    })
  ]
};