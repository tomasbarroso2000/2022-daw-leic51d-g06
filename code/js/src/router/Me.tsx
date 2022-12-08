import * as React from "react"
import { useCurrentUser } from './Authn'

export function Me() {
    const currentUser = useCurrentUser()
    return (
        <div>
            {`Hello ${currentUser}`}
        </div>
    )
}