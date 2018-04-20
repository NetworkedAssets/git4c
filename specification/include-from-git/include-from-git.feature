Feature: Include file from GIT repository in Confluence page using Single File macro
  The feature works like the Confluence built-in include file and code-feature together.
  A User can put a content of an arbitrary file at a point in page.
  The feature is useful, for instance, for including java-snippets, feature-content, test-examples etc.
  Single File macro enables User to see a preview of how the file would look like on a page, however
  the preview won't be seen on Confluence edit mode when macro edition is finished.

  Background: User is logged in

  Scenario: Opening Single File macro create mode
    Given Confluence page in edit mode
    When User has chosen Single File macro from the list of other macros
    Then Git4C Macro Parameters window is shown

  Scenario: User selects GIT repository from list of predefined repositories
  To make the process of defining the source for the files easier, there is a set
  of repositories that administrator can define upfront.
  User can select one of them to choose a file from.
    Given Git4C Macro Parameters window is shown
    When The list of all defined repositories is visible
    Then User can choose a repository to display a file from from a list of predefined repositories

  Scenario: User chooses a file from repository to put on a page
  User can select only one file from the repository to be included in the page when using Single File macro.
    Given Git4C Macro Parameters window is shown
    When User chooses a repository
    And User opens the file selection window
    Then A window with search through files is shown
    And Directory tree with the repository structure is shown to the User
    And User is now able to choose a file to display on a page in scope of Single File macro

  Scenario: Preview a file from repository in the macro form
  To avoid misunderstanding of how the file will look like on a Confluence page, we offer a preview
  which should be as much as possible like the final result.
    Given Git4C Macro Parameters window is shown
    And File Selection is opened
    When User has chosen a file from a directory tree
    Then Preview of the selected file shows up in the File Selection window

  Scenario: Select scenario from gherkin file to include
    Given Git4C Macro Parameters window is shown
    When User has selected a gherkin file with ".feature" extension
    And The file is loaded
    Then Preview of the file should be presented
    And List of all scenarios from gherkin feature should be shown for selection
    And User is able to select one of the scenarios to put on a Confluence page
    And If scenario is selected, only that scenario is displayed on preview

  Scenario: User chooses a repository from the list of 5 most recently used repositories.
    Given User has created a Single File macro including file from a repository
    When User creates another macro
    Then He can select previously chosen repository from the list of 5 most recently used repositories

  Scenario: Select file line range to include
     Given Git4C Macro Parameters window is shown
     And User has selected a file with code to include
     When User selects line range of a file
     And User saves the Single File macro
     And User opens Confluence page with it
     Then Only selected line range is displayed on a Confluence page

  Scenario: Select code method to display
     Given Git4C Macro Parameters window is shown
     And User has selected a file with code to include
     When User selects method to display
     And User saves the Single File macro
     And User opens Confluence page with it
     Then Only selected method is displayed on a Confluence page
     And Selected method name is displayed on toolbar

  Scenario: Include file using macro with collapsable toolbar
     Given Git4C Macro Parameters window is shown
     When User selects a collapsable toolbar option
     And User saves the Single File macro
     And User opens Confluence page with it
     Then Toolbar can be collapsed using collapse source link

  Scenario: Include file with collapsed by default toolbar
     Given Git4C Macro Parameters window is shown
     When User selects a collapsed by default option
     And User saves the Single File macro
     And User opens Confluence page with it
     Then Toolbar is collapsed by default