import * as React from "react";
import { useState } from "react";
import { useLocation, Navigate } from "react-router-dom";
import { Field } from "siren-types";
import { CurrentUser } from "../domain/CurrentUser";
import { askService, Result } from "../service/askService"
import { service } from "./App"
import { useSetUser } from "./Authn"
import { useCookies } from "react-cookie";
import { Loading } from "./Loading";
import { ProblemJson } from "../domain/ProblemJson";
 
export function Login() {
    const fields: Result<Array<Field>> | undefined = askService(service, service.getCreateTokenFields)

    const [inputs, setInputs]: [any, React.Dispatch<React.SetStateAction<{}>>] = useState({})
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [redirect, setRedirect] = useState(false)
    const setUser = useSetUser()
    const [cookies, setCookie] = useCookies();
    const location = useLocation()

    if (!fields) {
        return <Loading />
    }

    if (fields.kind == "success") {
        if (redirect) {
            return <Navigate to={location.state?.source?.pathname || "/me"} replace={true}/>
        }
    
        function handleChange(ev: React.FormEvent<HTMLInputElement>) {
            const name = ev.currentTarget.name
            setInputs({ ...inputs, [name]: ev.currentTarget.value })
        }
    
        function handleSubmit(ev: React.FormEvent<HTMLFormElement>) {
            ev.preventDefault()
            setIsSubmitting(true)
            service.createToken(inputs.email, inputs.password)
                .then(token => {
                    setIsSubmitting(false)
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
                })
                .catch((e: ProblemJson) => {
                    setIsSubmitting(false)
                    alert(e.detail)
                })
        }
    
        return (
            <div id="content">
                <div id="login">
                    <div id="right-side-login">
                        <h1>Login</h1>
                        <fieldset disabled={isSubmitting}>
                            <form onSubmit={handleSubmit}>
                                {fields.result.map((field: Field) => 
                                    <input key={field.name} type={field.type} name={field.name} value={inputs[field.name] || ""} placeholder={field.name} onChange={handleChange}/>
                                )}
                                <input id="create-token" type="submit" value="Login" />
                            </form>
                        </fieldset>  
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