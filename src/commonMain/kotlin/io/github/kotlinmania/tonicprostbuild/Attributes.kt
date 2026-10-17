package io.github.kotlinmania.tonicprostbuild

internal data class CodegenAttributes(
    val module: List<Pair<String, String>> = emptyList(),
    val struct: List<Pair<String, String>> = emptyList(),
    val trait: List<Pair<String, String>> = emptyList(),
) {
    fun pushModule(path: String, attribute: String): CodegenAttributes =
        copy(module = module + (path to attribute))

    fun pushStruct(path: String, attribute: String): CodegenAttributes =
        copy(struct = struct + (path to attribute))

    fun pushTrait(path: String, attribute: String): CodegenAttributes =
        copy(trait = trait + (path to attribute))
}
