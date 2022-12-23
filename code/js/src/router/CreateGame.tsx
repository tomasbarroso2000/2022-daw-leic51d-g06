import * as React from "react"
import { Link } from "react-router-dom"
import { GameType, GameTypes } from "../domain/GameTypes"
import { askService, Result } from "../service/askService"
import { service } from "./App"
import { useCurrentUser } from './Authn'

export function CreateGame() {
    const gameTypes: Result<GameTypes> | undefined = askService(service, service.gameTypes)

    if (!gameTypes) {
        return (
            <div>
                ...loading...
            </div>
        )
    }

    if (gameTypes.kind == "success") {
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
                                <button><Link to={`/games/new/${type.name}`}>play</Link></button>
                            </div>
                        </div>
                )}
            </div>
        )
    }
}