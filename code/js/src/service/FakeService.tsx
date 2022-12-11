import { CreateToken } from "../domain/CreateToken"
import { CreateUser } from "../domain/CreateUser"
import { GameType, GameTypes } from "../domain/GameTypes"
import { Home } from "../domain/Home"
import { Rankings } from "../domain/Rankings"
import { Service } from "./Service"

export class FakeService implements Service {
    createToken: (email: string, password: string) => Promise<CreateToken>
    home = function home(): Promise<Home> {
        return Promise.resolve(
            {
                name: "Exploding Battleships",
                version: "1.0.0",
                authors: ["Aleixo", "Tomasso", "Palmilha"]
            }
        )
    }

    homeNavigation = ["/rankings"]

    rankings = function(): Promise<Rankings> {
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

    createUser = function(): Promise<CreateUser> {
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
                        boardSize: 0,
                        shotsPerRound: 0,
                        layoutDefTime: 0,
                        shootingTime: 0,
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

    rankingsNavigation: ["/quaqua"]
}