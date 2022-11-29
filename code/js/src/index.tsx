import * as React from 'react'
import { createRoot } from 'react-dom/client'
import {App} from './router/App'

const root = createRoot(document.getElementById("the-div"))

root.render(
    <App />
)

