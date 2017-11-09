Solution Strategy
=================

-  Solution is kept as in a way that most accurately describes the architecture of the system. This means that top level decomposition consists of:
    - Application
        - Interfaces
        - Delivery
        - REST
    - Core
    - Infrastructure
    - Boundary
    - Data

Ideally we want to keep Core and business logic inside it as separate from implementation contained in infrastructure.


![Architecture diagram 1](../images/03-diagram.png)

As pictrued on the diagram above, Git Viewer For Confluence uses architecture based on Clean Architecture.
Each Request that is processed by the Rest layer is forwarded by the Executor layer to the business layer via use cases.


