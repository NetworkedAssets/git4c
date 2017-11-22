Feature: Select root directory in multi file macro
  The feature allows an user to an root directory whilst creating macro in macro creation dialog.
  After clicking on the root directory button the file tree will be displayed and user will choose single folder that will be saved as a glob filter.
  only files from that directory will be displayed.

  Background: The user is logged in

  Scenario: User can see a list of directories
    Given a confluence page with opened multi file macro creation dialog
    When the user clicks on root directory selection button
    Then dialog with file tree is displayed

  Scenario: User can select a root directory
    Given a confluence page with opened multi file macro creation dialog
    When root directory selection dialog with file tree is opened
    When the user selects a root directory
    When the user confirms selection with "Select" button
    Then selected directory is saved as a glob filter
    Then selected directory is displayed in glob filter field

  Scenario: List of files is filtered by selected root directory
    Given a confluence page with multi file macro
    When The user selected root directory
    Then File tree only shows files within selected root directory
    Then Root directory is displayed in glob information tooltip
    