---
---
# Git Viewer For Confluence

## Adding macro

Create a new page or edit an existing one, then select **"insert more content"** button and choose **"Other macros"** option.

![](images/6.png)

Choose Git Viewer For Confluence macro from the list of available macros in your Confluence.

![](images/2017-09-04_13-47-13.png)

Macro configuration dialog should appear.

![](images/multi_file_dialog.png)

### Repository Configuration
Git4C supports two types of repositories.

#### Predefined Repository

Predefined Repository is a repository defined by the Administrator and made available for all users. Those repositories will be available until Administrator removes them from Confluence. 
You can choose a Predefined Repository from the list of repositories in the macro configuration dialog. 

#### Custom Repository

If you wish to include a repository that has not been defined in the Predefined Repositories list, simply click on the **Custom Repository Button** next to the **Predefined Repositories list**.

This option may be blocked by the administrator. In that case, the custom repository button will be disabled.

**Custom Repository Dialog** should appear.

Git Viewer For Confluence macro configuration dialog will ask you to pass necessary parameters of your repository.

![](images/chrome_2017-09-04_14-45-43.png) ![](images/chrome_2017-09-04_14-43-55.png) ![](images/chrome_2017-09-04_14-43-25.png)

There are three possible authentication options:

- No authentication for public repositories.
- Username and password authentication for http(s) connection.
- SSH key authentication for ssh connection.

You can choose them in the "Connection and authentication type" dropdown.

Requried fields for Authentication Types:

| Authentication Type        | Required Fields           |
| ------------- |:-------------:|
| HTTP: No Authorization      | Repository Name<br />Repository URL |
| HTTP: Username + Password   | Repository Name<br />Repository URL<br />Username<br />Password      |
| SSH: Private Key | Repository Name <br />Repository URL<br />SSH Key|

After you fill out all the required fields click the **Save** button. Git Viewer For Confluence will verify your parameters and close the **Custom Repository Dialog**.

### Macro Parameters Configuration

After you successfully choose desired repostiory, branch list will be downloaded automatically. <br />
Select default branch to be displayed in Git Viewer For Confluence Macro.

![](images/chrome_2017-11-06_10-48-28.png)

The last remaining fields are Filter Pattern field and Default File field. Those fields are optional. <br />
First one specifies a filter for your files, you can choose any pattern that satisfies your needs, and display only those files in Git Viewer For Confluence Macro you want. <br />
Filter Patterns can be specified by the Administrator, and will be displayed after you click on Filter Pattern input field. <br />
You can also input your own pattern. For more information about how to define Glob Patterns Check: https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob

Git4C offers you the posibility to select the root folder. Simply click on the root folder selection button next to the filter input filed, and the tree of files will apprear.

![](images/chrome_2017-11-06_10-48-08.png)

Then select desired root folder and apply it by clicking ok. The filter will be automatically generated and visible in the filter input field.

Second field specifies a path to the file, that will be displayed everytime you visit the page, i.e. "home/source/readme.md"

After you're done, the **Save** button should be enabled, click it to save the macro. Save your page and enjoy Git4C. 

## Using Git Viewer For Confluence

After you have successfully configured and added Git4C macro, you should notice it on your page after it has loaded.

![](images/1.2/multifilemacro/overview.png)

Git4C consists of following elements:
1. Branch selection
2. Hiding sidebar and activating sticky toolbar
3. File name
4. Other file information
5. File source
6. File commits
7. Table on contents
8. Files tree
9. File content

The branch selection dropdown allows you to switch between different branches of your repository in view time. Simply click on the dropdown.

![](images/1.2/multifilemacro/branch_selection.png)

and select desired branch. You should notice the loading screen for a moment. After it's done you have successfully changed your branch.

![](images/1.2/multifilemacro/topbar.png)

In the main content view, you'll see few information about the file. It's path and last changes date and their author.

Git4C will automatically generate table of contents for your document and display it in collapsed form.

![](images/1.2/multifilemacro/toc-collapsed.png)

Click on it to expand it so you can easily navigate through the document.

![](images/1.2/multifilemacro/toc-expanded.png)

Git4C also supports global search functionality, it allows you to find a page containing Git4C with specified text in it. <br />
Simply put the sought fragment into the quick search bar and click search.

![](images/1.2/multifilemacro/search.png)

If any of your pages contains Git4C with that fragment inside of any file, it'll direct you to that page.

![](images/chrome_2017-07-24_16-19-24.png)

### Sticky toolbar

Sticky toolbar is new functionality added in 1.2 that allows users to choose files in tree while reading long files.

![](images/1.2/multifilemacro/sticky-toolbar.png)

## Git Viewer For Confluence Single File

### Adding macro

Create a new page or edit an existing one, then select **"insert more content"** button and choose **"Other macros"** option.

![](images/6.png)

Choose Git Viewer For Confluence macro from the list of available macros in your Confluence.

![](images/2017-09-04_15-08-29.png)

Macro configuration dialog should appear. 

![](images/single_file_dialog.png)

#### Repository Configuration

Repository configuration is the same as in Git Viewer For Confluence Macro.

#### Macro Parameters Configuration

After you successfully choose desired repository, branch list will be downloaded automatically.  

Git Viewer For Confluence Single File will then present you with a list of Files available in the choosen branch. You can select the File directly from **File Select Dropdown** <br />
You can also click the **File Tree Display Button** to see the file tree. Choose the file you need from the tree and click **Save**.

File preview will be displayed on the right side of macro configuration dialog.

Last thing to do is to specify the options for displaying macro.

![](images/1.2/singlemacrodialog/options.png)

- **Methods in file** this field allows you to choose one method that should be displayed in the macro view, you can also select "All" option to display the whole file.
- **Show top bar** specifies if the bar with file information should be displayed
- **Collapsible** lets you decide if user can collapse the Git4C Single File View. You can also check the **Collapse by default** option to  make Git4C Single File view collapsed every time user opens the page.
- **Collapse by default** specifies if macro will be collapsed after entering page
- **Show line numbers** option decides if enumeration of lines is visible.
- **Show** lets you decide if you want to show whole file, selected method (for code) or selected line range

### Using macro

![](images/1.2/singlefilemacro/overview.png)

Macro consists of following elements:
1. File name
2. Information about file
3. Button to collapse macro
4. Button to show file source (only for .md, .svg and .puml)
5. Button to show file commits
6. File content

## PUML support

To add puml to markdown you have to add tag for image, but include puml path in it. <br />

For example this Markdown file:
```
![This is Puml](puml.puml)
```

With this [file](puml.puml) in the same directory will generate:

![](images/image2017-7-5_15-27-11.png)
