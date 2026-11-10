package com.expense.ruleengine;

import com.expense.ruleengine.engine.RuleEngine;
import com.expense.ruleengine.enums.ExpenseType;
import com.expense.ruleengine.models.Expense;
import com.expense.ruleengine.models.ExpenseEvaluationResult;
import com.expense.ruleengine.models.TripEvaluationResult;
import com.expense.ruleengine.registry.RuleRegistry;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("        EXPENSE RULE ENGINE EVALUATION           ");
        System.out.println("=================================================\n");

        // Initialize Rule Engine with rules from RuleRegistry
        RuleEngine engine = new RuleEngine(RuleRegistry.createDefaultRegistry());

        // -------------------------------------------------------------------
        // 1. Expense-Level Evaluation Scenarios
        // -------------------------------------------------------------------
        System.out.println("--- 1. EXPENSE-LEVEL EVALUATION ---");

        List<Expense> sampleExpenses = List.of(
                // Valid restaurant expense (<= $75)
                new Expense("EXP-001", 50.0, ExpenseType.RESTAURANT, "TRIP-101"),

                // Exceeds restaurant limit ($75), but within global limit ($250)
                new Expense("EXP-002", 120.0, ExpenseType.RESTAURANT, "TRIP-101"),

                // Disallowed airfare expense
                new Expense("EXP-003", 200.0, ExpenseType.AIRFARE, "TRIP-101"),

                // Disallowed entertainment expense
                new Expense("EXP-004", 80.0, ExpenseType.ENTERTAINMENT, "TRIP-101"),

                // Exceeds global maximum expense limit ($250)
                new Expense("EXP-005", 280.0, ExpenseType.HOTEL, "TRIP-101"),

                // Multiple violations: Exceeds restaurant limit ($75) AND global limit ($250)
                new Expense("EXP-006", 350.0, ExpenseType.RESTAURANT, "TRIP-101")
        );

        List<ExpenseEvaluationResult> expenseResults = engine.evaluateExpenses(sampleExpenses);
        for (ExpenseEvaluationResult res : expenseResults) {
            System.out.println(String.format("Expense ID: %-8s | Type: %-13s | Amount: $%6.2f | Status: %-8s",
                    res.getExpenseId(),
                    res.getExpense().getExpenseType(),
                    res.getExpense().getAmountUsd(),
                    res.getStatus()));
            if (res.isRejected()) {
                res.getViolations().forEach(v -> System.out.println("   --> Violation: " + v));
            }
        }

        // -------------------------------------------------------------------
        // 2. Trip-Level Evaluation Scenarios
        // -------------------------------------------------------------------
        System.out.println("\n--- 2. TRIP-LEVEL EVALUATION ---");

        List<Expense> tripDataset = List.of(
                // TRIP-100: Compliant Trip (Total: $200, Meal: $120)
                new Expense("E101", 60.0, ExpenseType.RESTAURANT, "TRIP-100"),
                new Expense("E102", 60.0, ExpenseType.RESTAURANT, "TRIP-100"),
                new Expense("E103", 80.0, ExpenseType.HOTEL, "TRIP-100"),

                // TRIP-200: Trip Exceeding $2,000 Limit (Total: $2,200)
                new Expense("E201", 240.0, ExpenseType.HOTEL, "TRIP-200"),
                new Expense("E202", 240.0, ExpenseType.HOTEL, "TRIP-200"),
                new Expense("E203", 240.0, ExpenseType.HOTEL, "TRIP-200"),
                new Expense("E204", 240.0, ExpenseType.HOTEL, "TRIP-200"),
                new Expense("E205", 240.0, ExpenseType.HOTEL, "TRIP-200"),
                new Expense("E206", 240.0, ExpenseType.HOTEL, "TRIP-200"),
                new Expense("E207", 240.0, ExpenseType.HOTEL, "TRIP-200"),
                new Expense("E208", 240.0, ExpenseType.HOTEL, "TRIP-200"),
                new Expense("E209", 240.0, ExpenseType.HOTEL, "TRIP-200"),
                new Expense("E210", 40.0, ExpenseType.OTHER, "TRIP-200"),

                // TRIP-300: Trip Exceeding $1,000 Meal Limit (Total: $1,250, Meals: $1,050)
                // (each meal is <= $75, but total restaurant expenses > $1,000)
                new Expense("E301", 70.0, ExpenseType.RESTAURANT, "TRIP-300"),
                new Expense("E302", 70.0, ExpenseType.RESTAURANT, "TRIP-300"),
                new Expense("E303", 70.0, ExpenseType.RESTAURANT, "TRIP-300"),
                new Expense("E304", 70.0, ExpenseType.RESTAURANT, "TRIP-300"),
                new Expense("E305", 70.0, ExpenseType.RESTAURANT, "TRIP-300"),
                new Expense("E306", 70.0, ExpenseType.RESTAURANT, "TRIP-300"),
                new Expense("E307", 70.0, ExpenseType.RESTAURANT, "TRIP-300"),
                new Expense("E308", 70.0, ExpenseType.RESTAURANT, "TRIP-300"),
                new Expense("E309", 70.0, ExpenseType.RESTAURANT, "TRIP-300"),
                new Expense("E310", 70.0, ExpenseType.RESTAURANT, "TRIP-300"),
                new Expense("E311", 70.0, ExpenseType.RESTAURANT, "TRIP-300"),
                new Expense("E312", 70.0, ExpenseType.RESTAURANT, "TRIP-300"),
                new Expense("E313", 70.0, ExpenseType.RESTAURANT, "TRIP-300"),
                new Expense("E314", 70.0, ExpenseType.RESTAURANT, "TRIP-300"),
                new Expense("E315", 70.0, ExpenseType.RESTAURANT, "TRIP-300"), // 15 * 70 = 1050
                new Expense("E316", 200.0, ExpenseType.HOTEL, "TRIP-300")
        );

        List<TripEvaluationResult> tripResults = engine.evaluateTripsFromExpenses(tripDataset);

        for (TripEvaluationResult result : tripResults) {
            System.out.println(String.format("Trip ID: %-10s | Total: $%7.2f | Meals: $%7.2f | Status: %-8s",
                    result.getTripId(),
                    result.getTotalTripExpenses(),
                    result.getTotalRestaurantExpenses(),
                    result.getStatus()));

            if (!result.getTripViolations().isEmpty()) {
                result.getTripViolations().forEach(v -> System.out.println("   --> Trip Violation: " + v));
            }
            long failedExpenses = result.getExpenseResults().stream().filter(r -> r.isRejected()).count();
            if (failedExpenses > 0) {
                System.out.println("   --> Expense Violations Count: " + failedExpenses);
            }
        }

        System.out.println("\n=================================================");
        System.out.println("        EVALUATION COMPLETED SUCCESSFULLY        ");
        System.out.println("=================================================");
    }
}
