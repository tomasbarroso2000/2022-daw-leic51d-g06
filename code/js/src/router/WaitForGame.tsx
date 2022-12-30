import * as React from "react"
import { useState, useCallback } from 'react'
import { Navigate, useParams } from "react-router-dom"
import { useIntervalAsync } from "../utils/useIntervalAsync"
import { paths, service } from "./App"
import { useCurrentUser } from './Authn'

export function WaitForGame() {
    document.title = "Waiting In Lobby"
    const currentUser = useCurrentUser()
    const params = useParams()
    const [gameId, setGameId]: [number | undefined, React.Dispatch<any>] = useState(undefined)

    const updateGameId = useCallback(async () => {
        if (!gameId) {
            const enteredGame = await service.enteredGame(currentUser.token, parseInt(params["lobbyId"]))
            if (enteredGame.gameId)
                setGameId(enteredGame.gameId);
        }
    }, [gameId])

    useIntervalAsync(updateGameId, 3000)

    if (gameId)
        return <Navigate to={`${paths['game'].replace(":gameId", gameId.toString())}`} replace/>

    return (
        <div id="wait">
            <h1>Waiting for game</h1>
            <div className="lds-default"><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div></div>
            <p>Remember to always have fun!</p>
        </div>        
    )
}