export type Square = {
    row: string,
    column: number
}

function up(square: Square): Square { 
    return {row: String.fromCharCode(square.row.charCodeAt(0) - 1), column: square.column}
}
function down(square: Square): Square {
     return {row: String.fromCharCode(square.row.charCodeAt(0) + 1), column: square.column}
}
function left(square: Square): Square {
    return {row: square.row, column: square.column - 1}
}
function right(square: Square): Square {
    return {row: square.row, column: square.column + 1}
}

export function surroundingSquares(square: Square) {
    return [
        left(up(square)),
        up(square),
        right(up(square)),
        left(square),
        square,
        right(square),
        left(down(square)),
        down(square),
        right(down(square))
    ]
}