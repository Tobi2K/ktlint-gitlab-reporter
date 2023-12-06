package reporter

import com.pinterest.ktlint.cli.reporter.core.api.ReporterV2
import com.pinterest.ktlint.cli.reporter.core.api.KtlintCliError
import com.pinterest.ktlint.cli.reporter.core.api.KtlintCliError.Status.FORMAT_IS_AUTOCORRECTED
import org.apache.commons.text.StringEscapeUtils
import java.io.File
import java.io.PrintStream
import java.math.BigInteger
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap
import java.security.MessageDigest


class JsonReporter(private val out: PrintStream) : ReporterV2 {

    private val acc = ConcurrentHashMap<String, MutableList<KtlintCliError>>()

    override fun onLintError(file: String, ktlintCliError: KtlintCliError) {
        if (ktlintCliError.status != FORMAT_IS_AUTOCORRECTED) {
            acc.getOrPut(file) { ArrayList() }.add(ktlintCliError)
        }
    }

    override fun afterAll() {
        val md = MessageDigest.getInstance("MD5")
        out.println("""[""")

        out.println(acc.entries.sortedBy { it.key }.map { entry ->
            val (file, errList) = entry

            errList.map { err ->
                val line = err.line
                val ruleId = err.ruleId
                val detail = err.detail

                val name = "${File(file).nameWithoutExtension}:$line - $detail"
                val description = StringEscapeUtils.escapeJson("$name (Rule: $ruleId)")
                val fingerprint = String.format("%032x", BigInteger(1, md.digest(description.toByteArray(Charsets.UTF_8))))

                val rootPath = Paths.get("").toAbsolutePath()
                val filePath = File(file).toPath()
                val relativeFilePath = rootPath.relativize(filePath).toString().replace(File.separatorChar, '/')

                """
                |    {
                |        "type": "issue",
                |        "check_name": "$ruleId",
                |        "description": "$description",
                |        "categories": ["Style"],
                |        "location": {
                |            "path": "$relativeFilePath",
                |            "lines": {
                |                "begin": $line
                |            }
                |        },
                |        "severity": "major",
                |        "fingerprint": "$fingerprint"
                |    }""".trimMargin()
            }
        }.flatten().joinToString(",\n"))

        out.println("""]""")
    }
}
