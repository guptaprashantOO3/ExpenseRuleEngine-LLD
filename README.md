# Expense Rule Engine - Low Level Design (LLD)

A robust, extensible, and interview-ready **Expense Rule Engine** implemented in Java 21, designed to evaluate individual expenses and aggregate trip expenses against configurable business compliance rules.

---

## 1. Problem Statement

Corporate expense tracking systems require automatic validation of employee claims against company expense policies.

### Core Requirements

#### 1. Core Models
* **`Expense`**: Individual expense record with ID, trip ID, amount (USD), and expense type.
* **`Trip`**: Aggregation model for expenses belonging to the same trip.
* **`Rule` & `EvaluationResult`**: Domain models representing rule contracts and evaluation status.
* **Enums**:
  * `ExpenseType`: `RESTAURANT`, `AIRFARE`, `ENTERTAINMENT`, `HOTEL`, `OTHER`.
  * `EvaluationStatus`: `APPROVED`, `REJECTED`.

#### 2. Expense-Level Evaluation
* **Restaurant Limit**: Reject if single restaurant expense exceeds **\$75**.
* **Disallowed Expenses**:
  * Reject **Airfare** expenses.
  * Reject **Entertainment** expenses.
* **Maximum Expense Limit**: Reject any single expense exceeding **\$250** (regardless of category).

#### 3. Trip-Level Evaluation
* **Group by Trip**: Group expenses by `tripId`.
* **Total Trip Limit**: Reject if cumulative trip expenses exceed **\$2,000**.
* **Total Meal Limit**: Reject if cumulative restaurant/meal expenses for a trip exceed **\$1,000**.

#### 4. Extensibility & Engine Features
* **Collect Multiple Violations**: Do NOT fail-fast; gather all violations across all applicable rules so the user receives comprehensive feedback.
* **Separate Granularity**: Generate detailed results at both the individual expense level and the trip aggregation level.
* **Open-Closed Principle (OCP)**: Easily register new rules without altering existing business logic.

---

## 2. Architecture & Design Patterns

```
                                  +-------------------+
                                  |      Rule<T>      |
                                  +-------------------+
                                    ^               ^
                                    |               |
                    +--------------------+     +--------------------+
                    |    ExpenseRule     |     |      TripRule      |
                    +--------------------+     +--------------------+
                    | check(Expense):Opt |     | check(Trip):Opt    |
                    +--------------------+     +--------------------+
                              ^                          ^
               +--------------+--------------+           |
               |              |              |           +--------------------+
        MaxAmountRule   DisallowRule   MaxExpenseLimit   |                    |
                                                   TripTotalLimitRule   TripMealLimitRule
                                                         
                                  +-------------------+
                                  |    RuleEngine     |
                                  +-------------------+
                                    | evaluates via
                                    v
                               RuleRegistry
                                    |
                    +---------------+---------------+
                    v                               v
         ExpenseEvaluationResult          TripEvaluationResult
         (Status + List<Violation>)       (Status + Trip Violations + Expense Results)
```

### Key Design Patterns Used:
1. **Strategy Pattern**: Business rules implement `ExpenseRule` or `TripRule`, allowing policies to vary independently from the engine that executes them.
2. **Registry Pattern (`RuleRegistry`)**: Decouples rule definition/configuration from execution. Provides pre-configured default rules or accepts dynamic registrations.
3. **Engine / Facade Pattern (`RuleEngine`)**: Orchestrates grouping expenses by trip, executing rules, collecting all violations, and assembling composite evaluation results.
4. **Result Object Pattern**: Instead of throwing exceptions or returning plain booleans, returns strongly-typed results (`ExpenseEvaluationResult`, `TripEvaluationResult`) holding approval statuses and specific failure reasons.

---

## 3. Project Structure

```
src/com/expense/ruleengine/
├── Main.java                               # Complete runnable demonstration
├── engine/
│   └── RuleEngine.java                     # Rule evaluation & trip grouping engine
├── enums/
│   ├── EvaluationStatus.java               # APPROVED, REJECTED
│   └── ExpenseType.java                    # RESTAURANT, AIRFARE, ENTERTAINMENT, etc.
├── models/
│   ├── EvaluationResult.java               # Abstract base result
│   ├── Expense.java                        # Individual expense model
│   ├── ExpenseEvaluationResult.java        # Expense-level evaluation output
│   ├── Trip.java                           # Trip model with aggregation methods
│   └── TripEvaluationResult.java           # Trip-level evaluation output
├── registry/
│   └── RuleRegistry.java                   # Static & instance rule catalog
└── service/rules/
    ├── Rule.java                           # Common generic rule interface
    ├── ExpenseRule.java                    # Expense rule contract
    ├── TripRule.java                       # Trip rule contract
    ├── Violation.java                      # Violation message container
    └── Impl/
        ├── AirfareDisallowRule.java        # Rejects airfare
        ├── DisallowRule.java               # Generic rejection rule by type
        ├── EntertainmentDisallowRule.java  # Rejects entertainment
        ├── MaxAmountRule.java              # Generic amount validation rule
        ├── MaxExpenseLimitRule.java        # Global $250 limit
        ├── RestaurantLimitRule.java        # Restaurant $75 limit
        ├── TripMealLimitRule.java          # Cumulative meal $1,000 limit
        └── TripTotalLimitRule.java         # Cumulative trip $2,000 limit
```

---

## 4. How to Approach This in a 60–90 Minute Interview

When coding this in a timed interview, writing 20 individual files can take too long. You can balance speed with clean architecture using this plan:

### 15-Minute Phased Interview Strategy

| Phase | Duration | Focus Area | What to Write |
|---|---|---|---|
| **Phase 1** | 0 – 10 min | Clarify & Scope | Clarify requirements (currency, fail-fast vs collect-all, aggregation rules). |
| **Phase 2** | 10 – 25 min | Core Models | Write `ExpenseType` (enum), `Expense`, `Trip`, and `Violation`. |
| **Phase 3** | 25 – 45 min | Rule Interfaces & Classes | Define `ExpenseRule` and `TripRule`.<br/>*Interview Pro-Tip*: Write 2 reusable parameterized rules instead of 6 specific classes:<br/>- `MaxAmountRule(double limit)`<br/>- `DisallowRule()` |
| **Phase 4** | 45 – 65 min | RuleRegistry & RuleEngine | Write `RuleRegistry` with `Map<ExpenseType, List<ExpenseRule>>` and `RuleEngine.evaluate()` using Java streams (`Collectors.groupingBy`). |
| **Phase 5** | 65 – 80 min | Driver / Test (`Main`) | Write clean test scenarios in `main()` showing: normal pass, category violation, global limit violation, trip aggregation violation, and multi-violation output. |
| **Phase 6** | 80 – 90 min | Edge Cases & Q&A | Discuss concurrency, database persistence, JSON-based dynamic rules, and currency conversion. |

---

## 5. How to Compile and Run

### Prerequisites
* Java JDK 17 or Java 21+

### Compile
```bash
javac -d out/production/ExpenseRuleEngine $(find src -name "*.java")
```

### Run
```bash
java -cp out/production/ExpenseRuleEngine com.expense.ruleengine.Main
```

### Sample Output
```text
=================================================
        EXPENSE RULE ENGINE EVALUATION           
=================================================

--- 1. EXPENSE-LEVEL EVALUATION ---
Expense ID: EXP-001  | Type: RESTAURANT    | Amount: $ 50.00 | Status: APPROVED
Expense ID: EXP-002  | Type: RESTAURANT    | Amount: $120.00 | Status: REJECTED
   --> Violation: [RestaurantLimitRule] Restaurant expense of $120.0 exceeds the limit of $75.0
Expense ID: EXP-003  | Type: AIRFARE       | Amount: $200.00 | Status: REJECTED
   --> Violation: [AirfareDisallowRule] Airfare expenses are not permitted and must be rejected.
Expense ID: EXP-004  | Type: ENTERTAINMENT | Amount: $ 80.00 | Status: REJECTED
   --> Violation: [EntertainmentDisallowRule] Entertainment expenses are not permitted and must be rejected.
Expense ID: EXP-005  | Type: HOTEL         | Amount: $280.00 | Status: REJECTED
   --> Violation: [MaxExpenseLimitRule] Expense amount $280.0 exceeds the maximum allowed limit of $250.0
Expense ID: EXP-006  | Type: RESTAURANT    | Amount: $350.00 | Status: REJECTED
   --> Violation: [RestaurantLimitRule] Restaurant expense of $350.0 exceeds the limit of $75.0
   --> Violation: [MaxExpenseLimitRule] Expense amount $350.0 exceeds the maximum allowed limit of $250.0

--- 2. TRIP-LEVEL EVALUATION ---
Trip ID: TRIP-100   | Total: $ 200.00 | Meals: $ 120.00 | Status: APPROVED
Trip ID: TRIP-200   | Total: $2200.00 | Meals: $   0.00 | Status: REJECTED
   --> Trip Violation: [TripTotalLimitRule] Total trip expenses ($2200.0) exceed the allowed trip limit of $2000.0
Trip ID: TRIP-300   | Total: $1250.00 | Meals: $1050.00 | Status: REJECTED
   --> Trip Violation: [TripMealLimitRule] Total meal expenses ($1050.0) exceed the allowed meal limit of $1000.0

=================================================
        EVALUATION COMPLETED SUCCESSFULLY        
=================================================
```
