import * as React from "react";
import { Link, Navigate, redirect, useParams, useSearchParams } from "react-router-dom";
import { BoardView, showGameBoard } from "../utils/board";
import { Game, isEnemySquareAroundDestroyed, isEnemySquareDestroyed } from "../domain/Game";
import { Ship } from "../domain/ship";
import { askService, Result } from "../service/askService";
import { paths, service } from "./App";
import { useCurrentUser } from "./Authn";
import { CurrentUser } from "../domain/CurrentUser";
import { Square } from "../domain/Square";
import { deepEqual } from "../utils/deepEqual";
import { contains } from "../utils/contains";
import { Dispatch, useState } from "react";

function isShootingBoard(boardSize: number, fleet: Array<Ship>, hits: Array<Square>, gameState: string): JSX.Element {
    if(gameState == "shooting")
        return showGameBoard(boardSize, fleet, hits, true)
    else
        return showGameBoard(boardSize, fleet, hits, false)
}

export function PlayGame() {
    const currentUser = useCurrentUser()
    const params = useParams()

    const [selectedSquares, setSelectedSquares]: [Array<Square>, Dispatch<React.SetStateAction<Square[]>>] = useState([])

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
                return Shooting(gameInfo.result, currentUser, selectedSquares, setSelectedSquares)
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

const INNER_COLOR = "#008DD5"
const DESTROYED_SHIP_COLOR = "#000000"
const AROUND_DESTROYED_COLOR = "FF0B5394"
const SHIP_COLOR = "AED4E6"
const SELECTED_COLOR = "FF0000"

const occupiedSquareStyle: React.CSSProperties = {
    width: "25px",
    height: "25px",
    backgroundColor: SHIP_COLOR
}

const hitSquareStyle: React.CSSProperties = {
    width: "25px", 
    height: "25px", 
    backgroundColor: "#rgb(219, 81, 81)"
}

const defaultSquareStyle: React.CSSProperties = {
    width: "25px", 
    height: "25px", 
    backgroundColor: INNER_COLOR
}

function enemySquareStyle(color: string): React.CSSProperties {
    return {
        width: "25px", 
        height: "25px", 
        backgroundColor: color
    }
}

function Shooting(game: Game, currentUser: CurrentUser, selectedSquares: Array<Square>, setSelectedSquares: Dispatch<React.SetStateAction<Square[]>>) {
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
                    {BoardView(game.type.boardSize, (square: Square, isLast: boolean) => {
                        const isOccupied = game.fleet.some((ship: Ship) => ship.squares.some((shipSquare) => deepEqual(shipSquare, square)))
                        const isHit: boolean = game.takenHits.includes(square)
                        if(isOccupied)
                            return <div key={JSON.stringify(square)} style={occupiedSquareStyle}></div>
                        if(isHit)
                            return <div key={JSON.stringify(square)} style={hitSquareStyle}></div>
                        return <div key={JSON.stringify(square)} style={defaultSquareStyle}></div>
                    })}
                </div>
                <div className="board-content" id="enemy-board-container">
                    <h1>Enemy Board</h1>
                    {BoardView(game.type.boardSize, (square: Square, isLast: boolean) => {
                        let canClick = true
                        let squareColor: string
                        if (isEnemySquareDestroyed(game, square)) {
                            squareColor = DESTROYED_SHIP_COLOR
                            canClick = false
                        } else if (isEnemySquareAroundDestroyed(game, square)) {
                            squareColor = AROUND_DESTROYED_COLOR
                            canClick = false
                        } else if (contains(game.hits, square)) {
                            squareColor = SHIP_COLOR,
                            canClick = false
                        } else if (contains(selectedSquares, square)) {
                            squareColor = SELECTED_COLOR
                        } else {
                            squareColor = INNER_COLOR
                        }
                        const onClick: () => void = canClick ? () => setSelectedSquares(selectedSquares.concat(square)) : () => { }
                        const style = enemySquareStyle(squareColor)
                        return <div key={JSON.stringify(square)} onClick={onClick} style={style}></div>
                    })}
                    { /*isShootingBoard(game.type.boardSize, game.enemySunkFleet, game.hits, "shooting")*/ }
                </div>
            </div>
        </div>
    )
}

function Completed(game: Game) {
    return (
        <div>
            YOU LOSE BECAUSE YOU ALWAYS LOSE, YOU LOSER
        </div>
    )
}