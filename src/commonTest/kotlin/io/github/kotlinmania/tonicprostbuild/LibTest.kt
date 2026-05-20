// port-lint: source tests.rs
package io.github.kotlinmania.tonicprostbuild

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LibTest {
    private fun createTestMethod(inputType: String, outputType: String): TonicBuildMethod =
        TonicBuildMethod(
            inputType = inputType,
            outputType = outputType,
            codecPath = "tonic_prost::ProstCodec",
        )

    @Test
    fun configureDefaults() {
        val builder = configure()

        assertTrue(builder.buildClient)
        assertTrue(builder.buildServer)
        assertTrue(builder.buildTransport)
        assertEquals(null, builder.fileDescriptorSetPath)
        assertFalse(builder.skipProtocRun)
        assertEquals(null, builder.outDir)
        assertEquals(emptyList(), builder.externPath)
        assertEquals(emptyList(), builder.fieldAttributes)
        assertEquals(emptyList(), builder.messageAttributes)
        assertEquals(emptyList(), builder.enumAttributes)
        assertEquals(emptyList(), builder.typeAttributes)
        assertEquals(emptyList(), builder.boxed)
        assertEquals(null, builder.btreeMap)
        assertEquals(null, builder.bytes)
        assertEquals(CodegenAttributes(), builder.serverAttributes)
        assertEquals(CodegenAttributes(), builder.clientAttributes)
        assertEquals("super", builder.protoPath)
        assertFalse(builder.compileWellKnownTypes)
        assertTrue(builder.emitPackage)
        assertEquals(emptyList(), builder.protocArgs)
        assertEquals(null, builder.includeFile)
        assertFalse(builder.emitRerunIfChanged)
        assertEquals(emptySet(), builder.disableComments)
        assertFalse(builder.useArcSelf)
        assertFalse(builder.generateDefaultStubs)
        assertEquals("tonic_prost::ProstCodec", builder.codecPath)
        assertEquals(emptySet(), builder.skipDebug)
    }

    @Test
    fun builderFluentOptionsAccumulateState() {
        val builder = configure()
            .buildClient(false)
            .buildServer(false)
            .buildTransport(false)
            .outDir("generated")
            .externPath(".google.protobuf", "::prost_types")
            .fieldAttribute(".pkg.Message.field", "@field:Deprecated")
            .messageAttribute(".pkg.Message", "@Deprecated")
            .enumAttribute(".pkg.Enum", "@Deprecated")
            .typeAttribute(".pkg.Type", "@Serializable")
            .boxed(".pkg.Message.child")
            .btreeMap(".pkg.Message.tags")
            .btreeMap(".pkg.Message.more_tags")
            .bytes(".pkg.Message.payload")
            .bytes(".pkg.Message.more_payload")
            .serverModAttribute(".pkg.Service", "@ServerModule")
            .serverAttribute(".pkg.Service", "@Deprecated")
            .traitAttribute(".pkg.Service", "@Serializable")
            .clientModAttribute(".pkg.Service", "@ClientModule")
            .clientAttribute(".pkg.Service", "@Deprecated")
            .protoPath("crate::proto")
            .compileWellKnownTypes(true)
            .emitPackage(false)
            .fileDescriptorSetPath("descriptor.bin")
            .skipProtocRun()
            .protocArg("--experimental_allow_proto3_optional")
            .includeFile("include.kt")
            .emitRerunIfChanged(true)
            .disableComments(listOf(".pkg.Service", ".pkg.Service/Method"))
            .useArcSelf(true)
            .generateDefaultStubs(true)
            .codecPath("my.Codec")
            .skipDebug(listOf(".pkg.Request", ".pkg.Response"))

        assertFalse(builder.buildClient)
        assertFalse(builder.buildServer)
        assertFalse(builder.buildTransport)
        assertEquals("generated", builder.outDir)
        assertEquals(listOf(".google.protobuf" to "::prost_types"), builder.externPath)
        assertEquals(listOf(".pkg.Message.field" to "@field:Deprecated"), builder.fieldAttributes)
        assertEquals(listOf(".pkg.Message" to "@Deprecated"), builder.messageAttributes)
        assertEquals(listOf(".pkg.Enum" to "@Deprecated"), builder.enumAttributes)
        assertEquals(listOf(".pkg.Type" to "@Serializable"), builder.typeAttributes)
        assertEquals(listOf(".pkg.Message.child"), builder.boxed)
        assertEquals(listOf(".pkg.Message.tags", ".pkg.Message.more_tags"), builder.btreeMap)
        assertEquals(listOf(".pkg.Message.payload", ".pkg.Message.more_payload"), builder.bytes)
        assertEquals(
            CodegenAttributes(
                module = listOf(".pkg.Service" to "@ServerModule"),
                struct = listOf(".pkg.Service" to "@Deprecated"),
                trait = listOf(".pkg.Service" to "@Serializable"),
            ),
            builder.serverAttributes,
        )
        assertEquals(
            CodegenAttributes(
                module = listOf(".pkg.Service" to "@ClientModule"),
                struct = listOf(".pkg.Service" to "@Deprecated"),
            ),
            builder.clientAttributes,
        )
        assertEquals("crate::proto", builder.protoPath)
        assertTrue(builder.compileWellKnownTypes)
        assertFalse(builder.emitPackage)
        assertEquals("descriptor.bin", builder.fileDescriptorSetPath)
        assertTrue(builder.skipProtocRun)
        assertEquals(listOf("--experimental_allow_proto3_optional"), builder.protocArgs)
        assertEquals("include.kt", builder.includeFile)
        assertTrue(builder.emitRerunIfChanged)
        assertEquals(setOf(".pkg.Service", ".pkg.Service/Method"), builder.disableComments)
        assertTrue(builder.useArcSelf)
        assertTrue(builder.generateDefaultStubs)
        assertEquals("my.Codec", builder.codecPath)
        assertEquals(setOf(".pkg.Request", ".pkg.Response"), builder.skipDebug)
    }

    @Test
    fun requestResponseNameGoogleTypesNotCompiled() {
        val testCases = listOf(
            ".google.protobuf.Empty" to "()",
            ".google.protobuf.Any" to ":: prost_types :: Any",
            ".google.protobuf.StringValue" to ":: prost :: alloc :: string :: String",
            ".google.protobuf.Timestamp" to ":: prost_types :: Timestamp",
            ".google.protobuf.Duration" to ":: prost_types :: Duration",
            ".google.protobuf.Value" to ":: prost_types :: Value",
        )

        for ((typeName, expected) in testCases) {
            val method = createTestMethod(typeName, typeName)
            val (request, response) = method.requestResponseName("super", false)

            assertEquals(expected, request, "Failed for input type: $typeName")
            assertEquals(expected, response, "Failed for output type: $typeName")
        }
    }

    @Test
    fun requestResponseNameGoogleTypesCompiled() {
        val testCases = listOf(
            ".google.protobuf.Empty",
            ".google.protobuf.Any",
            ".google.protobuf.StringValue",
            ".google.protobuf.Timestamp",
        )

        for (typeName in testCases) {
            val method = createTestMethod(typeName, typeName)
            val (request, response) = method.requestResponseName("super", true)
            val expectedPath = "super :: google :: protobuf :: ${typeName.removePrefix(".google.protobuf.")}"

            assertEquals(expectedPath, request, "Failed for input type: $typeName")
            assertEquals(expectedPath, response, "Failed for output type: $typeName")
        }
    }

    @Test
    fun requestResponseNameNonPathTypes() {
        val method = createTestMethod("()", "()")
        val (request, response) = method.requestResponseName("super", false)

        assertEquals("()", request)
        assertEquals("()", response)
    }

    @Test
    fun requestResponseNameExternTypes() {
        val testCases = listOf(
            "::my_crate::MyType" to ":: my_crate :: MyType",
            "crate::module::MyType" to "crate :: module :: MyType",
            "::external::lib::Type" to ":: external :: lib :: Type",
        )

        for ((typeName, expected) in testCases) {
            val method = createTestMethod(typeName, typeName)
            val (request, response) = method.requestResponseName("super", false)

            assertEquals(expected, request, "Failed for input type: $typeName")
            assertEquals(expected, response, "Failed for output type: $typeName")
        }
    }

    @Test
    fun requestResponseNameRegularProtobufTypes() {
        val testCases = listOf(
            "mypackage.MyMessage" to "super :: mypackage :: MyMessage",
            "com.example.User" to "super :: com :: example :: User",
            ".mypackage.MyMessage" to "super :: mypackage :: MyMessage",
            "nested.package.Message" to "super :: nested :: package :: Message",
        )

        for ((input, expected) in testCases) {
            val method = createTestMethod(input, input)
            val (request, response) = method.requestResponseName("super", false)

            assertEquals(expected, request, "Failed for input type: $input")
            assertEquals(expected, response, "Failed for output type: $input")
        }
    }

    @Test
    fun requestResponseNameDifferentProtoPaths() {
        val method = createTestMethod(
            inputType = "mypackage.MyMessage",
            outputType = "mypackage.MyResponse",
        )

        val testPaths = listOf("super", "crate::proto", "crate")

        for (protoPath in testPaths) {
            val (request, response) = method.requestResponseName(protoPath, false)
            val renderedProtoPath = protoPath.replace("::", " :: ")
            val expectedRequest = "$renderedProtoPath :: mypackage :: MyMessage"
            val expectedResponse = "$renderedProtoPath :: mypackage :: MyResponse"

            assertEquals(expectedRequest, request, "Failed for proto path: $protoPath")
            assertEquals(expectedResponse, response, "Failed for proto path: $protoPath")
        }
    }

    @Test
    fun requestResponseNameMixedTypes() {
        val googleRequest = createTestMethod(
            inputType = ".google.protobuf.Empty",
            outputType = "mypackage.MyResponse",
        )
        val (googleRequestName, googleResponseName) = googleRequest.requestResponseName("super", false)

        assertEquals("()", googleRequestName)
        assertEquals("super :: mypackage :: MyResponse", googleResponseName)

        val externRequest = createTestMethod(
            inputType = "::external::Request",
            outputType = ".google.protobuf.Any",
        )
        val (externRequestName, externResponseName) = externRequest.requestResponseName("super", false)

        assertEquals(":: external :: Request", externRequestName)
        assertEquals(":: prost_types :: Any", externResponseName)
    }

    @Test
    fun googleTypeDetection() {
        assertTrue(isGoogleType(".google.protobuf.Empty"))
        assertTrue(isGoogleType(".google.protobuf.Any"))
        assertTrue(isGoogleType(".google.protobuf.Timestamp"))

        assertFalse(isGoogleType("google.protobuf.Empty"))
        assertFalse(isGoogleType(".google.api.Http"))
        assertFalse(isGoogleType("mypackage.Message"))
        assertFalse(isGoogleType(""))
    }

    @Test
    fun nonPathTypeAllowlist() {
        assertTrue(NON_PATH_TYPE_ALLOWLIST.contains("()"))
        assertEquals(1, NON_PATH_TYPE_ALLOWLIST.size)
    }

    @Test
    fun edgeCases() {
        val multipleDots = createTestMethod("a.b.c.d.Message", "x.y.z.Response")
        val (request, response) = multipleDots.requestResponseName("super", false)
        assertEquals("super :: a :: b :: c :: d :: Message", request)
        assertEquals("super :: x :: y :: z :: Response", response)

        val nonPathSuffix = createTestMethod("mypackage.()", "mypackage.()")
        val (nonPathRequest, nonPathResponse) = nonPathSuffix.requestResponseName("super", false)
        assertEquals("mypackage . ()", nonPathRequest)
        assertEquals("mypackage . ()", nonPathResponse)
    }
}
