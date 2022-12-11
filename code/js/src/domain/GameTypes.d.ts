import { Ship } from "./Ship"

type GameType = {
    name: string,
    boardSize: number,
    shotsPerRound: number,
    layoutDefTime: number,
    shootingTime: number,
    fleet: Array<Ship>
}

export type GameTypes = {
    gameTypes: Array<GameType>
}