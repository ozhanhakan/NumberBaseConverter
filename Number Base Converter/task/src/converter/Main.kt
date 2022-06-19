package converter

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

val numList = ('0'..'9').toMutableList()
val alphaList = ('A'..'Z').toMutableList()
val baseList = (numList + alphaList).joinToString("")

/*
0:0 / 1:1 / 2:2 / 3:3 / 4:4 / 5:5 / 6:6 / 7:7 / 8:8 / 9:9 / 10:A /
11:B / 12:C / 13:D / 14:E / 15:F / 16:G / 17:H / 18:I / 19:J / 20:K /
21:L / 22:M / 23:N / 24:O / 25:P / 26:Q / 27:R / 28:S / 29:T / 30:U / 31:V / 32:W / 33:X / 34:Y / 35:Z
*/
fun main() {

    //
    firstLevel()
    // secondLevel()
}

// +
// - format kontrolü yapılmalı
fun firstLevel() {
    var firstLevel = ""
    while (firstLevel != "/exit") {
        println("Enter two numbers in format: {source base} {target base} (To quit type /exit)")
        val firstLevelInput = readln().split(" ")

        if (firstLevelInput.size > 1) {
            val sourceBase = firstLevelInput[0].toInt()
            val targetBase = firstLevelInput[1].toInt()
            secondLevel(sourceBase, targetBase)
        } else {
            firstLevel = firstLevelInput[0]
        }
    }
}

// +-
// - basamak değerinden yüksek değer girilmesi önlenmeli
fun secondLevel(source: Int, target: Int) {
    var secondLevel = ""
    while (secondLevel != "/back") {
        println("Enter number in base ${source} to convert to base ${target} (To go back type /back)")
        secondLevel = readln()
        if (secondLevel != "/back") {
            val userNumber = secondLevel
            var result = convertAll(userNumber, source, target)
            if (result.contains(".") && result.substringAfter(".").length > 5) {
                //println(result)
                var a = result.substringBefore(".")
                //println(a)
                var b = result.substringAfter(".")
                //println(b)
                b = b.substring(0, 5)
                //println(b)
                result = a + "." + b
                //println(result)
            }
            println("Conversion result: ${result}")
        }
    }
}

// +
fun convertAll(userNumber: String, sourceBase: Int, targetBase: Int): String {
    if (targetBase == sourceBase) return userNumber

    var fractPartString = ""
    var fractPartConvertedString = ""
    var fractPart = BigDecimal.ZERO
    var intPart = ""
    if (userNumber.contains(".")) {
        intPart = userNumber.substringBefore(".")
        if (userNumber.substringAfter(".").length > 0) {
            fractPartString = userNumber.substringAfter(".")
            // println(fractPartString)
            if (targetBase == 10) {
                fractPartConvertedString = convertToDecimalFract(fractPartString, sourceBase)
            } else {
                if (sourceBase == 10) {
                    fractPartConvertedString = convertFromDecimalFract(fractPartString, targetBase)
                } else
                // first convert to decimal than convert to target base
                    fractPartConvertedString =
                        convertFromDecimalFract(convertToDecimalFract(fractPartString, sourceBase), targetBase)
            }
        }
    } else {
        intPart = userNumber
    }
    // if no fraction same as before
    if (targetBase == 10) {
        return convertToDecimal(intPart, sourceBase) + fractPartConvertedString.substringAfter("0")
    } else {
        if (sourceBase == 10) return convertFromDecimal(
            intPart,
            targetBase
        ) + fractPartConvertedString.substringAfter("0")
        // first convert to decimal than convert to target base
        return convertFromDecimal(
            convertToDecimal(intPart, sourceBase),
            targetBase
        ) + fractPartConvertedString.substringAfter("0")
    }
}

// +-
fun convertToDecimal(inputNumber: String, base: Int): String {
    var sourceNum = inputNumber
    var decimalNum = 0.toBigDecimal()
    val length = inputNumber.length
    for (i in inputNumber.lastIndex downTo 0) {
        // find letter in given number
        val iLetter = sourceNum.substring(i, i + 1).uppercase()
        val iLetterValue = baseList.indexOf(iLetter).toBigDecimal()
        decimalNum += iLetterValue * base.toBigDecimal().pow(length - i - 1)
        sourceNum = sourceNum.substring(0, sourceNum.lastIndex)
    }
    //println(decimalNum)
    return decimalNum.toString()

}

// +-
fun convertFromDecimal(decimalInput: String, base: Int): String {
    // integer part
    var quotient = decimalInput.toBigInteger()
    // var quotient = decimalInput.toBigDecimal()
    val remainder = mutableListOf<String>()
    if (quotient == BigInteger.ZERO) return quotient.toString()
    while (quotient > BigInteger.ZERO) {
        remainder.add(0, "${baseList[(quotient % base.toBigInteger()).toInt()]}")
        quotient /= base.toBigInteger()
    }

    return if (remainder.joinToString("").isEmpty()) "0" else remainder.joinToString("")
    //var remainderStr = if (remainder.joinToString("").isEmpty()) "0" else remainder.joinToString("")
    //remainderStr = if (remainderStr.length > 7) remainderStr.substring(0, 7) else remainderStr
    //return remainderStr
}

// convert fractional
// +-
fun convertToDecimalFract(inputNumber: String, base: Int): String {
    //return "12345"
    var sourceNum = inputNumber
    var decimalNum = 0.toBigDecimal()
    val length = inputNumber.length
    for (i in inputNumber.indices) {
        // find letter in given number
        // take a letter
        val iLetter = sourceNum.substring(i, i + 1).uppercase()
        // convert base type letter to decimal type number
        val iLetterValue = baseList.indexOf(iLetter).toBigDecimal()
//        decimalNum += iLetterValue * base.toBigDecimal().pow( - i - 1)
        decimalNum += iLetterValue.setScale(6, RoundingMode.HALF_EVEN) / base.toBigDecimal().pow(i + 1)
        // decimalNum = decimalNum.setScale(5, RoundingMode.HALF_DOWN)
        //sourceNum = sourceNum.substring(0, sourceNum.lastIndex)
    }
    // println(decimalNum)
    return decimalNum.setScale(6, RoundingMode.HALF_EVEN).toString()
//    return decimalNum.toString()
}

// +-
fun convertFromDecimalFract(decimalInput: String, base: Int): String {
    // return "12321"
    var quotient = decimalInput.toBigDecimal()
    if (!decimalInput.contains(".")) {
        quotient = ("0." + decimalInput).toBigDecimal()
    }
    val remainder = mutableListOf<String>()
    if (quotient == BigDecimal.ZERO) return quotient.toString()
    for (i in 1..5) {
        quotient = base.toBigDecimal() * quotient
        if (quotient.setScale(6, RoundingMode.HALF_EVEN) == 0.000000.toBigDecimal()) break
        val letter = quotient.toString().substringBefore(".").toInt()
        remainder.add("${baseList[letter]}")
        quotient = ("0." + quotient.toString().substringAfter(".")).toBigDecimal()

    }

    var remainderStr = if (remainder.joinToString("").isEmpty()) "0" else "0." + remainder.joinToString("")
    remainderStr = if (remainderStr.length > 7) remainderStr.substring(0, 8) else remainderStr
    return remainderStr
}
/*fun fromDecimal() {
    println("Enter number in decimal system:")
    val decimalInput = readln().toInt()
    println("Enter target base:")
    val targetBase = readln().toInt()
    val conversionResult = convertFromDecimal(decimalInput, targetBase)
    println("Conversion result: $conversionResult")
}*/


/*fun toDecimal(sourceBase: Int, userNumber: String) {
    val noDecimalInput = userNumber.toString()
    val base = sourceBase
    val conversionResult = convertToDecimal(noDecimalInput, base)
    println("Conversion to decimal result: $conversionResult")
}*/
/*

fun convertFromDecimalToBinary(_decimalInput: Int): String {
    var quotient = _decimalInput
    val remainder = mutableListOf<String>()
    //if (quotient < 2) return quotient.toString()
    while (quotient > 0) {
        remainder.add(0, "${quotient % 2}")
        quotient /= 2
    }
    return if (remainder.joinToString("").isEmpty()) "0".toString() else remainder.joinToString("")
}

fun convertFromDecimalToOctal(_decimalInput: Int): String {
    var temp = convertFromDecimalToBinary(_decimalInput)
    val octal = mutableListOf<String>()
    while (temp.length % 3 != 0) {
        temp = "0" + temp
    }
    while (temp.isNotEmpty()) {
        octal.add(0, tripletForOctal(temp.substring(temp.lastIndex - 2, temp.lastIndex + 1)))
        temp = temp.substring(0, temp.lastIndex - 2)
    }
    return octal.joinToString("")
}

fun convertFromDecimalToHexadecimal(_decimalInput: Int): String {
    var temp = convertFromDecimalToBinary(_decimalInput)
    val hekta = mutableListOf<String>()
    while (temp.length % 4 != 0) {
        temp = "0" + temp
    }
    while (temp.isNotEmpty()) {
        hekta.add(0, quodForHexa(temp.substring(temp.lastIndex - 3, temp.lastIndex + 1)))
        temp = temp.substring(0, temp.lastIndex - 3)
    }
    return hekta.joinToString("")
}

fun tripletForOctal(triplet: String): String {
    return when (triplet) {
        "000" -> "0"
        "001" -> "1"
        "010" -> "2"
        "011" -> "3"
        "100" -> "4"
        "101" -> "5"
        "110" -> "6"
        "111" -> "7"
        else -> "X"
    }
}

fun quodForHexa(quod: String): String {
    return when (quod) {
        "0000" -> "0"
        "0001" -> "1"
        "0010" -> "2"
        "0011" -> "3"
        "0100" -> "4"
        "0101" -> "5"
        "0110" -> "6"
        "0111" -> "7"
        "1000" -> "8"
        "1001" -> "9"
        "1010" -> "A"
        "1011" -> "B"
        "1100" -> "C"
        "1101" -> "D"
        "1110" -> "E"
        "1111" -> "F"
        else -> "X"
    }
}

fun quodForDecimal(quod: String): String {
    return when (quod) {
        "0" -> "0"
        "1" -> "1"
        "2" -> "2"
        "3" -> "3"
        "4" -> "4"
        "5" -> "5"
        "6" -> "6"
        "7" -> "7"
        "8" -> "8"
        "9" -> "9"
        "A", "a" -> "10"
        "B", "b" -> "11"
        "C", "c" -> "12"
        "D", "d" -> "13"
        "E", "e" -> "14"
        "F", "f" -> "15"
        else -> "-"
    }
}
fun convertToDecimal(inputNumber: String, base: Int): String {
    var binaryNumber = inputNumber
    var decimalNum = 0
    //decimalNum =
    if (base != 16) {
        for (i in inputNumber.indices) {
            decimalNum += binaryNumber.substring(binaryNumber.lastIndex).toInt() * (Math.pow(
                base.toDouble(),
                (inputNumber.length - binaryNumber.length).toDouble()
            )).toInt()
            binaryNumber = binaryNumber.substring(0, binaryNumber.lastIndex)
        }
    } else {
        // if number is hexa
        for (i in inputNumber.indices) {
            decimalNum += quodForDecimal(binaryNumber.substring(binaryNumber.lastIndex)).toInt() * (Math.pow(
                base.toDouble(),
                (inputNumber.length - binaryNumber.length).toDouble()
            )).toInt()
            binaryNumber = binaryNumber.substring(0, binaryNumber.lastIndex)
        }
    }
    return decimalNum.toString()
}


*/
