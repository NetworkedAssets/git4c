Feature: Selecting Table of Contents option during creation of Single File Macro

  Background: User is logged in and Git4C Macro Parameters window for Single File macro is opened

  Scenario: User enables Table of Contents for a file
    When User selects a file with Table of Contents (Markdown, Asciidoc)
    And Option to switch on Table of Contents is visible
    And User enables Table of Contents
    Then Table of Content is visible in preview
    And Table of Content is visible in macro content on Confluence page

  Scenario: User disables Table of Contents
    When User selects a file with Table of Contents (Markdown, Asciidoc)
    And Option to switch on Table of Contents is visible
    And User disables Table of Contents
    Then Table of Content is not visible in preview
    And Table of Content is not visible in macro content on Confluence page

  Scenario: User selects file without Table of Contents
    When User selects file without Table of Contents (code, image)
    Than Option to switch on Table of Contents is not visible
    And Table of Content is not visible in macro

