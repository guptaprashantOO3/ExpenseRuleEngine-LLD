package com.expense.ruleengine.models;

import com.expense.ruleengine.enums.ExpenseType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Trip model representing aggregated expenses for a specific trip.
 */
public class Trip {
    private final String tripId;
    private final List<Expense> expenses;

    public Trip(String tripId) {
        this(tripId, new ArrayList<>());
    }

    public Trip(String tripId, List<Expense> expenses) {
        this.tripId = Objects.requireNonNull(tripId, "tripId must not be null");
        this.expenses = new ArrayList<>(expenses != null ? expenses : Collections.emptyList());
    }

    public String getTripId() {
        return tripId;
    }

    public List<Expense> getExpenses() {
        return Collections.unmodifiableList(expenses);
    }

    public void addExpense(Expense expense) {
        if (expense != null) {
            expenses.add(expense);
        }
    }

    /**
     * Calculates the total amount for all expenses in this trip.
     *
     * @return total expenses in USD
     */
    public double calculateTotalExpenses() {
        return expenses.stream()
                .mapToDouble(e -> e.getAmountUsd() != null ? e.getAmountUsd() : 0.0)
                .sum();
    }

    /**
     * Calculates the total amount spent on restaurant/meal expenses in this trip.
     *
     * @return total restaurant expenses in USD
     */
    public double calculateTotalRestaurantExpenses() {
        return expenses.stream()
                .filter(e -> e.getExpenseType() == ExpenseType.RESTAURANT)
                .mapToDouble(e -> e.getAmountUsd() != null ? e.getAmountUsd() : 0.0)
                .sum();
    }

    @Override
    public String toString() {
        return "Trip{" +
                "tripId='" + tripId + '\'' +
                ", expensesCount=" + expenses.size() +
                ", totalExpenses=" + calculateTotalExpenses() +
                ", totalRestaurantExpenses=" + calculateTotalRestaurantExpenses() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return Objects.equals(tripId, trip.tripId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripId);
    }
}
