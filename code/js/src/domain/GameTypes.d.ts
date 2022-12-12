import { ShipType } from "./ShipType"

export type GameType = {
    name: string,
    boardSize: number,
    shotsPerRound: number,
    layoutDefTime: number,
    shootingTime: number,
    fleet: Array<ShipType>
}

export type GameTypes = {
    gameTypes: Array<GameType>
}