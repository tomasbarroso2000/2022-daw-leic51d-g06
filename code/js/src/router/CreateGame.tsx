import * as React from "react"
import { GameType } from "../domain/GameTypes"
import { askService } from "../service/askService"
import { service } from "./App"


export function CreateGame() {
    const gameTypes = askService(service, service.gameTypes)

    if (!gameTypes) {
        return (
            <div>
                ...loading...
            </div>
        )
    }

    return (
        <div>
            {gameTypes.gameTypes.map((types: GameType) => 
                    <div key={types.name}>
                        <h3>{types.name}</h3>
                        <ul>
                            <li>Board size: {types.boardSize}</li>
                            <li>Shots per round: {types.shotsPerRound}</li>
                            <li>Layout definition time: {types.layoutDefTime}</li>
                            <li>Shooting time: {types.shootingTime}</li>
                            <h3>Ships in Game</h3>
                            {types.fleet.map(ship => 
                                <ul key={ship.name}>
                                    <li> Name: {ship.name}</li>
                                    <li> Size: {ship.size}</li>
                                </ul>   
                            )}
                            
                        </ul>
                        <button>Continue</button>
                    </div>
            )}
        </div>
    )
}