import * as React from "react";
import { Game} from "../domain/Game";
import { paths, service } from "./App";
import { useCurrentUser } from "./Authn";
import { Square } from "../domain/Square";
import { Dispatch, useCallback, useEffect, useState } from "react";
import { Layout } from "./Layout";
import { LayoutShip } from "../domain/LayoutShip";
import { useIntervalAsync } from "../utils/useIntervalAsync";
import { Shooting } from "./Shooting";
import { FinishedGame } from "./FinishedGame";
import { Navigate, useParams } from "react-router-dom";
import { calcTimeLeft } from "../utils/calcTimeLeft";
import { Loading } from "./Loading";

export function PlayGame() {
    const currentUser = useCurrentUser()
    const params = useParams()
    const gameId = parseInt(params["gameId"])
    const [layoutShips, setLayoutShips]: [Array<LayoutShip>, Dispatch<React.SetStateAction<LayoutShip[]>>] = useState([])
    const [selectedSquares, setSelectedSquares]: [Array<Square>, Dispatch<React.SetStateAction<Square[]>>] = useState([])
    const [gameInfo, setGameInfo]: [Game | undefined, Dispatch<Game>] = useState(undefined)
    const [timer, setTimer]: [number | undefined, Dispatch<number>] = useState(undefined)
    const [gameRemoved, setGameRemoved] = useState(false)

    const updateGameInfo = useCallback(async () => {
        const newGameInfo = await service.gameInfo(currentUser.token, gameId)
        if (gameInfo && gameInfo.state == "layout_definition" && newGameInfo.state == "completed")
            setGameRemoved(true)
        setGameInfo(newGameInfo)
    }, [])

    useEffect(() => {
        const tid = setInterval(() => {
            if (gameInfo) {
                console.log("updating timer")
                switch (gameInfo.state) {
                    case "layout_definition": {
                        const timeLeft = Math.round(calcTimeLeft(gameInfo.type.layoutDefTime, Date.parse(gameInfo.startedAt)))
                        if (timeLeft <= 0) {
                            setGameRemoved(true)
                        } else {
                            setTimer(timeLeft)
                        } 
                        break
                    }
                    case "shooting": {
                        setTimer(Math.round(calcTimeLeft(gameInfo.type.shootingTime, Date.parse(gameInfo.startedAt))))
                        break
                    }
                }
            }
        }, 1000)
        return () => {
            clearInterval(tid)
        }
    }, [gameInfo])

    useIntervalAsync(updateGameInfo, 3000)

    if(!gameInfo) {
        return <Loading />
    }

    if (gameRemoved) {
        return <Navigate to={paths["list-games"]}></Navigate>
    }

    switch (gameInfo.state) {
        case "layout_definition": {
            console.log("layout_definition")
            return Layout(gameInfo, currentUser, timer, layoutShips, setLayoutShips)
        }
        case "shooting": {
            console.log("shooting")
            return Shooting(gameInfo, currentUser, timer, selectedSquares, setSelectedSquares)
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


