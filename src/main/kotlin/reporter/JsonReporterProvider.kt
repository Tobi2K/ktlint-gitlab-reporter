package reporter

import com.pinterest.ktlint.cli.reporter.core.api.ReporterProviderV2
import java.io.PrintStream

class JsonReporterProvider : ReporterProviderV2<JsonReporter> {
    override val id: String = "gitlab"
    override fun get(out: PrintStream, opt: Map<String, String>): JsonReporter {
        return JsonReporter(out)
    }
}
