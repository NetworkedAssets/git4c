Feature: Editing a file in Single File macro
  The feature allows user to edit currently shown file from the Confluence page.
  Files that can be edited by a User, have to be a part of predefined repository having editable option enabled.
  This action can be performed in Admin panel.

  Background: User is logged in

  Scenario: User is unable to edit a file
    Given Confluence page with Single File macro
    And Macro has repository with editing disabled
    Then Macro has no edit button displayed on its toolbar

  Scenario: User is able to edit a file
    Given Confluence page with Single File macro
    And Given branch is writable
    Then Macro has an edit button displayed on its toolbar

  Scenario: User edits a file
    Given Confluence page with Single File macro
    And Dialog with editor is shown
    When User edits the file
    And User enters commit message
    And Current branch is writable
    And User clicks Publish button
    Then File is uploaded to server
    And Page is being refreshed 3 seconds after publishing the changes made by User
    And After refreshing the page, changes made by User are visible on macro content

  Scenario: Uploading changed file to read-only repository
    Given Confluence page with Single File macro
    And Macro repository is read-only
    And Dialog with editor is shown
    When User edits the file
    And User enters commit message
    And User clicks Publish button
    Then Error message is shown, saying that file cannot be uploaded to the repository

  Scenario: Uploading changed file to read-only branch on writable repository
    Given Confluence page with Single File macro
    And Repository is writable
    And Branch is read-only
    And Dialog with editor is shown
    When User edits the file
    And User enters commit message
    And User clicks Publish button
    Then File is uploaded to newly created temporary branch
    And Page is being refreshed 3 seconds after publishing the changes made by User
    And After refreshing the page, changes made by User are visible on macro content
    And Background of file display is red
    And Edit button is red
    And Show toolbar button is red
    
  Scenario: Uploading changed file when no commit message was entered
    Given Confluence page with Single File macro
    When User clicks on edit button
    And Dialog with editor is shown
    And User edits file
    And User clicks Publish button
    Then Error message is shown that commit message is empty
    And The change is not published

  Scenario: Preview of edited file
    Given Confluence page with Single File macro
    When User clicks on edit button
    And Dialog with editor is shown
    And User clicks Preview button
    Then Preview of how edited file would look like is shown


