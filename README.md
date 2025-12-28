# Argus 👁️

**Argus** is a sophisticated, modular toolkit designed for the **semantic analysis and comparison of Java libraries** at the bytecode level. It provides a robust pipeline to download artifacts, dissect their structure without source code, and perform intelligent diffing to identify breaking changes.

---

## 📖 Introduction to Bytecode Analysis

Bytecode analysis is the process of inspecting compiled Java classes (`.class` files) to understand their structure, behavior, and purity without needing the original source code. This is crucial for:
- **API Governance**: Ensuring library updates don't accidentally break consumers.
- **Dependency auditing**: Understanding what external libraries are actually doing.
- **Automated Refactoring**: Building tools that can verify binary compatibility.

Argus operates directly on this binary representation, making it independent of build systems or source availability.

## 🛠️ Technology Choices: Why ASM on the Edge?

We chose **ASM (OW2)** as our core driver for bytecode manipulation. While high-level reflection or other libraries exist, ASM offers:
- **Zero-Copy Performance**: It parses bytecode as a stream of events (Visitor Pattern), avoiding the memory overhead of loading full Class objects into the JVM.
- **Low-Level Access**: It allows us to see technical details (like synthetic flags or specific attributes) that standard Reflection might hide.
- **Speed**: It is the industry standard for performance-critical bytecode tasks (used by Gradle, Spring, JDK itself).

## 🧩 Architecture & Design

Argus is built with **Clean Code** and **Hexagonal Architecture (Ports & Adapters)** principles at its heart to ensure long-term maintainability and extensibility.

### 1. Hexagonal Core (`:core`)
The domain logic is completely isolated from infrastructure.
- **Domain Models**: Immutable, rich models (`ClassInfo`, `MethodInfo`) that represent our understanding of a library.
- **Diff Engine**: The core algorithm that compares two versions. It uses a **Smart comparison strategy**:
    - **Identity Matching**: Matches methods by exact signature.
    - **Fuzzy Matching**: Detects "Signature Changes" (e.g., `int` -> `long`) as evolutions (BREAKING_CHANGE) rather than deletion-insertion pairs.
- **Ports**: Interfaces like `ArtifactAnalyzer` define *what* we need, not *how* it's done.

### 2. Adapters (`:bat:asm`, `:utilities:artifact_fetcher`)
Implementation details are pushed to the edges.
- **ASM Adapter**: Implements `ArtifactAnalyzer` to feed the core with data parsed from raw Bytecode.
- **Maven Fetcher**: A utility adapter to interface with the outside world (Maven Central), respecting standard local caches (`~/.m2/repository`).

### 3. Modularization
Values are effectively separated into Gradle modules to enforce boundaries:
- `core`: Pure Java, no heavy dependencies.
- `bat:asm`: The "dirty" work of parsing binary streams.
- `utilities`: Helper tools.

### 🔮 Future Extensibility
This architecture makes Argus highly extensible. Future adapters could easily include:
- **Presentation**: A `MermaidPresenter` that visualizes the `LibraryDiff` as a diagram.
- **Persistence**: A `Neo4jAdapter` to store library evolution as a graph, querying the history of a method across 10 years of versions.
- **Integration**: A Github Action that runs Argus on every PR.

## 🚀 Usage

### Building the Project
```bash
./gradlew build
```

### Running integration tests
```bash
./gradlew :utilities:artifact_fetcher:test
```

## License

MIT
