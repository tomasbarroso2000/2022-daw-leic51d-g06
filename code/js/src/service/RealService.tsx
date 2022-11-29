import { Home } from "../domain/Home"
import { Rankings } from "../domain/Rankings"
import { useFetch } from "../fetch/useFetch"
import { paths } from "../router/App"
import { Service } from "./Service"
import { EmbeddedLink } from "siren-types"

const baseURL = "http://localhost:8080"
const homeURL = baseURL + "/api/"

export class RealService implements Service {
    homeNavigation = []
    rankingsNavigation = []
    rankingsLink: EmbeddedLink | undefined = undefined

    home = function (): Home | undefined {
        this.homeNavigation = []

        const content = useFetch(homeURL)
        console.log(content)

        if (!content) {
            return undefined
        }

        const jsonObj = JSON.parse(content)

        console.log(jsonObj)

        jsonObj.links.forEach((link: EmbeddedLink) => {
            const path = paths[link.rel[0]]
            if (path) {
                this.homeNavigation.push(path)
            }
        })

        this.rankingsLink = jsonObj.links.find((link: EmbeddedLink) => link.rel[0] == "rankings")
        console.log(this.rankingsLink)

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
            return this.rankingsLink.href
        }
        console.log(this.rankingsLink)
        return this.rankingsLink.href
    }

    rankings = function (): Rankings | undefined {
        const path = this.ensureRankingsLink()

        console.log(path)

        this.rankingsNavigation = []

        const content = useFetch(baseURL + path)

        if (!content) {
            return undefined
        }

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
