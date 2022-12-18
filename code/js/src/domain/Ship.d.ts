import { Square } from "./Square"

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