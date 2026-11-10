package com.expense.ruleengine.registry;

import com.expense.ruleengine.enums.ExpenseType;
import com.expense.ruleengine.service.rules.ExpenseRule;
import com.expense.ruleengine.service.rules.TripRule;
import com.expense.ruleengine.service.rules.Impl.AirfareDisallowRule;
import com.expense.ruleengine.service.rules.Impl.EntertainmentDisallowRule;
import com.expense.ruleengine.service.rules.Impl.MaxAmountRule;
import com.expense.ruleengine.service.rules.Impl.MaxExpenseLimitRule;
import com.expense.ruleengine.service.rules.Impl.RestaurantLimitRule;
import com.expense.ruleengine.service.rules.Impl.TripMealLimitRule;
import com.expense.ruleengine.service.rules.Impl.TripTotalLimitRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry holding expense-level and trip-level rules.
 * Supports static defaults and custom instance registrations.
 */
public class RuleRegistry {

    private final Map<ExpenseType, List<ExpenseRule>> typeSpecificExpenseRules;
    private final List<ExpenseRule> globalExpenseRules;
    private final List<TripRule> tripRules;

    public RuleRegistry() {
        this.typeSpecificExpenseRules = new HashMap<>();
        this.globalExpenseRules = new ArrayList<>();
        this.tripRules = new ArrayList<>();
    }

    /**
     * Creates and returns a registry populated with all standard rules according to specifications:
     * - Restaurant limit: $75
     * - Airfare: Disallowed
     * - Entertainment: Disallowed
     * - Max individual expense limit: $250
     * - Total trip limit: $2,000
     * - Trip meal limit: $1,000
     */
    public static RuleRegistry createDefaultRegistry() {
        RuleRegistry registry = new RuleRegistry();

        // Expense-level rules
        registry.registerExpenseRule(ExpenseType.RESTAURANT, new RestaurantLimitRule(75.0));
        registry.registerExpenseRule(ExpenseType.AIRFARE, new AirfareDisallowRule());
        registry.registerExpenseRule(ExpenseType.ENTERTAINMENT, new EntertainmentDisallowRule());

        // Global expense rule ($250 max limit)
        registry.registerGlobalExpenseRule(new MaxExpenseLimitRule(250.0));

        // Trip-level rules ($2,000 trip limit, $1,000 meal limit)
        registry.registerTripRule(new TripTotalLimitRule(2000.0));
        registry.registerTripRule(new TripMealLimitRule(1000.0));

        return registry;
    }

    /**
     * Returns type-specific expense rules matching the requirement specification.
     */
    public static Map<ExpenseType, List<ExpenseRule>> getExpenseRulesRegistry() {
        Map<ExpenseType, List<ExpenseRule>> registry = new HashMap<>();

        // Validate restaurant limit ($75)
        registry.put(ExpenseType.RESTAURANT, List.of(
                new RestaurantLimitRule(75.0)
        ));

        // Reject airfare expenses
        registry.put(ExpenseType.AIRFARE, List.of(
                new AirfareDisallowRule()
        ));

        // Reject entertainment expenses
        registry.put(ExpenseType.ENTERTAINMENT, List.of(
                new EntertainmentDisallowRule()
        ));

        return registry;
    }

    /**
     * Returns global expense rules applied to all expenses ($250 max limit).
     */
    public static List<ExpenseRule> getAllExpenseRulesRegistry() {
        return List.of(
                new MaxExpenseLimitRule(250.0)
        );
    }

    /**
     * Returns trip-level rules ($2,000 total trip limit and $1,000 meal limit).
     */
    public static List<TripRule> getTripRulesRegistry() {
        return List.of(
                new TripTotalLimitRule(2000.0),
                new TripMealLimitRule(1000.0)
        );
    }

    /**
     * Alias for getTripRulesRegistry for compatibility with mentor interview template.
     */
    public static List<TripRule> getAllTripRulesRegistry() {
        return getTripRulesRegistry();
    }

    public void registerExpenseRule(ExpenseType type, ExpenseRule rule) {
        typeSpecificExpenseRules.computeIfAbsent(type, k -> new ArrayList<>()).add(rule);
    }

    public void registerGlobalExpenseRule(ExpenseRule rule) {
        globalExpenseRules.add(rule);
    }

    public void registerTripRule(TripRule rule) {
        tripRules.add(rule);
    }

    public Map<ExpenseType, List<ExpenseRule>> getTypeSpecificExpenseRules() {
        return Collections.unmodifiableMap(typeSpecificExpenseRules);
    }

    public List<ExpenseRule> getGlobalExpenseRules() {
        return Collections.unmodifiableList(globalExpenseRules);
    }

    public List<TripRule> getTripRules() {
        return Collections.unmodifiableList(tripRules);
    }
}
