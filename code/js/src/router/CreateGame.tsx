import * as React from "react"
import { GameType } from "../domain/GameTypes"
import { askService } from "../service/askService"
import { service } from "./App"
import { useCurrentUser } from './Authn'


export function CreateGame() {
    const currentUser = useCurrentUser()
    const gameTypes = askService(service, service.gameTypes)

    if (!gameTypes) {
        return (
            <div>
                ...loading...
            </div>
        )
    }
    
    return (
        <div id="content-games-type">
            {gameTypes.gameTypes.map((type: GameType) => 
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
                            <button onClick={() => handleContinueClick(currentUser.token, type.name)}>Continue</button> 
                        </div>
                    </div>
            )}
        </div>
    )
}

function handleContinueClick(token: string, gameType: string) {
    const lobby = service.enterLobby(token, gameType)
        .then( (lobby) => {
            console.log(lobby.waitingForGame)
            console.log(lobby.lobbyOrGameId)
            // navegar para a página do enteredGame
        })
}