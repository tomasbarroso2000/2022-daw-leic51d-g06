import * as React from "react"
import { useState } from "react"
import { Ship } from "./domain/ship"
import { Square } from "./domain/Square"

function gridSquare(key: string, hasShip: boolean) {
    if(hasShip)
        return <div key={key} className="grid-square ship" />
    else
        return <div key={key} className="grid-square" />
}

function gridCross(key: string) {
    return <div key={key} className="cross" />
}

  const gameBoard = []

export function showGameBoard(boardSize: number, ships: Array<Ship>, hits: Array<Square>) {
    for (let row = 0; row < boardSize; row ++) {
        gameBoard.push([])
        for (let col = 0; col < boardSize; col++) {
            gameBoard[row].push(gridSquare(`${col}${row}`, false))
        }
    }
  
    ships.forEach(ship => {
        for (let i = 0; i < ship.size; i++) {
            if(ship.orientation === "vertical")
                gameBoard[ship.firstSquare.row.charCodeAt(0) - 97 + i][ship.firstSquare.column-1] =
                gridSquare(`${ship.firstSquare.row}${ship.firstSquare.column}${i}`, true)
            else
                gameBoard[ship.firstSquare.row.charCodeAt(0) - 97][ship.firstSquare.column-1 + i] = 
                gridSquare(`${ship.firstSquare.row}${ship.firstSquare.column}${i}`, true)
  
        }
    })

    hits.forEach(hit => {
        gameBoard[hit.row.charCodeAt(0) - 97][hit.column] = gridCross(`${hit.row}${hit.column}`)
    })
      
    if(boardSize == 10)
        return <div className='grid-board' id="beginner"> {gameBoard}</div>
    if(boardSize == 12)
        return <div className='grid-board' id="experienced"> {gameBoard}</div>
    if(boardSize == 15)
        return <div className='grid-board' id="expert"> {gameBoard}</div>
}

export function showDefaultShips(fleet: Array<Ship>) {
    const arrFleet = []
    
    fleet.forEach(ship => {
        arrFleet.push(
            (
                <table>
                    {
                        <tr>
                            
                        </tr>
                    }
                </table>
            )
        )
    })

    return (
        <div className='grid-board'> 
            {fleet.map(ship => 
                <p></p>
            )}
        </div>
    )
}