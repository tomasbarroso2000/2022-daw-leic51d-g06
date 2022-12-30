import * as React from "react";

const loadingStyle: React.CSSProperties = {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    height: '100vh',
}

const loadingTextStyle: React.CSSProperties = {
    fontSize: "80px"
}

export function Loading() {
    document.title = "Loading"
    return (
        <div style={loadingStyle}>
            <div style={loadingTextStyle}>Loading...</div>
        </div>
    )
}