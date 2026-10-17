package io.github.kotlinmania.tonicprostbuild

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.quote

internal val NON_PATH_TYPE_ALLOWLIST: Set<String> = setOf("()")

internal fun isGoogleType(typeName: String): Boolean =
    typeName.startsWith(".google.protobuf")

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
        wrappedMethods =
            prostService.methods.map { prostMethod ->
                TonicBuildMethod(
                    prostMethod = prostMethod,
                    codecPath = codecPath,
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
    val prostMethod: ProstMethod,
    val codecPath: String = "tonic_prost::ProstCodec",
) {
    constructor(
        inputType: String,
        outputType: String,
        codecPathValue: String = "tonic_prost::ProstCodec",
        methodName: String = "",
        methodIdentifier: String = "",
        comments: ProstComments = ProstComments(),
        methodClientStreaming: Boolean = false,
        methodServerStreaming: Boolean = false,
        isDeprecated: Boolean = false,
    ) : this(
        prostMethod =
            ProstMethod(
                name = methodName,
                protoName = methodIdentifier,
                comments = comments,
                inputType = inputType,
                outputType = outputType,
                clientStreaming = methodClientStreaming,
                serverStreaming = methodServerStreaming,
                deprecated = isDeprecated,
            ),
        codecPath = codecPathValue,
    )

    fun name(): String =
        prostMethod.name

    fun identifier(): String =
        prostMethod.protoName

    fun clientStreaming(): Boolean =
        prostMethod.clientStreaming

    fun serverStreaming(): Boolean =
        prostMethod.serverStreaming

    fun comment(): List<String> =
        prostMethod.comments.leading

    fun codecPath(): String =
        codecPath

    fun deprecated(): Boolean =
        prostMethod.deprecated

    fun requestResponseName(
        protoPath: String,
        compileWellKnownTypes: Boolean,
    ): Pair<TokenStream, TokenStream> {
        val request = renderMessageType(prostMethod.inputType, protoPath, compileWellKnownTypes)
        val response = renderMessageType(prostMethod.outputType, protoPath, compileWellKnownTypes)
        return Pair(request, response)
    }

    private fun renderMessageType(
        typeName: String,
        protoPath: String,
        compileWellKnownTypes: Boolean,
    ): TokenStream =
        if (isGoogleType(typeName) && !compileWellKnownTypes) {
            when (typeName) {
                ".google.protobuf.Empty" -> quote("()")
                ".google.protobuf.Any" -> quote("::prost_types::Any")
                ".google.protobuf.StringValue" -> quote("::prost::alloc::string::String")
                else -> {
                    val googleType = typeName.removePrefix(".google.protobuf.")
                    TokenStream.fromString("::prost_types::$googleType").getOrThrow()
                }
            }
        } else if (NON_PATH_TYPE_ALLOWLIST.any { typeName.endsWith(it) }) {
            TokenStream.fromString(typeName).getOrThrow()
        } else if (typeName.startsWith("::") || typeName.startsWith("crate::")) {
            TokenStream.fromString(typeName).getOrThrow()
        } else {
            val rustType = typeName.replace('.', ':').replace(":", "::").trimStart(':')
            TokenStream.fromString("$protoPath::$rustType").getOrThrow()
        }
}
