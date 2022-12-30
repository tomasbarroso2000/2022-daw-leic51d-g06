import * as React from 'react'
import { useState, createContext, useContext } from 'react'
import { CurrentUser } from '../domain/CurrentUser'

type ContextType = {
    user: CurrentUser | undefined
    setUser: (v: CurrentUser | undefined) => void
}
const LoggedInContext = createContext<ContextType>({
    user: undefined,
    setUser: () => { },
})

export function AuthnContainer({ children }: { children: React.ReactNode }) {
    const [user, setUser] = useState(undefined)

    return (
        <LoggedInContext.Provider value={{ user: user, setUser: setUser }}>
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