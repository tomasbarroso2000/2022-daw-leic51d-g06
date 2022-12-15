import { Home } from "../domain/Home"
import { Rankings } from "../domain/Rankings"
import { paths } from "../router/App"
import { Service } from "./Service"
import { EmbeddedLink, Action, Field, isEmbeddedLink } from "siren-types"
import { doFetch } from "./doFetch"
import { CreateUser, UserRequest } from "../domain/CreateUser"
import { CreateToken } from "../domain/CreateToken"
import { GameType, GameTypes } from "../domain/GameTypes"
import { ShipType } from "../domain/ShipType"
import { GamesList } from "../domain/GamesList"
import { EnteredGame, EnterLobby } from "../domain/Lobby"
import { UserHome } from "../domain/UserHome"

const baseURL = "http://localhost:8080"
const homeURL = baseURL + "/api/"

//temporary
const token = "0nBHDqo71pqby0UDawaddIdDWl44KUP4QChJaYCs5ns="

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
    
    createUserAction: Action | undefined = undefined
    createTokenAction: Action | undefined = undefined
    enterLobbyAction: Action | undefined = undefined

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

        console.log("in user home")

        if (!path)
            return undefined

        this.userHomeNavigation = []

        const res = await doFetch(baseURL + path, { token: token })

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

    rankings = async function (): Promise<Rankings | undefined> {
        const path = await this.ensureRankingsLink()

        if (!path)
            return undefined

        this.rankingsNavigation = []

        const res = await doFetch(baseURL + path)

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
            rankings: jsonObj.properties.rankings,
            hasMore: jsonObj.properties.hasMore
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

        const res = async () => {
            const resp = await fetch(baseURL + path, {
                method: 'POST',
                body: JSON.stringify({
                    name: name,
                    email: email,
                    password: password,
                }),
                headers: {
                   'Content-type': 'application/json',
                },
             })
             const body = await resp.json()
             return JSON.stringify(body)
        }

        const resp = await res()
        if (!resp) {
            return undefined
        }

        const jsonObj = JSON.parse(resp)

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

        const res = async () => {
            const resp = await fetch(baseURL + path, {
                method: 'POST',
                body: JSON.stringify({
                   email: email,
                   password: password,
                }),
                headers: {
                   'Content-type': 'application/json',
                },
             })
             const body = await resp.json()
             return JSON.stringify(body)
        }
        const resp = await res()
        if (!resp) {
            return undefined
        }

        const jsonObj = JSON.parse(resp)

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
        //console.log("res: " + JSON.stringify(jsonObj))
        const fleet: Array<ShipType> = []
        const listGameTypes: Array<GameType> = []
        jsonObj.properties["game-types"].forEach(gameType => {
            gameType.fleet.forEach(ship => {
                fleet.push ({
                     name: ship.name,
                     size: ship.size,
                     gameType: ship["game-type"]
                 })
             })
            listGameTypes.push(
                {
                    name: gameType.name,
                    boardSize: gameType["board-size"],
                    shotsPerRound: gameType["shots-per-round"],
                    layoutDefTime: gameType["layout-def-time-in-secs"],
                    shootingTime: gameType["shooting-time-in-secs"],
                    fleet: fleet
                }
            )
        })
        return {
            gameTypes: listGameTypes 
        }
    }

    /**
     * LOBBY
     */

    enterLobby = async function(gameType: string) : Promise<EnterLobby | undefined> {
        const path = await this.ensureEnterLobbyAction()
        if (!path)
            return undefined

        const res = async () => {
            const resp = await fetch(baseURL + path, {
                method: 'POST',
                body: JSON.stringify({
                   gameType: gameType
                }),
                headers: {
                   'Content-type': 'application/json',
                },
             })
             const body = await resp.json()
             return JSON.stringify(body)
        }
        const resp = await res()
        if (!resp) {
            return undefined
        }

        const jsonObj = JSON.parse(resp)

        return {
            waitingForGame: jsonObj.properties.waitingForGame,
            lobbyOrGameId: jsonObj.properties.lobbyOrGameId
        }
    }

    enteredGame = async function(lobbyId: number) : Promise<EnteredGame | undefined> {
        const res = async () => {
            const resp = await fetch(baseURL + 'lobby/' + lobbyId, {
                method: 'DELETE',
                headers: {
                   'Content-type': 'application/json',
                },
             })
             const body = await resp.json()
             return JSON.stringify(body)
        }
        const resp = await res()
        if (!resp) {
            return undefined
        }

        const jsonObj = JSON.parse(resp)

        return {
            gameId: jsonObj.properties.gameId
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

    games = async function (): Promise<GamesList | undefined> {
        
        const path = await this.ensureGamesLink()

        if (!path)
            return undefined


        console.log("gamesLink: " + path)
        
        this.gamesNavigation = []

        const res = async () => {
            console.log("here")
            const resp = await fetch(baseURL + path, {
                method: 'GET',
                headers: {
                    'Content-type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
             })
             const body = await resp.json()
             console.log("resp: " + body)
             return JSON.stringify(body)
        }

        const resp = await res()
        if (!resp) {
            return undefined
        }

        const jsonObj = JSON.parse(resp)

        console.log("jsonObj: " + jsonObj)

        jsonObj.links.forEach((link: EmbeddedLink) => {
            const path = paths[link.rel[0]]
            if (path) {
                this.rankingsNavigation.push(path)
            }
        })

        return {
            games: jsonObj.properties.games,
            hasMore: jsonObj.properties.hasMore
        }
    }
}
