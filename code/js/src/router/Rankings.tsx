import * as React from "react"
import { useState } from "react"
import { UserInfo } from "../domain/UserInfo"
import { askService } from "../service/askService"
import { ButtonFab } from "../utils/ButtonFab"
import { service } from "./App"
import { Loading } from "./Loading"

export function Rankings() {
    const limit = 5
    const [skip, setSkip] = useState(0)

    const rankings = askService(service, service.rankings, limit, skip)

    let rank = 0

    if (!rankings) {
        return <Loading />
    }

    if (rankings.kind == "success") {
        return (
            <div id="content-rankings">
                <h1 id="rankings-title">Rankings</h1>
                <ul>
                    <li>
                        <dl>
                            <dt>Rank - Name: </dt><dd>Score: </dd>
                        </dl>
                    </li>
                    {rankings.result.rankings.map((user: UserInfo) => 
                        <li key={user.id}>
                            <dl>
                                <dt id="rankings-player">{++rank} - {user.name}</dt><span></span><dd>{user.score}</dd>
                            </dl>
                        </li>
                    )}
                </ul>
                <div>
                    <ButtonFab isDisabled={skip == 0} onClick={() => {setSkip(skip - limit)}} text={"Previous"}/>
                    <ButtonFab isDisabled={!rankings.result.hasMore} onClick={() => {setSkip(skip + limit)}} text={"Next"}/>
                </div>
            </div>
        )
    }
}