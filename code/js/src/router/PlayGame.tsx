import * as React from "react";
import { Game} from "../domain/Game";
import { service } from "./App";
import { useCurrentUser } from "./Authn";
import { Square } from "../domain/Square";
import { Dispatch, useCallback, useState } from "react";
import { Layout } from "./Layout";
import { LayoutShip } from "../domain/LayoutShip";
import { useIntervalAsync } from "../utils/useIntervalAsync";
import { Shooting } from "./Shooting";
import { FinishedGame } from "./FinishedGame";
import { useParams } from "react-router-dom";

export function PlayGame() {
    const currentUser = useCurrentUser()
    const params = useParams()
    const gameId = parseInt(params["gameId"])
    const [layoutShips, setLayoutShips]: [Array<LayoutShip>, Dispatch<React.SetStateAction<LayoutShip[]>>] = useState([])
    const [selectedSquares, setSelectedSquares]: [Array<Square>, Dispatch<React.SetStateAction<Square[]>>] = useState([])
    const [gameInfo, setGameInfo]: [Game | undefined, Dispatch<Game>] = useState(undefined)

    const updateGameInfo = useCallback(async () => {
        console.log("updating")
        const newGameInfo = await service.gameInfo(currentUser.token, gameId)
        console.log("newGameInfo: " + newGameInfo)
        console.log(newGameInfo)
        setGameInfo(newGameInfo)
    }, [])

    useIntervalAsync(updateGameInfo, 3000)

    if(!gameInfo) {
        console.log("!gameInfo")
        return (
            <div>
                ...loading...
            </div>
        )
    }
    switch (gameInfo.state) {
        case "layout_definition": {
            console.log("layout_definition")
            return Layout(gameInfo, currentUser, layoutShips, setLayoutShips)
        }
        case "shooting": {
            console.log("shooting")
            return Shooting(gameInfo, currentUser, selectedSquares, setSelectedSquares)
        }
        case "completed": {
            console.log("completed")
            return FinishedGame(gameInfo)
        }
    }
}

export const INNER_COLOR = "#008DD5"
export const DESTROYED_SHIP_COLOR = "#000000"
export const AROUND_DESTROYED_COLOR = "#FF0B5394"
export const SHIP_COLOR = "#AED4E6"
export const SELECTED_COLOR = "#FF0000"

export const SMALL_BOARD_SQUARE_SIZE = 25
export const BIG_BOARD_SQUARE_SIZE = 40


