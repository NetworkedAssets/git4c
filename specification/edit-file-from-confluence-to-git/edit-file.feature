Feature: Editing in single file macro
  The feature allows user to edit currently shown file

  Background: The user is logged in

  Scenario: User cannot edit file when editing is disabled for repository
    Given a confluence page with macro
    And macro has repository with editing disabled
    Then user doesn't see edit button

  Scenario: User can edit file
    Given a confluence page with macro
    When user clicks on edit button
    And given branch is writable
    Then dialog with editor is shown
    Then user edits file
    Then user sets commit message
    When user clicks Publish button
    Then file is send to server

  Scenario: User sees error when file cannot be uploaded to any branch
    Given a confluence page with macro
    When repository is read only
    When user clicks on edit button
    Then dialog with editor is shown
    Then user edits file
    Then user sets commit message
    When user clicks Publish button
    Then error is shown that file cannot be uploaded

  Scenario: File is uploaded when current branch is readonly, but repo is writable
    Given a confluence page with macro
    When branch is read only
    And repository is writable
    When user clicks on edit button
    Then dialog with editor is shown
    Then user edits file
    Then user sets commit message
    When user clicks Publish button
    Then error is shown that file cannot be uploaded

  Scenario: User see error when commit message is empty
    Given a confluence page with macro
    When user clicks on edit button
    Then dialog with editor is shown
    Then user edits file
    When user clicks Publish button
    Then error is shown that commit message is empty

  Scenario: User can see preview of edited file
    Given a confluence page with macro
    When user clicks on edit button
    Then dialog with editor is shown
    When user clicks Preview button
    When spinner is shown
    Then preview is shown

  Scenario: When file is edited user see orange border and edit button
    Given a confluence page with macro
    When file is edited
    Then border is orange
    And edit button is orange

