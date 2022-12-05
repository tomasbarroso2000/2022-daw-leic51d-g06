import { Home } from "../domain/Home";
import { Rankings } from "../domain/Rankings";
import { CreateUser, UserRequest } from "../domain/CreateUser";

interface Service {
    home: () => Promise<Home | undefined>
    rankings: () => Promise<Rankings | undefined>
    createUser: (body: UserRequest) => Promise<CreateUser | undefined>

    homeNavigation: Array<string>
    rankingsNavigation: Array<string>
}