import * as React from 'react'
import {
    useState,
    createContext,
    useContext,
} from 'react'
import { Cookies, useCookies } from 'react-cookie'
import { CreateCookie } from '../domain/CreateToken';
import { CurrentUser } from '../domain/CurrentUser'
import { UserHome } from '../domain/UserHome';
import { askService } from '../service/askService';
import { service } from './App';

type ContextType = {
    user: CurrentUser | undefined,
    cookies: CreateCookie | undefined
    setUser: (v: CurrentUser | undefined) => void,
    setCookie: (name: string, value: any) => void,
}
const LoggedInContext = createContext<ContextType>({
    user: undefined,
    cookies: undefined,
    setUser: () => { },
    setCookie: () => { },
})

export function AuthnContainer({ children }: { children: React.ReactNode }) {
    const [cookies, setCookie] = useCookies(undefined);
    const [user, setUser] = useState(undefined)
    
    return (
        <LoggedInContext.Provider value = {{ user: user, cookies: cookies, setUser: setUser, setCookie: setCookie }}>
            {children}
        </LoggedInContext.Provider>
    )
}

export function useCurrentUser() {
    return useContext(LoggedInContext).user
}

export function useSetUser() {
    return useContext(LoggedInContext).setUser
}

export function useCurrentCookie() {
    return useContext(LoggedInContext).cookies
}

export function useSetCookie() {
    return useContext(LoggedInContext).setCookie
}