import * as React from "react"
import { Dispatch } from "react"
import { CurrentUser } from "../domain/CurrentUser"
import { Game, isEnemySquareDestroyed, isEnemySquareAroundDestroyed, isEnemySquareHit } from "../domain/Game"
import { Ship } from "../domain/ship"
import { Square } from "../domain/Square"
import { BoardView } from "../utils/board"
import { ButtonFab } from "../utils/ButtonFab"
import { capitalize } from "../utils/capitalize"
import { contains } from "../utils/contains"
import { remove } from "../utils/remove"
import { service } from "./App"
import { SHIP_COLOR, INNER_COLOR, SMALL_BOARD_SQUARE_CONST, BIG_BOARD_SQUARE_CONST, DESTROYED_SHIP_COLOR, AROUND_DESTROYED_COLOR, SELECTED_COLOR } from "./PlayGame"

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

export function Shooting(
    game: Game,
    currentUser: CurrentUser, 
    timer: number, 
    selectedSquares: Array<Square>, 
    setSelectedSquares: Dispatch<React.SetStateAction<Square[]>>
) {
    return (
        <div id="layout-content">
            <div style={{float: "left"}}>
                <div id="layout-status">
                    <h1>Game type: {capitalize(game.type.name)}</h1>
                    <h2>{`${currentUser.name} vs. ${game.opponent.name}`}</h2>
                    <p>Phase: {game.playing ? "Shooting" : "Waiting"}</p>
                </div>
                <div id="timer">Time left: <p>{timer}</p></div>
                <div id="btn-container">
                    <ButtonFab isDisabled={!game.playing} onClick={() => {service.sendHits(currentUser.token, game.id, selectedSquares).then(() => setSelectedSquares([]))}} text={"Shoot"}/>
                    <ButtonFab isDisabled={!game.playing} onClick={() => {service.forfeit(currentUser.token, game.id)}} text={"Forfeit"}/>
                </div>
            </div>
            <div className="board-content" id="self-board-container" style={{marginTop: "100px"}}>
                <h1>Your Board</h1>
                {BoardView(game.type.boardSize, SMALL_BOARD_SQUARE_CONST / game.type.boardSize, (square: Square, squareSize: number, isLast: boolean) => {
                    const isOccupied = game.fleet.some((ship: Ship) => contains(ship.squares, square))
                    const isHit: boolean = contains(game.takenHits, square)
                    const style = isOccupied ? occupiedSquareStyle(squareSize) : defaultSquareStyle(squareSize)
                    const hit = isHit ? <p>X</p> : <p></p>
                    return <div key={JSON.stringify(square)} style={style}>{hit}</div>
                })}
            </div>
            <div className="board-content" id="enemy-board-container">
                <h1>Enemy Board</h1>
                {BoardView(game.type.boardSize, BIG_BOARD_SQUARE_CONST / game.type.boardSize, (square: Square, squareSize: number, isLast: boolean) => {
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
                    const hit = isEnemySquareHit(game, square) ? <p className="text">X</p> : <p className="text"></p>
                    const className = !contains(selectedSquares, square) && canClick ? "canSelect" : ""
                    return <div key={JSON.stringify(square)} className={className} onClick={onClick} style={style}>{hit}</div>
                })}
            </div>
        </div>
    )
}
