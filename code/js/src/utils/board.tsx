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


const borderDivStyle: React.CSSProperties = {
    width: "25px", 
    height: "25px",
    backgroundColor: "#4A6FA5"
}

/**
 * Creates column letters squares
 * @param boardSize the side size of the board
 * @returns the squares
 */
function ColumnNumbersView(boardSize: number): Array<JSX.Element> {
    const squares: Array<JSX.Element> = []
    squares.push(<div key={0} style={borderDivStyle}></div>)
    for(let column = 1; column <= boardSize; column++) {
        squares.push(<div key={column} style={borderDivStyle}>{column}</div>) 
    }
    return squares
}

function Squares(boardSize: number, getSquareAppearance: (square: Square, isLast: boolean) => JSX.Element): Array<JSX.Element> {
    const squares: Array<JSX.Element> = []
    const firstSquare: Square = {row: "a", column: 1}
    for (let rowNumber: number = firstSquare.row.charCodeAt(0) - 97 + 1; rowNumber <= boardSize; rowNumber++) {
        const row = String.fromCharCode(rowNumber - 1 + 97)
        squares.push(<div key={`{row: ${row}, column${0}}`} style={borderDivStyle}>{row}</div>)
        for (let column: number = firstSquare.column; column <= boardSize; column++) {
            const square: Square = {row: row, column: column}
            const isLast = 
                rowNumber == square.row.charCodeAt(0) - 97 + boardSize - 1 &&
                column == square.column + boardSize - 1
            squares.push(getSquareAppearance(square, isLast))
        }
    }
    return squares
}

function boardDivStyle(boardSize: number): React.CSSProperties {
    return {
        display: "grid",
        gap: 1,
        gridTemplateColumns: `repeat(${boardSize+1}, 1fr)`,
        gridTemplateRows: `repeat(${boardSize+1}, 1fr)`,
        float: "left",
        textAlign: "center",
        alignItems: "center"
    }
}

/**
 * Creates a board
 * @param boardSize the side size of the board
 * @param getSquareAppearance the function that gets the appearence of each square
 * @returns the board
 */
export function BoardView(boardSize: number, getSquareAppearance: (square: Square, isLast: boolean) => JSX.Element): JSX.Element {
    return (
        <div style={boardDivStyle(boardSize)}> 
            {ColumnNumbersView(boardSize)}
            {Squares(boardSize, getSquareAppearance)}
        </div>
    )
}