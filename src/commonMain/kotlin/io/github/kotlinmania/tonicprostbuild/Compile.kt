package io.github.kotlinmania.tonicprostbuild

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
