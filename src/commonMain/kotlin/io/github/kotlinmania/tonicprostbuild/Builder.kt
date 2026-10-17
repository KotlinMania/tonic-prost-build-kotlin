package io.github.kotlinmania.tonicprostbuild

/**
 * Configure tonic-prost-build code generation.
 */
public fun configure(): Builder =
    Builder()

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

    public fun buildClient(enable: Boolean): Builder =
        copy(buildClient = enable)

    public fun buildServer(enable: Boolean): Builder =
        copy(buildServer = enable)

    public fun buildTransport(enable: Boolean): Builder =
        copy(buildTransport = enable)

    public fun outDir(path: String): Builder =
        copy(outDir = path)

    public fun externPath(protoPath: String, rustPath: String): Builder =
        copy(externPath = externPath + (protoPath to rustPath))

    public fun fieldAttribute(path: String, attribute: String): Builder =
        copy(fieldAttributes = fieldAttributes + (path to attribute))

    public fun serverModAttribute(path: String, attribute: String): Builder =
        copy(serverAttributes = serverAttributes.pushModule(path, attribute))

    public fun serverAttribute(path: String, attribute: String): Builder =
        copy(serverAttributes = serverAttributes.pushStruct(path, attribute))

    public fun traitAttribute(path: String, attribute: String): Builder =
        copy(serverAttributes = serverAttributes.pushTrait(path, attribute))

    public fun clientModAttribute(path: String, attribute: String): Builder =
        copy(clientAttributes = clientAttributes.pushModule(path, attribute))

    public fun clientAttribute(path: String, attribute: String): Builder =
        copy(clientAttributes = clientAttributes.pushStruct(path, attribute))

    public fun protoPath(path: String): Builder =
        copy(protoPath = path)

    public fun messageAttribute(path: String, attribute: String): Builder =
        copy(messageAttributes = messageAttributes + (path to attribute))

    public fun enumAttribute(path: String, attribute: String): Builder =
        copy(enumAttributes = enumAttributes + (path to attribute))

    public fun typeAttribute(path: String, attribute: String): Builder =
        copy(typeAttributes = typeAttributes + (path to attribute))

    public fun boxed(path: String): Builder =
        copy(boxed = boxed + path)

    public fun btreeMap(path: String): Builder =
        copy(btreeMap = (btreeMap ?: emptyList()) + path)

    public fun bytes(path: String): Builder =
        copy(bytes = (bytes ?: emptyList()) + path)

    public fun compileWellKnownTypes(enable: Boolean): Builder =
        copy(compileWellKnownTypes = enable)

    public fun emitPackage(enable: Boolean): Builder =
        copy(emitPackage = enable)

    public fun fileDescriptorSetPath(path: String): Builder =
        copy(fileDescriptorSetPath = path)

    public fun skipProtocRun(): Builder =
        copy(skipProtocRun = true)

    public fun protocArg(arg: String): Builder =
        copy(protocArgs = protocArgs + arg)

    public fun includeFile(path: String): Builder =
        copy(includeFile = path)

    public fun emitRerunIfChanged(enable: Boolean): Builder =
        copy(emitRerunIfChanged = enable)

    public fun disableComments(paths: Iterable<String>): Builder =
        copy(disableComments = disableComments + paths)

    public fun useArcSelf(enable: Boolean): Builder =
        copy(useArcSelf = enable)

    public fun generateDefaultStubs(enable: Boolean): Builder =
        copy(generateDefaultStubs = enable)

    public fun codecPath(path: String): Builder =
        copy(codecPath = path)

    public fun skipDebug(paths: Iterable<String>): Builder =
        copy(skipDebug = skipDebug + paths)

    public fun compileProtos(protos: List<String>, includes: List<String>) {
        compileWithConfig(Config.new(), protos, includes)
    }

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
            val serviceGen =
                ServiceGenerator.new(
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

    public fun compileFds(fds: FileDescriptorSet) {
        compileFdsWithConfig(fds, Config.new())
    }

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
            val serviceGen =
                ServiceGenerator.new(
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

    internal fun serviceGenerator(): ServiceGenerator =
        toServiceGenerator()

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
