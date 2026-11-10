package com.expense.ruleengine.models;

import com.expense.ruleengine.enums.EvaluationStatus;
import com.expense.ruleengine.service.rules.Violation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of evaluating a trip against trip-level rules and its aggregated expenses.
 */
public class TripEvaluationResult extends EvaluationResult {
    private final String tripId;
    private final double totalTripExpenses;
    private final double totalRestaurantExpenses;
    private final List<ExpenseEvaluationResult> expenseResults;

    public TripEvaluationResult(String tripId,
                                double totalTripExpenses,
                                double totalRestaurantExpenses,
                                List<Violation> tripViolations,
                                List<ExpenseEvaluationResult> expenseResults) {
        super(determineStatus(tripViolations, expenseResults), tripViolations);
        this.tripId = tripId;
        this.totalTripExpenses = totalTripExpenses;
        this.totalRestaurantExpenses = totalRestaurantExpenses;
        this.expenseResults = expenseResults != null ? new ArrayList<>(expenseResults) : new ArrayList<>();
    }

    private static EvaluationStatus determineStatus(List<Violation> tripViolations, List<ExpenseEvaluationResult> expenseResults) {
        boolean hasTripViolations = tripViolations != null && !tripViolations.isEmpty();
        boolean hasExpenseViolations = expenseResults != null && expenseResults.stream().anyMatch(EvaluationResult::isRejected);
        return (hasTripViolations || hasExpenseViolations) ? EvaluationStatus.REJECTED : EvaluationStatus.APPROVED;
    }

    public String getTripId() {
        return tripId;
    }

    public double getTotalTripExpenses() {
        return totalTripExpenses;
    }

    public double getTotalRestaurantExpenses() {
        return totalRestaurantExpenses;
    }

    public List<ExpenseEvaluationResult> getExpenseResults() {
        return Collections.unmodifiableList(expenseResults);
    }

    public List<Violation> getTripViolations() {
        return getViolations();
    }

    /**
     * Collects all violations across this trip, including trip-level and individual expense violations.
     *
     * @return combined list of all violations
     */
    public List<Violation> getAllViolations() {
        List<Violation> all = new ArrayList<>(getTripViolations());
        for (ExpenseEvaluationResult er : expenseResults) {
            all.addAll(er.getViolations());
        }
        return all;
    }

    @Override
    public String toString() {
        return "TripEvaluationResult{" +
                "tripId='" + tripId + '\'' +
                ", totalTripExpenses=" + totalTripExpenses +
                ", totalRestaurantExpenses=" + totalRestaurantExpenses +
                ", status=" + getStatus() +
                ", tripViolations=" + getTripViolations() +
                ", expenseViolationsCount=" + expenseResults.stream().filter(EvaluationResult::isRejected).count() +
                '}';
    }
}
