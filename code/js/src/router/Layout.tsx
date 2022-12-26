import * as React from "react"
import { Dispatch, useCallback, useReducer } from "react"
import Draggable from "react-draggable"
import { CurrentUser } from "../domain/CurrentUser"
import { Game } from "../domain/Game"
import { LayoutShip } from "../domain/ship"
import { Square, squareToString } from "../domain/Square"
import { BoardView } from "../utils/board"
import { deepEqual } from "../utils/deepEqual"
import { BIG_BOARD_SQUARE_SIZE, INNER_COLOR, SHIP_COLOR } from "./PlayGame"

// The function to compute the CSS style for a source `div`
function sourceDivStyle(column: number, row: number): React.CSSProperties {
    return {
        gridColumn: column,
        gridRow: row,
        border: "solid",
        width: "50px",
        height: "50px",
        borderColor: 'green'
    }
}


function handleDragStart(event: React.DragEvent<HTMLDivElement>) {
    const shipName = event.currentTarget.attributes.getNamedItem('data-name').textContent
    console.log(`dragStart - ${shipName}`)
    event.dataTransfer.effectAllowed = "all"
    event.dataTransfer.setData("text/plain", shipName)
}

function handleDragOver(event: React.DragEvent<HTMLDivElement>) {
    event.preventDefault()
    console.log("dragOver")
    event.dataTransfer.dropEffect = "copy";
}

function makeHandleDropFunction(layoutShips: Array<LayoutShip>, setLayoutShips: Dispatch<React.SetStateAction<LayoutShip[]>>) {
    return function handleDrop(event: React.DragEvent<HTMLDivElement>) {

        const layoutShip: LayoutShip = JSON.parse(event.dataTransfer.getData("text/plain"))
        const square: Square = JSON.parse(event.currentTarget.attributes.getNamedItem('data-square').textContent)

        console.log(layoutShip)
        console.log(square)

        setLayoutShips(layoutShips.map((originalLayoutShip) => {
            if (deepEqual(originalLayoutShip.type, layoutShip.type)) {
                console.log("same ship in " + originalLayoutShip.type.name)
                return {
                    type: originalLayoutShip.type,
                    position: square,
                    orientation: originalLayoutShip.orientation
                }
            } else {
                console.log("noship")
                return originalLayoutShip
            }
        }))
    }
}

function draggableShipDivStyle(shipSize: number, squareSize: number): React.CSSProperties {
    return {
        display: "grid",
        gap: 1,
        gridTemplateColumns: `repeat(${shipSize}, 1fr)`,
        gridTemplateRows: `repeat(1, 1fr)`,
        float: "left",
        textAlign: "center",
        alignItems: "center",
    }
}

function layoutSquareStyle(squareSize: number): React.CSSProperties {
    return {
        width: `${squareSize}px`, 
        height: `${squareSize}px`, 
        backgroundColor: INNER_COLOR
    }
}

function layoutShipSquareStyle(squareSize: number): React.CSSProperties {
    return {
        width: `${squareSize}px`, 
        height: `${squareSize}px`, 
        backgroundColor: SHIP_COLOR
    }
}

function DraggableShips(layoutShips: Array<LayoutShip>, squareSize: number): Array<JSX.Element> {
    const ships: Array<JSX.Element> = []
    layoutShips.map((ship: LayoutShip) => {
        const shipType = ship.type
        const squares: Array<JSX.Element> = []
        for (let i = 0; i < shipType.size; i++) {
            squares.push(<div style={layoutShipSquareStyle(squareSize)}></div>)
        }
        ships.push(<Draggable><div style={draggableShipDivStyle(shipType.size, squareSize)}>{squares}</div></Draggable>)
    })
    return ships
}

export function Layout(
    game: Game,
    layoutShips: Array<LayoutShip>,
    setLayoutShips: Dispatch<React.SetStateAction<LayoutShip[]>>
) {  
    console.log(layoutShips)  
    return (
            <div>
                <h1>Layout</h1>
                <div>
                    {
                        layoutShips.map((layoutShip, ix) => 
                        <div
                            key={layoutShip.type.name}
                            style={sourceDivStyle(ix + 1, 1)}
                            draggable="true"
                            onDragStart={handleDragStart}
                            data-name={JSON.stringify(layoutShip)}>
                            {ix}
                        </div>)
                    }
                </div>
                <div>
                    {BoardView(game.type.boardSize, BIG_BOARD_SQUARE_SIZE, (square: Square, squareSize: number, isLast: boolean) => {
                        return (
                            <div key={squareToString(square)} 
                                style={layoutSquareStyle(squareSize)}
                                onDragOver={handleDragOver}
                                onDrop={makeHandleDropFunction(layoutShips, setLayoutShips)}
                                data-square={JSON.stringify(square)}>
                                {layoutShips.some((layoutShip: LayoutShip) => layoutShip.position && deepEqual(layoutShip.position, square)) ? "X" : ""}
                            </div>
                        )
                    }
                    )}
                </div>
            </div>

    )
}
