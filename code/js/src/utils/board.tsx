import * as React from "react"
import { useState } from "react"
import { Ship } from "../domain/ship"
import { ShipType } from "../domain/ShipType"
import { Square } from "../domain/Square"

function gridSquare(key: string, hasShip: boolean, isShooting: boolean) {
    if(hasShip)
        return <div key={key} className="grid-square ship" />
    else if(isShooting)
        return <div key={key} className="grid-square shooting" />
    else
        return <div key={key} className="grid-square" />
}

function gridSquareShipHit(key: string) {
    return <div key={key} className="grid-square cross-ship-hit" /> 
}

function gridCross(key: string) {
    return <div key={key} className="cross" />
}

export function showGameBoard(boardSize: number, ships: Array<Ship>, hits: Array<Square>, isShooting: boolean) {
    const gameBoard = []
    for (let row = 0; row < boardSize; row ++) {
        gameBoard.push([])
        for (let col = 0; col < boardSize; col++) {
            gameBoard[row].push(gridSquare(`${col}${row}`, false, isShooting))
        }
    }
    if(ships.length > 0) {
        ships.forEach(ship => {
            for (let i = 0; i < ship.size; i++) {
                if(ship.orientation === "vertical")
                gameBoard[ship.firstSquare.row.charCodeAt(0) - 97 + i][ship.firstSquare.column-1] =
                gridSquare(`${ship.firstSquare.row}${ship.firstSquare.column}${i}`, true, false)
            else
                gameBoard[ship.firstSquare.row.charCodeAt(0) - 97][ship.firstSquare.column-1 + i] = 
                gridSquare(`${ship.firstSquare.row}${ship.firstSquare.column}${i}`, true, false)
            }
        })
    }

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

function createLettersView(boardSize: number): Array<JSX.Element> {
    const letters: Array<JSX.Element> = []
    for(let i = 0; i <= boardSize; i++) {
        if(i == 0) 
            letters.push(<div key={i} style={{width: "25px", height: "25px", backgroundColor: "#4A6FA5"}}></div>)
        else 
            letters.push(
                <div key={i} style={{width: "25px", height: "25px", backgroundColor: "#4A6FA5"}}>
                    {String.fromCharCode(i - 1 + 97)}
                </div>
            ) 
    }
    return letters
}

function createItems(boardSize: number, getSquareAppearance: (square: Square, bool: boolean) => any): Array<JSX.Element> {
    const items: Array<JSX.Element> = []
    const defaultSquare = new Square("a", 0)
    for (let row: number = defaultSquare.firstRow.charCodeAt(0) - 97 + 1; row <= boardSize + 1; row++) {
        for (let column: number = defaultSquare.firstColumn; column <= boardSize; column++) {
            const square = new Square(String.fromCharCode(row - 1 + 97), column - 1)
            const isLast = 
                row == square.firstRow.charCodeAt(0) - 97 + boardSize - 1 &&
                column == square.firstColumn + boardSize - 1
            if(column - row + 1 == defaultSquare.firstColumn) {
                items.push(<div key={JSON.stringify(square)} style={{width: "25px", height: "25px", backgroundColor: "#4A6FA5"}}>{row}</div>)
            } else {
                items.push(getSquareAppearance(square, isLast))
            }
        }
    }
    return items
}

export function BoardView(boardSize: number, getSquareAppearance: (square: Square, bool: boolean) => any): JSX.Element {
    return (
        <div>
            <div
            style={{
            display: "grid",
            gap: 1,
            gridTemplateColumns: `repeat(${boardSize+1}, 1fr)`,
            gridTemplateRows: `repeat(${boardSize+1}, 1fr)`,
            float: "left",
            textAlign: "center",
            alignItems: "center"
                }}
            > 
                {createLettersView(boardSize)}
                {createItems(boardSize, getSquareAppearance)}
            </div>
        </div>
    )
}