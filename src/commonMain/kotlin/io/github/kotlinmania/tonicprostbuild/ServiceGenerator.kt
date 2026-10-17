package io.github.kotlinmania.tonicprostbuild

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
        val output =
            buildString {
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
