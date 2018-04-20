Feature: Single File macro view

  Background: User is logged in

  Scenario: Hide Single File Macro toolbar
    Given Confluence page with Single File macro
    When User clicks on hide toolbar button
    Then Toolbar disappears
    And Open toolbar button appears
    And Open toolbar button floats at the top of the document as User scrolls through the file

  Scenario: Display commit history
    Given Confluence page with Single File macro
    When User clicks on view commits button
    Then Window with previous commits listed opens

  Scenario: View source of displayed file
    This scenario covers Markdown, Asciidoc, PUML and SVG files
    Given Confluence page with Single File macro
    When User clicks on view source button
    Then Window with file source opens