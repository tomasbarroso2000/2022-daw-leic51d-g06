import * as React from "react"
import { useState } from "react"
import { Link, Navigate } from "react-router-dom"
import { Game } from "../domain/Game"
import { GamesList } from "../domain/GamesList"
import { askService, Result } from "../service/askService"
import { ButtonFab } from "../utils/ButtonFab"
import { paths, service } from "./App"
import { useCurrentUser } from './Authn'

function Playing(game: Game) {
    let result: JSX.Element

    if (game.state != "shooting") {
        result = undefined
    } else if (game.playing) {
        result = <p>Playing</p>
    } else {
        result = <p>Not your turn</p>
    }

    return (
        <div key={game.id} className="game-box">
            <p>Game type: {game.type.name}</p>
            <p>Game state: {game.state}</p>
            {result}
            <button><Link to={paths['play-game'].replace(":gameId", game.id.toString())}>Enter Game</Link></button>
        </div>
    )
}

export function ListGames() {
    const currentUser = useCurrentUser()
    const limit = 5
    const [skip, setSkip] = useState(0)

    console.log(skip)

    const games: Result<GamesList> | undefined = askService(service, service.games, currentUser.token, limit, skip)

    console.log(games)

    if (!games) {
        return (
            <div>
                ...loading...
            </div>
        )
    }

    if (games.kind == "success") {
        return (
            <div>
                <h1>Games</h1>
                {games.result.games.map((game: Game) => Playing(game))}
                <div>
                    <ButtonFab isDisabled={skip == 0} onClick={() => {setSkip(skip - limit)}} text={"Previous"}/>
                    <ButtonFab isDisabled={!games.result.hasMore} onClick={() => {setSkip(skip + limit)}} text={"Next"}/>
                </div>
                <div>
                    <Link to={paths['create-game']}>New Game</Link>
                </div>
            </div>
        )
    }
}