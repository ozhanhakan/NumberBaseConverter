fun main() {
    // do {
   /* val str = readln()
    for (index in 0 until str.length / 2) {
        if (str[index] != str[str.length - index - 1]) {
            println("no")
            break
        }
        if (index == str.length / 2 - 1) {
            println("yes")
            break
        }
    }
    if (str.length == 1) println("yes")*/
    // }while (str != "exit")
    val input = readln()
    print(if (input == input.reversed()) "yes" else "no")
}