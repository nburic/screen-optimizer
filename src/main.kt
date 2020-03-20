import java.io.File
import java.io.InputStream

private class InputModel(val width: Int, val height: Int, val text: String) {
    override fun toString(): String {
        return "$width $height $text"
    }
}

private val inputs = listOf(
    InputModel(20, 6, "led display"),
    InputModel(100, 20, "led display 2020"),
    InputModel(10, 20, "MUST BE ABLE TO DISPLAY"),
    InputModel(55, 25, "Can you hack"),
    InputModel(100, 20, "display product text")
)

fun main() {
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
}

class Line() {

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

class OutputModel() {
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

private fun calculate(text: String, screenWidth: Int, screenHeight: Int): Int {
    val words = getWords(text)
    val startLine = Line(words.toMutableList())

    val lines = mutableListOf<Line>()
    lines.add(startLine)

    var i = 0

    val outputs = mutableListOf<OutputModel>()
    outputs.add(OutputModel(Line(words.toMutableList())))

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

            val outPut = OutputModel(linesClone)

            outputs.add(outPut)
        } else {
            i++
        }
    }

    var remainedScreen = screenWidth * screenHeight
    // remaineder and charsize
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

        remainedScreen = screenWidth * screenHeight - takenSpace

        results.add(Pair(remainedScreen, charSize))
    }

    // get best output
    val res = results.minBy { it.first }?.second ?: 0

    return res
}

private fun getWords(text: String): List<String> {
    return text.split(" ")
}