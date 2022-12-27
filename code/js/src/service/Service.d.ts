import { Home } from "../domain/Home";
import { Rankings } from "../domain/Rankings";
import { CreateUser, UserRequest } from "../domain/CreateUser";
import { CreateToken } from "../domain/CreateToken";
import { GameTypes } from "../domain/GameTypes";
import { EnterLobby, EnteredGame } from "../domain/Lobby";
import { Game } from "../domain/Game";
import { UserHome } from "../domain/UserHome";
import { GamesList } from "../domain/GamesList";
import { DefineLayout } from "../domain/DefineLayout";
import { LayoutShip } from "../domain/LayoutShip";
import { SendHits } from "../domain/SendHits";
import { Square } from "../domain/Square";

interface Service {

    home: () => Promise<Home | undefined>
    userHome: (token: string) => Promise<UserHome | undefined>
    rankings: (limit: number, skip: number) => Promise<Rankings | undefined>
    createUser: (name:string, email: string, password: string) => Promise<CreateUser | undefined>
    createToken: (email: string, password: string) => Promise<CreateToken |undefined>
    gameTypes: () => Promise<GameTypes | undefined>
    enterLobby: (token: string, gameType: string) => Promise<EnterLobby | undefined>
    enteredGame: (token: string, lobbyId: number) => Promise<EnteredGame | undefined>
    games: (token: string, limit: number, skip: number) => Promise<GamesList | undefined>
    gameInfo: (token: string, gameId: number) => Promise<Game | undefined> 
    defineLayout: (token: string, gameId: number, fleet: Array<LayoutShip>) => Promise<DefineLayout | undefined>
    sendHits: (token: string, gameId: number, squares: Array<Square>) => Promise<SendHits | undefined>
    
    homeNavigation: Array<string>
    userHomeNavigation: Array<string>
    rankingsNavigation: Array<string>
}