import * as React from 'react'
import { createBrowserRouter, Navigate, Outlet, RouterProvider } from 'react-router-dom'
import { FakeService } from '../service/FakeService'
import { RealService } from '../service/RealService'
import { Home } from './Home'
import { Rankings } from './Rankings'
import { CreateUser } from './CreateUser'
import { Login } from './Login'
import { GetAuthn } from './GetAuthn'
import { Me } from './Me'
import { CreateGame } from './CreateGame'
import { ListGames } from './ListGames'
import { AuthnContainer } from './Authn'
import { PlayGame } from './PlayGame'
import { WaitForGame } from './WaitForGame'
import { useEffect, useState } from 'react'

export const paths = {
    "home": "/",
    "rankings": "/rankings",
    "create-user": "/signup",
    "create-token": "/login",
    "user-home": "/me",
    "game-types": "/games/new",
    "wait-for-game": "/games/new/:lobbyId",
    "games": "/games/list",
    "game": "/games/play/:gameId"
}

const router = createBrowserRouter([
    {
        "path": "/",
        "element": <AuthnContainer><Outlet /></AuthnContainer>,
        "children": [
            {
                "path": paths['home'],
                "element": <GetAuthn><Home /></GetAuthn>
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
                "path": paths['game-types'],
                "element": <GetAuthn required={false}><CreateGame /></GetAuthn>
            },
            {
                "path": paths['wait-for-game'],
                "element": <GetAuthn required><WaitForGame /></GetAuthn>
            },
            {
                "path": paths['user-home'],
                "element": <GetAuthn required><Me /></GetAuthn>
            },
            {
                "path": paths['games'],
                "element": <GetAuthn required><ListGames /></GetAuthn>
            },
            {
                "path": paths['game'],
                "element": <GetAuthn required><PlayGame /></GetAuthn>
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
    document.title = "Exploding Battleships"
    return (
        <RouterProvider router={router} />
    )
}
 