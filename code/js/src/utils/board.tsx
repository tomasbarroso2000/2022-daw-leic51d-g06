import * as React from "react"
import { Square } from "../domain/Square"

function borderDivStyle(squareSize: number): React.CSSProperties {
    return {
        width: `${squareSize}px`, 
        height: `${squareSize}px`,
        backgroundColor: "#4A6FA5"
    }
}

/**
 * Creates column letters squares
 * @param boardSize the side size of the board
 * @param squareSize the size of the squares
 * @returns the squares
 */
function ColumnNumbersView(boardSize: number, squareSize: number): Array<JSX.Element> {
    const squares: Array<JSX.Element> = []
    squares.push(<div key={0} style={borderDivStyle(squareSize)}></div>)
    for(let column = 1; column <= boardSize; column++) {
        squares.push(<div key={column} style={borderDivStyle(squareSize)}>{column}</div>) 
    }
    return squares
}

function Squares(
    boardSize: number, 
    squareSize: number, 
    getSquareAppearance: (square: Square, squareSize: number, isLast: boolean) => JSX.Element
): Array<JSX.Element> {
    const squares: Array<JSX.Element> = []
    const firstSquare: Square = {row: "a", column: 1}
    for (let rowNumber: number = firstSquare.row.charCodeAt(0) - 97 + 1; rowNumber <= boardSize; rowNumber++) {
        const row = String.fromCharCode(rowNumber - 1 + 97)
        squares.push(<div key={`{row: ${row}, column${0}}`} style={borderDivStyle(squareSize)}>{row}</div>)
        for (let column: number = firstSquare.column; column <= boardSize; column++) {
            const square: Square = {row: row, column: column}
            const isLast = 
                rowNumber == square.row.charCodeAt(0) - 97 + boardSize - 1 &&
                column == square.column + boardSize - 1
            squares.push(getSquareAppearance(square, squareSize, isLast))
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
export function BoardView(
    boardSize: number, 
    squareSize: number, 
    getSquareAppearance: (square: Square, squareSize: number, isLast: boolean) => JSX.Element
): JSX.Element {
    return (
        <div style={boardDivStyle(boardSize)}> 
            {ColumnNumbersView(boardSize, squareSize)}
            {Squares(boardSize, squareSize, getSquareAppearance)}
        </div>
    )
}