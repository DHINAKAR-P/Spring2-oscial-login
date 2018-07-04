package com.zimidy.api

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.util.StreamUtils.copyToString
import org.springframework.web.bind.annotation.RequestMapping
import java.nio.charset.Charset
import javax.servlet.http.HttpServletResponse

@Component
@ConfigurationProperties(prefix = PACKAGE)
class AppProperties {

    lateinit var env: String
    var metadata = Metadata()
    var web = Web()
    var superAdmin = SuperAdmin()

    companion object Env {
        const val DEV = "dev"
        const val STAGING = "staging"
        const val PROD = "prod"
        const val EXP = "exp"
    }

    class Metadata {
        var build: Build = Build()
        var commit: Commit = Commit()

        class Build {
            lateinit var timestamp: String
        }

        class Commit {
            lateinit var timestamp: String
            lateinit var branch: String
            lateinit var revision: String
        }
    }

    class SuperAdmin {
        lateinit var defaultFirstName: String
        lateinit var defaultLastName: String
        lateinit var defaultEmail: String
        lateinit var defaultPassword: String
    }

    class Web {
        lateinit var protocol: String
        lateinit var domain: String
        lateinit var port: String
    }
}

@Controller
class GraphQLBrowserController {
    private val html: String = copyToString(ClassPathResource("browser.html").inputStream, Charset.defaultCharset())
    @RequestMapping(value = ["/browser"])
    fun graphiql(response: HttpServletResponse) {
        response.contentType = "text/html; charset=UTF-8"
        response.outputStream.write(html.toByteArray(Charset.defaultCharset()))
    }
}
