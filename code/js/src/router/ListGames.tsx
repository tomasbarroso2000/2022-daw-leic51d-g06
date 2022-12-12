import * as React from "react"
import { Link } from "react-router-dom"
import { askService } from "../service/askService"
import { paths, service } from "./App"

export function ListGames() {
    const games = askService(service, service.games)

    if (!games) {
        return (
            <div>
                ...loading...
            </div>
        )
    }

    return (
        <div>
            <h1>Games</h1>
            {
                    games.games.map(game => {
                        {
                            if (game.type.playing)
                                return (
                                    <div key={game.id}>
                                        <p>{game.type.name}</p>
                                        <p>Your turn</p>
                                    </div>
                                )
                            else
                                return (
                                    <div key={game.id}>
                                        <p>{game.type.name}</p>
                                        <p>Not your turn</p>
                                    </div>
                                )
                        }
                    })
                }
        </div>
    )
}