import * as React from "react"
import { UserInfo } from "../domain/UserInfo"
import { service } from "./App"

export function Rankings() {
    const rankings = service.rankings()
    let rank = 0

    if (!rankings) {
        return (
            <div>
                ...loading...
            </div>
        )
    }

    console.log(rankings)

    return (
        <div id="content-rankings">
            <h1 id="rankings-title">Rankings</h1>
            <ul>
                <li>
                    <dl>
                        <dt>Rank - Name: </dt><dd>Score: </dd>
                    </dl>
                </li>
            {rankings.rankings.map((user: UserInfo) => 
                <li key={user.id}>
                    <dl>
                        <dt id="rankings-player">{++rank} - {user.name}</dt><span></span><dd>{user.score}</dd>
                    </dl>
                </li>
            )}
            </ul>
        </div>
    )
}