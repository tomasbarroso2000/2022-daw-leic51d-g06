import * as React from "react"
import { Link } from "react-router-dom"
import { Game } from "../domain/Game"
import { paths } from "./App"

export function FinishedGame(game: Game) {
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
    const gameStats = 
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
    if (game.playing)    
        return (
            <div id="win-bg">
                <div id="game-over">
                    <h1>YOU WIN!</h1>
                    <div id="game-end-stats">
                        {gameStats}
                    </div>
                    <div>
                        <Link to={paths["home"]}><button>Continue</button></Link>
                    </div>
                </div>
            </div>

        )
    else 
    return (
        <div id="lose-bg">
            <div id="game-over">
                <h1>YOU LOST!</h1>
                <div id="game-end-stats">
                    {gameStats}
                </div>
                <div>
                    <Link to={paths["home"]}><button>Continue</button></Link>
                </div>
            </div>
        </div>
    )
    }