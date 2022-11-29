import * as React from "react"
import { Link } from "react-router-dom"
import { askService } from "../service/askService"
import { service } from "./App"

export function Home() {
    const home = askService(service, service.home)

    if (!home) {
        return (
            <div>
                ...loading...
            </div>
        )
    }

    return (
        <div id="content">
            <div id="right-side-content">
                <h1 id="title">{home.name}</h1>
                <img src="images/battleship.png" alt="battleship" id="battleship" />
                <div id="footer">
                    <div id="authors">
                        <h3>Authors:</h3>
                        <ul>
                            {home.authors.map((author: string) => <li key={author}>{author}</li>)}
                        </ul>
                    </div>
                    <div id="version">
                        <h4>Version: {home.version}</h4>
                    </div>
                </div>
                
            </div>
            <div id="left-side-content">
                <div id="menu">
                    <h2 id="menu-title">Menu</h2>
                    <ol>
                        {service.homeNavigation.map((nav) => 
                            <li key={nav}> 
                                <Link to={nav}>{nav}</Link>
                            </li>
                        )}
                    </ol> 
                </div>
            </div>
            
        </div>
    )
}