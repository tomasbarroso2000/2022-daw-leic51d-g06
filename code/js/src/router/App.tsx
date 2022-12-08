import * as React from 'react'
import {
    createBrowserRouter, 
    Link, 
    Navigate, 
    RouterProvider, 
    useParams,
    useRouteError
} from 'react-router-dom'
import { FakeService } from '../service/FakeService'
import { RealService } from '../service/RealService'
import { Home } from './Home'
import { Rankings } from './Rankings'
import { CreateUser } from './CreateUser'
import { CreateToken } from './CreateToken'
import { RequireAuthn } from './RequireAuthn'
import { Me } from './Me'

export const paths = {
    "home": "/",
    "rankings": "/rankings",
    "create-user": "/signup",
    "create-token": "/login",
    "me": "/me"
}

const router = createBrowserRouter(
    [
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
            "element": <CreateToken />
        },
        {
            "path": paths['me'],
            "element": <RequireAuthn><Me /></RequireAuthn>
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
)

export const service = new RealService()
//export const service = new FakeService()

export function App() {
    return (
        <RouterProvider router={router} />
    )
}

function Screen2() {
    return (
        <div>
            <h1>Screen 2</h1>
        </div>
    )
}

function Screen3() {
    return (
        <div>
            <h1>Screen 3</h1>
        </div>
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
 