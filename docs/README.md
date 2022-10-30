## Introduction
This document contains the relevant design and implementation aspects of the battleships game api.
## Modeling the database
### Conceptual Model
The following diagram holds the Entity-Relationship model for the information managed by the system.

![draw_final](https://user-images.githubusercontent.com/76069448/198884875-fc5e7b0b-584c-4a29-aba7-735af61aadb2.jpg)

We highlight the following aspects:
* The realtions between users and games represent the player1, the player2 and the current player, respectively;
* The users entity is associated to every other entity.

The conceptual model has the following restrictions: 
* The name, email and password_ver in the users entity cannot be null;
* The score in the users entity must be above or equal to zero;
* The type, state and started_at in the games entity cannot be null;
* The curr_player must be either player1 or player2;
* The type in the games entity must be "beginner", "experienced" or "expert";
* The state in the games entity must be "layout_definition", "shooting" or "completed";
* The hit_timestamp and on_ship in the hits entity cannot be null;
* The square in the hits and ship entities must follow a specific format (a letter followed by a number);
* The name, size, destroyed and orientation in the ships entity cannot be null;
* The size in the ships entity must be above 0;
* The n_of_hits in the ships entity must be above or equal to zero;
* The name in the ships entity must be "carrier", "battleship", "cruiser", "submarine" or "destroyer";
* The orientation in the ships entity must be "vertical" or "horizontal";
* The game_type and enter_time in the lobbies entity cannot be null;
* The game_type in the lobbies entity must be "beginner", "experienced" or "advanced".

### Physical Model
The physical model of the database is available in [create.sql](https://github.com/isel-leic-daw/2022-daw-leic51d-g06/tree/main/code/jvm/sql/create.sql).

We highlight the following aspects of this model:
* Our design allows the user to be associated with many tokens (stored in the database) which should happen in "real-world applications" where a new token is generated for each session;
* The stored password is just the hash code of the actual password to provide more security;
* The physical model contains tables that are not currently used but will be useful for improving the flexibility of the application in the near future.

## Software organization

### Request Details

example use-case scenario

### Connection Management
The Data module implements a transaction system that uses the same connection throughout an entire operation, thus preventing the creation of multiple connections for the same operation. The transaction is created and executed in the Services module (using the Data.getTransaction and executeTransaction functions, respectively) and passed to the Data module as an argument. When all the operations inside the executeTransaction block are terminated, the connection is closed. This way we guarantee that the database-related operations are atomic.

### Data Access
The Data module is divided into five sections: transactions, usersdata, shipsdata, lobbiesdata, hitsdata and gamesdata. Each section has an interface implemented for database access and data in memory (used for unit tests). The Data module also implements an interface that connects all of the sections previously mentioned.
Each version of the Data module has a method called getTransaction that creates a Transaction object that prevents the use of multiple connections to the database for the same operation.

### Error Handling/Processing
We created a class called AppException that extends Exception and represents an exception thrown in the services module. Whether a validation fails or an exception is thrown in the Data module, the Services module will catch it and transform it into an AppException (which also logs the error) so that the WebApi module can interpret it. We also created a DataException class that extends Exception to represent the exceptions thrown by failed verifications in the DataMem module. We did this to recreate the SQLException thrown by the database when a constraint is violated. In the WebApi, all the AppExceptions are caught and transformed into HTTP responses for the user of the API to receive.

### HTTP Module

## Critical Evaluation

Identified defects and improvements to be made:
* The authenticated endpoints currently require to different connections to the database (one for the authentication and one for the actual operation) and this should be changed in the future;
* Game types and ship types should probably be stored in the database so they can be managed by someone with privileged access;
* The services module uses error codes based on the http protocol which should only be known by the http module;
* Because the Kotlin language's type system has no knowledge of the exceptions that might be thrown in a function, it might be better to stop using exceptions in the services module to represent an error and replace them with different return types for each error in every function;
* The hashing mechanism should be improved with the classes that already in the project and the tokens should also be hashed;
* The database should have more tests;
* Limit the amount of tokens each user can have simultaneously;
* Act upon the layout definition time deadline;
* Prevent ships to be placed next to each other by always having at least a square of distance between each ship.

##

<p align="center">ISEL - LEIC - DAW - 51D - G06<p>
