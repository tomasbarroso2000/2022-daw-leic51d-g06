import * as React from "react";
import { useSearchParams } from "react-router-dom";
import { paths } from "./App";

export function PlayGame() {
    const [searchParams, setSearchParams] = useSearchParams();
    const gameTypeSearchParam = searchParams.get("game")

    if(!gameTypeSearchParam) {
        window.location.replace(`${paths['list-games']}`);
    }

    return (
        <h1>Playing game: {gameTypeSearchParam}</h1>
    )
}