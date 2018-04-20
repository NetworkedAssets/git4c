Feature: Multi File macro navigation
  This feature allows a user to navigate between files in macro scope.
  After switching file, it should be visible in both file tree and URL.
  Content of macro should change.
  Changing files using URL is described in Changing Branch feature.

  Background: User is logged in and visits a Confluence page with Multi File Macro.

  Scenario: User switches between files using file tree
    Given Confluence page with Multi File Macro
    When User selects a file in file tree
    Then Selected file is highlighted in file tree
    And Selected file's content is displayed
    And Selected file name is present in the URL


  Scenario: User switches between files using browser history navigation buttons
    Given Confluence page with Multi File Macro
    When User selects a file in file tree
    And User clicks "back" navigation browser button
    Then Previous file's content is displayed
    And Previous file is highlighted in file tree
    And Previous file name is present in the URL


  Scenario: User uses a link to another file/section in Multi File macro content
    Given Confluence page with Multi File Macro
    When User uses a link to another file/section in macro scope
    Then Git4C macro will navigate to file/section specified in link.

  Scenario: Hide file tree
    Given Confluence page with Multi File Macro
    When User clicks on hide file tree button
    Then Left panel with file tree collapses
    And File tree button appears along with open toolbar button

  Scenario: Display commit history
    Given Confluence page with Multi File macro
    When User clicks on view commits button
    Then Window with previous commits listed opens

  Scenario: View source of displayed file
  This scenario covers Markdown, Asciidoc, PUML and SVG files
    Given Confluence page with Multi File macro
    When User clicks on view source button
    Then Window with file source opens