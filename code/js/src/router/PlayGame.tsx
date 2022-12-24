import * as React from "react";
import { Link, Navigate, redirect, useParams, useSearchParams } from "react-router-dom";
import { BoardView, showGameBoard } from "../utils/board";
import { Game } from "../domain/GamesList";
import { Ship } from "../domain/ship";
import { askService, Result } from "../service/askService";
import { paths, service } from "./App";
import { useCurrentUser } from "./Authn";
import { CurrentUser } from "../domain/CurrentUser";
import { Square } from "../domain/Square";

function isShootingBoard(boardSize: number, fleet: Array<Ship>, hits: Array<Square>, gameState: string): JSX.Element {
    if(gameState == "shooting")
        return showGameBoard(boardSize, fleet, hits, true)
    else
        return showGameBoard(boardSize, fleet, hits, false)
}

export function PlayGame() {
    const currentUser = useCurrentUser()
    const params = useParams()

    const gameInfo: Result<Game> | undefined = askService(service, service.gameInfo, currentUser.token, params["gameId"])

    if(!gameInfo) {
        return (
            <div>
                ...loading...
            </div>
        )
    }

    if (gameInfo.kind == "success") {
        switch (gameInfo.result.state) {
            case "layout_definition": {
                console.log("layout_definition")
                return Layout(gameInfo.result)
            }
            case "shooting": {
                console.log("shooting")
                return Shooting(gameInfo.result, currentUser)
            }
            case "completed": {
                console.log("completed")
                return Completed(gameInfo.result)
            }
        }
    }
}

function Layout(game: Game) {  
    return (
        <div id="content">
            <h1>Layout</h1>
            <div id="board-content">
                {showGameBoard(game.type.boardSize, game.fleet, game.takenHits, false)}
            </div>
        </div>
    )
}

function Shooting(game: Game, currentUser: CurrentUser) {
    console.log("ships: " + JSON.stringify(game.fleet))
    return (
        <div>
            <div>
                <div>
                    <h1>Game type: {game.type.name}</h1>
                    <p>Shots per round: {game.type.shotsPerRound}</p>
                    <p>Shooting time: {game.type.shootingTime}</p>
                    <h2>{currentUser.name + " VS " + game.opponent.name}</h2>
                </div>
                <div className="board-content" id="self-board-container">
                    <h1>Your Board</h1>
                    {/*showGameBoard(game.type.boardSize, game.fleet, game.takenHits, false)*/}
                    {BoardView(game.type.boardSize, (square: Square, bool: boolean) => {
                        const isOccupied: boolean = game.fleet.some((ship: Ship) => {ship.squares.includes(square)})
                        console.log("isOccupied: " + isOccupied)
                        const isHit: boolean = game.takenHits.includes(square)
                        if(isOccupied)
                            return <div key={JSON.stringify(square)} style={{width: "25px", height: "25px", backgroundColor: "#aed4e6"}}></div>
                        if(isHit)
                            return <div key={JSON.stringify(square)} style={{width: "25px", height: "25px", backgroundColor: "#rgb(219, 81, 81)"}}></div>
                        
                        return <div key={JSON.stringify(square)} style={{width: "25px", height: "25px", backgroundColor: "#008DD5"}}></div>
                    })}
                </div>
                <div className="board-content" id="enemy-board-container">
                    <h1>Enemy Board</h1>
                    {isShootingBoard(game.type.boardSize, game.enemySunkFleet, game.hits, "shooting")}
                </div>
            </div>
        </div>
    )
}

function Completed(game: Game) {
    const yourFleet = game.fleet.map((ship) => 
        <li>{ship.name}</li>
    ) 
    const enemyFleet = game.enemySunkFleet.map((ship) => 
        <li>{ship.name}</li> //falta colocar o resto dos barcos
    ) 
    const gameStats = 
        <div>
            <h3>Game Statistics:</h3>
            <p>Shots: {game.hits.length + game.misses.length}</p>
            <p>Hits: {game.hits.length}</p>
            <p>Misses: {game.misses.length}</p>
            <h3>Your fleet:</h3>
            <ul>{yourFleet}</ul>
            <h3>{game.opponent.name}'s fleet</h3>
            <ul>{enemyFleet}</ul>
        </div>
    if (game.playing)    
        return (
            <div>
                <div>
                    YOU WIN!
                    {gameStats}
                </div>
            </div>

        )
    else 
    return (
        <div>
            <div>
                YOU LOST!
                {gameStats}
            </div>
        </div>

    )
    }