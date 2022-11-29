import { Home } from "../domain/Home"
import { Rankings } from "../domain/Rankings"
import { useFetch } from "../fetch/useFetch"
import { paths } from "../router/App"
import { Service } from "./Service"
import { EmbeddedLink } from "siren-types"
import { doFetch } from "../fetch/doFetch"
import SirenClient from '@siren-js/client'

const baseURL = "http://localhost:8080"
const homeURL = baseURL + "/api/"

export class RealService implements Service {
    client = new SirenClient()

    homeNavigation = []
    rankingsNavigation = []
    rankingsLink: EmbeddedLink | undefined = undefined

    home = function (): Home | undefined {
        this.homeNavigation = []
        const [content, loading] = useFetch(homeURL)

        if (!content || loading) {
            return undefined
        }

        const jsonObj = JSON.parse(content)

        jsonObj.links.forEach((link: EmbeddedLink) => {
            const path = paths[link.rel[0]]
            if (path) {
                this.homeNavigation.push(path)
            }
        })

        this.rankingsLink = jsonObj.links.find((link: EmbeddedLink) => link.rel[0] == "rankings")

        jsonObj.actions.forEach((action) => {
            const path = paths[action.rel]
            if (path) {
                this.homeNavigation.push(path)
            }
        })

        return {
            name: jsonObj.properties.name,
            authors: jsonObj.properties.authors,
            version: jsonObj.properties.verion
        }
    }

    ensureRankingsLink = function (): string | undefined {
        if (this.rankingsLink == undefined) {
            console.log("in if")
            const home = this.home()
            if (!home) {
                console.log("in inner if")
                return undefined
            }
            console.log("in inner else")
            return this.rankingsLink.href
        }
        console.log("in else")
        return this.rankingsLink.href
    }

    rankings = function (): Rankings | undefined {
        const path = this.ensureRankingsLink()

        if (!path)
            return undefined

        console.log(baseURL + path)

        this.rankingsNavigation = []

        const [content, loading] = useFetch(baseURL + path)

        if (!content || loading) {
            return undefined
        }

        console.log(content)

        const jsonObj = JSON.parse(content)

        console.log(jsonObj)

        jsonObj.links.forEach((link: EmbeddedLink) => {
            const path = paths[link.rel[0]]
            if (path) {
                this.rankingsNavigation.push(path)
            }
        })

        jsonObj.actions.forEach((action) => {
            const path = paths[action.rel]
            if (path) {
                this.rankingsNavigation.push(path)
            }
        })

        return {
            rankings: jsonObj.properties.rankings,
            hasMore: jsonObj.properties.hasMore
        }
    }
}
