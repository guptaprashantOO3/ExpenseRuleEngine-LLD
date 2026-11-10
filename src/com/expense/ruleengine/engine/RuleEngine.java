package com.expense.ruleengine.engine;

import com.expense.ruleengine.enums.ExpenseType;
import com.expense.ruleengine.models.Expense;
import com.expense.ruleengine.models.ExpenseEvaluationResult;
import com.expense.ruleengine.models.Trip;
import com.expense.ruleengine.models.TripEvaluationResult;
import com.expense.ruleengine.registry.RuleRegistry;
import com.expense.ruleengine.service.rules.ExpenseRule;
import com.expense.ruleengine.service.rules.TripRule;
import com.expense.ruleengine.service.rules.Violation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Rule Evaluation Engine responsible for evaluating expenses and trips against business rules,
 * grouping expenses by trip ID, collecting multiple violations, and generating results.
 */
public class RuleEngine {

    private final Map<ExpenseType, List<ExpenseRule>> typeSpecificRules;
    private final List<ExpenseRule> globalExpenseRules;
    private final List<TripRule> tripRules;

    /**
     * Initializes the engine with default business rules configured in RuleRegistry.
     */
    public RuleEngine() {
        this(RuleRegistry.createDefaultRegistry());
    }

    /**
     * Initializes the engine with the provided RuleRegistry.
     *
     * @param registry rule registry containing expense and trip rules
     */
    public RuleEngine(RuleRegistry registry) {
        Objects.requireNonNull(registry, "RuleRegistry cannot be null");
        this.typeSpecificRules = new LinkedHashMap<>(registry.getTypeSpecificExpenseRules());
        this.globalExpenseRules = new ArrayList<>(registry.getGlobalExpenseRules());
        this.tripRules = new ArrayList<>(registry.getTripRules());
    }

    /**
     * Initializes the engine with custom rules.
     */
    public RuleEngine(Map<ExpenseType, List<ExpenseRule>> typeSpecificRules,
                      List<ExpenseRule> globalExpenseRules,
                      List<TripRule> tripRules) {
        this.typeSpecificRules = typeSpecificRules != null ? new LinkedHashMap<>(typeSpecificRules) : new LinkedHashMap<>();
        this.globalExpenseRules = globalExpenseRules != null ? new ArrayList<>(globalExpenseRules) : new ArrayList<>();
        this.tripRules = tripRules != null ? new ArrayList<>(tripRules) : new ArrayList<>();
    }

    /**
     * Evaluates a single expense against all applicable rules and collects multiple violations.
     *
     * @param expense the expense to evaluate
     * @return ExpenseEvaluationResult containing approval status and all violations
     */
    public ExpenseEvaluationResult evaluateExpense(Expense expense) {
        if (expense == null) {
            throw new IllegalArgumentException("Expense cannot be null");
        }

        List<Violation> violations = new ArrayList<>();

        // 1. Evaluate type-specific rules
        if (expense.getExpenseType() != null && typeSpecificRules.containsKey(expense.getExpenseType())) {
            List<ExpenseRule> rules = typeSpecificRules.get(expense.getExpenseType());
            for (ExpenseRule rule : rules) {
                violations.addAll(rule.evaluate(expense));
            }
        }

        // 2. Evaluate global expense rules (e.g. max expense limit)
        for (ExpenseRule rule : globalExpenseRules) {
            violations.addAll(rule.evaluate(expense));
        }

        return new ExpenseEvaluationResult(expense, violations);
    }

    /**
     * Evaluates a list of expenses individually.
     *
     * @param expenses the list of expenses
     * @return list of ExpenseEvaluationResult
     */
    public List<ExpenseEvaluationResult> evaluateExpenses(List<Expense> expenses) {
        if (expenses == null) {
            return List.of();
        }
        return expenses.stream()
                .map(this::evaluateExpense)
                .collect(Collectors.toList());
    }

    /**
     * Evaluates a trip against trip-level rules and its aggregated expenses.
     *
     * @param trip the trip to evaluate
     * @return TripEvaluationResult
     */
    public TripEvaluationResult evaluateTrip(Trip trip) {
        if (trip == null) {
            throw new IllegalArgumentException("Trip cannot be null");
        }

        // Evaluate all individual expenses
        List<ExpenseEvaluationResult> expenseResults = evaluateExpenses(trip.getExpenses());

        // Evaluate trip-level rules (e.g. trip total limit, meal limit)
        List<Violation> tripViolations = new ArrayList<>();
        for (TripRule rule : tripRules) {
            tripViolations.addAll(rule.evaluate(trip));
        }

        return new TripEvaluationResult(
                trip.getTripId(),
                trip.calculateTotalExpenses(),
                trip.calculateTotalRestaurantExpenses(),
                tripViolations,
                expenseResults
        );
    }

    /**
     * Groups a collection of expenses by trip ID.
     *
     * @param expenses flat list of expenses
     * @return map of tripId to list of expenses
     */
    public Map<String, List<Expense>> groupExpensesByTrip(List<Expense> expenses) {
        if (expenses == null) {
            return Map.of();
        }
        return expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getTripId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    /**
     * Groups expenses into Trip models.
     *
     * @param expenses flat list of expenses
     * @return list of Trip objects
     */
    public List<Trip> groupExpensesIntoTrips(List<Expense> expenses) {
        return groupExpensesByTrip(expenses).entrySet().stream()
                .map(entry -> new Trip(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Groups expenses by trip ID and evaluates each trip, generating trip-level results.
     *
     * @param expenses flat list of expenses
     * @return list of TripEvaluationResult
     */
    public List<TripEvaluationResult> evaluateTripsFromExpenses(List<Expense> expenses) {
        List<Trip> trips = groupExpensesIntoTrips(expenses);
        return trips.stream()
                .map(this::evaluateTrip)
                .collect(Collectors.toList());
    }

    /**
     * Evaluates a list of expenses directly against provided rule registries,
     * matching the interview mentor pattern.
     *
     * @param expenses the incoming expenses
     * @param expenseRulesRegistry map of expense type to rules
     * @param allExpenseRulesRegistry list of global expense rules
     * @param tripRulesRegistry list of trip rules
     * @return flat list of all violations detected
     */
    public List<Violation> evaluate(
            List<Expense> expenses,
            Map<ExpenseType, List<ExpenseRule>> expenseRulesRegistry,
            List<ExpenseRule> allExpenseRulesRegistry,
            List<TripRule> tripRulesRegistry
    ) {
        RuleEngine customEngine = new RuleEngine(expenseRulesRegistry, allExpenseRulesRegistry, tripRulesRegistry);
        List<Violation> allViolations = new ArrayList<>();

        List<TripEvaluationResult> tripResults = customEngine.evaluateTripsFromExpenses(expenses);
        for (TripEvaluationResult tr : tripResults) {
            allViolations.addAll(tr.getAllViolations());
        }
        return allViolations;
    }
}
