import * as React from "react"
import { Link, Navigate, useParams } from "react-router-dom"
import { GameType, GameTypes } from "../domain/GameTypes"
import { EnterLobby } from "../domain/Lobby"
import { askService, Result } from "../service/askService"
import { paths, service } from "./App"
import { useCurrentUser } from './Authn'

export function WaitForGame() {
    const currentUser = useCurrentUser()
    const params = useParams()
    const enterLobby: Result<EnterLobby> | undefined = 
        askService(service, service.enterLobby, currentUser.token, params["gameType"])

    console.log(params["gameType"])

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
            return (
                <div>Waiting for game</div>
            )
        } else {
            return <Navigate to={`${paths["play-game"]}/${enterLobby.result.lobbyOrGameId}`} state={{source: location.pathname}} replace={true}/>
        }
    }
}