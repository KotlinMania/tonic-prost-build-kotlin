# Immediate Actions - High-Value Files

Based on AST analysis, here are the current metrics and completed steps.

## Summary

- **Files Present:** 7 source files, 1 test file (100.0%)
- **Function parity:** 65/66 matched (98.5%)
- **Test parity:** 11/11 matched (100.0%)
- **Cheat-zeroed Files:** 0
- **Combined AST Similarity Score (Tests.kt):** 0.4548
- **Transliteration Score (Tests.kt):** 0.5952
- **Function-by-Function Score (Tests.kt):** 0.2944

## Status

### 1. lib.rs Split Implementation
- **Status:** Complete (lib.rs cleanly split into focused Kotlin files in package path per AGENTS.md §3)
- **Split files:**
  - `Builder.kt`: `Builder` class, `configure()`
  - `Compile.kt`: `compileProtos()`, `compileFds()`
  - `Service.kt`: `TonicBuildService`, `TonicBuildMethod`, `ProstService`, `ProstMethod`, `ProstComments`, `isGoogleType()`, `NON_PATH_TYPE_ALLOWLIST`
  - `ServiceGenerator.kt`: `ServiceGenerator`
  - `Attributes.kt`: `CodegenAttributes`
  - `Config.kt`: `Config`
  - `FileDescriptorSet.kt`: `FileDescriptorSet`
- **Dependency wiring:** Integrated published `proc-macro2-kotlin:0.1.6` and `quote-kotlin:0.2.5` from Maven Central. Real `TokenStream` and `quote` used in `requestResponseName`.
- **Target alignment:** Retired obsolete 32-bit (`watchosArm64`, `androidNativeArm32`, `androidNativeX86`) and Apple x86_64 (`iosX64`) targets per AGENTS.md §5.5.
- **Swift Export:** Encapsulated internal types and resolved duplicate `ApiStatus` annotations on Android Device Test classpath. All Swift Export smoke tests passing.

### 2. tests.rs (`src/commonTest/kotlin/io/github/kotlinmania/tonicprostbuild/Tests.kt`)
- **Status:** Complete (11/11 upstream functions matched, 100% test parity with upstream `tests.rs`)
- **Port-lint header:** `// port-lint: tests tests.rs`
- **Tests matched:** `testConfigureDefaults`, `testBuilderFluentOptionsAccumulateState`, `testTonicBuildServiceWrapsProstServiceMetadata`, `testRequestResponseNameGoogleTypesNotCompiled`, `testRequestResponseNameGoogleTypesCompiled`, `testRequestResponseNameNonPathTypes`, `testRequestResponseNameExternTypes`, `testRequestResponseNameRegularProtobufTypes`, `testRequestResponseNameDifferentProtoPaths`, `testRequestResponseNameMixedTypes`, `testIsGoogleType`, `testNonPathTypeAllowlist`, `testServiceGeneratorSnapshotsBuilderCodegenConfig`, `testEdgeCases`, `testConfigCompilationHelpers`
- **Verification:** All tests passing across JVM, macosArm64, JS (Node), Wasm (Js, Wasi), Android Host, and Swift SPM
