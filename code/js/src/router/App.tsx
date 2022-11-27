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
        <div>
            <h1>{contentJson.properties.name}</h1>
            <p>Authors:</p>
            <ul>
                {contentJson.properties.authors.map((author: string) => <li key={author}>{author}</li>)}
            </ul>
            <p>Version: {contentJson.properties.version}</p>
            <ol>
                {contentJson.links.map((link: {rel: Array<string>, href: string}) => 
                    <li key={link.rel[0]}> 
                        <Link to={link.href}>{link.rel[0]}</Link>
                    </li>
                )}
            </ol> 
        </div>
    )
}

function Rankings() {
    const content = JSON.stringify(useFetch(baseURL + "users/rankings/"))

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
        <div>
            <h1>Rankings</h1>
            <ul>
            {contentJson.properties.rankings.map((user: UserInfo) => 
                <li key={user.id}>
                    <dl>
                        <dt>Name: </dt><dd>{user.name}</dd>
                        <dt>Score: </dt><dd>{user.score}</dd>
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
 