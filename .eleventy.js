const govukEleventyPlugin = require('govuk-eleventy-plugin')

module.exports = function(eleventyConfig) {
  eleventyConfig.addPlugin(govukEleventyPlugin, {
    header: {
      productName: 'Sign In',
      homepageUrl: 'https://www.sign-in.service.gov.uk/'

    }
  })

  eleventyConfig.addPassthroughCopy("v1/json-schemas");

  return {
    dataTemplateEngine: 'njk',
    htmlTemplateEngine: 'njk',
    markdownTemplateEngine: 'njk',
    dir: {
      input: "docs",
      // Use layouts from the plugin
      layouts: '../node_modules/govuk-eleventy-plugin/layouts'
    }
  }
};
