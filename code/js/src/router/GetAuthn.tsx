import * as React from 'react'
import { Dispatch, useEffect, useState } from 'react'
import { useCookies } from 'react-cookie'
import { Navigate, useLocation } from 'react-router-dom'
import { UserHome } from '../domain/UserHome'
import { service } from './App'
import { useCurrentUser, useSetUser } from './Authn'
import { Loading } from './Loading'

type Authentication = "error" | "success" | undefined

export type GetAuthnProps = {
    required?: boolean,
    children: React.ReactNode
}

export function GetAuthn(props: GetAuthnProps): React.ReactElement {
    const location = useLocation()
    const [cookies, setCookie, removeCookie] = useCookies(["token"]);
    const currentUser = useCurrentUser()
    const setCurrentUser = useSetUser()

    const [authentication, setAuthentication]: [Authentication, Dispatch<Authentication>] = useState(undefined)

    const tokenInCookie = cookies.token

    const token = currentUser ? currentUser.token : tokenInCookie

    useEffect(() => {
        service.userHome(token)
            .then((userHome: UserHome) => {
                if (!currentUser)
                    setCurrentUser({ token: token, name: userHome.name })
                setAuthentication("success")
            })
            .catch(() => {
                setAuthentication("error")
            })
    }, [])

    if (!authentication) {
        return <Loading />
    }

    if (authentication == "success" || !props.required) {
        return <>{props.children}</>
    } else {
        if (tokenInCookie)
            removeCookie("token")
        return <Navigate to="/login" state={{ source: location.pathname }} replace={true} />
    }
}