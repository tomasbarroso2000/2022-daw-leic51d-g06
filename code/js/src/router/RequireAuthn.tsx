import * as React from 'react'
import { useCookies } from 'react-cookie'
import { Navigate, useLocation } from 'react-router-dom'
import { ProblemJson } from '../domain/ProblemJson'
import { UserHome } from '../domain/UserHome'
import { UserInfo } from '../domain/UserInfo'
import { askService, Result } from '../service/askService'
import { service } from './App'
import { useCurrentUser, useSetUser } from './Authn'

export function RequireAuthn({ children }: { children: React.ReactNode }): React.ReactElement {
    const location = useLocation()
    const [cookies] = useCookies();

    const currentUser = useCurrentUser()
    const setUser = useSetUser()

    const tokenInCookie = cookies["token"]

    const token = currentUser != null ? currentUser.token : tokenInCookie
    
    const user: Result<UserHome> | undefined = askService(service, service.userHome, token)

    console.log(user)

    if (!user) {
        return (
            <div>
                ...loading...
            </div>
        )
    }

    console.log(user)

    if (user.kind == "success") {
        console.log(`info: ${token + ":" + user.result.name}`)
        setUser({token: tokenInCookie, name: user.result.name})
        return <>{children}</>
    } else {
        console.log("error")
        return <Navigate to="/login" state={{source: location.pathname}} replace={true}/>
    } 
}