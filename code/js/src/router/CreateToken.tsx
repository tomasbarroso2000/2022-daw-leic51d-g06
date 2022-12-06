import * as React from "react";
import { useState } from "react";
import { useNavigate, useLocation, Navigate } from "react-router-dom";
import { Field } from "siren-types";
import { askService } from "../service/askService"
import { service } from "./App"

export function CreateToken() {
    
    const fields: Array<Field> | undefined = askService(service, service.getCreateTokenFields)

    const [inputs, setInputs]: [any, React.Dispatch<React.SetStateAction<{}>>] = useState({})

    if(!fields) {
        console.log("loading")
        return (
            <div>
                ...loading...
            </div>
        )
    }

    

    function handleChange(ev: React.FormEvent<HTMLInputElement>) {
        const name = ev.currentTarget.name
        setInputs({ ...inputs, [name]: ev.currentTarget.value })
        //setError(undefined)
    }
    function handleSubmit(ev: React.FormEvent<HTMLFormElement>) {
        ev.preventDefault()
        //setIsSubmitting(true)
        service.createToken(inputs.email, inputs.password)
            .then((token) => {
                console.log(token.token)
            })
    }

    //const [isSubmitting, setIsSubmitting] = useState(false)
    //const [error, setError] = useState(undefined)
    //const [redirect, setRedirect] = useState(false)
    //const setUser = useSetUser()
    //const navigate = useNavigate()
    //const location = useLocation()
    /*
    if(redirect) {
        return <Navigate to={location.state?.source?.pathname || "/me"} replace={true}/>
    }
    */
    
    return (
        <div id="content">
            <h1>Login</h1>
            <form onSubmit={handleSubmit}>
                {fields.map((field: Field) => 
                    <input key={field.name} type={field.type} name={field.name} value={inputs[field.name]} placeholder={field.name} onChange={handleChange}/>
                )}
                <input id="create-token" type="submit" value="Login" />
            </form>
        </div>
    )
}