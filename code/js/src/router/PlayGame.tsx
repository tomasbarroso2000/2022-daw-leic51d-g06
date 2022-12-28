import * as React from "react";
import { Game} from "../domain/Game";
import { paths, service } from "./App";
import { useCurrentUser } from "./Authn";
import { Square } from "../domain/Square";
import { Dispatch, useEffect, useState } from "react";
import { Layout } from "./Layout";
import { LayoutShip } from "../domain/LayoutShip";
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
    const [loading, setLoading]: [boolean, Dispatch<boolean>] = useState(false)
    const [goBack, setGoBack] = useState(false)

    useEffect(() => {
        const tid = setInterval(() => {
            service.gameInfo(currentUser.token, gameId)
            .then((newGameInfo: Game) => {
                console.log(newGameInfo)
                if (gameInfo && gameInfo.state == "layout_definition" && newGameInfo.state == "completed") {
                    console.log("go back dud")
                    if (!goBack)
                        setGoBack(true)
                } else {
                    setGameInfo(newGameInfo)
                }
                
            })
            .catch(() => {
                console.log("catch")
                if (!goBack)
                    setGoBack(true)
            })
        }, gameInfo ? 3000 : 1000)
        return () => {
            clearInterval(tid)
        }
    }, [gameInfo])

    useEffect(() => {
        const tid = setInterval(() => {
            if (gameInfo) {
                console.log("updating timer")
                switch (gameInfo.state) {
                    case "layout_definition": {
                        const timeLeft = Math.round(calcTimeLeft(gameInfo.type.layoutDefTime, Date.parse(gameInfo.startedAt)))
                        setTimer(timeLeft)
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

    if (goBack) {
        return <Navigate to={paths["list-games"]}></Navigate>
    }

    if(!gameInfo) {
        return <Loading />
    }

    switch (gameInfo.state) {
        case "layout_definition": {
            console.log("layout_definition")
            return Layout(gameInfo, currentUser, timer, layoutShips, loading, setLayoutShips, setGameInfo, setLoading)
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

export const SMALL_BOARD_SQUARE_CONST = 250
export const BIG_BOARD_SQUARE_CONST = 400


