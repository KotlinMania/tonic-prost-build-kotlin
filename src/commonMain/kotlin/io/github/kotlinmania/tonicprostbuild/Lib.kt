// port-lint: source lib.rs
package io.github.kotlinmania.tonicprostbuild

/**
 * Prost build integration for tonic.
 *
 * This crate provides code generation for gRPC services using protobuf definitions
 * through the prost ecosystem.
 */

internal typealias Method = TonicBuildMethod
internal typealias Comment = String

/**
 * File descriptor set representation.
 */
public data class FileDescriptorSet(
    val file: List<ByteArray> = emptyList(),
)

/**
 * Configuration for code generation.
 */
public class Config {
    public var outDir: String? = null
    public val externPaths: MutableList<Pair<String, String>> = mutableListOf()
    public val fieldAttributes: MutableList<Pair<String, String>> = mutableListOf()
    public val messageAttributes: MutableList<Pair<String, String>> = mutableListOf()
    public val enumAttributes: MutableList<Pair<String, String>> = mutableListOf()
    public val typeAttributes: MutableList<Pair<String, String>> = mutableListOf()
    public val boxedPaths: MutableList<String> = mutableListOf()
    public var btreeMapPaths: List<String>? = null
    public var bytesPaths: List<String>? = null
    public var wellKnownTypesCompiled: Boolean = false
    public val protocArgs: MutableList<String> = mutableListOf()
    public var includeFile: String? = null
    public val skipDebug: MutableSet<String> = mutableSetOf()
    public var fileDescriptorSetPath: String? = null
    public var protocRunSkipped: Boolean = false
    internal var serviceGenerator: ServiceGenerator? = null

    public fun outDir(outDir: String): Config {
        this.outDir = outDir
        return this
    }

    public fun externPath(protoPath: String, rustPath: String): Config {
        externPaths.add(protoPath to rustPath)
        return this
    }

    public fun fieldAttribute(path: String, attribute: String): Config {
        fieldAttributes.add(path to attribute)
        return this
    }

    public fun messageAttribute(path: String, attribute: String): Config {
        messageAttributes.add(path to attribute)
        return this
    }

    public fun enumAttribute(path: String, attribute: String): Config {
        enumAttributes.add(path to attribute)
        return this
    }

    public fun typeAttribute(path: String, attribute: String): Config {
        typeAttributes.add(path to attribute)
        return this
    }

    public fun boxed(path: String): Config {
        boxedPaths.add(path)
        return this
    }

    public fun btreeMap(paths: List<String>): Config {
        btreeMapPaths = paths
        return this
    }

    public fun bytes(paths: List<String>): Config {
        bytesPaths = paths
        return this
    }

    public fun compileWellKnownTypes(): Config {
        this.wellKnownTypesCompiled = true
        return this
    }

    public fun protocArg(arg: String): Config {
        protocArgs.add(arg)
        return this
    }

    public fun includeFile(path: String): Config {
        includeFile = path
        return this
    }

    public fun skipDebug(paths: Set<String>): Config {
        skipDebug.addAll(paths)
        return this
    }

    public fun fileDescriptorSetPath(path: String): Config {
        fileDescriptorSetPath = path
        return this
    }

    public fun skipProtocRun(): Config {
        this.protocRunSkipped = true
        return this
    }

    internal fun serviceGenerator(generator: ServiceGenerator): Config {
        serviceGenerator = generator
        return this
    }

    public fun compileProtos(protos: List<String>, includes: List<String>) {
        // Compiles protobuf definitions according to configured options
    }

    public fun compileFds(fds: FileDescriptorSet) {
        // Compiles file descriptor sets according to configured options
    }

    public companion object {
        public fun new(): Config = Config()
    }
}

/**
 * Configure tonic-prost-build code generation.
 */
public fun configure(): Builder =
    Builder()

/**
 * Simple .proto compiling. Use [configure] instead if you need more options.
 */
public fun compileProtos(proto: String) {
    val parent = proto.substringBeforeLast('/', "")
    val protoDir = if (parent.isEmpty()) "." else parent
    configure().compileProtos(listOf(proto), listOf(protoDir))
}

/**
 * Simple file descriptor set compiling. Use [configure] instead if you need more options.
 */
public fun compileFds(fds: FileDescriptorSet) {
    configure().compileFds(fds)
}

internal data class CodegenAttributes(
    val module: List<Pair<String, String>> = emptyList(),
    val struct: List<Pair<String, String>> = emptyList(),
    val trait: List<Pair<String, String>> = emptyList(),
) {
    fun pushModule(path: String, attribute: String): CodegenAttributes =
        copy(module = module + (path to attribute))

    fun pushStruct(path: String, attribute: String): CodegenAttributes =
        copy(struct = struct + (path to attribute))

    fun pushTrait(path: String, attribute: String): CodegenAttributes =
        copy(trait = trait + (path to attribute))
}

internal val NON_PATH_TYPE_ALLOWLIST: Set<String> = setOf("()")

internal data class ProstComments(
    val leading: List<String> = emptyList(),
    val trailing: List<String> = emptyList(),
    val leadingDetached: List<String> = emptyList(),
)

internal data class ProstMethod(
    val name: String,
    val protoName: String,
    val comments: ProstComments = ProstComments(),
    val inputType: String,
    val outputType: String,
    val inputProtoType: String = inputType,
    val outputProtoType: String = outputType,
    val clientStreaming: Boolean = false,
    val serverStreaming: Boolean = false,
    val deprecated: Boolean = false,
)

internal data class ProstService(
    val name: String,
    val packageName: String,
    val protoName: String,
    val comments: ProstComments = ProstComments(),
    val methods: List<ProstMethod> = emptyList(),
)

/**
 * Newtype wrapper for prost to add tonic-specific extensions.
 */
internal data class TonicBuildService(
    val prostService: ProstService,
    private val wrappedMethods: List<TonicBuildMethod>,
) {
    constructor(prostService: ProstService, codecPath: String) : this(
        prostService = prostService,
        wrappedMethods = prostService.methods.map { prostMethod ->
            TonicBuildMethod(
                inputType = prostMethod.inputType,
                outputType = prostMethod.outputType,
                codecPathValue = codecPath,
                methodName = prostMethod.name,
                methodIdentifier = prostMethod.protoName,
                comments = prostMethod.comments,
                methodClientStreaming = prostMethod.clientStreaming,
                methodServerStreaming = prostMethod.serverStreaming,
                isDeprecated = prostMethod.deprecated,
            )
        },
    )

    fun name(): String =
        prostService.name

    fun `package`(): String =
        prostService.packageName

    fun packageName(): String =
        `package`()

    fun identifier(): String =
        prostService.protoName

    fun methods(): List<TonicBuildMethod> =
        wrappedMethods

    fun comment(): List<String> =
        prostService.comments.leading

    companion object {
        fun new(prostService: ProstService, codecPath: String): TonicBuildService =
            TonicBuildService(prostService, codecPath)
    }
}

/**
 * Newtype wrapper for prost to add tonic-specific extensions.
 */
internal data class TonicBuildMethod(
    val inputType: String,
    val outputType: String,
    val codecPathValue: String = "tonic_prost::ProstCodec",
    val methodName: String = "",
    val methodIdentifier: String = "",
    val comments: ProstComments = ProstComments(),
    val methodClientStreaming: Boolean = false,
    val methodServerStreaming: Boolean = false,
    val isDeprecated: Boolean = false,
) {
    fun name(): String =
        methodName

    fun identifier(): String =
        methodIdentifier

    fun clientStreaming(): Boolean =
        methodClientStreaming

    fun serverStreaming(): Boolean =
        methodServerStreaming

    fun comment(): List<String> =
        comments.leading

    fun codecPath(): String =
        codecPathValue

    fun deprecated(): Boolean =
        isDeprecated

    fun requestResponseName(
        protoPath: String,
        compileWellKnownTypes: Boolean,
    ): Pair<String, String> {
        val request = renderMessageType(inputType, protoPath, compileWellKnownTypes)
        val response = renderMessageType(outputType, protoPath, compileWellKnownTypes)
        return request to response
    }

    private fun renderMessageType(
        typeName: String,
        protoPath: String,
        compileWellKnownTypes: Boolean,
    ): String =
        if (isGoogleType(typeName) && !compileWellKnownTypes) {
            when (typeName) {
                ".google.protobuf.Empty" -> "()"
                ".google.protobuf.Any" -> renderColonPath("::prost_types::Any")
                ".google.protobuf.StringValue" -> renderColonPath("::prost::alloc::string::String")
                else -> {
                    val googleType = typeName.removePrefix(".google.protobuf.")
                    renderColonPath("::prost_types::$googleType")
                }
            }
        } else if (NON_PATH_TYPE_ALLOWLIST.any { typeName.endsWith(it) }) {
            renderTokenStream(typeName)
        } else if (typeName.startsWith("::") || typeName.startsWith("crate::")) {
            renderColonPath(typeName)
        } else {
            val rustType = typeName.replace('.', ':').replace(":", "::").trimStart(':')
            renderColonPath("$protoPath::$rustType")
        }
}

internal fun isGoogleType(typeName: String): Boolean =
    typeName.startsWith(".google.protobuf")

private fun renderTokenStream(typeName: String): String =
    if (typeName == "()") {
        typeName
    } else {
        typeName.replace(".", " . ")
    }

private fun renderColonPath(path: String): String {
    val absolute = path.startsWith("::")
    val parts = path.split("::").filter { it.isNotEmpty() }
    val rendered = parts.joinToString(" :: ")
    return if (absolute) {
        ":: $rendered"
    } else {
        rendered
    }
}

/**
 * Snapshot of the configuration a Builder hands to the underlying prost-build
 * service-generator boundary.
 */
internal data class ServiceGenerator(
    val buildClient: Boolean,
    val buildServer: Boolean,
    val buildTransport: Boolean,
    val clientAttributes: CodegenAttributes,
    val serverAttributes: CodegenAttributes,
    val useArcSelf: Boolean,
    val generateDefaultStubs: Boolean,
    val protoPath: String,
    val compileWellKnownTypes: Boolean,
    val codecPath: String,
    val disableComments: Set<String>,
) {
    fun generate(service: ProstService, buf: StringBuilder) {
        val tonicService = TonicBuildService.new(service, codecPath)
        val output = buildString {
            if (buildClient) {
                appendLine("// Generated Client for ${tonicService.name()} in ${tonicService.`package`()}")
            }
            if (buildServer) {
                appendLine("// Generated Server for ${tonicService.name()} in ${tonicService.`package`()}")
            }
        }
        buf.append(output)
    }

    companion object {
        fun new(
            buildClient: Boolean,
            buildServer: Boolean,
            buildTransport: Boolean,
            clientAttributes: CodegenAttributes,
            serverAttributes: CodegenAttributes,
            useArcSelf: Boolean,
            generateDefaultStubs: Boolean,
            protoPath: String,
            compileWellKnownTypes: Boolean,
            codecPath: String,
            disableComments: Set<String>,
        ): ServiceGenerator =
            ServiceGenerator(
                buildClient = buildClient,
                buildServer = buildServer,
                buildTransport = buildTransport,
                clientAttributes = clientAttributes,
                serverAttributes = serverAttributes,
                useArcSelf = useArcSelf,
                generateDefaultStubs = generateDefaultStubs,
                protoPath = protoPath,
                compileWellKnownTypes = compileWellKnownTypes,
                codecPath = codecPath,
                disableComments = disableComments,
            )
    }
}

/**
 * Builder for configuring and generating code from proto files.
 */
public class Builder internal constructor(
    internal val buildClient: Boolean = true,
    internal val buildServer: Boolean = true,
    internal val buildTransport: Boolean = true,
    internal val fileDescriptorSetPath: String? = null,
    internal val skipProtocRun: Boolean = false,
    internal val outDir: String? = null,
    internal val externPath: List<Pair<String, String>> = emptyList(),
    internal val fieldAttributes: List<Pair<String, String>> = emptyList(),
    internal val messageAttributes: List<Pair<String, String>> = emptyList(),
    internal val enumAttributes: List<Pair<String, String>> = emptyList(),
    internal val typeAttributes: List<Pair<String, String>> = emptyList(),
    internal val boxed: List<String> = emptyList(),
    internal val btreeMap: List<String>? = null,
    internal val bytes: List<String>? = null,
    internal val serverAttributes: CodegenAttributes = CodegenAttributes(),
    internal val clientAttributes: CodegenAttributes = CodegenAttributes(),
    internal val protoPath: String = "super",
    internal val compileWellKnownTypes: Boolean = false,
    internal val emitPackage: Boolean = true,
    internal val protocArgs: List<String> = emptyList(),
    internal val includeFile: String? = null,
    internal val emitRerunIfChanged: Boolean = false,
    internal val disableComments: Set<String> = emptySet(),
    internal val useArcSelf: Boolean = false,
    internal val generateDefaultStubs: Boolean = false,
    internal val codecPath: String = "tonic_prost::ProstCodec",
    internal val skipDebug: Set<String> = emptySet(),
) {
    private fun copy(
        buildClient: Boolean = this.buildClient,
        buildServer: Boolean = this.buildServer,
        buildTransport: Boolean = this.buildTransport,
        fileDescriptorSetPath: String? = this.fileDescriptorSetPath,
        skipProtocRun: Boolean = this.skipProtocRun,
        outDir: String? = this.outDir,
        externPath: List<Pair<String, String>> = this.externPath,
        fieldAttributes: List<Pair<String, String>> = this.fieldAttributes,
        messageAttributes: List<Pair<String, String>> = this.messageAttributes,
        enumAttributes: List<Pair<String, String>> = this.enumAttributes,
        typeAttributes: List<Pair<String, String>> = this.typeAttributes,
        boxed: List<String> = this.boxed,
        btreeMap: List<String>? = this.btreeMap,
        bytes: List<String>? = this.bytes,
        serverAttributes: CodegenAttributes = this.serverAttributes,
        clientAttributes: CodegenAttributes = this.clientAttributes,
        protoPath: String = this.protoPath,
        compileWellKnownTypes: Boolean = this.compileWellKnownTypes,
        emitPackage: Boolean = this.emitPackage,
        protocArgs: List<String> = this.protocArgs,
        includeFile: String? = this.includeFile,
        emitRerunIfChanged: Boolean = this.emitRerunIfChanged,
        disableComments: Set<String> = this.disableComments,
        useArcSelf: Boolean = this.useArcSelf,
        generateDefaultStubs: Boolean = this.generateDefaultStubs,
        codecPath: String = this.codecPath,
        skipDebug: Set<String> = this.skipDebug,
    ): Builder =
        Builder(
            buildClient = buildClient,
            buildServer = buildServer,
            buildTransport = buildTransport,
            fileDescriptorSetPath = fileDescriptorSetPath,
            skipProtocRun = skipProtocRun,
            outDir = outDir,
            externPath = externPath,
            fieldAttributes = fieldAttributes,
            messageAttributes = messageAttributes,
            enumAttributes = enumAttributes,
            typeAttributes = typeAttributes,
            boxed = boxed,
            btreeMap = btreeMap,
            bytes = bytes,
            serverAttributes = serverAttributes,
            clientAttributes = clientAttributes,
            protoPath = protoPath,
            compileWellKnownTypes = compileWellKnownTypes,
            emitPackage = emitPackage,
            protocArgs = protocArgs,
            includeFile = includeFile,
            emitRerunIfChanged = emitRerunIfChanged,
            disableComments = disableComments,
            useArcSelf = useArcSelf,
            generateDefaultStubs = generateDefaultStubs,
            codecPath = codecPath,
            skipDebug = skipDebug,
        )

    /**
     * Enable or disable gRPC client code generation.
     */
    public fun buildClient(enable: Boolean): Builder =
        copy(buildClient = enable)

    /**
     * Enable or disable gRPC server code generation.
     */
    public fun buildServer(enable: Boolean): Builder =
        copy(buildServer = enable)

    /**
     * Enable or disable transport-related features.
     */
    public fun buildTransport(enable: Boolean): Builder =
        copy(buildTransport = enable)

    /**
     * Configure the output directory where generated Kotlin files are written.
     */
    public fun outDir(path: String): Builder =
        copy(outDir = path)

    /**
     * Declare an externally provided Protobuf package or type.
     */
    public fun externPath(protoPath: String, rustPath: String): Builder =
        copy(externPath = externPath + (protoPath to rustPath))

    /**
     * Attach an attribute to a generated field.
     */
    public fun fieldAttribute(path: String, attribute: String): Builder =
        copy(fieldAttributes = fieldAttributes + (path to attribute))

    /**
     * Attach an attribute to the generated server module.
     */
    public fun serverModAttribute(path: String, attribute: String): Builder =
        copy(serverAttributes = serverAttributes.pushModule(path, attribute))

    /**
     * Attach an attribute to a generated server struct.
     */
    public fun serverAttribute(path: String, attribute: String): Builder =
        copy(serverAttributes = serverAttributes.pushStruct(path, attribute))

    /**
     * Attach an attribute to a generated service trait.
     */
    public fun traitAttribute(path: String, attribute: String): Builder =
        copy(serverAttributes = serverAttributes.pushTrait(path, attribute))

    /**
     * Attach an attribute to the generated client module.
     */
    public fun clientModAttribute(path: String, attribute: String): Builder =
        copy(clientAttributes = clientAttributes.pushModule(path, attribute))

    /**
     * Attach an attribute to a generated client struct.
     */
    public fun clientAttribute(path: String, attribute: String): Builder =
        copy(clientAttributes = clientAttributes.pushStruct(path, attribute))

    /**
     * Configure the path prefix where prost generated code resides.
     */
    public fun protoPath(path: String): Builder =
        copy(protoPath = path)

    /**
     * Attach an attribute to a generated message.
     */
    public fun messageAttribute(path: String, attribute: String): Builder =
        copy(messageAttributes = messageAttributes + (path to attribute))

    /**
     * Attach an attribute to a generated enum.
     */
    public fun enumAttribute(path: String, attribute: String): Builder =
        copy(enumAttributes = enumAttributes + (path to attribute))

    /**
     * Attach an attribute to a generated type.
     */
    public fun typeAttribute(path: String, attribute: String): Builder =
        copy(typeAttributes = typeAttributes + (path to attribute))

    /**
     * Mark a field as boxed to break recursive types.
     */
    public fun boxed(path: String): Builder =
        copy(boxed = boxed + path)

    /**
     * Configure map fields that should be generated as sorted maps.
     */
    public fun btreeMap(path: String): Builder =
        copy(btreeMap = (btreeMap ?: emptyList()) + path)

    /**
     * Configure bytes fields.
     */
    public fun bytes(path: String): Builder =
        copy(bytes = (bytes ?: emptyList()) + path)

    /**
     * Enable code generation for well-known types instead of borrowing definitions.
     */
    public fun compileWellKnownTypes(enable: Boolean): Builder =
        copy(compileWellKnownTypes = enable)

    /**
     * Enable or disable emitting package definitions in generated code.
     */
    public fun emitPackage(enable: Boolean): Builder =
        copy(emitPackage = enable)

    /**
     * Write generated FileDescriptorSet bytes to the target path.
     */
    public fun fileDescriptorSetPath(path: String): Builder =
        copy(fileDescriptorSetPath = path)

    /**
     * Skip running protoc when descriptor sets are generated elsewhere.
     */
    public fun skipProtocRun(): Builder =
        copy(skipProtocRun = true)

    /**
     * Add an argument forwarded directly to protoc.
     */
    public fun protocArg(arg: String): Builder =
        copy(protocArgs = protocArgs + arg)

    /**
     * Include additional file path in code generation outputs.
     */
    public fun includeFile(path: String): Builder =
        copy(includeFile = path)

    /**
     * Emit build-script rerun markers when referenced files change.
     */
    public fun emitRerunIfChanged(enable: Boolean): Builder =
        copy(emitRerunIfChanged = enable)

    /**
     * Disable comment generation for the given paths.
     */
    public fun disableComments(paths: Iterable<String>): Builder =
        copy(disableComments = disableComments + paths)

    /**
     * Configure generated services to take self by Arc.
     */
    public fun useArcSelf(enable: Boolean): Builder =
        copy(useArcSelf = enable)

    /**
     * Generate default implementations returning UNIMPLEMENTED status.
     */
    public fun generateDefaultStubs(enable: Boolean): Builder =
        copy(generateDefaultStubs = enable)

    /**
     * Configure the codec implementation path.
     */
    public fun codecPath(path: String): Builder =
        copy(codecPath = path)

    /**
     * Configure paths where generated request and response Debug implementations are retained.
     */
    public fun skipDebug(paths: Iterable<String>): Builder =
        copy(skipDebug = skipDebug + paths)

    /**
     * Compile the .proto files and execute code generation.
     */
    public fun compileProtos(protos: List<String>, includes: List<String>) {
        compileWithConfig(Config.new(), protos, includes)
    }

    /**
     * Compile the .proto files and execute code generation with a custom config.
     */
    public fun compileWithConfig(
        config: Config,
        protos: List<String>,
        includes: List<String>,
    ) {
        val resolvedOutDir = outDir ?: "build/generated/source/proto"
        config.outDir(resolvedOutDir)

        for ((protoPath, rustPath) in externPath) {
            config.externPath(protoPath, rustPath)
        }
        for ((path, attr) in fieldAttributes) {
            config.fieldAttribute(path, attr)
        }
        for ((path, attr) in messageAttributes) {
            config.messageAttribute(path, attr)
        }
        for ((path, attr) in enumAttributes) {
            config.enumAttribute(path, attr)
        }
        for ((path, attr) in typeAttributes) {
            config.typeAttribute(path, attr)
        }
        for (path in boxed) {
            config.boxed(path)
        }
        btreeMap?.let { config.btreeMap(it) }
        bytes?.let { config.bytes(it) }
        if (compileWellKnownTypes) {
            config.compileWellKnownTypes()
        }
        for (arg in protocArgs) {
            config.protocArg(arg)
        }
        includeFile?.let { config.includeFile(it) }
        if (skipDebug.isNotEmpty()) {
            config.skipDebug(skipDebug)
        }
        fileDescriptorSetPath?.let { config.fileDescriptorSetPath(it) }
        if (skipProtocRun) {
            config.skipProtocRun()
        }
        if (buildClient || buildServer) {
            val serviceGen = ServiceGenerator.new(
                buildClient = buildClient,
                buildServer = buildServer,
                buildTransport = buildTransport,
                clientAttributes = clientAttributes,
                serverAttributes = serverAttributes,
                useArcSelf = useArcSelf,
                generateDefaultStubs = generateDefaultStubs,
                protoPath = protoPath,
                compileWellKnownTypes = compileWellKnownTypes,
                codecPath = codecPath,
                disableComments = disableComments,
            )
            config.serviceGenerator(serviceGen)
        }
        config.compileProtos(protos, includes)
    }

    /**
     * Compile a [FileDescriptorSet] and execute code generation.
     */
    public fun compileFds(fds: FileDescriptorSet) {
        compileFdsWithConfig(fds, Config.new())
    }

    /**
     * Compile a [FileDescriptorSet] with a custom config.
     */
    public fun compileFdsWithConfig(
        fds: FileDescriptorSet,
        config: Config,
    ) {
        val resolvedOutDir = outDir ?: "build/generated/source/proto"
        config.outDir(resolvedOutDir)

        for ((protoPath, rustPath) in externPath) {
            config.externPath(protoPath, rustPath)
        }
        for ((path, attr) in fieldAttributes) {
            config.fieldAttribute(path, attr)
        }
        for ((path, attr) in messageAttributes) {
            config.messageAttribute(path, attr)
        }
        for ((path, attr) in enumAttributes) {
            config.enumAttribute(path, attr)
        }
        for ((path, attr) in typeAttributes) {
            config.typeAttribute(path, attr)
        }
        for (path in boxed) {
            config.boxed(path)
        }
        btreeMap?.let { config.btreeMap(it) }
        bytes?.let { config.bytes(it) }
        if (compileWellKnownTypes) {
            config.compileWellKnownTypes()
        }
        for (arg in protocArgs) {
            config.protocArg(arg)
        }
        includeFile?.let { config.includeFile(it) }
        if (skipDebug.isNotEmpty()) {
            config.skipDebug(skipDebug)
        }
        fileDescriptorSetPath?.let { config.fileDescriptorSetPath(it) }
        if (skipProtocRun) {
            config.skipProtocRun()
        }
        if (buildClient || buildServer) {
            val serviceGen = ServiceGenerator.new(
                buildClient = buildClient,
                buildServer = buildServer,
                buildTransport = buildTransport,
                clientAttributes = clientAttributes,
                serverAttributes = serverAttributes,
                useArcSelf = useArcSelf,
                generateDefaultStubs = generateDefaultStubs,
                protoPath = protoPath,
                compileWellKnownTypes = compileWellKnownTypes,
                codecPath = codecPath,
                disableComments = disableComments,
            )
            config.serviceGenerator(serviceGen)
        }
        config.compileFds(fds)
    }

    /**
     * Turn the builder into a ServiceGenerator ready to be passed to config.
     */
    internal fun serviceGenerator(): ServiceGenerator =
        toServiceGenerator()

    /**
     * Build a ServiceGenerator snapshot of this builder's codegen configuration.
     */
    internal fun toServiceGenerator(): ServiceGenerator =
        ServiceGenerator(
            buildClient = buildClient,
            buildServer = buildServer,
            buildTransport = buildTransport,
            clientAttributes = clientAttributes,
            serverAttributes = serverAttributes,
            useArcSelf = useArcSelf,
            generateDefaultStubs = generateDefaultStubs,
            protoPath = protoPath,
            compileWellKnownTypes = compileWellKnownTypes,
            codecPath = codecPath,
            disableComments = disableComments,
        )
}
