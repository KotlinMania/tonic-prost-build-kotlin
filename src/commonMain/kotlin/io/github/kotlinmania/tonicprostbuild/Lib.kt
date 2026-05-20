// port-lint: source lib.rs
package io.github.kotlinmania.tonicprostbuild

/**
 * Prost build integration for tonic.
 *
 * This crate provides code generation for gRPC services using protobuf definitions
 * through the prost ecosystem.
 */

/**
 * Configure tonic-prost-build code generation.
 */
public fun configure(): Builder =
    Builder()

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

internal data class TonicBuildMethod(
    val inputType: String,
    val outputType: String,
    val codecPath: String = "tonic_prost::ProstCodec",
) {
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
    public fun outDir(outDir: String): Builder =
        copy(outDir = outDir)

    /**
     * Declare an externally provided Protobuf package or type.
     */
    public fun externPath(protoPath: String, kotlinPath: String): Builder =
        copy(externPath = externPath + (protoPath to kotlinPath))

    /**
     * Add an attribute to matched fields.
     */
    public fun fieldAttribute(path: String, attribute: String): Builder =
        copy(fieldAttributes = fieldAttributes + (path to attribute))

    /**
     * Add an attribute to matched messages.
     */
    public fun messageAttribute(path: String, attribute: String): Builder =
        copy(messageAttributes = messageAttributes + (path to attribute))

    /**
     * Add an attribute to matched enums.
     */
    public fun enumAttribute(path: String, attribute: String): Builder =
        copy(enumAttributes = enumAttributes + (path to attribute))

    /**
     * Add an attribute to matched messages, enums, and one-of declarations.
     */
    public fun typeAttribute(path: String, attribute: String): Builder =
        copy(typeAttributes = typeAttributes + (path to attribute))

    /**
     * Add a field that should be boxed.
     */
    public fun boxed(path: String): Builder =
        copy(boxed = boxed + path)

    /**
     * Configure map fields that should be generated as sorted maps.
     */
    public fun btreeMap(path: String): Builder =
        copy(btreeMap = btreeMap.orEmpty() + path)

    /**
     * Configure bytes fields.
     */
    public fun bytes(path: String): Builder =
        copy(bytes = bytes.orEmpty() + path)

    /**
     * Add an attribute to matched server modules.
     */
    public fun serverModAttribute(path: String, attribute: String): Builder =
        copy(serverAttributes = serverAttributes.pushModule(path, attribute))

    /**
     * Add an attribute to matched service servers.
     */
    public fun serverAttribute(path: String, attribute: String): Builder =
        copy(serverAttributes = serverAttributes.pushStruct(path, attribute))

    /**
     * Add an attribute to matched server traits.
     */
    public fun traitAttribute(path: String, attribute: String): Builder =
        copy(serverAttributes = serverAttributes.pushTrait(path, attribute))

    /**
     * Add an attribute to matched client modules.
     */
    public fun clientModAttribute(path: String, attribute: String): Builder =
        copy(clientAttributes = clientAttributes.pushModule(path, attribute))

    /**
     * Add an attribute to matched service clients.
     */
    public fun clientAttribute(path: String, attribute: String): Builder =
        copy(clientAttributes = clientAttributes.pushStruct(path, attribute))

    /**
     * Set the path to generated Protobuf types in the module tree.
     */
    public fun protoPath(protoPath: String): Builder =
        copy(protoPath = protoPath)

    /**
     * Enable or disable compiling well-known Protobuf types.
     */
    public fun compileWellKnownTypes(enable: Boolean): Builder =
        copy(compileWellKnownTypes = enable)

    /**
     * Enable or disable emitting package information.
     */
    public fun emitPackage(enable: Boolean): Builder =
        copy(emitPackage = enable)

    /**
     * Set the output file path used to write the file descriptor set.
     */
    public fun fileDescriptorSetPath(path: String): Builder =
        copy(fileDescriptorSetPath = path)

    /**
     * Skip compiling protos and generate code from a provided descriptor set.
     */
    public fun skipProtocRun(): Builder =
        copy(skipProtocRun = true)

    /**
     * Add an extra protoc argument.
     */
    public fun protocArg(arg: String): Builder =
        copy(protocArgs = protocArgs + arg)

    /**
     * Set the include file path.
     */
    public fun includeFile(path: String): Builder =
        copy(includeFile = path)

    /**
     * Control generation of build-system rerun hints in output files.
     */
    public fun emitRerunIfChanged(enable: Boolean): Builder =
        copy(emitRerunIfChanged = enable)

    /**
     * Set service and method paths whose generated comments should be disabled.
     */
    public fun disableComments(paths: Iterable<String>): Builder =
        copy(disableComments = disableComments + paths)

    /**
     * Use a shared self receiver on the server trait.
     */
    public fun useArcSelf(enable: Boolean): Builder =
        copy(useArcSelf = enable)

    /**
     * Generate the default stubs for gRPC services.
     */
    public fun generateDefaultStubs(enable: Boolean): Builder =
        copy(generateDefaultStubs = enable)

    /**
     * Set the codec path for generated gRPC services.
     */
    public fun codecPath(path: String): Builder =
        copy(codecPath = path)

    /**
     * Configure paths where generated request and response Debug implementations are retained.
     */
    public fun skipDebug(paths: Iterable<String>): Builder =
        copy(skipDebug = skipDebug + paths)
}
