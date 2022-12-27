import { Home } from "../domain/Home"
import { Rankings } from "../domain/Rankings"
import { Service } from "./Service"
import { EmbeddedLink, Action, Field, isEmbeddedLink } from "siren-types"
import { doFetch } from "../utils/doFetch"
import { CreateUser, UserRequest } from "../domain/CreateUser"
import { CreateToken } from "../domain/CreateToken"
import { GameType, GameTypes } from "../domain/GameTypes"
import { ShipType } from "../domain/ShipType"
import { Game } from "../domain/Game"
import { EnteredGame, EnterLobby } from "../domain/Lobby"
import { UserHome } from "../domain/UserHome"
import { UserInfo } from "../domain/UserInfo"
import { Ship } from "../domain/ship"
import { makeFleet, makeTypeFleet as makeTypeFleet, makeGameType, makeHitsOrMIsses, makeUserInfo } from "../utils/make"
import { paths } from "../router/App"   
import { Square, squareToString } from "../domain/Square"
import { GamesList } from "../domain/GamesList"
import { LayoutShip } from "../domain/LayoutShip"
import { DefineLayout } from "../domain/DefineLayout"
import { SendHits } from "../domain/SendHits"
import { Forfeit } from "../domain/Forfeit"
import { json } from "react-router-dom"

const baseURL = "http://localhost:8083"
const homeURL = baseURL + "/api/"

export class RealService implements Service {
    homeNavigation = []
    userHomeNavigation: []
    rankingsNavigation = []
    gamesNavigation = []
    createUserNavigation = []

    homeActions = []
    userHomeActions = []

    userHomeLink: EmbeddedLink | undefined = undefined
    rankingsLink: EmbeddedLink | undefined = undefined
    gamesLink: EmbeddedLink | undefined = undefined
    gameInfoLink: EmbeddedLink | undefined = undefined
    
    createUserAction: Action | undefined = undefined
    createTokenAction: Action | undefined = undefined
    enterLobbyAction: Action | undefined = undefined
    defineLayoutAction: Action | undefined = undefined
    sendHitsAction: Action | undefined = undefined
    forfeitAction: Action | undefined = undefined

    /**
     * HOME
     */

    home = async function (): Promise<Home | undefined> {
        this.homeNavigation = []
        this.homeActions = []

        const res = await doFetch(homeURL)

        if (!res) {
            return undefined
        }

        const jsonObj = JSON.parse(res)

        jsonObj.links.forEach((link: EmbeddedLink) => {
            const path = paths[link.rel[0]]
            if (path) {
                this.homeNavigation.push(path)
            }
        })

        this.userHomeLink = jsonObj.links.find((link: EmbeddedLink) => link.rel[0] == "user-home")
        this.rankingsLink = jsonObj.links.find((link: EmbeddedLink) => link.rel[0] == "rankings")
        this.gamesLink = jsonObj.links.find((link: EmbeddedLink) => link.rel[0] == "games")

        jsonObj.actions.forEach((action: Action) => {
            const path = paths[action.name]
            if (path) {
                this.homeActions.push(path)
            }
        })

        this.createUserAction = jsonObj.actions.find((action: Action) => action.name == "create-user")

        this.createTokenAction = jsonObj.actions.find((action: Action) => action.name == "create-token")

        return {
            name: jsonObj.properties.name,
            authors: jsonObj.properties.authors,
            version: jsonObj.properties.version
        }
        
    }

    /**
     * USER HOME
     */

    ensureUserHomeLink = async function (): Promise<string | undefined> {
        if (this.userHomeLink == undefined) {
            return this.home().then(() => this.userHomeLink.href)
        }
        return this.userHomeLink.href
    }

    userHome = async function (token: string): Promise<UserHome | undefined> {
        const path = await this.ensureUserHomeLink()
        
        if (!path)
            return undefined

        this.userHomeNavigation = []

        const res = await doFetch(baseURL + path, { "token": token })

        if (!res) {
            return undefined
        }

        const jsonObj = JSON.parse(res)

        jsonObj.links.forEach((link: EmbeddedLink) => {
            const path = paths[link.rel[0]]
            if (path) {
                this.userHomeNavigation.push(path)
            }
        })

        jsonObj.actions.forEach((action: Action) => {
            const path = paths[action.name]
            if (path) {
                this.userHomeActions.push(path)
            }
        })

        this.enterLobbyAction = jsonObj.actions.find((action: Action) => action.name == "enter-lobby")

        return {
            id: jsonObj.properties.id,
            name: jsonObj.properties.name,
            email: jsonObj.properties.email,
            score: jsonObj.properties.score
        }
    }

    /**
     * RANKINGS
     */

    ensureRankingsLink = async function (): Promise<string | undefined> {
        if (this.rankingsLink == undefined) {
            return this.home().then(() => this.rankingsLink.href)
        }
        return this.rankingsLink.href
    }

    rankings = async function (limit: number, skip: number): Promise<Rankings | undefined> {
        const path = await this.ensureRankingsLink()

        if (!path)
            return undefined

        this.rankingsNavigation = []

        const res = await doFetch(baseURL + path + `?limit=${limit}&skip=${skip}`)

        if (!res) {
            return undefined
        }

        const jsonObj = JSON.parse(res)

        jsonObj.links.forEach((link: EmbeddedLink) => {
            const path = paths[link.rel[0]]
            if (path) {
                this.rankingsNavigation.push(path)
            }
        })

        return {
            rankings: jsonObj.properties["rankings"],
            hasMore: jsonObj.properties["has-more"]
        }
    }

    /**
     * CREATE USER
     */

    ensureCreateUserAction = async function (): Promise<string | undefined> {
        if (this.createUserAction == undefined) {
            return this.home().then(() => this.createUserAction.href)
        }
        return this.createUserAction.href
    }

    getCreateUserFields = async function (): Promise<Array<Field> | undefined> {
        if (this.createUserAction == undefined) {
            return this.home().then(() => this.createUserAction.fields)
        } else {
            return this.createUserAction.fields
        }
    }

    createUser = async function (name:string, email: string, password: string): Promise<CreateUser | undefined> {
        const path = await this.ensureCreateUserAction()
        if (!path)
            return undefined

        this.createUserNavigation = []

        const res = await doFetch(baseURL + path, {
            method: 'POST',
            body: {
                "name": name,
                "email": email,
                "password": password,
            }
        })

        if (!res) {
            return undefined
        }

        const jsonObj = JSON.parse(res)

        //user creation actions and links
        return {
            id: jsonObj.properties.id
        }
    }

    /**
     *  LOGIN
     */

     ensureCreateTokenAction = async function (): Promise<string | undefined> {
        if (this.createTokenAction == undefined) {
            return this.home().then(() => this.createTokenAction.href)
        }   
        return this.createTokenAction.href
    }

    getCreateTokenFields = async function (): Promise<Array<Field> | undefined> {
        if (this.createTokenAction == undefined) {
            return this.home().then(() => this.createTokenAction.fields)
        } else {
            return this.createTokenAction.fields
        }
    }

    // needs to be dynamic
    createToken = async function (email: string, password: string): Promise<CreateToken | undefined> {
        const path = await this.ensureCreateTokenAction()
        if (!path)
            return undefined

        this.createTokenNavigation = []

        const res = await doFetch(baseURL + path, {
            method: 'POST',
            body: {
                "email": email,
                "password": password
                },
            headers: {
                'Content-type': 'application/json',
            }
        })

        if (!res) {
            return undefined
        }

        const jsonObj = JSON.parse(res)

        //user creation actions and links
        return {
            token: jsonObj.properties.token
        }
    }

    /**
     * CREATE GAME
     */

    gameTypes = async function (): Promise<GameTypes | undefined> {
        const res = await doFetch(baseURL + "/api/games/types")

        if (!res) {
            return undefined
        }

        const jsonObj = JSON.parse(res)
        const listGameTypes: Array<GameType> = 
        jsonObj.properties["game-types"].map(gameType => {
            return makeGameType(
                gameType.name,
                gameType["board-size"],
                gameType["shots-per-round"],
                gameType["layout-def-time-in-secs"],
                gameType["shooting-time-in-secs"],
                makeTypeFleet(gameType.fleet)
            )
        })

        return {
            gameTypes: listGameTypes 
        }
    }

    /**
     * LOBBY
     */

    ensureEnterLobbyAction = async function (token: string): Promise<string | undefined> {
        if (this.enterLobbyAction == undefined) {
            return this.userHome(token).then(() => this.enterLobbyAction.href)
        }
        return this.enterLobbyAction.href
    }

    enterLobby = async function(token: string, gameType: string) : Promise<EnterLobby | undefined> {
        const path = await this.ensureEnterLobbyAction()

        if (!path)
            return undefined

        const res = await doFetch(baseURL + path, {
            method: 'POST',
            body: {
                "game-type": gameType
            },
            token: token
        })

        const jsonObj = JSON.parse(res)

        console.log("jsonObj")
        console.log(jsonObj)

        return {
            waitingForGame: jsonObj.properties["waiting-for-game"],
            lobbyOrGameId: jsonObj.properties["lobby-or-game-id"]
        }
    }

    enteredGame = async function(token: string, lobbyId: number) : Promise<EnteredGame | undefined> {
        const res = await doFetch(
            baseURL + '/api/lobby/' + lobbyId, 
            {   
                method: 'DELETE',
                token: token
            }
        )

        if (!res) {
            return undefined
        }

        const jsonObj = JSON.parse(res)

        return {
            gameId: jsonObj.properties["game-id"]
        }
    }

    /**
     *  AVALABLE GAMES
     */
    ensureGamesLink = async function (): Promise<string | undefined> {
        if (this.gamesLink == undefined) {
            return this.home().then(() => this.gamesLink.href)
        }
        
        return this.gamesLink.href
    }

    games = async function (token: string, limit: number, skip: number): Promise<GamesList | undefined> {
        
        const path = await this.ensureGamesLink()

        if (!path)
            return undefined

        console.log("gamesLink: " + path)
        
        this.gamesNavigation = []

        const res = await doFetch(
                baseURL + path + `?limit=${limit}&skip=${skip}`, 
                { token: token }
            )

        if (!res) {
            return undefined
        }

        const jsonObj = JSON.parse(res)

        jsonObj.links.forEach((link: EmbeddedLink) => {
            const path = paths[link.rel[0]]
            if (path) {
                this.gamesNavigation.push(path)
            }
        })

        this.gameInfoLink = jsonObj.links.find((link: EmbeddedLink) => link.rel[0] == "game")

        return {
            games: jsonObj.properties.games,
            hasMore: jsonObj.properties["has-more"]
        }
    }

    gameInfo = async function (token: string, gameId: number): Promise<Game | undefined> {
        let path = `/api/games/info/${gameId}` // improve

        if (!path)
            return undefined
        
        const res = await doFetch(
            baseURL + path, 
            { token: token }
        )

        if (!res) {
            return undefined
        }

        const jsonObj = JSON.parse(res)

        this.defineLayoutAction = jsonObj.actions.find((action: Action) => action.name == "define-layout")
        this.sendHitsAction = jsonObj.actions.find((action: Action) => action.name == "send-hits")
        this.forfeitAction = jsonObj.actions.find((action: Action) => action.name == "forfeit")

        const fleetTypes: Array<ShipType> = makeTypeFleet(jsonObj.properties.type["fleet"])
        
        const gameType: GameType = makeGameType(
            jsonObj.properties.type["name"],
            jsonObj.properties.type["board-size"],
            jsonObj.properties.type["shots-per-round"],
            jsonObj.properties.type["layout-def-time-in-secs"],
            jsonObj.properties.type["shooting-time-in-secs"],
            fleetTypes
        )

        const opponent: UserInfo = makeUserInfo(jsonObj.properties.opponent.id, jsonObj.properties.opponent.name, jsonObj.properties.opponent.score)
        const fleet: Array<Ship> = makeFleet(jsonObj.properties.fleet)
        const takenHits: Array<Square> = makeHitsOrMIsses(jsonObj.properties["taken-hits"])
        const enemySunkFleet: Array<Ship> = makeFleet(jsonObj.properties["enemy-sunk-fleet"])
        const hits: Array<Square> = makeHitsOrMIsses(jsonObj.properties.hits)
        const misses: Array<Square> = makeHitsOrMIsses(jsonObj.properties.misses)

        return {
            id: jsonObj.properties.id,
            type: gameType,
            state: jsonObj.properties.state,
            opponent: opponent,
            playing: jsonObj.properties.playing,
            startedAt: jsonObj.properties["started-at"],
            fleet: fleet,
            takenHits: takenHits,
            enemySunkFleet: enemySunkFleet,
            hits: hits,
            misses: misses
        }
    }

    /**
     *  DEFINE LAYOUT
     */
    ensureDefineLayoutAction = async function (token: string, gameId: number): Promise<Action | undefined> {
        if (this.defineLayoutAction == undefined) {
            return this.gameInfo(token, gameId).then(() => this.defineLayoutAction)
        }
        
        return this.defineLayoutAction
    }

    defineLayout = async function (token: string, gameId: number, fleet: Array<LayoutShip>): Promise<DefineLayout | undefined> {
        const action: Action = await this.ensureDefineLayoutAction(token, gameId)

        if (!action)
            return undefined

        const shipsArray = fleet.map((layoutShip) => {
            return {
                "name": layoutShip.type.name,
                "first-square": layoutShip.position,
                "orientation": layoutShip.orientation
            }
        })

        const res = await doFetch(
                baseURL + action.href, 
                { 
                    token: token,
                    method: action.method,
                    body: {
                        "game-id": gameId,
                        ships: shipsArray
                    }
                }
            )

        if (!res) {
            return undefined
        }

        const jsonObj = JSON.parse(res)

        return {
            status: jsonObj.properties.status
        }
    }

    /**
     * SEND HITS
     */
    ensureSendHitsAction = async function (token: string, gameId: number): Promise<Action | undefined> {
        if (this.sendHitsAction == undefined) {
            return this.gameInfo(token, gameId).then(() => this.sendHitsAction)
        }
        
        return this.sendHitsAction
    }

    sendHits = async function (token: string, gameId: number, squares: Array<Square>): Promise<SendHits | undefined> {
        const action: Action = await this.ensureSendHitsAction(token, gameId)

        if (!action)
            return undefined

        const res = await doFetch(
                baseURL + action.href, 
                { 
                    token: token,
                    method: action.method,
                    body: {
                        "game-id": gameId,
                        squares: squares
                    }
                }
            )

        if (!res) {
            return undefined
        }

        const jsonObj = JSON.parse(res)

        return {
            hitsOutcome: jsonObj.properties["hits-outcome"],
            win: jsonObj.properties.win
        }
    }

    /**
     * FORFEIT
     */

    ensureForfeitAction = async function (token: string, gameId: number): Promise<Action | undefined> {
        if (this.forfeitAction == undefined) {
            return this.gameInfo(token, gameId).then(() => this.forfeitAction)
        }
        
        return this.forfeitAction
    }

    forfeit = async function (token: string, gameId: number): Promise<Forfeit | undefined> {
        const action: Action = await this.ensureForfeitAction(token, gameId)

        const res = await doFetch(
            baseURL + action.href, 
            { 
                token: token,
                method: action.method,
                body: {
                    "game-id": gameId
                }
            }
        )

         if (!res) {
            return undefined
        }

        const jsonObj = JSON.parse(res)
        return {
            state: jsonObj.properties.state
        }
    }
}   
