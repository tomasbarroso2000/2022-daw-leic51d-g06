import { Home } from "../domain/Home"
import { Rankings } from "../domain/Rankings"
import { paths } from "../router/App"
import { Service } from "./Service"
import { EmbeddedLink } from "siren-types"
import { doFetch } from "./doFetch"
import { CreateUser } from "../domain/CreateUser"
import { Action } from "@remix-run/router"

const baseURL = "http://localhost:8080"
const homeURL = baseURL + "/api/"

export class RealService implements Service {
    homeNavigation = []
    rankingsNavigation = []
    rankingsLink: EmbeddedLink | undefined = undefined
    homeActions = []
    createUserAction: EmbeddedLink | undefined = undefined

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

        jsonObj.actions.forEach((action) => {
            const path = paths[action.name]
            if (path) {
                this.homeActions.push(path)
            }
        })

        this.createUserAction = jsonObj.actions.find((action) => action.name == "create-user")

        return {
            name: jsonObj.properties.name,
            authors: jsonObj.properties.authors,
            version: jsonObj.properties.verion
        }
        
    }

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

    ensureCreateUserAction = async function (): Promise<string | undefined> {
        if (this.createUserAction == undefined) {
            return this.home().then(() => this.createUserAction.href)
        }
        return this.createUserAction.href
    }

}
