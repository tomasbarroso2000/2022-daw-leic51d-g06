import { Home } from "../domain/Home"
import { Rankings } from "../domain/Rankings"
import { paths } from "../router/App"
import { Service } from "./Service"
import { EmbeddedLink, Action, Field } from "siren-types"
import { doFetch } from "./doFetch"
import { CreateUser, UserRequest } from "../domain/CreateUser"
import { CreateToken } from "../domain/CreateToken"

const baseURL = "http://localhost:8080"
const homeURL = baseURL + "/api/"

export class RealService implements Service {
    homeNavigation = []
    rankingsNavigation = []
    createUserNavigation = []
    rankingsLink: EmbeddedLink | undefined = undefined
    homeActions = []
    createUserAction: Action | undefined = undefined
    createTokenAction: Action | undefined = undefined

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

        this.rankingsLink = jsonObj.links.find((link: EmbeddedLink) => link.rel[0] == "rankings")

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

    createUser = async function (reqBody: UserRequest): Promise<CreateUser | undefined> {
        const path = await this.ensureCreateUserAction()
        if (!path)
            return undefined

        this.createUserNavigation = []

        const res = async () => {
            const resp = await fetch(baseURL + path, {
                method: 'POST',
                body: JSON.stringify({
                   name: reqBody.name,
                   email: reqBody.email,
                   password: reqBody.password,
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
}
