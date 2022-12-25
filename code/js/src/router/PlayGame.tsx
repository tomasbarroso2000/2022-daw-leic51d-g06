import * as React from "react";
import { useParams } from "react-router-dom";
import { BoardView } from "../utils/board";
import { Game, isEnemySquareAroundDestroyed, isEnemySquareDestroyed, isEnemySquareHit } from "../domain/Game";
import { LayoutShip, Ship } from "../domain/ship";
import { askService, Result } from "../service/askService";
import { service } from "./App";
import { useCurrentUser } from "./Authn";
import { CurrentUser } from "../domain/CurrentUser";
import { Square } from "../domain/Square";
import { contains } from "../utils/contains";
import { Dispatch, useCallback, useState } from "react";
import { remove } from "../utils/remove";
import { Layout } from "./Layout";

export function PlayGame() {
    const currentUser = useCurrentUser()
    const params = useParams()

    const [layoutShips, setLayoutShips]: [Array<LayoutShip>, Dispatch<React.SetStateAction<LayoutShip[]>>] = useState([])

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
                return Layout(gameInfo.result, layoutShips, setLayoutShips)
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

export const INNER_COLOR = "#008DD5"
export const DESTROYED_SHIP_COLOR = "#000000"
export const AROUND_DESTROYED_COLOR = "#FF0B5394"
export const SHIP_COLOR = "#AED4E6"
export const SELECTED_COLOR = "#FF0000"

export const SMALL_BOARD_SQUARE_SIZE = 25
export const BIG_BOARD_SQUARE_SIZE = 40

function occupiedSquareStyle(squareSize: number): React.CSSProperties {
    return {
        width: `${squareSize}px`,
        height: `${squareSize}px`,
        backgroundColor: SHIP_COLOR
    }
}

 function defaultSquareStyle(squareSize: number): React.CSSProperties {
    return {
        width: `${squareSize}px`, 
        height: `${squareSize}px`, 
        backgroundColor: INNER_COLOR
    }
}

function enemySquareStyle(color: string, squareSize: number): React.CSSProperties {
    return {
        width: `${squareSize}px`, 
        height: `${squareSize}px`, 
        backgroundColor: color
    }
}

function Shooting(game: Game, currentUser: CurrentUser, selectedSquares: Array<Square>, setSelectedSquares: Dispatch<React.SetStateAction<Square[]>>) {
    return (
        <div>
            <div>
                <h1>Game type: {game.type.name}</h1>
                <p>Shots per round: {game.type.shotsPerRound}</p>
                <p>Shooting time: {game.type.shootingTime}</p>
                <h2>{currentUser.name + " VS " + game.opponent.name}</h2>
            </div>
            <div className="board-content" id="self-board-container">
                <h1>Your Board</h1>
                {BoardView(game.type.boardSize, SMALL_BOARD_SQUARE_SIZE, (square: Square, squareSize: number, isLast: boolean) => {
                    const isOccupied = game.fleet.some((ship: Ship) => contains(ship.squares, square))
                    const isHit: boolean = contains(game.takenHits, square)
                    const style = isOccupied ? occupiedSquareStyle(squareSize) : defaultSquareStyle(squareSize)
                    const hit = isHit ? "X" : ""
                    return <div key={JSON.stringify(square)} style={style}>{hit}</div>
                })}
            </div>
            <div className="board-content" id="enemy-board-container">
                <h1>Enemy Board</h1>
                {BoardView(game.type.boardSize, BIG_BOARD_SQUARE_SIZE, (square: Square, squareSize: number, isLast: boolean) => {
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

                    const onClick = () => {
                        if (canClick) {
                            if (contains(selectedSquares, square))
                                setSelectedSquares(remove(selectedSquares, square))
                            else
                                setSelectedSquares(selectedSquares.concat(square))
                        }
                    }
                    const style = enemySquareStyle(squareColor, squareSize)
                    const hit = isEnemySquareHit(game, square) ? "X" : ""
                    const className = !contains(selectedSquares, square) && canClick ? "canSelect" : ""
                    return <div key={JSON.stringify(square)} className={className} onClick={onClick} style={style}>{hit}</div>
                })}
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


