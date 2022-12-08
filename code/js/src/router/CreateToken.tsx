import * as React from "react";
import { useState } from "react";
import { useLocation, Navigate, useNavigate } from "react-router-dom";
import { Field } from "siren-types";
import { askService } from "../service/askService"
import { service } from "./App"
import { useSetUser } from "./Authn"

export function CreateToken() {
    console.log("Login")
    const fields: Array<Field> | undefined = askService(service, service.getCreateTokenFields)

    const [inputs, setInputs]: [any, React.Dispatch<React.SetStateAction<{}>>] = useState({})
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [error, setError] = useState(undefined)
    const [redirect, setRedirect] = useState(false)
    const setUser = useSetUser()
    const navigate = useNavigate()
    const location = useLocation()


    if(!fields) {
        console.log("loading")
        return (
            <div>
                ...loading...
            </div>
        )
    }

    if(redirect) {
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
            .then((res) => {
                setIsSubmitting(false)
                if(res) {
                    //const redirect = location.state?.source?.pathname || "/home"
                    console.log("token: " + res.token)
                    setUser(res.token)
                    setRedirect(true)
                } else {
                    setError("Invalid username or password")
                }
            })
            .catch((error) => {
                setIsSubmitting(false)
                setError(error.message)
            })
    }

    return (
        <div id="content">
            <h1>Login</h1>
            <fieldset disabled={isSubmitting}>
                <form onSubmit={handleSubmit}>
                    {fields.map((field: Field) => 
                        <input key={field.name} type={field.type} name={field.name} value={inputs[field.name]} placeholder={field.name} onChange={handleChange}/>
                    )}
                    <input id="create-token" type="submit" value="Login" />
                </form>
            </fieldset>
            {error}
        </div>
    )
}