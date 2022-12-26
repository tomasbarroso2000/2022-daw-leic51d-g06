import * as React from "react"
import { Dispatch } from "react"
import { Link } from "react-router-dom"
import { CurrentUser } from "../domain/CurrentUser"
import { Game, isEnemySquareDestroyed, isEnemySquareAroundDestroyed, isEnemySquareHit } from "../domain/Game"
import { Ship } from "../domain/ship"
import { Square } from "../domain/Square"
import { BoardView } from "../utils/board"
import { contains } from "../utils/contains"
import { remove } from "../utils/remove"
import { paths, service } from "./App"
import { useCurrentUser } from "./Authn"
import { SHIP_COLOR, INNER_COLOR, SMALL_BOARD_SQUARE_SIZE, BIG_BOARD_SQUARE_SIZE, DESTROYED_SHIP_COLOR, AROUND_DESTROYED_COLOR, SELECTED_COLOR } from "./PlayGame"

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

export function Shooting(game: Game, currentUser: CurrentUser, selectedSquares: Array<Square>, setSelectedSquares: Dispatch<React.SetStateAction<Square[]>>) {
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
                            if (contains(selectedSquares, square)) {
                                setSelectedSquares(remove(selectedSquares, square))
                            } else if (selectedSquares.length < game.type.shotsPerRound) {
                                setSelectedSquares(selectedSquares.concat(square))
                            } 
                        }
                    }
                    const style = enemySquareStyle(squareColor, squareSize)
                    const hit = isEnemySquareHit(game, square) ? "X" : ""
                    const className = !contains(selectedSquares, square) && canClick ? "canSelect" : ""
                    return <div key={JSON.stringify(square)} className={className} onClick={onClick} style={style}>{hit}</div>
                })}
            </div>
            <div>
                
                <button onClick={() => service.sendHits(currentUser.token, game.id, selectedSquares)}>Shoot</button>
                <button>Forfeit</button>
            </div>
        </div>
    )
}
