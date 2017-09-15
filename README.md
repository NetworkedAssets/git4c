# Git4C - Git Viewer For Confluence

See plugin details at: <a href="http://opensource.networkedassets.com/confluence/git4c.html">Git Viewer for Confluence Description</a>  
User Guide: <a href="http://opensource.networkedassets.com/confluence/pdf/git4c-user-guide.pdf">Git Viewer for Confluence User Guide (as PDF)</a>  
Administration Guide: <a href="http://opensource.networkedassets.com/confluence/pdf/git4c-administration-guide.pdf">Git Viewer for Confluence Administration Guide (as PDF)</a>  
  
Plugin available to download:  
  - <a href="https://marketplace.atlassian.com/plugins/com.networkedassets.git4c.confluence-plugin/server/overview">On Atlassian Marketplace</a>

## Installation

1. Login to confluence as an administrator, click the cog icon in upper-right corner and select Add-ons.
1. Upload the .jar file as a new Plugin in the Plugin-Administration or search for "Git Viewer for Confluence" plugin.
2. Check if the plugin has been installed correctly and if all modules are enabled.
  
## Development
Before first build you must invoke `mvn validate` to install PlantUML jar in your local maven repository.

# <a href="http://www.networkedassets.com/"><img src="https://www.networkedassets.net/images/NA_logo.png" height="79"></a>  
  
### Adding new format  
  
To add new format create class that implements `InternalConverterPlugin` and add this class to converterPlugins in `ConfluencePlugin`  
  
To add support for method extraction create class that implements `Parser` and add it to Parsers class  
  

