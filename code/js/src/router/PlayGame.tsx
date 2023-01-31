import * as React from "react";
import { Game } from "../domain/Game";
import { paths, service } from "./App";
import { useCurrentUser } from "./Authn";
import { Square } from "../domain/Square";
import { Dispatch, useEffect, useState } from "react";
import { layout } from "./layout";
import { LayoutShip } from "../domain/LayoutShip";
import { shooting } from "./shooting";
import { finishedGame } from "./finishedGame";
import { Navigate, useParams } from "react-router-dom";
import { calcTimeLeft } from "../utils/calcTimeLeft";
import { Loading } from "./Loading";

export function PlayGame() {
    document.title = "Play Game"
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
                    if (gameInfo && gameInfo.state == "layout_definition" && newGameInfo.state == "completed") {
                        if (!goBack)
                            setGoBack(true)
                    } else {
                        setGameInfo(newGameInfo)
                    }

                })
                .catch(() => {
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
        return <Navigate to={paths["games"]}></Navigate>
    }

    if (!gameInfo) {
        return <Loading />
    }

    switch (gameInfo.state) {
        case "layout_definition": {
            return layout(gameInfo, currentUser, timer, layoutShips, loading, setLayoutShips, setGameInfo, setLoading)
        }
        case "shooting": {
            return shooting(gameInfo, currentUser, timer, selectedSquares, loading, setSelectedSquares, setGameInfo, setLoading)
        }
        case "completed": {
            return finishedGame(gameInfo)
        }
    }
}


