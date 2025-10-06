package org.makechtec.xihucalli.password_generator;

import java.util.ArrayList;
import java.util.List;

public class PasswordRulesInformation {

    private long minLength = 1;
    private long maxLength = 30;
    private long minNumberOfDigits = 0;
    private long maxNumberOfDigits = 30;
    private List<Integer> excludedDigits = new ArrayList<>();
    private List<Integer> includedDigits = new ArrayList<>();
    private long minNumberOfSymbols = 0;
    private long maxNumberOfSymbols = 30;
    private List<Character> excludedSymbols = new ArrayList<>();
    private List<Character> includedSymbols = new ArrayList<>();
    private List<Character> excludedLetters = new ArrayList<>();
    private List<Character> includedLetters = new ArrayList<>();

    public long getMinLength() {
        return minLength;
    }

    public void setMinLength(long minLength) {
        this.minLength = minLength;
    }

    public long getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(long maxLength) {
        this.maxLength = maxLength;
        // Actualizar automáticamente los límites máximos para mantener consistencia
        if (this.maxNumberOfDigits > maxLength) {
            this.maxNumberOfDigits = maxLength;
        }
        if (this.maxNumberOfSymbols > maxLength) {
            this.maxNumberOfSymbols = maxLength;
        }
    }

    public long getMinNumberOfDigits() {
        return minNumberOfDigits;
    }

    public void setMinNumberOfDigits(long minNumberOfDigits) {
        this.minNumberOfDigits = minNumberOfDigits;
    }

    public long getMaxNumberOfDigits() {
        return maxNumberOfDigits;
    }

    public void setMaxNumberOfDigits(long maxNumberOfDigits) {
        this.maxNumberOfDigits = maxNumberOfDigits;
    }

    public List<Integer> getExcludedDigits() {
        return excludedDigits;
    }

    public void setExcludedDigits(List<Integer> excludedDigits) {
        this.excludedDigits = excludedDigits;
    }

    public List<Integer> getIncludedDigits() {
        return includedDigits;
    }

    public void setIncludedDigits(List<Integer> includedDigits) {
        this.includedDigits = includedDigits;
    }

    public long getMinNumberOfSymbols() {
        return minNumberOfSymbols;
    }

    public void setMinNumberOfSymbols(long minNumberOfSymbols) {
        this.minNumberOfSymbols = minNumberOfSymbols;
    }

    public long getMaxNumberOfSymbols() {
        return maxNumberOfSymbols;
    }

    public void setMaxNumberOfSymbols(long maxNumberOfSymbols) {
        this.maxNumberOfSymbols = maxNumberOfSymbols;
    }

    public List<Character> getExcludedSymbols() {
        return excludedSymbols;
    }

    public void setExcludedSymbols(List<Character> excludedSymbols) {
        this.excludedSymbols = excludedSymbols;
    }

    public List<Character> getIncludedSymbols() {
        return includedSymbols;
    }

    public void setIncludedSymbols(List<Character> includedSymbols) {
        this.includedSymbols = includedSymbols;
    }

    public List<Character> getExcludedLetters() {
        return excludedLetters;
    }

    public void setExcludedLetters(List<Character> excludedLetters) {
        this.excludedLetters = excludedLetters;
    }

    public List<Character> getIncludedLetters() {
        return includedLetters;
    }

    public void setIncludedLetters(List<Character> includedLetters) {
        this.includedLetters = includedLetters;
    }
}
