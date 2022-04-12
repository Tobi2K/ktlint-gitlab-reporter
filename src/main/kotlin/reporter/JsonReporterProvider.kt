package reporter

import com.pinterest.ktlint.core.ReporterProvider
import java.io.PrintStream

class JsonReporterProvider : ReporterProvider<JsonReporter> {
    override val id: String = "gitlab"
    override fun get(out: PrintStream, opt: Map<String, String>): JsonReporter =
        JsonReporter(out)
}
