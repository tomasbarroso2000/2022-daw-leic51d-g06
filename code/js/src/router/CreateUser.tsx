import * as React from "react"
import { useState } from "react";
import { useLocation, Navigate, Link } from "react-router-dom"
import { Field } from "siren-types"
import { askService, Result } from "../service/askService"
import { CurrentUser } from "../domain/CurrentUser";
import { service } from "./App"
import { useSetUser } from "./Authn"
import { Cookies, useCookies } from "react-cookie"; // npm install react-cookie
import { Loading } from "./Loading";

export function CreateUser() {
    const fields: Result<Array<Field>> | undefined = askService(service, service.getCreateUserFields)
    
    const [inputs, setInputs]: [any, React.Dispatch<React.SetStateAction<{}>>] = useState({})
    const [redirect, setRedirect] = useState(false)
    const setUser = useSetUser()
    const [cookies, setCookie] = useCookies(['token']);
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [error, setError] = useState(undefined)
    const location = useLocation()

    if(!fields) {
        return <Loading />
    }

    if (fields.kind == "success") {
        if (redirect) {
            return <Navigate to={location.state?.source?.pathname || "/me"} replace={true}/>
        }
    
        function handleChange(ev: React.FormEvent<HTMLInputElement>) {
            const name = ev.currentTarget.name
            setInputs({ ...inputs, [name]: ev.currentTarget.value })
            //setError(undefined)
        }
    
        function handleSubmit(ev: React.FormEvent<HTMLFormElement>) {
            ev.preventDefault()
            //setIsSubmitting(true)
            service.createUser(inputs.name, inputs.email, inputs.password)
                .then((user) => {
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
                })
        }
    
        return (
            <div id="content">
                <div id="login">
                    <div id="right-side-login">
                        <h1>Sign up</h1>
                        <fieldset disabled={isSubmitting}>
                            <form onSubmit={handleSubmit}>
                                {fields.result.map((field: Field) => 
                                    <input key={field.name} type={field.type} name={field.name} value={inputs[field.name] || ""} placeholder={field.name} onChange={handleChange} />
                                )}
                                <input id="create-user" type="submit" value="Sign Up" />
                            </form>
                        </fieldset>
                        {error}   
                    </div>
                    <div id="left-side-login">
                        <img src="images/logo.png" alt="battleship-logo" id="logo" />
                        <h1>Exploding <br /> Battleships</h1>
                        <p>Lets play a game</p>
                    </div>
                </div>
            </div>
        )
    }
}