import * as React from "react"
import { useState } from "react"
import { Link } from "react-router-dom"
import { Game } from "../domain/Game"
import { GamesList } from "../domain/GamesList"
import { askService, Result } from "../service/askService"
import { ButtonFab } from "../utils/ButtonFab"
import { capitalize } from "../utils/capitalize"
import { paths, service } from "./App"
import { useCurrentUser } from './Authn'
import { Loading } from "./Loading"

function getState(game: Game) {
    switch (game.state) {
        case "layout_definition": return "Layout"
        case "shooting": return "Shooting"
        case "completed": return "Completed"
    }
}

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
            <h3>Game type: {capitalize(game.type.name)}</h3>
            <p>Game state: {getState(game)}</p>
            {result}
            <button><Link to={paths['game'].replace(":gameId", game.id.toString())}>Enter Game</Link></button>
        </div>
    )
}

export function ListGames() {
    document.title = "Games"
    const currentUser = useCurrentUser()
    const limit = 5
    const [skip, setSkip] = useState(0)

    const games: Result<GamesList> | undefined = askService(service, service.games, currentUser.token, limit, skip)

    if (!games) {
        return <Loading />
    }

    if (games.kind == "success") {
        return (
            <div id="content">
                <h1>Games</h1>
                <div id="games-list-content">
                    <div id="nav">
                        <div className="arrow-left"></div>
                        <ButtonFab isDisabled={skip == 0} onClick={() => { setSkip(skip - limit) }} text={"Previous"} />
                        <span> | </span>
                        <ButtonFab isDisabled={!games.result.hasMore} onClick={() => { setSkip(skip + limit) }} text={"Next"} />
                        <div className="arrow-right"></div>
                    </div>
                    {games.result.games.map((game: Game) => Playing(game))}
                    <div>
                        <Link to={paths['game-types']}><button id="game-new-btn">New Game</button></Link>
                    </div>
                </div>
            </div>
        )
    }
}