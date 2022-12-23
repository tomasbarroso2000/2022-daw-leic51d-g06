import * as React from 'react'
import {
    createBrowserRouter, 
    Link, 
    Navigate, 
    Outlet, 
    RouterProvider, 
    useParams,
    useRouteError
} from 'react-router-dom'
import { FakeService } from '../service/FakeService'
import { RealService } from '../service/RealService'
import { Home } from './Home'
import { Rankings } from './Rankings'
import { CreateUser } from './CreateUser'
import { Login } from './Login'
import { RequireAuthn } from './RequireAuthn'
import { Me } from './Me'
import { CreateGame } from './CreateGame'
//import { Lobby } from './Lobby'
import { DefineLayout } from './DefineLayout'
import { ListGames } from './ListGames'
import { AuthnContainer } from './Authn'
import { EnteredGame } from './Lobby'
import { PlayGame } from './PlayGame'
import { WaitForGame } from './WaitForGame'
import { MaybeSetUser } from './MaybeSet'

export const paths = {
    "home": "/",
    "rankings": "/rankings",
    "create-user": "/signup",
    "create-token": "/login",
    "me": "/me",
    "create-game": "/games/new",
    "wait-for-game": "/games/new/:gameType",
    "lobby":"/lobby",
    "define-layout": "/games/layout",
    "list-games": "/games/list",
    "play-game": "/games/play"
}

const router = createBrowserRouter([
    {
        "path": "/",
        "element": <AuthnContainer><Outlet /></AuthnContainer>,
        "children": [
            {
                "path": paths['home'],
                "element": <Home />
            },
            {
                "path": paths['rankings'],
                "element": <Rankings />
            },
            {
                "path": paths['create-user'],
                "element": <CreateUser />
            },
            {
                "path": paths['create-token'],
                "element": <Login />
            },
            {
                "path": paths['create-game'],
                "element": <RequireAuthn><CreateGame /></RequireAuthn>
            },
            {
                "path": paths['wait-for-game'],
                "element": <RequireAuthn><WaitForGame /></RequireAuthn>
            },
            {
                "path": paths['lobby'],
                "element": <RequireAuthn><EnteredGame/></RequireAuthn>
            },
            {
                "path": paths['me'],
                "element": <RequireAuthn><Me /></RequireAuthn>
            },
            {
                "path": paths['define-layout'],
                "element": <RequireAuthn><DefineLayout /></RequireAuthn>
            },
            {
                "path": paths['list-games'],
                "element": <RequireAuthn><ListGames /></RequireAuthn>
            },
            {
                "path": paths['play-game'],
                "element": <RequireAuthn><PlayGame /></RequireAuthn>
            },
            {
                "path": "/users/:uid/games/:gid",
                "element": <UserGameDetail />
            },
            {
                "path": "*",
                "element": <Navigate to="/"/>
            }
        ]
    }
])

export const service = new RealService()
//export const service = new FakeService()

export function App() {
    return (
        <RouterProvider router={router} />
    )
}

function UserDetail() {
    const {uid} = useParams()
    return (
        <div>
            <h2>User Detail</h2>
            {uid}
        </div>
    )
}

function UserGameDetail() {
    const {gid, uid} = useParams()
    return (
        <div>
            <h2>User Game Detail</h2>
            {uid}, {gid}
        </div>
    )
}
 