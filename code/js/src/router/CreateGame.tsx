import * as React from "react"
import { useState } from "react"
import { Navigate } from "react-router-dom"
import { GameType, GameTypes } from "../domain/GameTypes"
import { askService, Result } from "../service/askService"
import { capitalize } from "../utils/capitalize"
import { paths, service } from "./App"
import { useCurrentUser } from './Authn'
import { Loading } from "./Loading"

export function CreateGame() {
    document.title = "Create Game"
    const gameTypes: Result<GameTypes> | undefined = askService(service, service.gameTypes)
    const currentUser = useCurrentUser()
    const [lobbyId, setLobbyId]: [number | undefined, React.Dispatch<any>] = useState(undefined)
    const [gameId, setGameId]: [number | undefined, React.Dispatch<any>] = useState(undefined)

    if (!gameTypes) {
        return <Loading />
    }

    async function onPlayClick(gameType: GameType) {
        const enterLobby = await service.enterLobby(currentUser.token, gameType.name)
        if (enterLobby.waitingForGame)
            setLobbyId(enterLobby.lobbyOrGameId)
        else
            setGameId(enterLobby.lobbyOrGameId)
    }

    if (gameTypes.kind == "success") {
        if (lobbyId)
            return <Navigate to={`${paths['wait-for-game'].replace(":lobbyId", lobbyId.toString())}`} />
        if (gameId)
            return <Navigate to={`${paths['game'].replace(":gameId", gameId.toString())}`} />
        else
            return (
                <div id="content-games-type">
                    {gameTypes.result.gameTypes.map((type: GameType) =>
                        <div className="game-type" key={type.name}>
                            <h3>{capitalize(type.name)}</h3>
                            <hr />
                            <ul>
                                <li>Board: {type.boardSize}x{type.boardSize}</li>
                                <li>Shots: {type.shotsPerRound}</li>
                                <li>Layout time: {type.layoutDefTime} seconds</li>
                                <li>Shooting time: {type.shootingTime} seconds</li>
                                <div id="fleet-section">
                                    <h4 id="fleet-show">Fleet size: {type.fleet.length}</h4>
                                    <div id="fleet">
                                        {type.fleet.map(ship =>
                                            <ul key={ship.gameType + "+" + ship.name}>
                                                <li><b>{capitalize(ship.name)}</b></li>
                                                <li>Size: {ship.size}</li>
                                            </ul>
                                        )}
                                    </div>
                                </div>
                            </ul>
                            <div className="center-align-content">
                                <button onClick={() => { onPlayClick(type) }}>Start</button>
                            </div>
                        </div>
                    )}
                </div>
            )
    }
}