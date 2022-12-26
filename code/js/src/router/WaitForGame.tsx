import * as React from "react"
import { useState, useCallback } from 'react'
import { Navigate, useParams } from "react-router-dom"
import { EnterLobby } from "../domain/Lobby"
import { askService, Result } from "../service/askService"
import { useIntervalAsync } from "../utils/useIntervalAsync"
import { paths, service } from "./App"
import { useCurrentUser } from './Authn'

export function WaitForGame() {
    const currentUser = useCurrentUser()
    const params = useParams()
    const [gameId, setGameId]: [number | undefined, React.Dispatch<any>] = useState(undefined)
    const [lobbyId, setLobbyId]: [number | undefined, React.Dispatch<any>] = useState(undefined)

    const enterLobby: Result<EnterLobby> | undefined = 
        askService(service, service.enterLobby, currentUser.token, params["gameType"])

    const updateGameId = useCallback(async () => {
        if (lobbyId && !gameId) {
            console.log("calling entered game")
            const enteredGame = await service.enteredGame(currentUser.token, lobbyId)
            console.log(enteredGame)
            if (enteredGame.gameId)
                setGameId(enteredGame.gameId);
        }
    }, [lobbyId, gameId])

    useIntervalAsync(updateGameId, 3000)

    if (!enterLobby) {
        return (
            <div>
                ...loading...
            </div>
        )
    }

    console.log(enterLobby)

    if (enterLobby.kind == "success") {
        if (enterLobby.result.waitingForGame) {
            if (!lobbyId)
                setLobbyId(enterLobby.result.lobbyOrGameId)
            return (
                
                <div>{gameId != undefined ? <Navigate to={`${paths['play-game'].replace(":gameId", gameId.toString())}`}/> : "Waiting for game"}</div>
            )
        } else {
            console.log("didn't wait for game")
            return <Navigate to={`${paths['play-game'].replace(":gameId", enterLobby.result.lobbyOrGameId.toString())}`}/>
        }
    }
}