import { deepEqual } from "./deepEqual"

export function remove<T>(list: Array<T>, element: T) {
    const newList = []
    list.forEach((elem: T) => {
        if (!deepEqual(elem, element))
            newList.push(elem)
    })
    return newList
}