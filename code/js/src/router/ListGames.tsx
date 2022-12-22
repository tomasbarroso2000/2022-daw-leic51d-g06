import * as React from "react"
import { Link } from "react-router-dom"
import { Game, GamesList } from "../domain/GamesList"
import { askService } from "../service/askService"
import { paths, service } from "./App"
import { useCurrentUser } from './Authn'

function Playing(game: Game) {
    let result: JSX.Element
    if(game.state != "shooting") result = undefined
    else if (game.playing) result = <p>Playing</p>
    else result = <p>Not your turn</p>
    {console.log(`path: ${paths['play-game'] + `?game=${game.id}`}`)}
    return (
        <div key={game.id} className="game-box">
            <p>Game type: {game.type.name}</p>
            <p>Game state: {game.state}</p>
            {result}
            <Link to={paths['play-game'] + `?game=${game.id}`}> <button>Enter Game</button>  </Link>
        </div>
    )
}

export function ListGames() {
    const currentUser = useCurrentUser()
    const limit = 5
    const skip = 0
    console.log("token: " + currentUser.token)
    const games: GamesList | undefined = askService(service, service.games, currentUser.token, limit, skip)

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
                    games.games.map((game: Game) => {
                        return (
                            Playing(game)
                        )
                    })
            }
            <div>
                <Link to={paths['create-game']}>New Game</Link>
            </div>
        </div>
    )
}