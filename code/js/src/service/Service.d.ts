import { Home } from "../domain/Home";
import { Rankings } from "../domain/Rankings";
import { CreateUser, UserRequest } from "../domain/CreateUser";
import { CreateToken } from "../domain/CreateToken";

interface Service {
    home: () => Promise<Home | undefined>
    rankings: () => Promise<Rankings | undefined>
    createUser: (name:string, email: string, password: string) => Promise<CreateUser | undefined>
    createToken: (email: string, password: string) => Promise<CreateToken |undefined>
    homeNavigation: Array<string>
    rankingsNavigation: Array<string>
}