import { CreateToken } from "../domain/CreateToken"
import { CreateUser } from "../domain/CreateUser"
import { EnteredGame, EnterLobby } from "../domain/Lobby"
import { Game } from "../domain/Game"
import { GameType, GameTypes } from "../domain/GameTypes"
import { Home } from "../domain/Home"
import { Rankings } from "../domain/Rankings"
import { UserHome } from "../domain/UserHome"
import { Service } from "./Service"
import { GamesList } from "../domain/GamesList"
import { LayoutShip } from "../domain/LayoutShip"
import { Square } from "../domain/Square"
import { Field } from "siren-types"
import { paths } from "../router/App"

export class FakeService implements Service {

    getCreateUserFields = function (): Promise<Field[]> {
        return Promise.resolve([
            {name: "name", type: "text"},
            {name: "email", type: "email"},
            {name: "password", type: "password"}
        ])
    }

    getCreateTokenFields = function (): Promise<Field[]> {
        return Promise.resolve([
            {name: "email", type: "email"},
            {name: "password", type: "password"}
        ])
    }

    createUser = function (name: string, email: string, password: string): Promise<CreateUser> {
        return Promise.resolve(
            {
                id: 1
            }
        )
    }

    createToken: (email: string, password: string) => Promise<CreateToken>

    home = function (): Promise<Home> {
        return Promise.resolve(
            {
                name: "Exploding Battleships",
                version: "1.0.0",
                authors: ["Aleixo", "Tomasso", "Palmilha"]
            }
        )
    }

    homeNavigation = ["rankings", "create-user", "create-token", "list-games"]

    userHome = function (token: string): Promise<UserHome> {
        return Promise.resolve(
            {
                id: 1,
                name: "Fiona",
                email: "iloveshrek@gmail.com",
                score: 10
            }
        )
    }

    userHomeNavigation: string[]

    rankings = function (limit: number, skip: number): Promise<Rankings> {
        return Promise.resolve(
            {
                rankings: [
                    {
                        id: 1,
                        name: "Aleixo",
                        score: 10
                    },
                    {
                        id: 2,
                        name: "Tomasso",
                        score: 9
                    },
                    {
                        id: 3,
                        name: "Palmilha",
                        score: 0
                    }
                ],
                hasMore: false
            }
        )
    }

    gameTypes = async function (token: string): Promise<GameTypes | undefined> {
        return Promise.resolve(
            {
                gameTypes: [
                    {
                        name: "beginner",
                        boardSize: 10,
                        shotsPerRound: 1,
                        layoutDefTime: 60,
                        shootingTime: 60,
                        fleet: [
                            {
                                name: "duckShip",
                                size: 99,
                                gameType: "hardcore"
                            }
                        ]
                    }
                ]
            }
        )
    }

    enterLobby = async function (token: string, gameType: string): Promise<EnterLobby | undefined> {
        return Promise.resolve(
            {
                waitingForGame: true,
                lobbyOrGameId: 1
            }
        )
    }

    enteredGame = async function (token: string, lobbyId: number) : Promise<EnteredGame> {
        return Promise.resolve(
            {
                gameId: 1
            }
        )
    }

    games = async function (token: string, limit: number, skip: number): Promise<GamesList | undefined> {
        return Promise.resolve(
            {
            games: [
                {
                    id: 2,
                    type: {
                        name: "beginner",
                        boardSize: 10,
                        shotsPerRound: 1,
                        layoutDefTime: 60,
                        shootingTime: 60,
                        fleet: [
                            {
                                name: "duckShip",
                                size: 5,
                                gameType: "hardcore"
                            }
                        ]
                    },
                        state: "layout_definition",
                        opponent: {
                            id: 8,
                            name: "Fiona2",
                            score: 0
                        },
                        playing: false,
                        startedAt: "2022-12-12T10:19:04.662Z",
                        fleet: [],
                        takenHits: [],
                        enemySunkFleet: [],
                        hits: [],
                        misses: []
                }
            ],
            hasMore: false
        })
    }

    gameInfo = async function (token: string, gameId: number): Promise<Game | undefined> {
        return Promise.resolve(
            {
                id: 1,
                type: {
                    name: "beginner",
                        boardSize: 10,
                        shotsPerRound: 1,
                        layoutDefTime: 60,
                        shootingTime: 60,
                        fleet: [
                            {
                                name: "duckShip",
                                size: 5,
                                gameType: "hardcore"
                            }
                        ]
                },
                state: "layout_definition",
                opponent: {
                    id: 8,
                            name: "Fiona2",
                            score: 0
                },
                playing: false,
                startedAt: "",
                fleet: [],
                takenHits: [],
                enemySunkFleet: [],
                hits: [],
                misses: []
            }
        )
    }

    rankingsNavigation: ["/quaqua"]

    defineLayout = async function (token: string, gameId: number, fleet: LayoutShip[]): Promise<Game | undefined> {
        return Promise.resolve(
            {
                id: 1,
                type: {
                    name: "beginner",
                        boardSize: 10,
                        shotsPerRound: 1,
                        layoutDefTime: 60,
                        shootingTime: 60,
                        fleet: [
                            {
                                name: "duckShip",
                                size: 99,
                                gameType: "hardcore"
                            }
                        ]
                },
                state: "layout_definition",
                opponent: {
                    id: 8,
                            name: "Fiona2",
                            score: 0
                },
                playing: false,
                startedAt: "",
                fleet: [],
                takenHits: [],
                enemySunkFleet: [],
                hits: [],
                misses: []
            }
        )
    }

    sendHits = async function (token: string, gameId: number, squares: Array<Square>): Promise<Game | undefined> {
        return Promise.resolve(
            {
                id: 1,
                type: {
                    name: "beginner",
                        boardSize: 10,
                        shotsPerRound: 1,
                        layoutDefTime: 60,
                        shootingTime: 60,
                        fleet: [
                            {
                                name: "duckShip",
                                size: 99,
                                gameType: "hardcore"
                            }
                        ]
                },
                state: "shooting",
                opponent: {
                    id: 8,
                            name: "Fiona2",
                            score: 0
                },
                playing: false,
                startedAt: "",
                fleet: [],
                takenHits: [],
                enemySunkFleet: [],
                hits: [],
                misses: []
            }
        )
    }

    forfeit = async function (token: string, gameId: number): Promise<Game | undefined> {
        return Promise.resolve(
            {
                id: 1,
                type: {
                    name: "beginner",
                        boardSize: 10,
                        shotsPerRound: 1,
                        layoutDefTime: 60,
                        shootingTime: 60,
                        fleet: [
                            {
                                name: "duckShip",
                                size: 99,
                                gameType: "hardcore"
                            }
                        ]
                },
                state: "shooting",
                opponent: {
                    id: 8,
                            name: "Fiona2",
                            score: 0
                },
                playing: false,
                startedAt: "",
                fleet: [],
                takenHits: [],
                enemySunkFleet: [],
                hits: [],
                misses: []
            }
        )
    }
}