import * as React from "react"
import { Link, Navigate } from "react-router-dom"
import { Game } from "../domain/Game"
import { paths } from "./App"

export function FinishedGame(game: Game) {
    if (game.fleet.length == 0)
        return <Navigate to={paths["games"]} replace></Navigate>

    const yourFleet = game.fleet.map((ship) => {
        if(ship.destroyed)
            return <li>{ship.name} was destroyed</li>
        else
            return <li>{ship.name} took {ship.nOfHits} hit(s)</li>
    }) 
    const enemyFleet = function() {
        const enemySunkFleet = game.enemySunkFleet
        if (enemySunkFleet.length == 0) 
            return <p>No enemy ships were destroyed</p>
        enemySunkFleet.map((ship) => {
            return <li>{ship.name} was destroyed</li>
        }) 
    }   
    return (
        <div id={game.playing ? "win-bg" : "lose-bg"}>
            <div id="game-over">
                <h1>{game.playing ? "You Win!" : "You Lose!"}</h1>
                <div id="game-end-stats">
                    <div>
                        <h3>Game Statistics:</h3>
                        <p>Shots: {game.hits.length + game.misses.length}</p>
                        <p>Hits: {game.hits.length}</p>
                        <p>Misses: {game.misses.length}</p>
                        <h3>Your fleet:</h3>
                        <ul>{yourFleet}</ul>
                        <h3>{game.opponent.name}'s fleet</h3>
                        <ul>{enemyFleet()}</ul>
                    </div>
                </div>
                <div>
                    <Link to={paths["home"]}><button>Continue</button></Link>
                </div>
            </div>
        </div>
    )
    }