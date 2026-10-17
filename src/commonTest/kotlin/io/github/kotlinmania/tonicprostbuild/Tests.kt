// port-lint: tests tests.rs
package io.github.kotlinmania.tonicprostbuild

import io.github.kotlinmania.quote.quote
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Tests {
    private fun createTestMethod(inputType: String, outputType: String): TonicBuildMethod =
        TonicBuildMethod(
            inputType = inputType,
            outputType = outputType,
            codecPathValue = "tonic_prost::ProstCodec",
        )

    @Test
    fun testConfigureDefaults() {
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
    fun testBuilderFluentOptionsAccumulateState() {
        val builder =
            configure()
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
    fun testTonicBuildServiceWrapsProstServiceMetadata() {
        val prostService =
            ProstService(
                name = "Greeter",
                packageName = "helloworld",
                protoName = "greeter.proto",
                comments =
                    ProstComments(
                        leading = listOf("/// Leading comment on service"),
                    ),
                methods =
                    listOf(
                        ProstMethod(
                            name = "SayHello",
                            protoName = "say_hello",
                            comments =
                                ProstComments(
                                    leading = listOf("/// Leading comment on method"),
                                ),
                            inputType = ".helloworld.HelloRequest",
                            outputType = ".helloworld.HelloReply",
                            clientStreaming = true,
                            serverStreaming = false,
                            deprecated = true,
                        ),
                    ),
            )

        val tonicService = TonicBuildService.new(prostService, "tonic_prost::ProstCodec")

        assertEquals("Greeter", tonicService.name())
        assertEquals("helloworld", tonicService.`package`())
        assertEquals("helloworld", tonicService.packageName())
        assertEquals("greeter.proto", tonicService.identifier())
        assertEquals(listOf("/// Leading comment on service"), tonicService.comment())
        assertEquals(1, tonicService.methods().size)

        val method = tonicService.methods().first()
        assertEquals("SayHello", method.name())
        assertEquals("say_hello", method.identifier())
        assertTrue(method.clientStreaming())
        assertFalse(method.serverStreaming())
        assertTrue(method.deprecated())
        assertEquals("tonic_prost::ProstCodec", method.codecPath())
        assertEquals(listOf("/// Leading comment on method"), method.comment())
    }

    @Test
    fun testRequestResponseNameGoogleTypesNotCompiled() {
        val testCases =
            listOf(
                ".google.protobuf.Empty" to quote("()"),
                ".google.protobuf.Any" to quote("::prost_types::Any"),
                ".google.protobuf.StringValue" to quote("::prost::alloc::string::String"),
                ".google.protobuf.Timestamp" to quote("::prost_types::Timestamp"),
                ".google.protobuf.Duration" to quote("::prost_types::Duration"),
                ".google.protobuf.Value" to quote("::prost_types::Value"),
            )

        for ((typeName, expected) in testCases) {
            val method = createTestMethod(typeName, typeName)
            val (request, response) = method.requestResponseName("super", false)

            assertEquals(
                expected.toString(),
                request.toString(),
                "Failed for input type: $typeName",
            )
            assertEquals(
                expected.toString(),
                response.toString(),
                "Failed for output type: $typeName",
            )
        }
    }

    @Test
    fun testRequestResponseNameGoogleTypesCompiled() {
        val testCases =
            listOf(
                ".google.protobuf.Empty",
                ".google.protobuf.Any",
                ".google.protobuf.StringValue",
                ".google.protobuf.Timestamp",
            )

        for (typeName in testCases) {
            val method = createTestMethod(typeName, typeName)
            val (request, response) = method.requestResponseName("super", true)

            val stripped = typeName.removePrefix(".google.protobuf.")
            val expectedPath = "super :: google :: protobuf :: $stripped"

            assertEquals(
                expectedPath,
                request.toString(),
                "Failed for input type: $typeName",
            )
            assertEquals(
                expectedPath,
                response.toString(),
                "Failed for output type: $typeName",
            )
        }
    }

    @Test
    fun testRequestResponseNameNonPathTypes() {
        val method = createTestMethod("()", "()")
        val (request, response) = method.requestResponseName("super", false)

        assertEquals("()", request.toString())
        assertEquals("()", response.toString())
    }

    @Test
    fun testRequestResponseNameExternTypes() {
        val testCases =
            listOf(
                "::my_crate::MyType" to ":: my_crate :: MyType",
                "crate::module::MyType" to "crate :: module :: MyType",
                "::external::lib::Type" to ":: external :: lib :: Type",
            )

        for ((typeName, expected) in testCases) {
            val method = createTestMethod(typeName, typeName)
            val (request, response) = method.requestResponseName("super", false)

            assertEquals(
                expected,
                request.toString(),
                "Failed for input type: $typeName",
            )
            assertEquals(
                expected,
                response.toString(),
                "Failed for output type: $typeName",
            )
        }
    }

    @Test
    fun testRequestResponseNameRegularProtobufTypes() {
        val testCases =
            listOf(
                "mypackage.MyMessage" to "super :: mypackage :: MyMessage",
                "com.example.User" to "super :: com :: example :: User",
                ".mypackage.MyMessage" to "super :: mypackage :: MyMessage",
                "nested.package.Message" to "super :: nested :: package :: Message",
            )

        for ((input, expected) in testCases) {
            val method = createTestMethod(input, input)
            val (request, response) = method.requestResponseName("super", false)

            assertEquals(
                expected,
                request.toString(),
                "Failed for input type: $input",
            )
            assertEquals(
                expected,
                response.toString(),
                "Failed for output type: $input",
            )
        }
    }

    @Test
    fun testRequestResponseNameDifferentProtoPaths() {
        val method =
            createTestMethod(
                "mypackage.MyMessage",
                "mypackage.MyResponse",
            )

        val testPaths = listOf("super", "crate::proto", "crate")

        for (protoPath in testPaths) {
            val (request, response) = method.requestResponseName(protoPath, false)

            val expectedRequest =
                if (protoPath.contains("::")) {
                    "${protoPath.replace("::", " :: ")} :: mypackage :: MyMessage"
                } else {
                    "$protoPath :: mypackage :: MyMessage"
                }
            val expectedResponse =
                if (protoPath.contains("::")) {
                    "${protoPath.replace("::", " :: ")} :: mypackage :: MyResponse"
                } else {
                    "$protoPath :: mypackage :: MyResponse"
                }

            assertEquals(
                expectedRequest,
                request.toString(),
                "Failed for proto_path: $protoPath",
            )
            assertEquals(
                expectedResponse,
                response.toString(),
                "Failed for proto_path: $protoPath",
            )
        }
    }

    @Test
    fun testRequestResponseNameMixedTypes() {
        val method1 =
            createTestMethod(
                ".google.protobuf.Empty",
                "mypackage.MyResponse",
            )
        val (request1, response1) = method1.requestResponseName("super", false)

        assertEquals("()", request1.toString())
        assertEquals("super :: mypackage :: MyResponse", response1.toString())

        val method2 =
            createTestMethod(
                "::external::Request",
                ".google.protobuf.Any",
            )
        val (request2, response2) = method2.requestResponseName("super", false)

        assertEquals(":: external :: Request", request2.toString())
        assertEquals(":: prost_types :: Any", response2.toString())
    }

    @Test
    fun testIsGoogleType() {
        assertTrue(isGoogleType(".google.protobuf.Empty"))
        assertTrue(isGoogleType(".google.protobuf.Any"))
        assertTrue(isGoogleType(".google.protobuf.Timestamp"))

        assertFalse(isGoogleType("google.protobuf.Empty"))
        assertFalse(isGoogleType(".google.api.Http"))
        assertFalse(isGoogleType("mypackage.Message"))
        assertFalse(isGoogleType(""))
    }

    @Test
    fun testNonPathTypeAllowlist() {
        assertTrue(NON_PATH_TYPE_ALLOWLIST.contains("()"))
        assertEquals(1, NON_PATH_TYPE_ALLOWLIST.size)
    }

    @Test
    fun testServiceGeneratorSnapshotsBuilderCodegenConfig() {
        val service =
            ProstService(
                name = "Greeter",
                packageName = "helloworld",
                protoName = "greeter.proto",
            )

        val serviceGenerator =
            configure()
                .buildClient(true)
                .buildServer(false)
                .codecPath("tonic_prost::ProstCodec")
                .serviceGenerator()

        assertTrue(serviceGenerator.buildClient)
        assertFalse(serviceGenerator.buildServer)
        assertEquals("tonic_prost::ProstCodec", serviceGenerator.codecPath)

        val builder = StringBuilder()
        serviceGenerator.generate(service, builder)
        val generated = builder.toString()

        assertTrue(generated.contains("Generated Client for Greeter in helloworld"))
        assertFalse(generated.contains("Generated Server for Greeter in helloworld"))
    }

    @Test
    fun testEdgeCases() {
        val method1 = createTestMethod("a.b.c.d.Message", "x.y.z.Response")
        val (request1, response1) = method1.requestResponseName("super", false)
        assertEquals("super :: a :: b :: c :: d :: Message", request1.toString())
        assertEquals("super :: x :: y :: z :: Response", response1.toString())

        val method2 = createTestMethod("mypackage.()", "mypackage.()")
        val (request2, response2) = method2.requestResponseName("super", false)
        assertEquals("mypackage . ()", request2.toString())
        assertEquals("mypackage . ()", response2.toString())
    }

    @Test
    fun testConfigCompilationHelpers() {
        val config = Config.new()
        config.compileProtos(listOf("a.proto"), listOf("."))
        config.compileFds(FileDescriptorSet())

        val builder = configure()
        builder.compileProtos(listOf("a.proto"), listOf("."))
        builder.compileWithConfig(Config.new(), listOf("a.proto"), listOf("."))
        builder.compileFds(FileDescriptorSet())
        builder.compileFdsWithConfig(FileDescriptorSet(), Config.new())
        compileProtos("proto/service.proto")
        compileFds(FileDescriptorSet())
    }
}
