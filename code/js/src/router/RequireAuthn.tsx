import * as React from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import { UserHome } from '../domain/UserHome'
import { askService } from '../service/askService'
import { service } from './App'
import { useCurrentCookie, useCurrentUser, useSetUser } from './Authn'

export function RequireAuthn({ children }: { children: React.ReactNode }): React.ReactElement {
    const currentUser = useCurrentUser()
    const location = useLocation()
    const currentCookie = useCurrentCookie()
    const setUser = useSetUser()

    if(!currentUser && currentCookie) {
        const user: UserHome | undefined = askService(service, service.userHome, currentCookie.token)
        
        if (user) {
            console.log(`info: ${currentCookie.token + ":" + user.name}`)
            setUser({token: currentCookie.token, name: user.name})
        }
    }

    if (currentUser) {
        return <>{children}</>
    } else {
        console.log("redirecting to login")
        return <Navigate to="/login" state={{source: location.pathname}} replace={true}/>
    }

}