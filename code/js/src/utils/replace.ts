import { deepEqual } from "./deepEqual"

export function replace<T>(list: Array<T>, originalElement: T, newElement: T) {
    return list.map((elem: T) => {
        if (deepEqual(elem, originalElement))
            return newElement
        else 
            return elem
    })
}