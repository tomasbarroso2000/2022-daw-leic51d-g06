import * as React from "react"
import { Link } from "react-router-dom"
import { Field } from "../domain/CreateUser"
import { askService } from "../service/askService"
import { service } from "./App"

export function CreateUser() {
    const fields = askService(service, service.getUserFields)
    return (
        <div id="content">
            <form action="" method="post">
                {fields.map((field: Field) => {
                    <input name={field.name} type={field.type} />
                })}
            </form>
        </div>
    )
}