package io.github.kotlinmania.tonicprostbuild

/**
 * File descriptor set representation.
 */
public data class FileDescriptorSet(
    val file: List<ByteArray> = emptyList(),
)
