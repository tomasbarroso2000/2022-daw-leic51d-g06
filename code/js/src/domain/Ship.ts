import { ShipType } from "./ShipType"
import { down, right, Square } from "./Square"

export type Ship = {
    firstSquare: Square,
    name: string,
    size: number,
    destroyed: boolean,
    orientation: string,
    userId: number,
    gameId: number,
    nOfHits: number,
    squares: Array<Square>
}

export type ShipOrientation = "horizontal" | "vertical"

export function otherOrientation(orientation: ShipOrientation): ShipOrientation {
    return orientation == "horizontal" ? "vertical" : "horizontal" 
}

export function nextSquareFunction(orientation: ShipOrientation) {
    return orientation  == "horizontal" ? right : down
}

export type LayoutShip = {
    type: ShipType,
    position: Square,
    orientation: ShipOrientation
}