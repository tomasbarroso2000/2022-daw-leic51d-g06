import { Home } from "../domain/Home";
import { Rankings } from "../domain/Rankings";
import { CreateUser, UserRequest } from "../domain/CreateUser";
import { CreateToken } from "../domain/CreateToken";
import { GameTypes } from "../domain/GameTypes";
import { EnterLobby, EnteredGame } from "../domain/Lobby";
import { Game, GamesList } from "../domain/Game";
import { UserHome } from "../domain/UserHome";

interface Service {

    home: () => Promise<Home | undefined>
    userHome: (token: string) => Promise<UserHome | undefined>
    rankings: () => Promise<Rankings | undefined>
    createUser: (name:string, email: string, password: string) => Promise<CreateUser | undefined>
    createToken: (email: string, password: string) => Promise<CreateToken |undefined>
    gameTypes: () => Promise<GameTypes | undefined>
    enterLobby: (token: string, gameType: string) => Promise<EnterLobby | undefined>
    enteredGame: (token: string, lobbyId: number) => Promise<EnteredGame | undefined>
    games: (token: string, limit: number, skip: number) => Promise<GamesList | undefined>
    gameInfo: (token: string, gameId: number) => Promise<Game | undefined> 

    homeNavigation: Array<string>
    userHomeNavigation: Array<string>
    rankingsNavigation: Array<string>
}