import * as React from 'react'
import { Dispatch, useEffect, useState } from 'react'
import { useCookies } from 'react-cookie'
import { Navigate, useLocation } from 'react-router-dom'
import { CurrentUser } from '../domain/CurrentUser'
import { UserHome } from '../domain/UserHome'
import { askService, Result } from '../service/askService'
import { service } from './App'
import { useCurrentUser, useSetUser } from './Authn'

export function RequireAuthn({ children }: { children: React.ReactNode }): React.ReactElement {
    console.log("running")
    const location = useLocation()
    const [cookies, setCookie, removeCookie] = useCookies();

    const currentUser = useCurrentUser()
    const setCurrentUser = useSetUser()

    const tokenInCookie = cookies["token"]

    const token = currentUser ? currentUser.token : tokenInCookie
    
    const [user, setUser] : [CurrentUser | undefined,  Dispatch<React.SetStateAction<CurrentUser>>]= useState(undefined)
    
    const userHome = askService(service, service.userHome, token)

    useEffect(() => {
        console.log("user: " + user)
        if (user && !currentUser)
            setCurrentUser(user)
    }, [user])

    if (!userHome) {
        return (
            <div>
                ...loading...
            </div>
        )
    }

    if (userHome.kind == "success") {
        setUser({token: token, name: userHome.result.name})
        return <>{children}</>
    } else {
        removeCookie("token")
        return <Navigate to="/login" state={{source: location.pathname}} replace={true}/>
    } 
}