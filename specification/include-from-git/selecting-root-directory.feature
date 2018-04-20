Feature: Select Root Directory for repository in Multi File Macro
  The feature allows a User to select root directory whilst creating Multi File Macro.
  After clicking on the root directory button, the file tree will be displayed and User should be able to choose a single folder that will be saved as a glob filter.
  Only files from the selected directory will be displayed.

  Background: User is logged in

  Scenario: User browses through directories list in repository
    Given Confluence page in edit mode
    And Git4C Macro Parameters window is opened
    And Repository is chosen for the macro
    When User opens Root Directory Selection Dialog
    Then Dialog with repository file tree is displayed

  Scenario: User selects Root Directory for macro content
    Given Confluence page in edit mode
    And Git4C Macro Parameters window is opened
    And Repository is chosen for the macro
    When User opens Root Directory Selection Dialog
    And User chooses User Directory for the macro
    Then Selected Root Directory is displayed on its field on Git4c Parameters window

  Scenario: Displaying files in selected Root Directory on Confluence Page
    Given Confluence page with macro having Root Directory selected
    When User visits the page
    And Macro loaded its content
    Then File Tree shows only the files from selected Root Directory
    And Root directory path is displayed on the top of File Tree
