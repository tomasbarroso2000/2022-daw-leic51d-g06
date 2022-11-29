import { Home } from "../domain/Home";
import { Rankings } from "../domain/Rankings";

interface Service {
    home: () => Promise<Home | undefined>
    rankings: () => Promise<Rankings | undefined>

    homeNavigation: Array<string>
    rankingsNavigation: Array<string>
}