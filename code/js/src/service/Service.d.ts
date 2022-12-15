import { Home } from "../domain/Home";
import { Rankings } from "../domain/Rankings";
import { CreateUser, UserRequest } from "../domain/CreateUser";
import { CreateToken } from "../domain/CreateToken";
import { GameTypes } from "../domain/GameTypes";
import { GamesList } from "../domain/GamesList";
import { UserHome } from "../domain/UserHome";

interface Service {

    home: () => Promise<Home | undefined>
    userHome: (token: string) => Promise<UserHome | undefined>
    rankings: () => Promise<Rankings | undefined>
    createUser: (name:string, email: string, password: string) => Promise<CreateUser | undefined>
    createToken: (email: string, password: string) => Promise<CreateToken |undefined>
    gameTypes: () => Promise<GameTypes | undefined>
    games: () => Promise<GamesList | undefined>

    homeNavigation: Array<string>
    userHomeNavigation: Array<string>
    rankingsNavigation: Array<string>
}