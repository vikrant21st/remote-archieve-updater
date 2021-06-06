package jetbrains.compose.classfileupdator.service

import jetbrains.compose.classfileupdator.model.anyFile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.ParameterizedTest.ARGUMENTS_WITH_NAMES_PLACEHOLDER
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class FilterFilesKtTest {
    @ParameterizedTest(name = "Is camel cased? $ARGUMENTS_WITH_NAMES_PLACEHOLDER")
    @MethodSource("camelCaseTestsInput")
    fun `is string camel cased`(input: Pair<String, Boolean>) {
        val (string, expectedResult) = input
        assertEquals(string.isCamelCased(), expectedResult)
    }

    @ParameterizedTest(name = "Is inner class? $ARGUMENTS_WITH_NAMES_PLACEHOLDER")
    @MethodSource("innerClassTestsInput")
    fun `is an inner class`(input: Pair<String, Boolean>) {
        val (className, expectedResult) = input
        assertEquals(
            !(anyFile(className, baseDirectory = "").notAnInnerClass),
            expectedResult
        )
    }

    companion object {
        @Suppress("unused")
        @JvmStatic
        fun camelCaseTestsInput(): Stream<Pair<String, Boolean>> = Stream.of(
            "MyName" to true,
            "IAmMan" to true,
            "doThat" to true,
            "gama" to false
        )

        @Suppress("unused")
        @JvmStatic
        fun innerClassTestsInput(): Stream<Pair<String, Boolean>> = Stream.of(
            "UtilClass\$Inner.class" to true,
            "UtilClass.class" to false,
            "Util\$3.class" to true,
        )
    }
}