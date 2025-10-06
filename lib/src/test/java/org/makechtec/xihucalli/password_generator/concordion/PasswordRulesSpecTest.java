package org.makechtec.xihucalli.password_generator.concordion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.makechtec.xihucalli.password_generator.ApplicationPropertiesLoader;
import org.makechtec.xihucalli.password_generator.PasswordRulesInformation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Password Rules Information Tests")
public class PasswordRulesSpecTest {

    private PasswordRulesInformation passwordRules;
    private ApplicationPropertiesLoader propertiesLoader;
    private final Map<String, Object> testResults = new ConcurrentHashMap<>();

    @BeforeEach
    void setUp() {
        passwordRules = new PasswordRulesInformation();
        propertiesLoader = new ApplicationPropertiesLoader();
        testResults.clear();
    }

    public void createBasicRules(long minLength, long maxLength, long minDigits, long minSymbols) {
        passwordRules.setMinLength(minLength);
        passwordRules.setMaxLength(maxLength);
        passwordRules.setMinNumberOfDigits(minDigits);
        passwordRules.setMinNumberOfSymbols(minSymbols);

        testResults.put("rulesConfigured", true);
    }

    public long getMinLength() {
        return passwordRules.getMinLength();
    }

    public long getMaxLength() {
        return passwordRules.getMaxLength();
    }

    public long getMinDigits() {
        return passwordRules.getMinNumberOfDigits();
    }

    public long getMinSymbols() {
        return passwordRules.getMinNumberOfSymbols();
    }

    public boolean areRulesValid() {
        return passwordRules.getMinLength() <= passwordRules.getMaxLength() &&
                passwordRules.getMinLength() > 0 &&
                passwordRules.getMinNumberOfDigits() <= passwordRules.getMaxNumberOfDigits() &&
                passwordRules.getMinNumberOfSymbols() <= passwordRules.getMaxNumberOfSymbols() &&
                passwordRules.getMinNumberOfDigits() >= 0 &&
                passwordRules.getMaxNumberOfDigits() >= 0 &&
                passwordRules.getMinNumberOfSymbols() >= 0 &&
                passwordRules.getMaxNumberOfSymbols() >= 0;
    }

    public void setIncludedDigits(List<Integer> digits) {
        passwordRules.setIncludedDigits(new ArrayList<>(digits));
        testResults.put("includedDigitsSize", digits.size());
    }

    public void setIncludedSymbols(List<Character> symbols) {
        passwordRules.setIncludedSymbols(new ArrayList<>(symbols));
        testResults.put("includedSymbolsSize", symbols.size());
    }

    public void setIncludedLetters(List<Character> letters) {
        passwordRules.setIncludedLetters(new ArrayList<>(letters));
        testResults.put("includedLettersSize", letters.size());
    }

    public int getIncludedDigitsSize() {
        return passwordRules.getIncludedDigits().size();
    }

    public int getIncludedSymbolsSize() {
        return passwordRules.getIncludedSymbols().size();
    }

    public int getIncludedLettersSize() {
        return passwordRules.getIncludedLetters().size();
    }

    public void setExcludedDigits(List<Integer> digits) {
        passwordRules.setExcludedDigits(new ArrayList<>(digits));
        testResults.put("excludedDigitsSize", digits.size());
    }

    public void setExcludedSymbols(List<Character> symbols) {
        passwordRules.setExcludedSymbols(new ArrayList<>(symbols));
        testResults.put("excludedSymbolsSize", symbols.size());
    }

    public void setExcludedLetters(List<Character> letters) {
        passwordRules.setExcludedLetters(new ArrayList<>(letters));
        testResults.put("excludedLettersSize", letters.size());
    }

    public int getExcludedDigitsSize() {
        return passwordRules.getExcludedDigits().size();
    }

    public int getExcludedSymbolsSize() {
        return passwordRules.getExcludedSymbols().size();
    }

    public int getExcludedLettersSize() {
        return passwordRules.getExcludedLetters().size();
    }

    public boolean validateConflictingRules(long minLength, long maxLength, long minDigits, long maxDigits) {
        PasswordRulesInformation testRules = new PasswordRulesInformation();
        testRules.setMinLength(minLength);
        testRules.setMaxLength(maxLength);
        testRules.setMinNumberOfDigits(minDigits);
        testRules.setMaxNumberOfDigits(maxDigits);

        boolean isValid = testRules.getMinLength() <= testRules.getMaxLength() &&
                testRules.getMinLength() > 0 &&
                testRules.getMinNumberOfDigits() <= testRules.getMaxNumberOfDigits() &&
                testRules.getMinNumberOfDigits() >= 0;

        testResults.put("conflictValidation_" + minLength + "_" + maxLength, isValid);
        return isValid;
    }

    public void loadFromProperties() {
        try {
            propertiesLoader.load("application.properties");
            testResults.put("propertiesLoaded", true);
            testResults.put("numbersFromProps", propertiesLoader.getProperties().get("password-generator.numbers.list"));
            testResults.put("symbolsFromProps", propertiesLoader.getProperties().get("password-generator.symbols.list"));
            testResults.put("lettersFromProps", propertiesLoader.getProperties().get("password-generator.letters.list"));
        } catch (Exception e) {
            testResults.put("propertiesLoaded", false);
            testResults.put("propertiesException", e.getMessage());
        }
    }

    public String getNumbersFromProperties() {
        return (String) testResults.getOrDefault("numbersFromProps", "");
    }

    public String getSymbolsFromProperties() {
        return (String) testResults.getOrDefault("symbolsFromProps", "");
    }

    public String getLettersFromProperties() {
        return (String) testResults.getOrDefault("lettersFromProps", "");
    }

    public boolean numbersAreLoaded() {
        String numbers = getNumbersFromProperties();
        return numbers != null && numbers.contains("0123456789");
    }

    public boolean symbolsAreLoaded() {
        String symbols = getSymbolsFromProperties();
        return symbols != null && symbols.length() > 5;
    }

    public boolean lettersAreLoaded() {
        String letters = getLettersFromProperties();
        return letters != null && letters.contains("abcdef") && letters.contains("ABCDEF");
    }

    public boolean validateLengthBounds(long minValue, long maxValue, long defaultValue) {
        return minValue >= 1 && maxValue <= 128 &&
                defaultValue >= minValue && defaultValue <= maxValue;
    }

    public boolean validateDigitsBounds(long minValue, long maxValue, long defaultValue) {
        return minValue >= 0 && maxValue <= 128 &&
                defaultValue >= minValue && defaultValue <= maxValue;
    }

    public boolean validateSymbolsBounds(long minValue, long maxValue, long defaultValue) {
        return minValue >= 0 && maxValue <= 128 &&
                defaultValue >= minValue && defaultValue <= maxValue;
    }

    // Additional utility methods for comprehensive testing
    public void resetRules() {
        passwordRules = new PasswordRulesInformation();
    }

    public boolean hasValidDefaults() {
        PasswordRulesInformation defaultRules = new PasswordRulesInformation();
        return defaultRules.getMinLength() == 1 &&
                defaultRules.getMaxLength() == 30 &&
                defaultRules.getMinNumberOfDigits() == 0 &&
                defaultRules.getMinNumberOfSymbols() == 0 &&
                defaultRules.getExcludedDigits().isEmpty() &&
                defaultRules.getIncludedDigits().isEmpty() &&
                defaultRules.getExcludedSymbols().isEmpty() &&
                defaultRules.getIncludedSymbols().isEmpty() &&
                defaultRules.getExcludedLetters().isEmpty() &&
                defaultRules.getIncludedLetters().isEmpty();
    }

    public void configureComplexRulesCombination() {
        // Test complex combinations of rules
        passwordRules.setMinLength(12);
        passwordRules.setMaxLength(20);
        passwordRules.setMinNumberOfDigits(3);
        passwordRules.setMaxNumberOfDigits(8);
        passwordRules.setMinNumberOfSymbols(2);
        passwordRules.setMaxNumberOfSymbols(5);

        List<Integer> includedDigits = Arrays.asList(1, 2, 3);
        List<Character> excludedSymbols = Arrays.asList('!', '@');
        List<Character> includedLetters = Arrays.asList('A', 'B', 'C');

        passwordRules.setIncludedDigits(includedDigits);
        passwordRules.setExcludedSymbols(excludedSymbols);
        passwordRules.setIncludedLetters(includedLetters);

        testResults.put("complexRulesConfigured", true);
    }

    public boolean isComplexRulesValid() {
        return areRulesValid() &&
                !passwordRules.getIncludedDigits().isEmpty() &&
                !passwordRules.getExcludedSymbols().isEmpty() &&
                !passwordRules.getIncludedLetters().isEmpty();
    }

    @Test
    @DisplayName("Escenario 1: Configuración básica de reglas")
    void testCreateBasicRules() {
        // Given
        long minLength = 8;
        long maxLength = 20;
        long minDigits = 2;
        long minSymbols = 1;

        // When
        createBasicRules(minLength, maxLength, minDigits, minSymbols);

        // Then
        assertEquals(minLength, getMinLength(), "Min length should be configured correctly");
        assertEquals(maxLength, getMaxLength(), "Max length should be configured correctly");
        assertEquals(minDigits, getMinDigits(), "Min digits should be configured correctly");
        assertEquals(minSymbols, getMinSymbols(), "Min symbols should be configured correctly");
        assertTrue(areRulesValid(), "Rules should be valid");
    }

    @Test
    @DisplayName("Escenario 2: Configuración de caracteres incluidos")
    void testIncludedCharacters() {
        // Test digits inclusion
        List<Integer> digitsToInclude = Arrays.asList(1, 2, 3, 4);
        setIncludedDigits(digitsToInclude);
        assertEquals(4, getIncludedDigitsSize(), "Should include 4 digits");

        // Test symbols inclusion
        List<Character> symbolsToInclude = Arrays.asList('@', '#', '$');
        setIncludedSymbols(symbolsToInclude);
        assertEquals(3, getIncludedSymbolsSize(), "Should include 3 symbols");

        // Test letters inclusion
        List<Character> lettersToInclude = Arrays.asList('A', 'B', 'C');
        setIncludedLetters(lettersToInclude);
        assertEquals(3, getIncludedLettersSize(), "Should include 3 letters");
    }

    @Test
    @DisplayName("Escenario 3: Configuración de caracteres excluidos")
    void testExcludedCharacters() {
        // Test digits exclusion
        List<Integer> digitsToExclude = Arrays.asList(0, 1);
        setExcludedDigits(digitsToExclude);
        assertEquals(2, getExcludedDigitsSize(), "Should exclude 2 digits");

        // Test symbols exclusion
        List<Character> symbolsToExclude = Arrays.asList('!', '@');
        setExcludedSymbols(symbolsToExclude);
        assertEquals(2, getExcludedSymbolsSize(), "Should exclude 2 symbols");

        // Test letters exclusion
        List<Character> lettersToExclude = Arrays.asList('O', 'I', 'L');
        setExcludedLetters(lettersToExclude);
        assertEquals(3, getExcludedLettersSize(), "Should exclude 3 letters");
    }

    @Test
    @DisplayName("Escenario 4: Validación de reglas conflictivas")
    void testConflictingRules() {
        // Test conflicting length rules
        assertFalse(validateConflictingRules(10, 5, 0, 10), "Should detect conflicting length rules");

        // Test conflicting digit rules
        assertFalse(validateConflictingRules(8, 12, 5, 2), "Should detect conflicting digit rules");

        // Test valid rules
        assertTrue(validateConflictingRules(8, 20, 2, 10), "Should accept valid rules");
    }

    @Test
    @DisplayName("Escenario 5: Carga desde archivo de propiedades")
    void testLoadFromProperties() {
        // When
        loadFromProperties();

        // Then
        assertTrue(numbersAreLoaded(), "Numbers should be loaded from properties");
        assertTrue(symbolsAreLoaded(), "Symbols should be loaded from properties");
        assertTrue(lettersAreLoaded(), "Letters should be loaded from properties");
    }

    @Test
    @DisplayName("Escenario 6: Validación de límites extremos")
    void testBoundsValidation() {
        // Test length bounds
        assertTrue(validateLengthBounds(1, 128, 30), "Length bounds should be valid");
    }
}
