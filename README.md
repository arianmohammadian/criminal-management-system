# Criminal Management System

A Java-based criminal organization management system developed as a university software engineering project at the **Facultat d'Informàtica de Barcelona (FIB), Universitat Politècnica de Catalunya (UPC)**.

The project focuses on object-oriented design, domain modeling, software architecture, design patterns, and algorithms for managing criminal organizations, personnel, and missions.

## Project Overview

The system models a fictional criminal organization, including its members, missions, and operational resources.

Its architecture separates domain logic from application controllers, allowing the different components to evolve independently as the project develops.

This repository currently contains the **first academic delivery**, primarily focused on domain implementation, controller logic, and algorithmic components.

## Key Components

### Domain Model

The domain layer represents the main entities and their relationships, including:

- **Criminal personnel:** representation and management of organization members, including roles, availability, and accumulated earnings.
- **Missions:** domain entities with execution states and lifecycle transitions.
- **Personnel collections:** management and querying of registered members.
- **Resource and assignment logic:** components supporting the allocation of personnel to missions.

## Design Patterns

### State Pattern

The **State Pattern** encapsulates mission behavior according to its current state.

Each state defines the operations and transitions available to a mission, separating state-specific behavior from the main mission entity.

This improves maintainability and makes it easier to extend the mission lifecycle.

### Strategy Pattern

The **Strategy Pattern** provides interchangeable algorithms for assigning personnel to missions.

Assignment strategies share a common interface, allowing the system to select different approaches without modifying the domain components that use them.

The project includes two assignment strategies:

- **Greedy Assignment:** A heuristic approach that makes locally optimal decisions according to the assignment criteria.
- **Backtracking Assignment:** A systematic search approach that explores possible assignments while enforcing the problem's constraints.

This design separates the assignment problem from the algorithms used to solve it.

### Controller-Based Architecture

Application controllers coordinate operations on domain entities and expose the functionality required by the application.

This separation keeps business logic independent from presentation and interaction layers.

## Algorithms

### Greedy Assignment

The greedy algorithm constructs an assignment incrementally, selecting candidates according to the defined selection criteria.

It prioritizes efficient decision-making without necessarily guaranteeing a globally optimal assignment.

### Backtracking Assignment

The backtracking algorithm explores possible personnel-to-mission assignments recursively.

It builds partial solutions, checks assignment constraints, and backtracks when a candidate cannot lead to a valid solution.

Unlike the greedy approach, backtracking can systematically explore alternatives, at the cost of potentially exponential execution time.

### Comparison

| Algorithm | Approach | Main Trade-off |
|---|---|---|
| Greedy | Incremental heuristic selection | Faster decisions, without a general optimality guarantee |
| Backtracking | Recursive exploration of alternatives | More exhaustive search, potentially exponential cost |

Both algorithms are integrated through the Strategy Pattern, allowing their implementations to evolve independently of the surrounding domain logic.

## Project Structure

```text
criminal-management-system/
├── src/
│   ├── main/
│   │   ├── controllers/
│   │   └── domain/
│   └── test/
├── lib/
├── docs/
│   └── javadoc/
└── README.md
```

- `src/main/controllers/` — Application controllers.
- `src/main/domain/` — Domain entities, state implementations, and assignment strategies.
- `src/test/` — Test code and test drivers.
- `lib/` — External libraries required by the project.
- `docs/javadoc/` — Generated Java API documentation.

Compiled files are generated in `bin/` and are not required in version control.

## Technologies

- **Java** — Main implementation language.
- **Object-Oriented Programming** — Encapsulation, abstraction, and separation of responsibilities.
- **Design Patterns** — State pattern and strategy-based algorithm organization.
- **JUnit 4** — Automated unit testing.
- **Javadoc** — API documentation.
- **Git** — Version control.

## Building and Testing

### Requirements

- Java Development Kit (JDK), preferably Java 21.
- External libraries included in `lib/`.
- A Unix-like shell environment for the commands below.

Run the following commands from the repository root.

### Compile

```bash
mkdir -p bin
javac -encoding UTF-8 -cp "lib/*" -d bin $(find src/main src/test -name "*.java")
```

### Run Tests

```bash
java -cp "bin:lib/*" org.junit.runner.JUnitCore test.AllTests
```

The commands assume that the JUnit test suite is available as `test.AllTests`.

### Generate Javadoc

```bash
javadoc -encoding UTF-8 -charset UTF-8 \
  -d docs/javadoc \
  -sourcepath src/main \
  -subpackages controllers:domain
```

The generated API documentation can be opened locally at `docs/javadoc/index.html`.

## Development Status

**Current milestone: First Academic Delivery**

This version focuses on the internal architecture and core domain functionality rather than a finished end-user application.

Future development is expected to include further integration of the system's components, additional functionality, and a graphical user interface.

The repository will be updated as subsequent development milestones are completed.

## Academic Context

Developed as a project for **PROP (Projecte de Programació)** at the FIB-UPC.

The project provides practical experience in:

- Designing and implementing an object-oriented system.
- Applying the State and Strategy design patterns.
- Implementing and comparing greedy and backtracking assignment algorithms.
- Developing and integrating independently implemented components.
- Testing and documenting Java software.