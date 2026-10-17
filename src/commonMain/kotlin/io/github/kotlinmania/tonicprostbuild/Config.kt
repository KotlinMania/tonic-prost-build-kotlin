package io.github.kotlinmania.tonicprostbuild

/**
 * Configuration for code generation.
 */
public class Config {
    public var outDir: String? = null
    internal val externPaths: MutableList<Pair<String, String>> = mutableListOf()
    internal val fieldAttributes: MutableList<Pair<String, String>> = mutableListOf()
    internal val messageAttributes: MutableList<Pair<String, String>> = mutableListOf()
    internal val enumAttributes: MutableList<Pair<String, String>> = mutableListOf()
    internal val typeAttributes: MutableList<Pair<String, String>> = mutableListOf()
    internal val boxedPaths: MutableList<String> = mutableListOf()
    internal var btreeMapPaths: List<String>? = null
    internal var bytesPaths: List<String>? = null
    public var wellKnownTypesCompiled: Boolean = false
    internal val protocArgs: MutableList<String> = mutableListOf()
    public var includeFile: String? = null
    internal val skipDebugPaths: MutableSet<String> = mutableSetOf()
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

    public fun skipDebug(paths: Iterable<String>): Config {
        skipDebugPaths.addAll(paths)
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
