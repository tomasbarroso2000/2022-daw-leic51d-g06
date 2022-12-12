import { GameType } from "./GameTypes"
import { Ship } from "./ship"
import { Square } from "./Square"
import { UserInfo } from "./UserInfo"

type Game = {
    id: number,
    type: GameType,
    state: string,
    opponent: UserInfo,
    playing: boolean,
    startedAt: string,
    fleet: Array<Ship>,
    takenHits: Array<Square>
    enemySunkFleet: Array<Ship>
    hits: Array<Square>,
    misses: Array<Square>
}

export type GamesList = {
    games: Array<Game>,
    hasMore: boolean
}