import * as React from "react"
import { Link, Navigate, useSearchParams } from "react-router-dom"
import { GameType } from "../domain/GameTypes";
import { askService } from "../service/askService"
import { paths, service } from "./App"

function GridSquare() {
     return <div className="grid-square" />
  }

  function GridBoard(boardSize: number) {

    // generates an array of 18 rows, each containing 10 GridSquares.
  
      const grid = []
      for (let row = 0; row < boardSize; row ++) {
          grid.push([])
          for (let col = 0; col < boardSize; col ++) {
              grid[row].push(
              <GridSquare key={`${col}${row}`} />)
          }
      }
  
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

    return (
        <div id="content">
            <h1>{gameTypeSearchParam}</h1>
            <div id="board-content">
                {GridBoard(gameType.boardSize)}
            </div>
        </div>
    )
}