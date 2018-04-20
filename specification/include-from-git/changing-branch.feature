Feature: Switch branch in Multi File Macro
  The feature allows a user to switch between branches while displaying repository content.

  Background: User is logged in and visits a Confluence page with Multi File Macro.

  Scenario: User browses a list of available branches
    When User expands branch list
    Then List of branches is shown

  Scenario: User switches to another branch with the same file on it
    Given List of branches is expanded
    When User selects a branch
    Then Selected branch name is visible in URL
    And Branch name is changed on branch selector
    And File from selected branch is displayed

  Scenario: User switches to another branch without the same file on it
    Given List of branches is expanded
    When User selects a branch
    Then Selected branch name is visible in URL
    And Branch name is changed on branch selector
    And Error is shown, informing about lack of the file on current branch
    And Default file from selected branch is displayed

  Scenario: User switches to existing branch with the same file on it using URL
    When User changes branch name in URL
    Then Branch name is changed on branch selector
    And File from selected branch is displayed

  Scenario: User switches to existing branch without the same file on it using URL
    When  User changes branch name in URL
    Then Branch name is changed on branch selector
    And Error is shown, informing about lack of the file on current branch
    And Default file from selected branch is displayed

  Scenario: User switches to unexisting branch using URL
    When User changes branch name in URL
    Then Error is shown, informing about lack of the branch in repository
    And Link to default branch is displayed
