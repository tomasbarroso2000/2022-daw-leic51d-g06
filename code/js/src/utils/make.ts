import { GameType } from "../domain/GameTypes";
import { Ship } from "../domain/Ship";
import { ShipType } from "../domain/ShipType";
import { Square } from "../domain/Square";
import { UserInfo } from "../domain/UserInfo";

export function makeHitsOrMIsses(hits): Array<Square> {
    const squares: Array<Square> = []
    hits.forEach(hit => { squares.push({row: hit.row, column: hit.column}) })
    return squares
}

export function makeFleet(fleet): Array<Ship> {
    return fleet.map(ship => {
        return {
            firstSquare: {row: ship["first-square"].charAt(0), column: ship["first-square"].charCodeAt(1)-48},
            name: ship.name,
            size: ship.size,
            destroyed: ship.destroyed,
            orientation: ship.orientation,
            userId: ship["user-id"],
            gameId: ship["game-id"],
            nOfHits: ship["nof-hits"],
            squares: makeHitsOrMIsses(ship.squares)
        }
    })
}

export function makeUserInfo(id, name, score): UserInfo {
    return {
        id: id,
        name: name,
        score: score
    }
}

export function makeTypeFleet(gameTypeFleet): Array<ShipType> {
    return gameTypeFleet.map(shipType => {
        return {
            name: shipType.name,
            size: shipType.size,
            gameType: shipType["game-type"]
        }
    })
}

export function makeGameType(
    name, 
    boardSize, 
    shotsPerRound, 
    layoutDefTime, 
    shootingTime, 
    fleetTypes: Array<ShipType>
): GameType {
    return {
        name: name,
        boardSize: boardSize,
        shotsPerRound: shotsPerRound,
        layoutDefTime: layoutDefTime,
        shootingTime: shootingTime,
        fleet: fleetTypes
    }
}