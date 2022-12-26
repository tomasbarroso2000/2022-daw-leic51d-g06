import { Square } from "./Square"

export type HitOutcome = {
    square: Square,
    hitShip: boolean,
    destroyedShip: boolean
}

export type SendHits = {
    hitsOutcome: Array<HitOutcome>,
    win: boolean
}