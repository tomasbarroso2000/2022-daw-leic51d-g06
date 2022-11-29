import * as React from 'react'
import {
    createBrowserRouter, 
    Link, 
    Navigate, 
    RouterProvider, 
    useParams
} from 'react-router-dom'
import { FakeService } from '../service/FakeService'
import { RealService } from '../service/RealService'
import { Home } from './Home'
import { Rankings } from './Rankings'

export const paths = {
    "home": "/",
    "rankings": "/rankings",
    "create-user": "/signup",
    "create-token": "/signin"
}

const router = createBrowserRouter(
    [
        {
            "path": paths.home,
            "element": <Home />
        },
        {
            "path": paths.rankings,
            "element": <Rankings />
        },
        {
            "path": "/path2",
            "element": <Screen2 />
        },
        {
            "path": "/path3",
            "element": <Screen3 />
        },
        {
            "path": "/users/:uid",
            "element": <UserDetail />
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
 