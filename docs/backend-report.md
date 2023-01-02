## Introduction

This document contains the relevant design and implementation aspects of the Exploding Battleships game API.

## Modeling the database

### Conceptual Model

The following diagram holds the Entity-Relationship model for the information managed by the system.

![er](https://user-images.githubusercontent.com/76069448/210240970-e52d074b-913a-4bc7-a09b-1e84d4cbd58c.png)

We highlight the following aspects:

- The realtions between users and games represent the player1, the player2 and the current player, respectively;
- The fact that the game types and the ship types are stored in the database allows for the easy creation of new game/ship types by an administrator. 

The conceptual model has the following restrictions:

- The name, email and password_ver in the users entity cannot be null;
- The score in the users entity must be above or equal to zero;
- The type, state and started_at in the games entity cannot be null;
- The curr_player must be either player1 or player2;
- The state in the games entity must be "layout_definition", "shooting" or "completed";
- The hit_timestamp and on_ship in the hits entity cannot be null;
- The square in the hits and ship entities must follow a specific format (a letter followed by a number);
- The name, size, destroyed, n_of_hits and orientation in the ships entity cannot be null;
- The size in the ships entity must be above 0;
- The n_of_hits in the ships entity must be above or equal to zero;
- The orientation in the ships entity must be "vertical" or "horizontal";
- The game_type and enter_time in the lobbies entity cannot be null;
- The board_size, shots_per_round, layout_def_time_in_secs, shooting_time_in_secs in the game_types cannot be null.

### Physical Model

The physical model of the database is available in [create.sql](https://github.com/isel-leic-daw/2022-daw-leic51d-g06/tree/main/code/jvm/sql/create.sql).

We highlight the following aspects of this model:

- Our design allows the user to be associated with multiple tokens (3) (stored in the database) which should happen in "real-world applications" where a new token is generated for each session;
- The stored passwords and tokens are just the hash code of the actual value to provide more security;
- We thought about adding privilege levels to the users so that there could be database managers within the users. Even though it is not yet implemented, it could be something to think about in the future.

## Software organization

### Spring Framework

The Spring Framework is an open-source application framework that provides infrastructure support for developing Java applications.
This framework was used to build this application, allowing the devolopers to easily create HTTP endpoint handlers, app configurations (interceptors and argument resolvers) and add components to the overall application as its complexity increases.

### Request Details

We will use the defineLayout endpoint as an example of a use case scenario. When a request is made to this endpoint, the request token is intercepted and creates a User object that represents that tokens's user. This object is then sent to the HTTP handler which also receives the request body. In the HTTP handler, the service which is responsible for that operation is called and will execute all the validations necessary on the submitted data. If any anomally is detected, the services module will throw an exception that will propagate to the http module, which will create an HTTP response. The services module will call the necessary data functions to perform the necessary changes to the database (storing the ships in the databse). After all this is done, a success outcome is produced by the services module and the http module creates a success response for the user of the API to receive.

### Connection Management

The data module implements a transaction system that uses the same connection throughout an entire operation, thus preventing the creation of multiple connections for the same operation. The transaction is created and executed in the Services module (using the Data.getTransaction and executeTransaction functions, respectively) and passed to the data module as an argument. When all the operations inside the executeTransaction block are terminated, the connection is closed. This way we guarantee that the database-related operations are atomic.

### Data Access

The data module is divided into five sections: transactions, users data, ships data, lobbies data, hits data and games data. Each section has an interface implemented for database access and data in memory (used for unit tests). The data module also implements an interface that connects all of the sections previously mentioned.
Each version of the data module has a method called getTransaction that creates a Transaction object that prevents the use of multiple connections to the database for the same operation.

### Services

The services module contains all the logic of the operations the API provides and is divided into two sections: users services and games services. This module also contains a utils file to help organize the more complex operations of the module.

### Error Handling/Processing

We created a class called AppException that extends Exception and represents an exception thrown in the services module. Whether a validation fails or an exception is thrown in the data module, the services module will catch it and transform it into an AppException (which also logs the error) so that the http module can interpret it. We also created a DataException class that extends Exception to represent the exceptions thrown by failed verifications in the data in memory module. We did this to recreate the SQLException thrown by the database when a constraint is violated. In the http module, all the AppExceptions are caught and transformed into HTTP responses for the user of the API to receive.

### HTTP Module

The http module is divided into three sections: controllers, models, and pipeline.
The controllers section is divided into home controller (contains the home endpoint handler), users controller (contains the users-related endpoints handlers) and games controller (contains the games-related endpoints handlers).
The models section contains the signature of the models used to represent API request and response bodies.
The pipeline section contains the interceptor that converts a bearer token into a User object and the JacksonCustomizer to be able to correctly interpret the request bodies' properties.

## Critical Evaluation

Identified defects:

- The authenticated endpoints require 2 different connections to the database (one for the authentication and one for the actual operation);
- Using the current player to define the winner of a completed game might not be the ideal way to do it. After implementing the ending of a game when the layouts of the players aren't submitted, no player wins but the current player still has a value. Even though no player's score is increased, one of the two players is logically considered the winner (the player that first entered the lobby);
- The name and size of a ship should be associated with its ship type (there should be a relationship between the ships and ship_types entities). Since this would cause a major refactor in the whole project and the client applications, it's something that we'll have to figure out when we have the time to do it;
- The http and database modules are not fully tested because, since those tests require a full reset of the database, we focused our attention more on the services logic tests which give us more insightful results about the design quality of our code; 
- The services module uses error codes based on the HTTP protocol which should only be known by the http module;
- Because the Kotlin language's type system has no knowledge of the exceptions that might be thrown in a function, it might be better to stop using exceptions in the services module to represent an error and replace them with different return types for each error in every function.

##

<p align="center">ISEL - LEIC - DAW - 51D - G06<p>
