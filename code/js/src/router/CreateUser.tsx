import * as React from "react"
import { Form, Link } from "react-router-dom"
import { Field } from "siren-types"
import { askService } from "../service/askService"
import { service } from "./App"

export function CreateUser() {
    const fields = askService(service, service.getCreateUserFields)
    console.log("fields" + JSON.stringify(fields))

    if(!fields) {
        return (
            <div>
                ...loading...
            </div>
        )
    }

    fields.forEach((field: Field) => {
        console.log(field.name + " : " + field.type)
    })

    function handleCreateUser() {
        
    }

    let key = 0
    return (
        <div id="content">
            <h1>Sign up</h1>
            <form onSubmit={handleCreateUser}>
                {fields.map((field: Field) => 
                    <input key={key++} type={field.type} name={field.name} placeholder={field.name}/>
                )}
                <input id="create-user" type="submit" value="Sign Up" />
            </form>
        </div>
    )
}