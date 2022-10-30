## HTTP API Specification

### Introduction

All the success responses produced by this API use the Siren hypermedia specification.
The error responses use the Problem Json specification that provides the details of each error.  

### Hypermedia Relations

![image](https://user-images.githubusercontent.com/76069448/198892372-c400f351-da00-4d23-bd62-5e9bff4bbb86.png)

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

#### Create Token

URI: /api/token

Method: POST

Request body:
```json
{
  "email": "iloveshrek@gmail.com",
  "password": "LordFarquaad1"
}
```

Response body parameters:
```json
{
  "token": "a74509f5-2ba6-419a-9947-8c3b977236db"
}
```

#### Rankings

URI: /api/users/rankings?limit={limit}&skip={skip}

Method: GET

Response body parameters:
```json
{
  "rankings": {
    "list": [
      {
        "id": 3,
        "name": "Fiona",
        "score": 10
      }
    ],
    "has-more": true
  }
}
```

#### Enter Lobby

URI: /api/lobby

Method: POST

Authorization: Bearer token

Request body:
```json
{
  "game-type": "beginner"
}
```

Response body parameters:
```json
{
  "waiting-for-game": true,
  "lobby-or-game-id": 1
}
```

#### Entered Game

URI: /api/lobby/{lobbyId}

Method: PUT

Authorization: Bearer token

Response body parameters:
```json
{
  "game-id": 2
}
```

### Games

#### Game Info

URI: /api/games/info/{gameId}

Method: GET

Authorization: Bearer token

Response body parameters:
```json
{
  "id": 1,
  "type": "beginner",
  "state": "layout_definition",
  "opponent": 2,
  "playing": false,
  "started-at": "2022-10-30T16:51:55.593619Z",
  "fleet": [
    {
      "first-square": "a1",
      "name": "carrier",
      "size": 5,
      "n-of-hits": 0,
      "destroyed": false,
      "orientation": "horizontal",
      "user-id": 1,
      "game-id": 1
    }
  ],
  "taken-hits": [
    {
      "row": "c",
      "column": 1
    }
  ],
  "enemy-sunk-fleet": [],
  "hits": [
    {
      "row": "c",
      "column": 1
    }
  ],
  "misses": []
}
```

#### Number of Played Games

URI: /api/games/total

Method: GET

Response body parameters:
```json
{
  "nr": 2
}
```

#### Game State

URI: /api/games/state/{gameId}

Method: GET

Response body parameters:
```json
{
  "state": "shooting"
}
```

#### Player Fleet State

URI: /api/games/fleet/player/{gameId}

Method: GET

Authorization: Bearer token

Response body parameters:
```json
{
  "fleet": [
    {
      "name": "carrier",
      "destroyed": false
    }
  ]
}
```

#### Enemy Fleet State

URI: /api/games/fleet/enemy/{gameId}

Method: GET

Authorization: Bearer token

Response body parameters:
```json
{
  "fleet": [
    {
      "name": "carrier",
      "destroyed": false
    }
  ]
}
```

#### Send Hits

URI: /api/games/hit

Method: PUT

Authorization: Bearer token

Request body:
```json
{
  "game-id": 2,
  "squares": [
    {
      "row": "a",
      "column": 1
    }
  ]
}
```

Response body parameters:
```json
{
  "hits-outcome": [
    {
      "square": {
        "row": "a",
        "column": 1
      },
      "hit-ship": false,
      "destroyed-ship": null
    }
  ],
  "win": false
}
```

#### Define Layout

URI: /api/games/layout

Method: PUT

Authorization: Bearer token

Request body:
```json
{
  "game-id": 2,
  "ships": [
    {
      "name": "carrier",
      "first-square": "a1",
      "orientation": "horizontal"
    }
  ]
}
```

Response body parameters:
```json
{
  "status": "waiting"
}
```
