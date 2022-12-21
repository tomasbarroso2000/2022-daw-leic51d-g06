import { CreateToken } from "../domain/CreateToken"
import { CreateUser } from "../domain/CreateUser"
import { EnteredGame, EnterLobby } from "../domain/Lobby"
import { Game, GamesList } from "../domain/GamesList"
import { GameType, GameTypes } from "../domain/GameTypes"
import { Home } from "../domain/Home"
import { Rankings } from "../domain/Rankings"
import { UserHome } from "../domain/UserHome"
import { Service } from "./Service"

export class FakeService implements Service {

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

    homeNavigation = ["/rankings"]

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

    rankings = function (): Promise<Rankings> {
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

    createUser = function (): Promise<CreateUser> {
        return Promise.resolve(
            {
                id: 1
            }
        )
    }

    gameTypes = async function (): Promise<GameTypes | undefined> {
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

    enteredGame = async function (token:string, lobbyId: number) : Promise<EnteredGame> {
        return Promise.resolve(
            {
                gameId: 1
            }
        )
    }

    games = async function (): Promise<GamesList | undefined> {
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

    gameInfo = async function (): Promise<Game | undefined> {
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
                state: "",
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
}