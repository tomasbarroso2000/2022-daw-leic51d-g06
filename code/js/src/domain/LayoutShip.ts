import { ShipType } from "./ShipType"
import { right, down, Square } from "./Square"

export type ShipOrientation = "horizontal" | "vertical"

export function otherOrientation(orientation: ShipOrientation): ShipOrientation {
    return orientation == "horizontal" ? "vertical" : "horizontal" 
}

export function nextSquareFunction(orientation: ShipOrientation) {
    return orientation  == "horizontal" ? right : down
}

export type LayoutShip = {
    type: ShipType,
    position: Square | undefined,
    orientation: ShipOrientation
}

export function layoutShipSquares(layoutShip: LayoutShip): Array<Square> {
    const squares: Array<Square> = []
    if (layoutShip.position) {
        let currSsquare = layoutShip.position
        const nextSquare = nextSquareFunction(layoutShip.orientation)
        for (let i = 0; i < layoutShip.type.size; i++) {
            squares.push(currSsquare)
            currSsquare = nextSquare(currSsquare)
        }
    }
    return squares
} 