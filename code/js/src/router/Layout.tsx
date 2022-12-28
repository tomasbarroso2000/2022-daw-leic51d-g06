import * as React from "react"
import { Dispatch,  } from "react"
import { CurrentUser } from "../domain/CurrentUser"
import { Game } from "../domain/Game"
import { GameType } from "../domain/GameTypes"
import { LayoutShip, layoutShipSquares, nextSquareFunction, otherOrientation, ShipOrientation } from "../domain/LayoutShip"
import { ShipType } from "../domain/ShipType"
import { Square, squareToString, surroundingSquares } from "../domain/Square"
import { BoardView } from "../utils/board"
import { capitalize } from "../utils/capitalize"
import { contains } from "../utils/contains"
import { deepEqual } from "../utils/deepEqual"
import { replace } from "../utils/replace"
import { service } from "./App"
import { BIG_BOARD_SQUARE_SIZE, INNER_COLOR, SHIP_COLOR } from "./PlayGame"


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

function makeHandleDropFunction(boardSize: number, layoutShips: Array<LayoutShip>, setLayoutShips: Dispatch<React.SetStateAction<LayoutShip[]>>) {
    return function handleDrop(event: React.DragEvent<HTMLDivElement>) {

        const layoutShip: LayoutShip = JSON.parse(event.dataTransfer.getData("text/plain"))
        const square: Square = JSON.parse(event.currentTarget.attributes.getNamedItem('data-square').textContent)

        if (!isSquareValidForShip(boardSize, square, layoutShip, layoutShips))
            return

        const newLayoutShip: LayoutShip = JSON.parse(JSON.stringify(layoutShip))
        newLayoutShip.position = square

        console.log(layoutShip)
        console.log(square)

        setLayoutShips(layoutShips.map((originalLayoutShip) => {
            if (deepEqual(originalLayoutShip.type, newLayoutShip.type))
                return newLayoutShip
            else
                return originalLayoutShip
        }))
    }
}

function draggableShipDivStyle(shipSize: number, squareSize: number): React.CSSProperties {
    return {
        display: "inline",
        gap: 1,
        float: "left",
        textAlign: "center",
        alignItems: "center",
    }
}

function layoutSquareStyle(squareSize: number, isOccupied: boolean): React.CSSProperties {
    return {
        width: `${squareSize}px`, 
        height: `${squareSize}px`, 
        backgroundColor: isOccupied ? SHIP_COLOR : INNER_COLOR
    }
}

function layoutShipSquareStyle(squareSize: number, isFirst: boolean): React.CSSProperties {
    return {
        display: "inline-block",
        marginRight: "1px",
        width: isFirst ? `${squareSize-4}px` : `${squareSize}px`, 
        height: isFirst? `${squareSize-4}px` : `${squareSize}px`, 
        backgroundColor: SHIP_COLOR,
        border: isFirst ? "2px solid red" : undefined
    }
}

function layoutShipPropertiesStyle(squareSize: number): React.CSSProperties {
    return {
        display: "inline", 
        width: `${squareSize}px`,
        height: `${squareSize}px`, 
        lineHeight: `${squareSize}px`, 
        textAlign: "center"
    }
}

function unavailableSquares(layoutShips: Array<LayoutShip>, filteredShip: LayoutShip): Array<Square> {
    const squares = []
    layoutShips.forEach((ship) => {
        if (ship.position && !filteredShip.position || ship.position && ! deepEqual(ship.position, filteredShip.position)) {
            const shipSquares = layoutShipSquares(ship)
            shipSquares.forEach((square) => {
                const unavailableSquares = surroundingSquares(square)
                unavailableSquares.forEach((unavailableSquare) => {
                    if (!contains(squares, unavailableSquare))
                        squares.push(unavailableSquare)
                })
            })
        }
    })
    return squares
}

function isSquareValidForShip(boardSize: number, square: Square, layoutShip: LayoutShip, layoutShips: Array<LayoutShip>) {
    if (!square)
        return true
    console.log("valid square")
    const invalidSquares = unavailableSquares(layoutShips, layoutShip)
    let currSquare = square
    const nextSquare = nextSquareFunction(layoutShip.orientation)
    let valid = true
    for (let i = 0; i < layoutShip.type.size; i++) {
        if (currSquare.row.charCodeAt(0) < 'a'.charCodeAt(0) || currSquare.row.charCodeAt(0) >= 'a'.charCodeAt(0) + boardSize ||
            currSquare.column < 1 || currSquare.column >= 1 + boardSize || contains(invalidSquares, currSquare))
            valid = false
        currSquare = nextSquare(currSquare)
    }
    return valid
}

function isSquareOccupied(layoutShips: Array<LayoutShip>, square: Square) {
    return layoutShips.some((layoutShip: LayoutShip) => contains(layoutShipSquares(layoutShip), square))
}

function draggableShip(
    boardSize: number,
    squareSize: number,
    layoutShip: LayoutShip,
    layoutShips: Array<LayoutShip>,
    setLayoutShips: Dispatch<React.SetStateAction<LayoutShip[]>>
): JSX.Element {
    
    const shipType = layoutShip.type
    const shipSquares: Array<JSX.Element> = []
    shipSquares.push(<div style={layoutShipSquareStyle(squareSize, true)}></div>)
    for (let i = 1; i < shipType.size; i++) {
        shipSquares.push(<div style={layoutShipSquareStyle(squareSize, false)}></div>)
    }

    return (
        <div style={{marginBottom: "50px", marginRight: "200px"}}>
            <div>{capitalize(layoutShip.type.name)}</div>
            <div style={{display: "flex"}}>
                <div
                    key={layoutShip.type.name}
                    style={draggableShipDivStyle(layoutShip.type.size, squareSize)}
                    onClick={changeOrientation(boardSize, layoutShip, layoutShips, setLayoutShips)}
                    draggable="true"
                    onDragStart={handleDragStart}
                    data-name={JSON.stringify(layoutShip)}>
                    {shipSquares}
                </div>
                <div style={layoutShipPropertiesStyle(squareSize)}>
                    {layoutShip.orientation == "horizontal" ? "—" : "|"}
                </div>
                <div style={layoutShipPropertiesStyle(squareSize)}>
                    <button 
                        style={layoutShip.position ? undefined : {display: "none"}} 
                        onClick={() => {
                            setLayoutShips(
                                replace(
                                    layoutShips, 
                                    layoutShip, 
                                    {type: layoutShip.type, position: undefined, orientation: layoutShip.orientation}
                                )
                            )
                    }}>{"<"}
                    </button>
                
                </div>
            </div>
        </div>
    )
}

function initialLayoutShips(gameType: GameType): Array<LayoutShip> {
    return gameType.fleet.map((shipType: ShipType) => {
        return {type: shipType, position: undefined, orientation: "horizontal"}
    })
}

function changeOrientation(boardSize: number, layoutShip: LayoutShip, layoutShips: Array<LayoutShip>, setLayoutShips: Dispatch<React.SetStateAction<LayoutShip[]>>) {
    const newLayoutShip: LayoutShip = JSON.parse(JSON.stringify(layoutShip))
    newLayoutShip.orientation = otherOrientation(newLayoutShip.orientation)
    if (!isSquareValidForShip(boardSize, newLayoutShip.position, newLayoutShip, layoutShips))
        return () => { console.log("invalid positioning") }
    return () => { setLayoutShips(replace(layoutShips, layoutShip, newLayoutShip)) }
}

export function Layout(
    game: Game,
    currentUser: CurrentUser,
    timer: number,
    layoutShips: Array<LayoutShip>,
    setLayoutShips: Dispatch<React.SetStateAction<LayoutShip[]>>,
    setGameInfo: React.Dispatch<Game>
) {  
    if (layoutShips.length == 0) 
        setLayoutShips(initialLayoutShips(game.type))
    if (game.fleet.length == 0)
        return (
                <div>
                    <h1>Define Your Layout</h1>
                    <h2>{`${currentUser.name} vs. ${game.opponent.name}`}</h2>
                    <div>Grab ships by the first square and press the rotate button to rotate them</div>
                    <div>Timer: {timer}</div>
                    <div style={{textAlign: "center"}}>
                        <div style={{display: "inline-block", verticalAlign: "middle"}}>
                            {layoutShips.map((layoutShip) => draggableShip(game.type.boardSize, BIG_BOARD_SQUARE_SIZE, layoutShip, layoutShips, setLayoutShips))}
                        </div>
                        <div style={{display: "inline-block", verticalAlign: "middle"}}>
                            {BoardView(game.type.boardSize, BIG_BOARD_SQUARE_SIZE, (square: Square, squareSize: number, isLast: boolean) => {
                                return (
                                    <div key={squareToString(square)} 
                                        style={layoutSquareStyle(squareSize, isSquareOccupied(layoutShips, square))}
                                        onDragOver={handleDragOver}
                                        onDrop={makeHandleDropFunction(game.type.boardSize, layoutShips, setLayoutShips)}
                                        data-square={JSON.stringify(square)}>
                                    </div>
                                )
                            }
                            )}
                        </div>
                    </div>
                    <div>
                        <button onClick={() => { 
                            service.defineLayout(currentUser.token, game.id, layoutShips).then((game) => setGameInfo(game))}
                        }>Submit layout
                        </button>
                    </div>
                </div>
        )
    else
        return (
            <div>Waiting for {game.opponent.name}...</div>
        )
}
