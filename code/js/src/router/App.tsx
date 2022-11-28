import * as React from 'react'
import {
    createBrowserRouter, Link, RouterProvider, useParams,
} from 'react-router-dom'
import { UserInfo } from '../domain/UserInfo'
import { useFetch } from '../fetch/useFetch'

export const baseURL = "http://localhost:8080/api/"

const router = createBrowserRouter([
    {
        "path": "/",
        "element": <Home />
    },
    {
        "path": "/rankings",
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
    }
])

export function App() {
    return (
        <RouterProvider router={router} />
    )
}

function Home() {
    const content = JSON.stringify(useFetch(baseURL))

    if (!content) {
        return (
            <div>
                ...loading...
            </div>
        )
    }
    const contentJson = JSON.parse(content)

    return (
        <div id="content">
            <div id="right-side-content">
                <h1 id="title">{contentJson.properties.name}</h1>
                <img src="images/battleship.png" alt="battleship" id="battleship" />
                <div id="footer">
                    <div id="authors">
                        <h3>Authors:</h3>
                        <ul>
                            {contentJson.properties.authors.map((author: string) => <li key={author}>{author}</li>)}
                        </ul>
                    </div>
                    <div id="version">
                        <h4>Version: {contentJson.properties.version}</h4>
                    </div>
                </div>
                
            </div>
            <div id="left-side-content">
                <div id="menu">
                    <h2 id="menu-title">Menu</h2>
                    <ol>
                        {contentJson.links.map((link: {rel: Array<string>, href: string}) => 
                            <li key={link.rel[0]}> 
                                <Link to={link.href}>{link.rel[0]}</Link>
                            </li>
                        )}
                    </ol> 
                </div>
            </div>
            
        </div>
    )
}

function Rankings() {
    const content = JSON.stringify(useFetch(baseURL + "users/rankings/"))
    let rank = 0

    if (!content) {
        return (
            <div>
                ...loading...
            </div>
        )
    }

    const contentJson = JSON.parse(content)
    console.log(contentJson)

    return (
        <div id="content-rankings">
            <h1 id="rankings-title">Rankings</h1>
            <ul>
                <li>
                    <dl>
                        <dt>Rank - Name: </dt><dd>Score: </dd>
                    </dl>
                </li>
            {contentJson.properties.rankings.map((user: UserInfo) => 
                <li key={user.id}>
                    <dl>
                        <dt id="rankings-player">{++rank} - {user.name}</dt><span></span><dd>{user.score}</dd>
                    </dl>
                </li>
            )}
            </ul>
        </div>
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
 