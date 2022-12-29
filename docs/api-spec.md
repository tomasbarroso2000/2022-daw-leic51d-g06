## HTTP API Specification

### Introduction

All the success responses produced by this API use the Siren hypermedia specification.
The error responses use the Problem Json specification that provides the details of each error.

Note: Every endpoint that relates to some activity withing a game (send hits, define layout, forfeit) responds with the updated version of the game in question.

### Hypermedia Relations

![image](https://user-images.githubusercontent.com/76069448/198892372-c400f351-da00-4d23-bd62-5e9bff4bbb86.png)

### Home

#### Home

URI: /api/

Method: GET

Success response status: 200 - OK

Response body properties:

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

Success response status: 200 - OK

Response body properties:

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

Success response status: 201 - CREATED

Response body properties:

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

Success response status: 201 - CREATED

Response body properties:

```json
{
  "token": "a74509f5-2ba6-419a-9947-8c3b977236db"
}
```

#### Rankings

URI: /api/users/rankings?limit={limit}&skip={skip}

Method: GET

Success response status: 200 - OK

Response body properties:

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

Success response status: 200 - OK

Response body properties:

```json
{
  "waiting-for-game": true,
  "lobby-or-game-id": 1
}
```

#### Entered Game

URI: /api/lobby/{lobbyId}

Method: DELETE

Authorization: Bearer token

Success response status: 200 - OK

Response body properties:

```json
{
  "game-id": 2
}
```

### Games

#### Game Types

URI: /api/games/types

Method: GET

Success response status: 200 - OK

```json
{
  "game-types": [
    {
      "name": "beginner",
      "board-size": 10,
      "shots-per-round": 1,
      "layout-def-time-in-secs": 60,
      "shooting-time-in-secs": 60,
      "fleet": [
        {
          "name": "carrier",
          "size": 6,
          "game-type": "beginner"
        },
        {
          "name": "battleship",
          "size": 5,
          "game-type": "beginner"
        },
        {
          "name": "cruiser",
          "size": 4,
          "game-type": "beginner"
        },
        {
          "name": "submarine",
          "size": 4,
          "game-type": "beginner"
        },
        {
          "name": "destroyer",
          "size": 3,
          "game-type": "beginner"
        }
      ]
    },
    {
      "name": "experienced",
      "board-size": 12,
      "shots-per-round": 5,
      "layout-def-time-in-secs": 30,
      "shooting-time-in-secs": 60,
      "fleet": [
        {
          "name": "carrier",
          "size": 5,
          "game-type": "experienced"
        },
        {
          "name": "battleship",
          "size": 4,
          "game-type": "experienced"
        },
        {
          "name": "cruiser",
          "size": 3,
          "game-type": "experienced"
        },
        {
          "name": "submarine",
          "size": 3,
          "game-type": "experienced"
        },
        {
          "name": "destroyer",
          "size": 2,
          "game-type": "experienced"
        }
      ]
    },
    {
      "name": "expert",
      "board-size": 15,
      "shots-per-round": 6,
      "layout-def-time-in-secs": 30,
      "shooting-time-in-secs": 30,
      "fleet": [
        {
          "name": "carrier",
          "size": 5,
          "game-type": "expert"
        },
        {
          "name": "battleship",
          "size": 4,
          "game-type": "expert"
        },
        {
          "name": "destroyer",
          "size": 3,
          "game-type": "expert"
        }
      ]
    }
  ]
}
```

#### Available Games

URI: /api/games?limit={limit}&skip={skip}

Method: GET

Authorization: Bearer token

Success response status: 200 - OK

```json
{
  "games": [
    {
      "id": 1,
      "type": {
        "name": "beginner",
        "board-size": 10,
        "shots-per-round": 1,
        "layout-def-time-in-secs": 60,
        "shooting-time-in-secs": 60,
        "fleet": [
          {
            "name": "carrier",
            "size": 6,
            "game-type": "beginner"
          },
          {
            "name": "battleship",
            "size": 5,
            "game-type": "beginner"
          },
          {
            "name": "cruiser",
            "size": 4,
            "game-type": "beginner"
          },
          {
            "name": "submarine",
            "size": 4,
            "game-type": "beginner"
          },
          {
            "name": "destroyer",
            "size": 3,
            "game-type": "beginner"
          }
        ]
      },
      "state": "layout_definition",
      "opponent": {
        "id": 6,
        "name": "LordFarquaadReal",
        "score": 60
      },
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
  ],
  "has-more": false
}
```

#### Game Info

URI: /api/games/info/{gameId}

Method: GET

Authorization: Bearer token

Success response status: 200 - OK

Response body properties:

```json
{
  "id": 1,
  "type": {
    "name": "beginner",
    "board-size": 10,
    "shots-per-round": 1,
    "layout-def-time-in-secs": 60,
    "shooting-time-in-secs": 60,
    "fleet": [
      {
        "name": "carrier",
        "size": 6,
        "game-type": "beginner"
      },
      {
        "name": "battleship",
        "size": 5,
        "game-type": "beginner"
      },
      {
        "name": "cruiser",
        "size": 4,
        "game-type": "beginner"
      },
      {
        "name": "submarine",
        "size": 4,
        "game-type": "beginner"
      },
      {
        "name": "destroyer",
        "size": 3,
        "game-type": "beginner"
      }
    ]
  },
  "state": "layout_definition",
  "opponent": {
    "id": 6,
    "name": "LordFarquaadReal",
    "score": 60
  },
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

Success response status: 200 - OK

Response body properties:

```json
{
  "nr": 2
}
```

#### Game State

URI: /api/games/state/{gameId}

Method: GET

Success response status: 200 - OK

Response body properties:

```json
{
  "state": "shooting"
}
```

#### Player Fleet State

URI: /api/games/fleet/player/{gameId}

Method: GET

Authorization: Bearer token

Success response status: 200 - OK

Response body properties:

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

Success response status: 200 - OK

Response body properties:

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

Success response status: 201 - CREATED

Response body properties:

```json
{
  "id": 1,
  "type": {
    "name": "beginner",
    "board-size": 10,
    "shots-per-round": 1,
    "layout-def-time-in-secs": 60,
    "shooting-time-in-secs": 60,
    "fleet": [
      {
        "name": "carrier",
        "size": 6,
        "game-type": "beginner"
      },
      {
        "name": "battleship",
        "size": 5,
        "game-type": "beginner"
      },
      {
        "name": "cruiser",
        "size": 4,
        "game-type": "beginner"
      },
      {
        "name": "submarine",
        "size": 4,
        "game-type": "beginner"
      },
      {
        "name": "destroyer",
        "size": 3,
        "game-type": "beginner"
      }
    ]
  },
  "state": "layout_definition",
  "opponent": {
    "id": 6,
    "name": "LordFarquaadReal",
    "score": 60
  },
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

Success response status: 201 - CREATED

Response body properties:

```json
{
  "id": 1,
  "type": {
    "name": "beginner",
    "board-size": 10,
    "shots-per-round": 1,
    "layout-def-time-in-secs": 60,
    "shooting-time-in-secs": 60,
    "fleet": [
      {
        "name": "carrier",
        "size": 6,
        "game-type": "beginner"
      },
      {
        "name": "battleship",
        "size": 5,
        "game-type": "beginner"
      },
      {
        "name": "cruiser",
        "size": 4,
        "game-type": "beginner"
      },
      {
        "name": "submarine",
        "size": 4,
        "game-type": "beginner"
      },
      {
        "name": "destroyer",
        "size": 3,
        "game-type": "beginner"
      }
    ]
  },
  "state": "layout_definition",
  "opponent": {
    "id": 6,
    "name": "LordFarquaadReal",
    "score": 60
  },
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

#### Forfeit

URI: /api/games/forfeit

Method: PUT

Authorization: Bearer token

Request body:

```json
{
  "game-id": 2
}
```

Success response status: 201 - CREATED

Response body properties:

```json
{
  "id": 1,
  "type": {
    "name": "beginner",
    "board-size": 10,
    "shots-per-round": 1,
    "layout-def-time-in-secs": 60,
    "shooting-time-in-secs": 60,
    "fleet": [
      {
        "name": "carrier",
        "size": 6,
        "game-type": "beginner"
      },
      {
        "name": "battleship",
        "size": 5,
        "game-type": "beginner"
      },
      {
        "name": "cruiser",
        "size": 4,
        "game-type": "beginner"
      },
      {
        "name": "submarine",
        "size": 4,
        "game-type": "beginner"
      },
      {
        "name": "destroyer",
        "size": 3,
        "game-type": "beginner"
      }
    ]
  },
  "state": "completed",
  "opponent": {
    "id": 6,
    "name": "LordFarquaadReal",
    "score": 60
  },
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
