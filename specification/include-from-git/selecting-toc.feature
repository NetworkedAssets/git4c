Feature: Selecting Table of Contents option during creation of Single Page Macro

  Background: User is logged in and it adding Single Page Macro

  Scenario: User enables Table of Contents
    Given: Dialog of Single Page Macro creation
    When: User selects file with Table of Contents (Markdown, Asciidoc)
    Then: Option to toggle Table of Contents is visible
    When: User enables Table of Contents
    Then: Table of Content is visible in preview
    When: User saves Macro
    Then: Table of Content is visible in macro

  Scenario: User disables Table of Contents
    Given: Dialog of Single Page Macro creation
    When: User selects file with Table of Contents (Markdown, Asciidoc)
    Then: Option to toggle Table of Contents is visible
    When: User disables Table of Contents
    Then: Table of Content is not visible in preview
    When: User saves Macro
    Then: Table of Content is not visible in macro

  Scenario: User selects file without Table of Contents
    Given: Dialog of Single Page Macro creation
    When: User selects file without Table of Contents (code, image)
    Then: Option to toggle Table of Contents is not visible
    When: User saves Macro
    Then: Table of Content is not visible in macro

