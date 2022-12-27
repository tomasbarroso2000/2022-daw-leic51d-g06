import * as React from "react"
import { Link } from "react-router-dom"
import { UserHome } from "../domain/UserHome"
import { askService, Result } from "../service/askService"
import { paths, service } from "./App"
import { useCurrentUser } from './Authn'
import { Login } from "./Login"

export function Me() {
    const currentUser = useCurrentUser()

    const userHome: Result<UserHome> | undefined = askService(service, service.userHome, currentUser.token)
    
    if (!userHome) {
        return (
            <div>
                ...loading...
            </div>
        )
    }

    if (userHome.kind == "success") {
        return (
            <div id="content">
                <div id="user">
                    <span></span>
                    <h1>Hello There! <br /> {`Welcome back, ${userHome.result.name}.`}</h1>
                    <p>{`Overall Score`}</p>
                    <div id="score-circle">
                        <p>{userHome.result.score}</p>
                    </div>
                    <div id="footer">
                        <Link to={paths['home']} className="nav-user">Home</Link>
                        <Link to={paths['list-games']} className="nav-user">Play game</Link>
                    </div>
                </div>
            </div>
        )
    }    
}