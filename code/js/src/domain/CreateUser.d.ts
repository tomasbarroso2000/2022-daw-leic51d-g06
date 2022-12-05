export type CreateUser = {
    id: number
}

export type Field = {
    name: string,
    type: string,
    value: string | undefined
}

export type UserRequest = {
    name: string,
    email: string,
    password: string
}