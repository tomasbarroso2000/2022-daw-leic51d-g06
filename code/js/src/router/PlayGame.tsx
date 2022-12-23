import * as React from "react";
import { Link, Navigate, redirect, useSearchParams } from "react-router-dom";
import { showGameBoard } from "../board";
import { Game } from "../domain/GamesList";
import { Ship } from "../domain/ship";
import { Square } from "../domain/Square";
import { askService } from "../service/askService";
import { paths, service } from "./App";
import { useCurrentUser } from "./Authn";

function checkGameState(gameState: string, gameType: string): JSX.Element {
    if (gameState === "layout_definition") 
        return <Navigate to={paths['define-layout'] + "?game-type=" + gameType} replace={true}/>
} 

function isShootingBoard(boardSize: number, fleet: Array<Ship>, hits: Array<Square>, gameState: string): JSX.Element {
    if(gameState == "shooting")
        return showGameBoard(boardSize, fleet, hits, true)
    else
        return showGameBoard(boardSize, fleet, hits, false)
}

export function PlayGame() {
    const [searchParams, setSearchParams] = useSearchParams();
    const gameTypeSearchParam = searchParams.get("game")

    if(!gameTypeSearchParam) {
        window.location.replace(`${paths['list-games']}`);
    }

    const currentUser = useCurrentUser()

    const gameInfo: Game | undefined = askService(service, service.gameInfo, currentUser.token, gameTypeSearchParam)

    if(!gameInfo) {
        return (
            <div>
                ...loading...
            </div>
        )
    }


    console.log("gameInfo" + JSON.stringify(gameInfo))
    console.log("play game fleet" + JSON.stringify(gameInfo.fleet))
    return (
        <div>
            <div>
                {/*checkGameState(gameInfo.state, gameInfo.type.name)*/}
                <div>
                    <h1>Game type: {gameInfo.type.name}</h1>
                    <p>Shots per round: {gameInfo.type.shotsPerRound}</p>
                    <p>Shooting time: {gameInfo.type.shootingTime}</p>
                    <h2>{currentUser.name + " VS " + gameInfo.opponent.name}</h2>
                </div>
                <div className="board-content" id="self-board-container">
                    <h1>Your Board</h1>
                    {showGameBoard(gameInfo.type.boardSize, gameInfo.fleet, gameInfo.takenHits, false)}
                </div>
                <div className="board-content" id="enemy-board-container">
                    <h1>Enemy Board</h1>
                    {isShootingBoard(gameInfo.type.boardSize, gameInfo.enemySunkFleet, gameInfo.hits, "shooting")}
                </div>
            </div>
        </div>
    )
}