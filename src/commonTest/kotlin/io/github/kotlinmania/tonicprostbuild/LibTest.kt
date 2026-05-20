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
