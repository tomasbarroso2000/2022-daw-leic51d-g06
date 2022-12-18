import * as React from "react"
import { useState } from "react";
import { Form, Link } from "react-router-dom"
import { Field } from "siren-types"
import { askService } from "../service/askService"
import { service } from "./App"

export function CreateUser() {
    const fields: Array<Field> | undefined = askService(service, service.getCreateUserFields)
    
    const [inputs, setInputs]: [any, React.Dispatch<React.SetStateAction<{}>>] = useState({})

    if(!fields) {
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
        service.createUser(inputs.name, inputs.email, inputs.password)
            .then((user) => {
                console.log(user.id)
            })
    }

    return (
        <div id="content">
            <h1>Sign up</h1>
            <form onSubmit={handleSubmit}>
                {fields.map((field: Field) => 
                    <input key={field.name} type={field.type} name={field.name} value={inputs[field.name] || ""} placeholder={field.name} onChange={handleChange} />
                )}
                <input id="create-user" type="submit" value="Sign Up" />
            </form>
        </div>
    )
}