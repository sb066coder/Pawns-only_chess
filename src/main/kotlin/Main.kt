package ru.sb066coder

import kotlin.math.abs

const val EMP = ' '
const val WTP = 'W' // white pawn
const val BLP = 'B' // black pawn
const val EMPTY_EN_PASSANT = "nnnn"
const val FULL_SCORE = 8
val REGEX = Regex(pattern = "[a-h][1-8][a-h][1-8]")

val board: List<Array<Char>> = arrayOf(
    arrayOf(EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP),
    arrayOf(BLP, BLP, BLP, BLP, BLP, BLP, BLP, BLP),
    arrayOf(EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP),
    arrayOf(EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP),
    arrayOf(EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP),
    arrayOf(EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP),
    arrayOf(WTP, WTP, WTP, WTP, WTP, WTP, WTP, WTP),
    arrayOf(EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP)
).reversed()
lateinit var wName: String
lateinit var bName: String
var wMove = true // white move state
lateinit var player: String
var pColor = WTP
var colorName = "white"
var startPosition = 2
var enPassant = EMPTY_EN_PASSANT
var wScore = FULL_SCORE
var bScore = FULL_SCORE

fun main() {
    println(" Pawns-Only Chess") // Show Game name
    getNames()
    showBoard()
    while (true) { // Main loop
        if (checkStalemate()) { // Stalemate -> exit
            println("Stalemate!")
            break
        }
        println("$player's turn:")
        print("> ")
        val input = readln()
        if (input != "exit") {
            if (move(input)) { // Move is done
                showBoard()
                if (checkWin(input)) { // Win -> exit
                    println("${colorName[0].uppercase() + colorName.substring(1)} Wins!")
                    break
                }
                switchMove()
            }
        } else {
            break
        }
    }
    println("Bye!")
}

fun switchMove() {
    wMove = !wMove
    player = if (wMove) wName else bName
    pColor = if (wMove) WTP else BLP
    colorName = if (wMove) "white" else "black"
    startPosition = if (wMove) 2 else 7
}

fun move(moveCrd: String): Boolean {
    if (REGEX.matches(moveCrd)) { // check if input coordinates are within the field
        if (depField(moveCrd) != pColor) { // check if pawn is not at the field
            println("No $colorName pawn at ${moveCrd[0]}${moveCrd[1]}")
            return false
        }
        if (moveCrd[0] == moveCrd[2] && // check if vertical raw matches
            destField(moveCrd) == EMP && // check if destination field is empty
                (moveCrd[1] + (if (wMove) 1 else - 1) == moveCrd[3] || // check if 1 step
                (moveCrd[1] + (if (wMove) 2 else - 2) == moveCrd[3] && // check if 2 steps
                moveCrd[1].digitToInt() == startPosition && // check if start position
                destField("nn" + moveCrd[0] + (moveCrd[1] + (if (wMove) 1 else - 1))) == EMP)) // check if no obstacle between
            ) {
            assignField(moveCrd) // advance pawn
            return true
        }
        if (abs(moveCrd[0] - moveCrd[2]) == 1 && (if (wMove) moveCrd[1] + 1 else moveCrd[1] - 1) == moveCrd[3]) {
            if (destField(moveCrd) == (if (wMove) BLP else WTP)) {
                assignField(moveCrd) // standard capture
                if (wMove) bScore-- else wScore--
                return true
            }
            if (moveCrd[2] == enPassant[2] && moveCrd[3] == enPassant[3]) {
                enPassantCapture() // en passant capture
                assignField(moveCrd)
                if (wMove) bScore-- else wScore--
                return true
            }
        }
    }
    println("Invalid Input")
    return false
}

fun enPassantCapture() {
    board[enPassant[1].digitToInt() - 1][enPassant[0].code - 97] = EMP
}

fun assignField(crd: String) {
    board[crd[1].digitToInt() - 1][crd[0].code - 97] = EMP
    board[crd[3].digitToInt() - 1][crd[2].code - 97] = pColor
    enPassant = if (abs(crd[3] - crd[1]) == 2) crd[2].toString() + crd[3] + crd[2] +
        ((crd[1].code + crd[3].code) / 2).toChar() else EMPTY_EN_PASSANT
}

fun depField(crd: String): Char {
    return board[crd[1].digitToInt() - 1][crd[0].code - 97] // [h - 1][v.code - 97]
}

fun destField(crd: String): Char {
    return board[crd[3].digitToInt() - 1][crd[2].code - 97]
}

fun getNames() {
    println("First player's name:")
    print("> ")
    wName = readln()
    player = wName
    println("Second player's name:")
    print("> ")
    bName = readln()
}

fun showBoard() {
    for(h in 8 downTo 1) {
        printBorderLine()
        print("$h |")
        for (v in 'a'..'h') {
            print(" ${board[h - 1][v.code - 97]} |") // 'a'.code == 97
        }
        println()
    }
    printBorderLine()
    println("    a   b   c   d   e   f   g   h  ")
}

fun printBorderLine() {
    println("  +---+---+---+---+---+---+---+---+")
}

/**Check if opponent lost 8 pawns or pawn reached finish line*/
fun checkWin(crd: String): Boolean {
    if (crd[3] == if (wMove) '8' else '1') return true // Win when final field reached
    if (wScore == 0 || bScore == 0) return true // Win when opponent lost all pawns
    return false
}

/**Check if pawns have no available moves*/
fun checkStalemate(): Boolean {
    for (h in if (wMove) 1..5 else 6 downTo  2) {
        for (v in 0..7) {
            // check if possible advance
            if (board[h][v] == (if (wMove) WTP else BLP) && board[h + (if (wMove) 1 else - 1)][v] == EMP) return false
            // check if possible left capture
            if (v != 0) {
                if (board[h][v] == (if (wMove) WTP else BLP) &&
                    (board[h + (if (wMove) 1 else -1)][v - 1] == (if (wMove) BLP else WTP) ||
                        (v - 1 + 97).toChar().toString().plus(h + (if (wMove) 1 else - 1)) == enPassant.substring(2))
                ) return false
            }
            // check if possible right capture
            if (v != 7) {
                if (board[h][v] == (if (wMove) WTP else BLP) &&
                    (board[h + (if (wMove) 1 else -1)][v + 1] == (if (wMove) BLP else WTP) ||
                        (v + 1 + 97).toChar().toString().plus(h + (if (wMove) 1 else - 1)) == enPassant.substring(2))
                ) return false
            }
        }
    }
    return true
}
