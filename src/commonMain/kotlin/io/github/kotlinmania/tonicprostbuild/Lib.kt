// port-lint: source lib.rs
package io.github.kotlinmania.tonicprostbuild

/**
 * Prost build integration for tonic.
 *
 * This crate provides code generation for gRPC services using protobuf definitions
 * through the prost ecosystem.
 */

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
