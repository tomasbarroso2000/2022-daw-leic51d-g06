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
            <div>
                <div>{`Oh hi, ${userHome.result.name}`}</div>
                <div>{`Your score is ${userHome.result.score}`}</div>
                <div>
                    <Link to={paths['home']}>home</Link>
                </div>
            </div>
        )
    }    
}