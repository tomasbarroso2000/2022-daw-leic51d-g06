import * as React from "react"
import { useSearchParams } from "react-router-dom"
import { showGameBoard } from "../board";
import { GameType } from "../domain/GameTypes";
import { askService } from "../service/askService"
import { paths, service } from "./App"

export function DefineLayout() {
    const [searchParams, setSearchParams] = useSearchParams();
    const gameTypeSearchParam = searchParams.get("game-type")

    if(!gameTypeSearchParam) {
        window.location.replace(`${paths['create-game']}`);
    }

    const gameTypes = askService(service, service.gameTypes)

    if (!gameTypes) {
        return (
            <div>
                ...loading...
            </div>
        )
    }

    const gameType = gameTypes.gameTypes.find((game: GameType) => game.name === gameTypeSearchParam)

    if (!gameTypes) {
        return (
            <div>
                ...loading...
            </div>
        )
    }

    const ships = [
        {
            firstSquare: {row: 'b', column: 3},
            name: "carrier",
            size: 6,
            destroyed: false,
            orientation: "horizontal",
            userId: 1,
            gameId: 1,
            nOfHits: 0,
            squares: []
        },
        {
            firstSquare: {row: 'e', column: 4},
            name: "carrier",
            size: 3,
            destroyed: false,
            orientation: "vertical",
            userId: 1,
            gameId: 1,
            nOfHits: 0,
            squares: []
        }
    ]

    const hits = [
        {
            row: 'b',
            column: 3
        },
        {
            row: 'd',
            column: 8
        }
    ]

    return (
        <div id="content">
            <h1>{gameTypeSearchParam}</h1>
            <div id="board-content">
                {showGameBoard(gameType.boardSize, ships, hits)}
            </div>
        </div>
    )
}