import * as React from "react"
import { UserHome } from "../domain/UserHome"
import { askService } from "../service/askService"
import { service } from "./App"
import { useCurrentUser } from './Authn'

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
        </div>
    )
}