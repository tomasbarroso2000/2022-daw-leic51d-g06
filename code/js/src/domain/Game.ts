import { contains } from "../utils/contains"
import { deepEqual } from "../utils/deepEqual"
import { GameType } from "./GameTypes"
import { Ship } from "./ship"
import { Square, surroundingSquares } from "./Square"
import { UserInfo } from "./UserInfo"

export type GameState = "layout_definition" | "shooting" | "completed"

export type Game = {
    id: number,
    type: GameType,
    state: GameState,
    opponent: UserInfo,
    playing: boolean,
    startedAt: string,
    fleet: Array<Ship>,
    takenHits: Array<Square>
    enemySunkFleet: Array<Ship>
    hits: Array<Square>,
    misses: Array<Square>
}

export function isEnemySquareHit(game: Game, square: Square) {
    return contains(game.hits, square) || contains(game.misses, square)
}

export function isEnemySquareDestroyed(game: Game, square: Square) {
    return game.enemySunkFleet.some((ship: Ship) => contains(ship.squares, square))
}

export function isEnemySquareAroundDestroyed(game: Game, square: Square) {
    return game.enemySunkFleet.some((ship: Ship) => 
        ship.squares.some((shipSquare) => 
            contains(surroundingSquares(shipSquare), square) &&
            !deepEqual(shipSquare, square)
        )
    )
}
    
