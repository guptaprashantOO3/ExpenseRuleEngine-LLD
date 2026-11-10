package com.expense.ruleengine.service.rules;

import java.util.List;

/**
 * Common generic rule interface for evaluating target objects against business rules.
 *
 * @param <T> the type of object evaluated by this rule
 */
public interface Rule<T> {

    /**
     * Evaluates the given target and returns any violations detected.
     *
     * @param target the object to evaluate
     * @return a list of violations, or an empty list if compliant
     */
    List<Violation> evaluate(T target);

    /**
     * Returns a human-readable name of the rule.
     *
     * @return rule name
     */
    default String getName() {
        return getClass().getSimpleName();
    }
}
