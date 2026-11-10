package com.expense.ruleengine.service.rules;

import java.util.Objects;

public class Violation {
    private final String message;
    private final String ruleName;

    private Violation(String message) {
        this(message, null);
    }

    private Violation(String message, String ruleName) {
        this.message = message;
        this.ruleName = ruleName;
    }

    public static Violation of(String message) {
        return new Violation(message);
    }

    public static Violation of(String ruleName, String message) {
        return new Violation(message, ruleName);
    }

    public String getMessage() {
        return message;
    }

    public String getRuleName() {
        return ruleName;
    }

    @Override
    public String toString() {
        return ruleName != null ? "[" + ruleName + "] " + message : message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Violation violation = (Violation) o;
        return Objects.equals(message, violation.message) && Objects.equals(ruleName, violation.ruleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, ruleName);
    }
}
