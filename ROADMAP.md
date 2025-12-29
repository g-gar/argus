# 🗺️ Argus Project Roadmap

Our vision is to evolve Argus from a passive analysis tool into an active **Migration Intelligence Platform**.

## 1. 💾 Persistence & Graph History (The "Time Machine")
*Current limitation*: Argus only compares two specific JARs in memory.
*Goal*: Build a queryable history of library evolution.

### Implementation Strategy: Neo4j Graph Database
We should store the library structure as a graph, not just the diffs.
- **Nodes**: `LibraryVersion`, `Class`, `Method`.
- **Edges**: `CONTAINS`, `EVOLVED_TO` (The Diff), `CALLS` (Bonus: Dependency Graph).

**Key Decision: Diff Strategy**
- **Option A: Consecutive Chain (v1->v2->v3...)**
  - *Pros*: Efficient storage. mirrors reality (linear release history).
  - *Cons*: Comparing v1 vs v5 requires traversing 4 edges and aggregating changes.
- **Option B: All-to-All**
  - *Pros*: Fast checkout of any comparison.
  - *Cons*: Combinatorial explosion (O(N^2)). Costly storage.
- **Recommendation**: **Option A**. We store the "Atomic Steps" of evolution. If a user needs v1->v5, Argus computes it on demand or traverses the path.

## 2. 🧠 Intelligent Analysis (LLM Integration)
*Current limitation*: Argus reports "Method removed".
*Goal*: Argus explains *"Method removed because it was deprecated in v4. Use `newMethod()` instead."*

### Applications
- **Semantics**: Feed the `MethodDiff` (signatures + maybe bytecode snippet) to an LLM to categorize the change:
    - 🛡️ Security Fix
    - ⚡ Performance Improvement
    - 🧹 API Cleanup (Refactoring)
- **Migration Guides**: Generate a human-readable "Upgrade Guide" (Markdown) from the raw diff data.

## 3. 🛠️ Automated Remediation (The "Fixer")
*Current limitation*: Argus points out the problem.
*Goal*: Argus solves the problem.

### Integration with OpenRewrite
Instead of reinventing the wheel, we should leverage OpenRewrite's ecosystem.
- **Phase 1 (Deterministic Fixes)**: Simple renames or signature updates.
- **Phase 2 (Heuristic Fixes - *The "Magic"* )**:
  - Scenario: `OldParser` removed, `NewJsonParser` added.
  - Action: LLM correlates them -> Argus generates `org.openrewrite.java.ChangeType` recipe.
  - Complex migrations: "Replace `new Old()` with `New.builder().build()`".

---

## 📅 Suggested Phases

### Phase 2: Persistence (Core)
- [ ] Create `bat:neo4j` module.
- [ ] Design Graph Schema (`Class -(EVOLVED_TO)-> Class`).
- [ ] Implement `GraphRepository` to save `LibraryDiff` results.

### Phase 3: The Product (Semantic Changelog Generator)
*Goal: Turn "1400 breaking changes" into a readable 1-page summary.*

- [ ] **Data Enrichment**: Store Javadocs/Source as properties in Neo4j.
- [ ] **Context Strategy (Triple-Layer RAG)**: Fetch Migration Guides and Javadocs.
- [ ] **LLM Pipeline**:
    - Input: Raw Diff + Context.
    - Output: Grouped Changelog (e.g., "⚠️ Security: Removed insecure `connect`", "♻️ Refactor: `Document` is now `Query`").
- [ ] **Release Note Exporter**: Generate Markdown/HTML/PDF reports.
- [ ] **Visualization**:
    - **Mermaid.js**: Automatic Class Diagrams for simple refactors (Static Markdown).
    - **Cytoscape.js**: Interactive force-directed graph to explore dependency impact (HTML Report).

### Phase 4: Long Term Vision (Moonshots)
- [ ] **Automated Remediation**: Generating OpenRewrite recipes (Experimental).

