import { deepEqual } from "./deepEqual";

export function contains<T>(list: Array<T>, element: T) {
    return list.some((elem: T) => deepEqual(elem, element))
}