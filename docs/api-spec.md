## HTTP API Specification

### Introduction

All the success responses produced by this API use the Siren hypermedia specification.
The error responses use the Problem Json specification that provides the details of each error.  

### Home

#### Home
URI: /api/

Method: GET

Response body parameters:
```json
{
  "name": "Exploding Battleships",
  "version": "0.1.0",
  "authors": ["Alexandre Madeira", "Miguel Palma", "Tomás Barroso"]
}
```

### Users

#### User Home

URI: /api/me

Method: GET

Authorization: Bearer token

Response body parameters:
```json
{
  "id": "3",
  "name": "Fiona",
  "email": "iloveshrek@gmail.com",
  "score": 10
}
```

#### Create User

URI: /api/users

Method: POST

Request body:

```json
{
  "name": "Fiona",
  "email": "iloveshrek@gmail.com",
  "password": "LordFarquaad1"
}
```

Response body parameters:
```json
{
  "id": "3"
}
```





### Games
