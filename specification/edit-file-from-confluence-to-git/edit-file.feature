Feature: Editing in single and multi file macro
  The feature allows user to edit currently shown file

  Background: The user is logged in

    Scenario: User can edit file
      Given a confluence page with macro
      When user clicks on edit button
      Then dialog with editor is shown
      Then user edits file
      Then user sets commit message
      When user clicks Publish button
      Then file is send to server


  Scenario: User see error when commit message is empty
    Given a confluence page with macro
    When user clicks on edit button
    Then dialog with editor is shown
    Then user edits file
    When user clicks Publish button
    Then error is shown that commit message is empty

  Scenario: User can edit target branch
    Given a confluence page with macro
    When user clicks on edit button
    Then dialog with editor is shown
    Then user edits file
    Then user sets commit message
    When user sets branch
    Then file is send to server

  Scenario: User can see preview of edited file
    Given a confluence page with macro
    When user clicks on edit button
    Then dialog with editor is shown
    When user clicks Preview button
    When spinner is shown
    Then preview is shown
