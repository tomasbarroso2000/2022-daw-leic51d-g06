export type EnterLobby = {
    waitingForGame: boolean,
    lobbyOrGameId: number
}

export type EnteredGame = {
    gameId: number | undefined
}