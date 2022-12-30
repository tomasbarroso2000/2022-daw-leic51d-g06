import * as React from "react"
import { Square } from "../domain/Square"

export const INNER_COLOR = "#008DD5"
export const DESTROYED_SHIP_COLOR = "#000000"
export const AROUND_DESTROYED_COLOR = "#0074AF"
export const SHIP_COLOR = "#AED4E6"
export const SELECTED_COLOR = "#FF0000"

export const SMALL_BOARD_SQUARE_CONST = 250
export const BIG_BOARD_SQUARE_CONST = 400

const SQUARE_TEXT_CONST = 1.5

export function squareTextStyle(squareSize: number): React.CSSProperties {
    return {fontSize: `${squareSize / SQUARE_TEXT_CONST}px`}
}

function borderDivStyle(squareSize: number): React.CSSProperties {
    return {
        width: `${squareSize}px`, 
        height: `${squareSize}px`,
        backgroundColor: "#4A6FA5",
        color: "white",
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
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
        squares.push(<div key={column} style={borderDivStyle(squareSize)}><p style={squareTextStyle(squareSize)}>{column}</p></div>) 
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
        squares.push(<div key={`{row: ${row}, column${0}}`} style={borderDivStyle(squareSize)}><p style={squareTextStyle(squareSize)}>{row}</p></div>)
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
        float: "right",
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