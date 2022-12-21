import * as React from "react";
import { useState } from "react";
import { useLocation, Navigate } from "react-router-dom";
import { Field } from "siren-types";
import { CurrentUser } from "../domain/CurrentUser";
import { askService } from "../service/askService"
import { service } from "./App"
import { useSetCookie, useSetUser } from "./Authn"
import { Cookies, useCookies } from "react-cookie"; // npm install react-cookie
 
export function Login() {
    const fields: Array<Field> | undefined = askService(service, service.getCreateTokenFields)

    const [inputs, setInputs]: [any, React.Dispatch<React.SetStateAction<{}>>] = useState({})
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [error, setError] = useState(undefined)
    const [redirect, setRedirect] = useState(false)
    const setUser = useSetUser()
    const setCookie = useSetCookie()
    const location = useLocation()
    //const [cookies, setCookie] = useCookies(['token']);

    if (!fields) {
        return (
            <div>
                ...loading...
            </div>
        )
    }

    if (redirect) {
        return <Navigate to={location.state?.source?.pathname || "/me"} replace={true}/>
    }

    function handleChange(ev: React.FormEvent<HTMLInputElement>) {
        const name = ev.currentTarget.name
        setInputs({ ...inputs, [name]: ev.currentTarget.value })
        setError(undefined)
    }

    function handleSubmit(ev: React.FormEvent<HTMLFormElement>) {
        ev.preventDefault()
        setIsSubmitting(true)
        service.createToken(inputs.email, inputs.password)
            .then(token => {
                setIsSubmitting(false)
                if (token.token) {
                    service.userHome(token.token).then((userHome) => {
                        console.log(`setUser(${token.token})`)
                        const newCurrentUser: CurrentUser = {
                            token: token.token,
                            name: userHome.name
                        }
                        setUser(newCurrentUser)
                        setCookie("token", token.token)
                        setRedirect(true)
                    })
                } else {
                    setError("Invalid username or password")
                }
            })
            .catch(() => {
                setIsSubmitting(false)
                setError("Something went wrong!")
            })
    }

    return (
        <div id="content">
            <h1>Login</h1>
            <fieldset disabled={isSubmitting}>
                <form onSubmit={handleSubmit}>
                    {fields.map((field: Field) => 
                        <input key={field.name} type={field.type} name={field.name} value={inputs[field.name] || ""} placeholder={field.name} onChange={handleChange}/>
                    )}
                    <input id="create-token" type="submit" value="Login" />
                </form>
            </fieldset>
            {error}
        </div>
    )
}