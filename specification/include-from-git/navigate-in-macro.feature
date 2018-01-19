Feature: Navigate in multi file macro
  The feature allows an user to navigate between files in macro.
  After switching file, it should be selected in file tree and visible in url.
  content of macro should change.

  Background: The user is logged in

  Scenario: User can switch between files with file tree
    Given a confluence page with multi file macro
    When the user clicks selects a file in the file tree
    Then file is highlighted in file tree
    Then selected file's content is diplayed
    Then selected file is present in the url


  Scenario: User can switch between files with browser history navigation buttons
    Given a confluence page with multi file macro
    When the user has changed file in the file tree
    When the user clicks "back" button in browser
    Then previous file is displayed
    Then previous file is selected in file tree
    Then previous file is present in the url

  Scenario: User can use link to file with specified branch and anchor in Multi File macro and will be navigated to correct fragment
    Given a confluence page with multi file macro
    When user uses a link with specified branch and anchor
    Then Git4C macro will navigate to specified in link fragment.