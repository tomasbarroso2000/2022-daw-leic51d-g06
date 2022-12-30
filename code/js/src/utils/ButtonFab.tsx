import * as React from "react";

export type ButtonFabProps = {
    isDisabled: boolean,
    onClick: () => void,
    text: string
}

export function ButtonFab(props: ButtonFabProps) {
    return <button disabled={props.isDisabled} onClick={props.onClick}>{props.text}</button>;
}