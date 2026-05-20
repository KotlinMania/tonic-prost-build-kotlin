# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 2/2 (100.0%)
- **Function parity:** 34/59 matched (target 53) — 57.6%
- **Class/type parity:** 2/6 matched (target 4) — 33.3%
- **Combined symbol parity:** 36/65 matched (target 57) — 55.4%
- **Average inline-code cosine:** 0.24 (function body across 2 matched files)
- **Average documentation cosine:** 0.29 (doc text across 2 matched files)
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
- **Similarity:** 0.42
- **Dependents:** 0
- **Priority Score:** 195405.8
- **Functions:** 33/48 matched (target 40)
- **Missing functions:** `compile_protos`, `compile_fds`, `new`, `name`, `package`, `identifier`, `methods`, `comment`, `client_streaming`, `server_streaming`, `deprecated`, `generate`, `compile_with_config`, `compile_fds_with_config`, `service_generator`
- **Types:** 2/6 matched (target 3)
- **Missing types:** `TonicBuildService`, `Method`, `Comment`, `ServiceGenerator`

### 2. tests

- **Target:** `tonicprostbuild.LibTest`
- **Similarity:** 0.06
- **Dependents:** 0
- **Priority Score:** 101109.4
- **Functions:** 1/11 matched (target 13)
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
