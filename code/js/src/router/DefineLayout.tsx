import * as React from "react"
import { Link, Navigate, useSearchParams } from "react-router-dom"
import { GameType } from "../domain/GameTypes";
import { Ship } from "../domain/ship";
import { Square } from "../domain/square";
import { askService } from "../service/askService"
import { paths, service } from "./App"

function GridSquare(key: string, hasShip: boolean) {
    if(hasShip)
        return <div key={key} className="grid-square ship" />
    else
        return <div key={key} className="grid-square" />
  }

  const grid = []

  function GridBoard(boardSize: number, ships: Array<Ship>) {
      for (let row = 0; row < boardSize; row ++) {
            grid.push([])
            for (let col = 0; col < boardSize; col++) {
                grid[row].push(GridSquare(`${col}${row}`, false))
            }
      }
  
    ships.forEach(ship => {
        //grid[ship.firstSquare.row.charCodeAt(0) - 97][ship.firstSquare.column].pop()
        for (let i = 0; i < ship.size; i++) {
            if(ship.orientation === "vertical")
                grid[ship.firstSquare.row.charCodeAt(0) - 97 + i][ship.firstSquare.column-1] = GridSquare(`${ship.firstSquare.row}${ship.firstSquare.column}`, true)
            else
                grid[ship.firstSquare.row.charCodeAt(0) - 97][ship.firstSquare.column-1 + i] = GridSquare(`${ship.firstSquare.row}${ship.firstSquare.column}`, true)
  
        }
    })

    // The components generated in makeGrid are rendered in div.grid-board
      
    if(boardSize == 10)
        return <div className='grid-board' id="beginner"> {grid}</div>
    if(boardSize == 12)
        return <div className='grid-board' id="experienced"> {grid}</div>
    if(boardSize == 15)
        return <div className='grid-board' id="expert"> {grid}</div>
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
    //console.log("GameType: " + gameType.boardSize)

    if (!gameTypes) {
        return (
            <div>
                ...loading...
            </div>
        )
    }

    const ship = {
        firstSquare: {row: 'b', column: 3},
        name: "carrier",
        size: 6,
        destroyed: false,
        orientation: "horizontal",
        userId: 1,
        gameId: 1,
        nOfHits: 0,
        squares: []
    } 

    const ship2 = {
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

    return (
        <div id="content">
            <h1>{gameTypeSearchParam}</h1>
            <div id="board-content">
                {GridBoard(gameType.boardSize, [ship, ship2])}
            </div>
        </div>
    )
}