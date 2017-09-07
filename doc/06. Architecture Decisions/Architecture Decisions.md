Design Decisions 
================

- NoSQL way to manage data in database

    Database we use is confluence's ActiveObjects managed database. Though we use SQL database,
Git4C backend internally processes persistance objects the NoSQL way. This decision was caused by the problems with the way Active Objects manages polimophic tables in one to many relations.

- Clean Architecture