import * as React from 'react'
import { useCookies } from 'react-cookie'
import { Navigate, useLocation } from 'react-router-dom'
import { UserHome } from '../domain/UserHome'
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

    if (!user) {
        return (
            <div>
                ...loading...
            </div>
        )
    }

    if (user.kind == "success") {
        console.log("success")
        if (!currentUser)
            setUser({token: tokenInCookie, name: user.result.name})
        return <>{children}</>
    } else {
        return <Navigate to="/login" state={{source: location.pathname}} replace={true}/>
    } 
}