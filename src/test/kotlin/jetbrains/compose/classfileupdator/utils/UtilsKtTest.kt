package jetbrains.compose.classfileupdator.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.ParameterizedTest.ARGUMENTS_WITH_NAMES_PLACEHOLDER
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class UtilsKtTest {
    @ParameterizedTest(name = "List.divideIn($ARGUMENTS_WITH_NAMES_PLACEHOLDER)")
    @MethodSource("divideInTestInputs")
    internal fun divideIn(params: ListDivideInTestParams) = with(params) {
        val actualResult = input.divideIn(divideIn)
        assertEquals(expectedSubLists, actualResult)
    }

    @ParameterizedTest(name = "Add File Separator: <{1}>, Input: {0}")
    @CsvSource(
        // input,       separator,      expected
        "/user/home,     /,             /user/home/",
        "/user/home/,    /,             /user/home/",
        "c:\\foo,        \\,            c:\\foo\\",
        "c:\\foo\\,      \\,            c:\\foo\\",
    )
    fun addFileSeparatorSuffix(path: String, separator: Char, expected: String) {
        assertEquals(expected, addFileSeparatorSuffix(path, separator))
    }

    companion object {
        @Suppress("unused")
        @JvmStatic
        fun divideInTestInputs(): Stream<ListDivideInTestParams> {
            return Stream.of(
                ListDivideInTestParams(
                    input = (1..15).toList(),
                    divideIn = 5,
                    ranges = arrayOf(0 to 3, 3 to 6, 6 to 9, 9 to 12, 12 to 15),
                ),
                ListDivideInTestParams(
                    input = (100..116).toList(),
                    divideIn = 5,
                    ranges = arrayOf(0 to 4, 4 to 8, 8 to 11, 11 to 14, 14 to 17)
                ),
                ListDivideInTestParams(
                    input = (100..118).toList(),
                    divideIn = 5,
                    ranges = arrayOf(0 to 4, 4 to 8, 8 to 12, 12 to 16, 16 to 19)
                ),
                ListDivideInTestParams(
                    input = listOf(1, 2, 3),
                    divideIn = 5,
                    ranges = arrayOf(0 to 1, 1 to 2, 2 to 3)
                ),
                ListDivideInTestParams(
                    input = emptyList(),
                    divideIn = 5
                )
            )
        }
    }
}

class ListDivideInTestParams(
    val input: List<Int>,
    val divideIn: Int,
    ranges: Array<Pair<Int, Int>> = emptyArray(),
) {
    val expectedSubLists: List<List<Int>> =
        ranges.map { (from, to) ->
            input.subList(from, to)
        }

    override fun toString(): String {
        return "$divideIn, $input"
    }
}