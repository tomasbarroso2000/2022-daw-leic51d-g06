import * as React from "react"
import { useCookies } from "react-cookie"
import { Link } from "react-router-dom"
import { Home } from "../domain/Home"
import { askService, Result } from "../service/askService"
import { paths, service } from "./App"
import { useCurrentUser, useSetUser } from "./Authn"
import { Loading } from "./Loading"

export function Home() {
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
                        <ul>
                            {currentUser ? 
                                <li>
                                    <Link to={paths['user-home']}>{currentUser.name}</Link>
                                    <button onClick={() => {setCurrentUser(undefined); removeCookie("token")}}>Sign Out</button>
                                </li> : 
                                <li><Link to={paths['create-token']}>Sign In</Link></li>
                            }
                            {service.homeNavigation.map((nav) => 
                                <li key={nav}> 
                                    <Link to={nav}>{nav.slice(1)}</Link>
                                </li>
                            )}
                            <li>
                                <Link to={paths['games']}>play</Link>
                            </li>
                        </ul> 
                    </div>
                </div>
            </div>
        )
    }
}