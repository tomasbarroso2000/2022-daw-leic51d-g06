import * as React from "react"
import { Link, Navigate, useSearchParams } from "react-router-dom"
import { GameType } from "../domain/GameTypes";
import { askService } from "../service/askService"
import { paths, service } from "./App"

let board = []

const Square = () => {
    return (
        <tr className="square">
            
        </tr>
    )
}

function DrawBoard(boardSize: number) {

    let board = [];
    for (var i = 0; i < boardSize; i++) {
        board[i] = [];
        for (var j = 0; j < boardSize; j++)
            board[i][j] = 0;
    }

    var cells = [];
    for (var i = 0; i < boardSize; i++)
      for (var j = 0; j < boardSize; j++)
        cells.push({row: i, col: j});
 
    let count = 0
    return (
        <div>
            {   
                <div>
                    {
                        cells.map(cell => {
                            if (count <= 9) {
                                count++
                                return <span> {"[r: " + cell.row + " c: " + cell.col + "]"} </span>
                            } else {
                                count = 0
                                return <div> {"[r: " + cell.row + " c: " + cell.col + "]"} </div> 
                            }
                        })
                    }
                </div>
                
            }
        </div>
    )
}

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
    console.log("GameType: " + gameType.boardSize)

    if (!gameTypes) {
        return (
            <div>
                ...loading...
            </div>
        )
    }

    return (
        <div>
            <h1>{gameTypeSearchParam}</h1>
            { DrawBoard(gameType.boardSize) }   
        </div>
    )
}