Feature: Change branch in multi file macro
  The feature allows an user to change a branch while reading selected file.
  After switching branches current file is not changed, but when file is not present
  macro resets to default file.

  Background: The user is logged in

  Scenario: User can see list of available branches
    Given a confluence page with multi file macro
    When the user clicks on branch select
    Then list of branches is shown

  Scenario: User can change current branch (file available)
    Given a confluence page with multi file macro
    When list of branches is shown
    When the user selects branch
    Then branch is visible in url
    Then branch is changed
    Then file from selected branch is displayed

  Scenario: User can change current branch (file not available)
    Given a confluence page with multi file macro
    When list of branches is shown
    When the user selects branch
    Then branch is visible in url
    Then branch is changed
    Then default file from selected branch is displayed

  Scenario: User changes branch in url (branch exists and file is available)
    Given a confluence page with multi file macro
    When the user enters another branch
    Then branch is changed
    Then file from selected branch is displayed

  Scenario: User changes branch in url (branch exists and file is not available)
    Given a confluence page with multi file macro
    When the user enters another branch
    Then branch is changed
    Then file from selected branch is displayed

  Scenario: User changes branch in url (branch doesn't exist)
    Given a confluence page with multi file macro
    When the user enters another branch
    Then error message is shown
