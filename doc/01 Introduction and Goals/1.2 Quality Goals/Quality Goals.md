Quality Goals 
-------------

### Modularity and independence of frameworks
Git Viewer For Confluence Architecture aims to be as open close as possible,
with infrastructural modules implemented independently from the business logic layer
we make sure that future changes and frameworks decisions do not influence current flow of information
and effective functionality of application.

### independence of Database

Git4C architecture allows you to easily replace your database for another one without changing your business rules and core layer.


### Testability

Large amount of abstractions makes Git4C architecture very testable allowing you to inject test structures without forcing you to use actual Database or other external services.
