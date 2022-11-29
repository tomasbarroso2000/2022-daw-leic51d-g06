import { Home } from "../domain/Home";
import { Rankings } from "../domain/Rankings";

interface Service {
    home: () => Home | undefined
    rankings: () => Rankings | undefined

    homeNavigation: Array<string>
    rankingsNavigation: Array<string>
}