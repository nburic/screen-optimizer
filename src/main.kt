import java.io.File

private class InputModel(val width: Int, val height: Int, val text: String) {
    override fun toString(): String {
        return "$width $height $text"
    }
}

private class OutputModel() {
    private var _lines: MutableList<Line> = mutableListOf()

    constructor(line: Line) : this() {
        _lines.clear()
        _lines.add(line)
    }

    constructor(lines: MutableList<Line>) : this() {
        _lines.clear()
        _lines = lines
    }

    fun getTakenSpace(charSize: Int): Int {
        var takenSpace = 0

        _lines.forEach {
            takenSpace += it.getCharLength() * charSize
        }

        return takenSpace
    }

    fun getNumOfLines(): Int {
        return _lines.size
    }

    fun getLongestLineLen(): Int {
        var longest = 0

        _lines.forEach {
            val len = it.getCharLength()

            if (len > longest) {
                longest = len
            }
        }

        return longest
    }
}

private class Line() {
    var words: MutableList<String> = mutableListOf()

    constructor(line: Line) : this() {
        line.words.forEach {
            words.add(it)
        }
    }

    constructor(words: MutableList<String>) : this() {
        this.words = words
    }

    fun getLastWord(): String {
        return words[words.size - 1]
    }

    fun hasOneWord(): Boolean {
        return words.size == 1
    }

    fun addWord(word: String) {
        words.add(0, word)
    }

    fun removeLastWord() {
        words.removeAt(words.size - 1)
    }

    fun getCharLength(): Int {
        var len = 0

        words.forEach {
            len += it.length
        }

        return len + words.size - 1
    }
}

fun main() {
    val testInputs = listOf(
        InputModel(20, 6, "led display"),
        InputModel(100, 20, "led display 2020"),
        InputModel(10, 20, "MUST BE ABLE TO DISPLAY"),
        InputModel(55, 25, "Can you hack"),
        InputModel(100, 20, "display product text")
    )

    val inputs = mutableListOf<InputModel>()

    val file = File("src/vhodi.txt")
    file.forEachLine {
        val width = it.substringBefore(" ")
        val height = it.substringAfter(" ").substringBefore(" ")
        val text = it.substringAfter(" ").substringAfter(" ")

        inputs.add(InputModel(width = width.toInt(), height = height.toInt(), text = text))
    }

    inputs.forEach {
        println("$it -> ${calculate(it.text, it.width, it.height)}")
    }
//    testInputs.forEach {
//        println("$it -> ${calculate(it.text, it.width, it.height)}")
//    }
}

/**
 * Calculates best char size to take as much screen size as possible.
 * @param text text to fit in the screen
 * @param screenWidth  screen width
 * @param screenHeight screen height
 * @return char size for the best result (min screen size remaining) and 0 if it cannot fit in the screen
 */
private fun calculate(text: String, screenWidth: Int, screenHeight: Int): Int {
    val outputs = getOutputs(text)
    val results = getResults(outputs, screenWidth, screenHeight)

    // get best output
    return results.minBy { it.first }?.second ?: 0
}

/**
 * Generates every possible output for given text. Splitting every line if it
 * contains more than one word by putting last word of current line to the
 * start of next line, until every line only has one word.
 * Starting case: May the force be with you
 * Ending case: May
 *              the
 *              force
 *              be
 *              with
 *              you
 * @param text starting text
 * @return [List] of all possible outputs
 */
private fun getOutputs(text: String): List<OutputModel> {
    val words = text.split(" ")
    val startLine = Line(words.toMutableList())
    val lines = mutableListOf(startLine)

    val outputs = mutableListOf<OutputModel>()
    outputs.add(OutputModel(Line(words.toMutableList())))

    var i = 0
    while (lines.size < words.size) {
        if (!lines[i].hasOneWord()) {
            val lastWord = lines[i].getLastWord()
            if (lines.indexOf(lines[i]) != lines.size - 1) {
                lines[i + 1].addWord(lastWord)
            } else {
                lines.add(Line(mutableListOf(lastWord)))
            }

            lines[i].removeLastWord()

            val linesClone = mutableListOf<Line>()
            lines.forEach {
                linesClone.add(Line(it))
            }

            outputs.add(OutputModel(linesClone))
        } else {
            i++
        }
    }

    return outputs
}

/**
 * Sets the result of remaining pixels on the screen and character size for each output.
 * For each output it calculates max possible character size by taking the longest line
 * and dividing it by screen width. Then checks if it fits into screen height (chars have to have
 * same width and height). If it does not fit to screen height, it shrinks size until it fits.
 * In the last step it saves remaining space of the screen and char size into list of results.
 * @param outputs list of possible outputs
 * @param screenWidth available screen width
 * @param screenHeight available screen height
 * @return [List] of [Pair] of remaining pixels and character size
 */
private fun getResults(outputs: List<OutputModel>, screenWidth: Int, screenHeight: Int): List<Pair<Int, Int>> {
    val results = mutableListOf<Pair<Int, Int>>()

    outputs.forEach {
        // calculate char size for output
        var charSize = screenWidth / it.getLongestLineLen()

        // check if fits into screen height
        val fitsToHeight = charSize * it.getNumOfLines() <= screenHeight
        if (!fitsToHeight) {
            // scale char size to fit into screen height
            do {
                charSize -= 1
            } while (charSize * it.getNumOfLines() > screenHeight)
        }

        // calculate taken space
        val takenSpace = it.getTakenSpace(charSize)
        val remainedScreen = screenWidth * screenHeight - takenSpace

        results.add(Pair(remainedScreen, charSize))
    }

    return results.toList()
}