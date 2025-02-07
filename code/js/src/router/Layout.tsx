import * as React from "react"
import { Dispatch, } from "react"
import { CurrentUser } from "../domain/CurrentUser"
import { Game } from "../domain/Game"
import { GameType } from "../domain/GameTypes"
import { LayoutShip, layoutShipSquares, nextSquareFunction, otherOrientation } from "../domain/LayoutShip"
import { ShipType } from "../domain/ShipType"
import { Square, squareToString, surroundingSquares } from "../domain/Square"
import { BoardView } from "../utils/board"
import { ButtonFab } from "../utils/ButtonFab"
import { capitalize } from "../utils/capitalize"
import { contains } from "../utils/contains"
import { deepEqual } from "../utils/deepEqual"
import { replace } from "../utils/replace"
import { service } from "./App"
import { BIG_BOARD_SQUARE_CONST, INNER_COLOR, SHIP_COLOR } from "../utils/board"


function handleDragStart(event: React.DragEvent<HTMLDivElement>) {
    const shipName = event.currentTarget.attributes.getNamedItem('data-name').textContent
    event.dataTransfer.effectAllowed = "all"
    event.dataTransfer.setData("text/plain", shipName)
}

function handleDragOver(event: React.DragEvent<HTMLDivElement>) {
    event.preventDefault()
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
        width: isFirst ? `${squareSize - 4}px` : `${squareSize}px`,
        height: isFirst ? `${squareSize - 4}px` : `${squareSize}px`,
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
        if (ship.position && !filteredShip.position || ship.position && !deepEqual(ship.position, filteredShip.position)) {
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
    document.title = "Layout Stage"
    const shipType = layoutShip.type
    const shipSquares: Array<JSX.Element> = []
    shipSquares.push(<div key={shipType.name + 0} style={layoutShipSquareStyle(squareSize, true)}></div>)
    for (let i = 1; i < shipType.size; i++) {
        shipSquares.push(<div key={shipType.name + i} style={layoutShipSquareStyle(squareSize, false)}></div>)
    }

    return (
        <div key={layoutShip.type.name} style={{ marginBottom: "50px" }}>
            <div>{capitalize(layoutShip.type.name)}</div>
            <div style={{ display: "flex" }}>
                <div
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
                    <button className="reset-btn"
                        style={layoutShip.position ? undefined : { display: "none" }}
                        onClick={() => {
                            setLayoutShips(
                                replace(
                                    layoutShips,
                                    layoutShip,
                                    { type: layoutShip.type, position: undefined, orientation: layoutShip.orientation }
                                )
                            )
                        }}>{"Reset"}
                    </button>

                </div>
            </div>
        </div>
    )
}

function initialLayoutShips(gameType: GameType): Array<LayoutShip> {
    return gameType.fleet.map((shipType: ShipType) => {
        return { type: shipType, position: undefined, orientation: "horizontal" }
    })
}

function changeOrientation(boardSize: number, layoutShip: LayoutShip, layoutShips: Array<LayoutShip>, setLayoutShips: Dispatch<React.SetStateAction<LayoutShip[]>>) {
    const newLayoutShip: LayoutShip = JSON.parse(JSON.stringify(layoutShip))
    newLayoutShip.orientation = otherOrientation(newLayoutShip.orientation)
    if (!isSquareValidForShip(boardSize, newLayoutShip.position, newLayoutShip, layoutShips))
        return () => { console.log("invalid positioning") }
    return () => { setLayoutShips(replace(layoutShips, layoutShip, newLayoutShip)) }
}

export function layout(
    game: Game,
    currentUser: CurrentUser,
    timer: number,
    layoutShips: Array<LayoutShip>,
    loading: boolean,
    setLayoutShips: Dispatch<React.SetStateAction<LayoutShip[]>>,
    setGameInfo: Dispatch<Game>,
    setLoading: Dispatch<boolean>
) {

    if (layoutShips.length == 0)
        setLayoutShips(initialLayoutShips(game.type))

    const squareSize = BIG_BOARD_SQUARE_CONST / game.type.boardSize

    if (game.fleet.length == 0)
        return (
            <div id="layout-content">
                <div style={{ float: "left" }}>
                    <div id="layout-status">
                        <h1>Define Your Layout</h1>
                        <h2>{`${currentUser.name} vs. ${game.opponent.name}`}</h2>
                        <p>Grab ships by the first square and press it to rotate them</p>
                    </div>
                    <div id="timer">Time left: <p>{timer}</p></div>
                    <div id="btn-container">
                        <ButtonFab
                            isDisabled={loading || !layoutShips.every((ship) => ship.position)}
                            onClick={() => {
                                setLoading(true)
                                service.defineLayout(currentUser.token, game.id, layoutShips).then((game) => {
                                    setGameInfo(game)
                                    setLoading(false)
                                })
                            }}
                            text={"Submit Layout"} />
                    </div>
                </div>
                <div id="board-container">
                    <div id="ships-layout-container">
                        {layoutShips.map((layoutShip) => draggableShip(game.type.boardSize, squareSize, layoutShip, layoutShips, setLayoutShips))}
                    </div>
                    <div style={{ display: "inline-block", verticalAlign: "middle" }}>
                        {BoardView(game.type.boardSize, squareSize, (square: Square, squareSize: number, isLast: boolean) => {
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
            </div>
        )
    else
        return (
            <div id="wait">
                <h1>Waiting for {game.opponent.name}...</h1>
                <div className="lds-default"><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div></div>
                <p>Almost there!</p>
            </div>
        )
}
