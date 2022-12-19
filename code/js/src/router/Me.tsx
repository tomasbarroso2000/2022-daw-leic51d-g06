import * as React from "react"
import { Link } from "react-router-dom"
import { UserHome } from "../domain/UserHome"
import { askService } from "../service/askService"
import { paths, service } from "./App"
import { useCurrentUser } from './Authn'
import { Login } from "./Login"

export function Me() {
    const currentUser = useCurrentUser()

    const userHome: UserHome | undefined = 
        askService(service, service.userHome, currentUser.token)
    
    if (!userHome) {
        return (
            <div>
                ...loading...
            </div>
        )
    }
    
    return (
        <div>
            <div>{`Oh hi, ${userHome.name}`}</div>
            <div>{`Your score is ${userHome.score}`}</div>
            <div>
                <Link to={paths['home']}>home</Link>
            </div>
        </div>
    )
}