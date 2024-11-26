import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.Reader
import java.util.TimeZone


class ProcessorJvm(
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger,
    val platforms: List<PlatformInfo>
) : SymbolProcessor {
    var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.warn("Processing... + platforms: $platforms")
        if (invoked) {
            return emptyList()
        }
        invoked = true

        val cfg = Configuration(Configuration.VERSION_2_3_33)
        val resourcePath = "/codegen/templates"

        cfg.setTemplateLoader(object : freemarker.cache.TemplateLoader {
            override fun findTemplateSource(name: String): Any? {
                return javaClass.getResourceAsStream("$resourcePath/$name")
            }

            override fun getLastModified(templateSource: Any?): Long {
                return -1
            }

            override fun getReader(templateSource: Any?, encoding: String?): Reader {
                return InputStreamReader(templateSource as InputStream, encoding)
            }

            override fun closeTemplateSource(templateSource: Any?) {
                (templateSource as InputStream).close()
            }
        })
        // Recommended settings for new projects:
        cfg.setDefaultEncoding("UTF-8")
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER)
        cfg.setLogTemplateExceptions(false)
        cfg.setWrapUncheckedExceptions(true)
        cfg.setFallbackOnNullLoopVariable(false)
        cfg.setSQLDateAndTimeTimeZone(TimeZone.getDefault())

        /* Create a data-model */
        val root = HashMap<String, Any>()
        root["maxScale"] = 18
        root["nlzScaleFactor"] =
            intArrayOf(63, 60, 57, 54, 50, 47, 44, 40, 37, 34, 30, 27, 24, 20, 17, 14, 10, 7, 4)

        if (resolver.getAllFiles().any { it.fileName.contains("MutableDecimalJvm.kt") }) {
            for (i in 0..18) {
                root["scale"] = i
                generateFactoryJvm(cfg, i, root)
                generateDecimalExtensions(cfg, i, root)
            }
        }
        else {
            for (i in 1..18) {
                root["scale"] = i
                generateScale(cfg, i, root)
            }
            for (i in 0..18) {
                root["scale"] = i
                generateDecimal(cfg, i, root)
                generateFactory(cfg, i, root)
                generateMultipliable(cfg, i, root)
                generateMutableDecimal(cfg, i, root)
//            if (platforms.any { it.platformName.lowercase().contains("jvm") })
//            {
//                generateFactoryJvm(cfg, i, root)
//                generateDecimalExtensions(cfg, i, root)
//            }
            }

            val temp = cfg.getTemplate("Multiplier.ftl")

            codeGenerator.createNewFile(
                Dependencies.ALL_FILES,
                "org.decimal4j.exact",
                "Multiplier",
                "kt"
            ).use { output ->
                OutputStreamWriter(output).use { writer ->
                    temp.process(root, writer)
                }
            }
        }




        return emptyList()
    }

    private fun generateScale(cfg: Configuration, i: Int, root: HashMap<String, Any>) {
        val temp = cfg.getTemplate("ScaleNf.ftl")

        codeGenerator.createNewFile(
            Dependencies.ALL_FILES,
            "org.decimal4j.scale",
            "Scale${i}f",
            "kt"
        ).use { output ->
            OutputStreamWriter(output).use { writer ->
                temp.process(root, writer)
            }
        }
    }

    private fun generateFactory(cfg: Configuration, i: Int, root: HashMap<String, Any>) {
        val temp = cfg.getTemplate("FactoryNf.ftl")

        codeGenerator.createNewFile(
            Dependencies.ALL_FILES,
            "org.decimal4j.factory",
            "Factory${i}f",
            "kt"
        ).use { output ->
            OutputStreamWriter(output).use { writer ->
                temp.process(root, writer)
            }
        }
    }

    private fun generateFactoryJvm(cfg: Configuration, i: Int, root: HashMap<String, Any>) {
        val temp = cfg.getTemplate("FactoryNfJvm.ftl")

        codeGenerator.createNewFile(
            Dependencies.ALL_FILES,
            "org.decimal4j.factory",
            "Factory${i}fJvm",
            "kt"
        ).use { output ->
            OutputStreamWriter(output).use { writer ->
                temp.process(root, writer)
            }
        }
    }

    private fun generateMultipliable(cfg: Configuration, i: Int, root: HashMap<String, Any>) {
        val temp = cfg.getTemplate("MultipliableNf.ftl")

        codeGenerator.createNewFile(
            Dependencies.ALL_FILES,
            "org.decimal4j.exact",
            "Multipliable${i}f",
            "kt"
        ).use { output ->
            OutputStreamWriter(output).use { writer ->
                temp.process(root, writer)
            }
        }
    }

    private fun generateDecimal(cfg: Configuration, i: Int, root: HashMap<String, Any>) {
        val temp = cfg.getTemplate("DecimalNf.ftl")

        codeGenerator.createNewFile(
            Dependencies.ALL_FILES,
            "org.decimal4j.immutable",
            "Decimal${i}f",
            "kt"
        ).use { output ->
            OutputStreamWriter(output).use { writer ->
                temp.process(root, writer)
            }
        }
    }

    private fun generateDecimalExtensions(cfg: Configuration, i: Int, root: HashMap<String, Any>) {
        val temp = cfg.getTemplate("DecimalNfExtensions.ftl")

        codeGenerator.createNewFile(
            Dependencies.ALL_FILES,
            "org.decimal4j.immutable",
            "Decimal${i}fExtensions",
            "kt"
        ).use { output ->
            OutputStreamWriter(output).use { writer ->
                temp.process(root, writer)
            }
        }
    }

    private fun generateMutableDecimal(cfg: Configuration, i: Int, root: HashMap<String, Any>) {
        val temp = cfg.getTemplate("MutableDecimalNf.ftl")

        codeGenerator.createNewFile(
            Dependencies.ALL_FILES,
            "org.decimal4j.mutable",
            "MutableDecimal${i}f",
            "kt"
        ).use { output ->
            OutputStreamWriter(output).use { writer ->
                temp.process(root, writer)
            }
        }
    }
}


class ProcessorJvmProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        println("Creating TestProcessor")
        return ProcessorJvm(environment.codeGenerator, environment.logger, environment.platforms)
    }
}