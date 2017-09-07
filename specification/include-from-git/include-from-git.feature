Feature: Include file from git in a page
  The feature works like the built-in include feature and the code-feature together.
  The user can include the content of an arbitrary file at a point in page. Everything should work
  as expected: the content is rendered accordingly and the user can use it in the context of the page.
  The feature is useful for instance for including java-snippets, feature-content, test-examples etc.

  Background: the user is logged in

  Scenario: user uses the macro within a page
    Given a confluence page
    When the user has chose the macro from the list of macros
    Then a form has been shown


  Scenario: user can choose from defined git-repositories
  To make the process of defining the source for the files easier, there is a set
  of repositories the administrator can define upfront. The user can select one of them
    Given the macro definition form is open
    When the list of all defined repositories is visible
    Then the user can choose from a list of defined repositories

  Scenario: user chooses file from repository
  The user can select only one file from the repository to be included in the page
    Given the macro definition form is open
    When the repository has been chosen
    Then a directory tree with the repository structure is show to the user
    And a search field

  Scenario: preview file from repository in the macro form
  To avoid misunderstanding of how the file will be rendered, we can offer a preview
  which should be as much as possible like the final result

    Given form shows the directory tree of the repository
    When user has chosen a file from the tree
    Then a preview of the rendered file shows up in the form
    And preview is the same as it would be in the page

  Scenario: include selected file to the page after preview
  the user includes the file in the page but there must not be a rendered result
  in the edit-mode

    Given preview of the file has been shown in the form
    And the page is still in the edit mode
    When user confirms the preview
    Then macro-definition with the chosen file and configuration is included in the page
    And the user can see the position of the included macro

  Scenario: select scenario from gherkin file to include
    Given user selects a gherkin file with the ending ".feature"
    When the file has been parsed
    Then a preview of the file should be presented
    And a list of all scenarios should be shown for selection
    And the user is able to select one of the scenarios

  Scenario: include a particular scenario from gherkin file
    Given user has selected a particular scenario from the gherkin file
    When page has been rendered
    Then only the selected scenario is part of the page