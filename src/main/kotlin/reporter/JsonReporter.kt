package reporter

import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.core.Reporter
import com.pinterest.ktlint.core.internal.relativeRoute
import java.io.File
import java.io.PrintStream
import java.util.concurrent.ConcurrentHashMap


class JsonReporter(private val out: PrintStream) : Reporter {

    private val acc = ConcurrentHashMap<String, MutableList<LintError>>()

    override fun onLintError(file: String, err: LintError, corrected: Boolean) {
        if (!corrected) {
            acc.getOrPut(file) { ArrayList() }.add(err)
        }
    }

    override fun afterAll() {
        out.println("""[""")

        for (entry in acc.entries.sortedBy { it.key }) {
            val (file, errList) = entry
            for ((index, err) in errList.withIndex()) {
                val (line, _, ruleId, detail) = err

                val name = (file.split("/")
                    .last()
                    .split(".")
                    .takeIf { it.size == 2 }
                    ?.firstOrNull()
                    ?.takeIf { it.isNotBlank() }
                    ?.let { "$it:$line - $detail" }
                    ?: detail)

                val relativeFilePath = File(file).relativeRoute

                out.println(
                    """
                    {
                        "description": "$name (Rule: $ruleId)",
                        "severity": "major",
                        "location": {
                            "path": "$relativeFilePath",
                            "lines": {
                                "begin": $line
                            }
                        }
                    }${if (index != (errList.size - 1)) "," else ""}""".trimMargin()
                )
            }
        }

        out.println("""]""")
    }
}
