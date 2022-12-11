import * as React from "react"
import { GameType } from "../domain/GameTypes"
import { askService } from "../service/askService"
import { paths, service } from "./App"


export function CreateGame() {
    const gameTypes = askService(service, service.gameTypes)

    if (!gameTypes) {
        return (
            <div>
                ...loading...
            </div>
        )
    }
    let shipKey: number = 0
    return (
        <div id="content-games-type">
            {gameTypes.gameTypes.map((types: GameType) => 
                    <div className="game-type" key={types.name}>
                        <h3>{types.name}</h3>
                        <hr />
                        <ul>
                            <li>Board size: {types.boardSize}</li>
                            <li>Shots per round: {types.shotsPerRound}</li>
                            <li>Layout definition time: {types.layoutDefTime}</li>
                            <li>Shooting time: {types.shootingTime}</li>
                            <h4>Ships in Game</h4>
                            {types.fleet.map(ship => 
                                <ul key={shipKey++}>
                                    <li> Name: {ship.name}</li>
                                    <li> Size: {ship.size}</li>
                                </ul>   
                            )}
                            
                        </ul>
                        <div className="center-align-content">
                            <a href={paths['define-layout'] + "?game-type=" + types.name}><button>Continue</button></a>
                        </div>
                    </div>
            )}
        </div>
    )
}