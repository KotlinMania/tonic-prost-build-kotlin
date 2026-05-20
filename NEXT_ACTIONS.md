# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 2/2 (100.0%)
- **Function parity:** 3/59 matched (target 16) — 5.1%
- **Class/type parity:** 1/6 matched (target 2) — 16.7%
- **Combined symbol parity:** 4/65 matched (target 18) — 6.2%
- **Average inline-code cosine:** 0.04 (function body across 2 matched files)
- **Average documentation cosine:** 0.21 (doc text across 2 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 2 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. lib

- **Target:** `tonicprostbuild.Lib`
- **Similarity:** 0.01
- **Dependents:** 0
- **Priority Score:** 515409.9
- **Functions:** 2/48 matched (target 5)
- **Missing functions:** `configure`, `compile_protos`, `compile_fds`, `new`, `name`, `package`, `identifier`, `methods`, `comment`, `client_streaming`, `server_streaming`, `codec_path`, `deprecated`, `generate`, `build_client`, `build_server`, `build_transport`, `out_dir`, `extern_path`, `field_attribute`, `message_attribute`, `enum_attribute`, `type_attribute`, `boxed`, `btree_map`, `bytes`, `server_mod_attribute`, `server_attribute`, `trait_attribute`, `client_mod_attribute`, `client_attribute`, `proto_path`, `compile_well_known_types`, `emit_package`, `file_descriptor_set_path`, `skip_protoc_run`, `protoc_arg`, `include_file`, `emit_rerun_if_changed`, `disable_comments`, `use_arc_self`, `generate_default_stubs`, `skip_debug`, `compile_with_config`, `compile_fds_with_config`, `service_generator`
- **Types:** 1/6 matched (target 1)
- **Missing types:** `TonicBuildService`, `Method`, `Comment`, `ServiceGenerator`, `Builder`

### 2. tests

- **Target:** `tonicprostbuild.LibTest`
- **Similarity:** 0.06
- **Dependents:** 0
- **Priority Score:** 101109.4
- **Functions:** 1/11 matched
- **Missing functions:** `test_request_response_name_google_types_not_compiled`, `test_request_response_name_google_types_compiled`, `test_request_response_name_non_path_types`, `test_request_response_name_extern_types`, `test_request_response_name_regular_protobuf_types`, `test_request_response_name_different_proto_paths`, `test_request_response_name_mixed_types`, `test_is_google_type`, `test_non_path_type_allowlist`, `test_edge_cases`
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Tests:** 0/10 matched

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Next Commands

```bash
# Initialize task queue for systematic porting
cd tools/ast_distance
./ast_distance --init-tasks ../../tmp/tonic-prost-build/src rust ../../src/commonMain/kotlin/io/github/kotlinmania/tonicprostbuild kotlin tasks.json ../../AGENTS.md

# Get next high-priority task
./ast_distance --assign tasks.json <agent-id>
```
