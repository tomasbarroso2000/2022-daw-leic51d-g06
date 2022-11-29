import { Home } from "../domain/Home"
import { Rankings } from "../domain/Rankings"
import { Service } from "./Service"

export class FakeService implements Service {
    home = function home(): Home {
        return {
            name: "Exploding Battleships",
            version: "1.0.0",
            authors: ["Aleixo", "Tomasso", "Palmilha"]
        }
    }

    homeNavigation = ["/rankings"]

    rankings = function(): Rankings {
        return {
            rankings: [
                {
                    id: 1,
                    name: "Aleixo",
                    score: 10
                },
                {
                    id: 2,
                    name: "Tomasso",
                    score: 9
                },
                {
                    id: 3,
                    name: "Palmilha",
                    score: 0
                }
            ],
            hasMore: false
        }
    }

    rankingsNavigation: ["/quaqua"]
}