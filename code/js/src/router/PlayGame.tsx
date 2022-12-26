import * as React from "react";
import { Navigate, useParams } from "react-router-dom";
import { BoardView } from "../utils/board";
import { Game, isEnemySquareAroundDestroyed, isEnemySquareDestroyed, isEnemySquareHit } from "../domain/Game";
import { Ship } from "../domain/ship";
import { askService, Result } from "../service/askService";
import { paths, service } from "./App";
import { useCurrentUser } from "./Authn";
import { CurrentUser } from "../domain/CurrentUser";
import { Square } from "../domain/Square";
import { contains } from "../utils/contains";
import { Dispatch, useCallback, useState } from "react";
import { remove } from "../utils/remove";
import { Layout } from "./Layout";
import { LayoutShip } from "../domain/LayoutShip";
import { useIntervalAsync } from "../utils/useIntervalAsync";
import { Shooting } from "./Shooting";

export function PlayGame() {
    const currentUser = useCurrentUser()
    console.log("current user: " + currentUser)
    const params = useParams()

    const gameId = parseInt(params["gameId"])

    const [layoutShips, setLayoutShips]: [Array<LayoutShip>, Dispatch<React.SetStateAction<LayoutShip[]>>] = useState([])

    const [selectedSquares, setSelectedSquares]: [Array<Square>, Dispatch<React.SetStateAction<Square[]>>] = useState([])

    const [gameInfo, setGameInfo]: [Game | undefined, Dispatch<Game>] = useState(undefined)

    //const gameInfo: Result<Game> | undefined = askService(service, service.gameInfo, currentUser.token, gameId)

    const updateGameInfo = useCallback(async () => {
        console.log("updating")
        const newGameInfo = await service.gameInfo(currentUser.token, gameId)
        console.log("newGameInfo: " + newGameInfo)
        console.log(newGameInfo)
        setGameInfo(newGameInfo)
    }, [])

    useIntervalAsync(updateGameInfo, 3000)

    if(!gameInfo) {
        console.log("!gameInfo")
        return (
            <div>
                ...loading...
            </div>
        )
    }
    switch (gameInfo.state) {
        case "layout_definition": {
            console.log("layout_definition")
            return Layout(gameInfo, currentUser, layoutShips, setLayoutShips)
        }
        case "shooting": {
            console.log("shooting")
            return Shooting(gameInfo, currentUser, selectedSquares, setSelectedSquares)
        }
        case "completed": {
            console.log("completed")
            return Completed(gameInfo)
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


