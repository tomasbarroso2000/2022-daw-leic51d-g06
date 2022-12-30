import * as React from "react"
import { useCookies } from "react-cookie"
import { Link } from "react-router-dom"
import { Home } from "../domain/Home"
import { askService, Result } from "../service/askService"
import { paths, service } from "./App"
import { useCurrentUser, useSetUser } from "./Authn"
import { Loading } from "./Loading"

export function Home() {
    document.title = "Exploding Battleships"
    const currentUser = useCurrentUser()
    const setCurrentUser = useSetUser()
    const [cookies, setCookie, removeCookie] = useCookies(["token"]);
    const home: Result<Home> | undefined = askService(service, service.home)

    if (!home) {
        return <Loading />
    }

    if (home.kind == "success") {
        return (
            <div id="content">
                <div id="right-side-content">
                    <h1 id="title">{home.result.name}</h1>
                    <img src="images/battleship.png" alt="battleship" id="battleship" />
                    <div id="footer">
                        <div id="authors">
                            <h3>Authors:</h3>
                            <ul>
                                {home.result.authors.map((author: string) => <li key={author}>{author}</li>)}
                            </ul>
                        </div>
                        <div id="version">
                            <h4>Version: {home.result.version}</h4>
                        </div>
                    </div>

                </div>
                <div id="left-side-content">

                    <div id="menu">
                        <h2 id="menu-title">Menu</h2>
                        <div id="menu-nav">
                            {currentUser ? <button onClick={() => { setCurrentUser(undefined); removeCookie("token") }}>Sign Out</button> : undefined}
                            {currentUser ?
                                (service.homeNavigation.includes("user-home") ? <div><Link to={paths['user-home']}>{currentUser.name}</Link></div> : undefined) :
                                (service.homeNavigation.includes("create-token") ? <div><Link to={paths['create-token']}>Login</Link></div> : undefined)
                            }
                            {service.homeNavigation.includes("rankings") ? <div key={"rankings"}><Link to={paths["rankings"]}>Rankings</Link></div> : undefined}
                            {service.homeNavigation.includes("games") ? <div key={"games"}><Link to={paths["games"]}>Play</Link></div> : undefined}
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}