import * as React from "react"
import { useState } from "react"
import { Ship } from "./domain/ship"
import { ShipType } from "./domain/ShipType"
import { Square } from "./domain/Square"

function gridSquare(key: string, hasShip: boolean) {
    if(hasShip)
        return <div key={key} className="grid-square ship" />
    else
        return <div key={key} className="grid-square" />
}

function gridSquareShipHit(key: string) {
    return <div key={key} className="grid-square cross-ship-hit" /> 
}

function gridCross(key: string) {
    return <div key={key} className="cross" />
}

export function showGameBoard(boardSize: number, ships: Array<Ship>, hits: Array<Square>) {
    const gameBoard = Array()
    for (let row = 0; row < boardSize; row ++) {
        gameBoard.push([])
        for (let col = 0; col < boardSize; col++) {
            gameBoard[row].push(gridSquare(`${col}${row}`, false))
        }
    }
  
    ships.forEach(ship => {
        for (let i = 0; i < ship.size; i++) {
            if(ship.orientation === "vertical") {
                    gameBoard[ship.firstSquare.row.charCodeAt(0) - 97 + i][ship.firstSquare.column-1] =
                    gridSquare(`${ship.firstSquare.row}${ship.firstSquare.column}${i}`, true)
            } else {
                    gameBoard[ship.firstSquare.row.charCodeAt(0) - 97][ship.firstSquare.column-1 + i] = 
                    gridSquare(`${ship.firstSquare.row}${ship.firstSquare.column}${i}`, true)
            }
        }
    })

    //tentativa em encontrar os hits que coincidem com barcos para mudar côr
            /*hits.forEach(hit => {
                if(isInRange(ships, hit)) {
                    console.log("Hit boat at" + JSON.stringify(hit))
                } else {
                    console.log("no hit at " + JSON.stringify(hit))
                    gameBoard[hit.row.charCodeAt(0) - 97][hit.column] = gridCross(`${hit.row}${hit.column}`)
                }
            })*/
    const entry: Array<Square> = []
    ships.forEach(ship => {
        for (let i = 0; i < ship.size; i++) {
            if(ship.orientation === "vertical") {
                entry.push({row: String.fromCharCode(97 + ship.firstSquare.row.charCodeAt(0) - 97 + i), column: ship.firstSquare.column - 1})
            } else {
                entry.push({row: ship.firstSquare.row,  column: ship.firstSquare.column - 1 + i})
            }
        }
    })

    console.log(`Squares with ships: ${JSON.stringify(entry)}`)
    console.log(`hits: ${hits.filter(hit => entry.includes(hit))}`)
    
    if(boardSize == 10)
        return <div className='grid-board' id="beginner"> {gameBoard}</div>
    if(boardSize == 12)
        return <div className='grid-board' id="experienced"> {gameBoard}</div>
    if(boardSize == 15)
        return <div className='grid-board' id="expert"> {gameBoard}</div>
}

const wrapperStyle: React.CSSProperties = {
    display: 'grid',
    gridTemplateColumns: "repeat(3, 1fr)",
    gap: "10px",
    gridAutoRows: "minmax(100px, auto)",
}

// The function to compute the CSS style for a source `div`
function sourceDivStyle(column: number, row: number): React.CSSProperties {
    return {
        gridColumn: column,
        gridRow: row,
        border: "solid",
        width: "50px",
        height: "50px",
    }
}

export function showDefaultShips(fleet: Array<ShipType>) {
    const arrFleet = []
    
    fleet.forEach(ship => {
        let ix = 0
        arrFleet.push(
            <div style={wrapperStyle}>
                {
                    
                }
                <div
                    key={ship.name}
                    style={sourceDivStyle(ix + 1, 1)}
                ></div>
            </div>
        )
    })

    return arrFleet
}