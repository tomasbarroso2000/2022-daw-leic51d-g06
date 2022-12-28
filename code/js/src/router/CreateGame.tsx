import * as React from "react"
import { useState } from "react"
import { Link, Navigate } from "react-router-dom"
import { GameType, GameTypes } from "../domain/GameTypes"
import { askService, Result } from "../service/askService"
import { paths, service } from "./App"
import { useCurrentUser } from './Authn'
import { Loading } from "./Loading"

export function CreateGame() {
    const gameTypes: Result<GameTypes> | undefined = askService(service, service.gameTypes)
    const currentUser = useCurrentUser()
    const [lobbyId, setLobbyId] : [number | undefined, React.Dispatch<any>] = useState(undefined)
    const [gameId, setGameId] : [number | undefined, React.Dispatch<any>] = useState(undefined)

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
            return <Navigate to={`${paths['wait-for-game'].replace(":lobbyId", lobbyId.toString())}`}/>
        if (gameId)
            return <Navigate to={`${paths['play-game'].replace(":gameId", gameId.toString())}`}/>
        else
            return (
                <div id="content-games-type">
                    {gameTypes.result.gameTypes.map((type: GameType) => 
                            <div className="game-type" key={type.name}>
                                <h3>{type.name}</h3>
                                <hr />
                                <ul>
                                    <li>Board size: {type.boardSize}</li>
                                    <li>Shots per round: {type.shotsPerRound}</li>
                                    <li>Layout definition time: {type.layoutDefTime}</li>
                                    <li>Shooting time: {type.shootingTime}</li>
                                    <h4>Ships in Game</h4>
                                    {type.fleet.map(ship => 
                                        <ul key={ship.gameType + "+" + ship.name}>
                                            <li> Name: {ship.name}</li>
                                            <li> Size: {ship.size}</li>
                                        </ul>   
                                    )}
                                    
                                </ul>
                                <div className="center-align-content">
                                    <button onClick={() => {onPlayClick(type)}}>play</button>
                                </div>
                            </div>
                    )}
                </div>
            )
    }
}