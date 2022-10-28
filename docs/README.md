## Introduction
This document contains the relevant design and implementation aspects of the battleships game api.
## Modeling the database
### Conceptual Model
The following diagram holds the Entity-Relationship model for the information managed by the system.

![daw-er](https://user-images.githubusercontent.com/51358916/198718241-e7504487-52cb-4c6b-a1db-826e43a743fc.png)

We highlight the following aspects:
* The Lobbies, Ships and Hits are associated with the Users and Games entities, which can never be null in those relations.

The conceptual model has the following restrictions: 
* The name, email and password_ver in the Users entity cannot be null
* The score in the Users entity must be above or equal to zero
* The type, state, player1, player2, curr_player and started_at in the Games entity cannot be null 
* The curr_player in the Games entity must be either player1 or player2
* The type in the Games entity must be "beginner", "experienced" or "expert"
* The state in the Games entity must be "layout_definition", "shooting" or "completed"
* The board_size, shots_per_round, layout_def_time_in_secs and shooting_time_in_secs in the Game_types entity cannot be null
* The size in the Ship_types entity must be above zero
* The hit_timestamp and on_ship in the Hits entity cannot be null
* The square in the Hits entity must follow a specific format (a letter followed by a number)
* The name, size, destroyed and orientation in the Ships entity cannot be null
* The size in the Ships entity must be above 0
* The n_of_hits in the Ships entity must be above or equal to zero
* The name in the Ships entity must be "carrier", "battleship", "cruiser", "submarine" or "destroyer"
* The orientation in the Ships entity must be "vertical" or "horizontal"
* The game_type and enter_time in the Lobbies entity cannot be null
* The game_type in the Lobbies entity must be "beginner", "experienced" or "advanced"

### Physical Model
The physical model of the database is available in [create.sql](https://github.com/isel-leic-daw/2022-daw-leic51d-g06/tree/main/code/jvm/sql/create.sql)

We highlight the following aspects of this model:
* Our design allows the user to be associated with many token (stored in the database) which should happen in "real-world applications" where a new token is generated for each session
* The stored password is just the hash code of the actual password to provide more security

## Software organization

### Connection Management
The Data module implements a transaction system that uses the same connection throughout an entire operation, thus preventing the creation of multiple connections for the same operation. The transaction is created and executed in the Services module (using the Data.getTransaction and executeTransaction functions, respectively) and passed to the Data module as an argument. When all the operations inside the executeTransaction block are terminated, the connection is closed. This way we guarantee that the database-related operations are atomic.

### Data Access
The Data module is divided into five sections: transactions, usersdata, shipsdata, lobbiesdata, hitsdata and gamesdata. Each section has an interface implemented for database access and data in memory (used for unit tests). The Data module also implements an interface that connects all of the sections previously mentioned.
Each version of the Data module has a method called getTransaction that creates a Transaction object that prevents the use of multiple connections to the database for the same operation.

### Error Handling/Processing
We created a class called AppException that extends Exception and represents an exception thrown in the services module. Whether a validation fails or an exception is thrown in the Data module, the Services module will catch it and transform it into an AppException (which also logs the error) so that the WebApi module can interpret it. We also created a DataException class that extends Exception to represent the exceptions thrown by failed verifications in the DataMem module. We did this to recreate the SQLException thrown by the database when a constraint is violated. In the WebApi, all the AppExceptions are caught and transformed into HTTP responses for the user of the API to receive.

### HTTP Module

<p align="center">ISEL - LEIC - DAW - 51D - G06<p>
