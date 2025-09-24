package org.makechtec.xihucalli.password_generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PasswordRulesInformationTest {

    private PasswordRulesInformation passwordRules;

    @BeforeEach
    void setUp() {
        passwordRules = new PasswordRulesInformation();
    }

    // ========== TESTS FOR DEFAULT VALUES ==========

    @Test
    void testDefaultValues() {
        assertEquals(1L, passwordRules.getMinLength(), 
                     "Default minimum length should be 1");
        assertEquals(30L, passwordRules.getMaxLength(), 
                     "Default maximum length should be 30");
        assertEquals(0L, passwordRules.getMinNumberOfDigits(), 
                     "Default minimum digits should be 0");
        assertEquals(30L, passwordRules.getMaxNumberOfDigits(), 
                     "Default maximum digits should be 30");
        assertEquals(0L, passwordRules.getMinNumberOfSymbols(), 
                     "Default minimum symbols should be 0");
        assertEquals(30L, passwordRules.getMaxNumberOfSymbols(), 
                     "Default maximum symbols should be 30");
        
        assertNotNull(passwordRules.getExcludedDigits(), "Excluded digits list should not be null");
        assertNotNull(passwordRules.getIncludedDigits(), "Included digits list should not be null");
        assertNotNull(passwordRules.getExcludedSymbols(), "Excluded symbols list should not be null");
        assertNotNull(passwordRules.getIncludedSymbols(), "Included symbols list should not be null");
        assertNotNull(passwordRules.getExcludedLetters(), "Excluded letters list should not be null");
        assertNotNull(passwordRules.getIncludedLetters(), "Included letters list should not be null");
        
        assertTrue(passwordRules.getExcludedDigits().isEmpty(), "Excluded digits should be empty by default");
        assertTrue(passwordRules.getIncludedDigits().isEmpty(), "Included digits should be empty by default");
        assertTrue(passwordRules.getExcludedSymbols().isEmpty(), "Excluded symbols should be empty by default");
        assertTrue(passwordRules.getIncludedSymbols().isEmpty(), "Included symbols should be empty by default");
        assertTrue(passwordRules.getExcludedLetters().isEmpty(), "Excluded letters should be empty by default");
        assertTrue(passwordRules.getIncludedLetters().isEmpty(), "Included letters should be empty by default");
    }

    // ========== TESTS FOR LENGTH SETTERS AND GETTERS ==========

    @Test
    void testMinLengthSetterAndGetter() {
        passwordRules.setMinLength(5L);
        assertEquals(5L, passwordRules.getMinLength(), "Min length should be set correctly");
        
        passwordRules.setMinLength(0L);
        assertEquals(0L, passwordRules.getMinLength(), "Min length should handle zero");
        
        passwordRules.setMinLength(100L);
        assertEquals(100L, passwordRules.getMinLength(), "Min length should handle large values");
    }

    @Test
    void testMaxLengthSetterAndGetter() {
        passwordRules.setMaxLength(50L);
        assertEquals(50L, passwordRules.getMaxLength(), "Max length should be set correctly");
        
        passwordRules.setMaxLength(1L);
        assertEquals(1L, passwordRules.getMaxLength(), "Max length should handle minimum value");
        
        passwordRules.setMaxLength(1000L);
        assertEquals(1000L, passwordRules.getMaxLength(), "Max length should handle large values");
    }

    // ========== TESTS FOR DIGITS SETTERS AND GETTERS ==========

    @Test
    void testMinNumberOfDigitsSetterAndGetter() {
        passwordRules.setMinNumberOfDigits(3L);
        assertEquals(3L, passwordRules.getMinNumberOfDigits(), "Min digits should be set correctly");
        
        passwordRules.setMinNumberOfDigits(0L);
        assertEquals(0L, passwordRules.getMinNumberOfDigits(), "Min digits should handle zero");
        
        passwordRules.setMinNumberOfDigits(20L);
        assertEquals(20L, passwordRules.getMinNumberOfDigits(), "Min digits should handle larger values");
    }

    @Test
    void testMaxNumberOfDigitsSetterAndGetter() {
        passwordRules.setMaxNumberOfDigits(10L);
        assertEquals(10L, passwordRules.getMaxNumberOfDigits(), "Max digits should be set correctly");
        
        passwordRules.setMaxNumberOfDigits(0L);
        assertEquals(0L, passwordRules.getMaxNumberOfDigits(), "Max digits should handle zero");
        
        passwordRules.setMaxNumberOfDigits(50L);
        assertEquals(50L, passwordRules.getMaxNumberOfDigits(), "Max digits should handle large values");
    }

    @Test
    void testExcludedDigitsSetterAndGetter() {
        List<Integer> excludedDigits = Arrays.asList(0, 1, 5, 9);
        passwordRules.setExcludedDigits(excludedDigits);
        
        assertEquals(excludedDigits, passwordRules.getExcludedDigits(), 
                     "Excluded digits should be set correctly");
        assertEquals(4, passwordRules.getExcludedDigits().size(), 
                     "Excluded digits list size should be correct");
        assertTrue(passwordRules.getExcludedDigits().contains(0), 
                   "Should contain excluded digit 0");
        assertTrue(passwordRules.getExcludedDigits().contains(1), 
                   "Should contain excluded digit 1");
        assertTrue(passwordRules.getExcludedDigits().contains(5), 
                   "Should contain excluded digit 5");
        assertTrue(passwordRules.getExcludedDigits().contains(9), 
                   "Should contain excluded digit 9");
    }

    @Test
    void testIncludedDigitsSetterAndGetter() {
        List<Integer> includedDigits = Arrays.asList(2, 4, 7, 8);
        passwordRules.setIncludedDigits(includedDigits);
        
        assertEquals(includedDigits, passwordRules.getIncludedDigits(), 
                     "Included digits should be set correctly");
        assertEquals(4, passwordRules.getIncludedDigits().size(), 
                     "Included digits list size should be correct");
        assertTrue(passwordRules.getIncludedDigits().contains(2), 
                   "Should contain included digit 2");
        assertTrue(passwordRules.getIncludedDigits().contains(4), 
                   "Should contain included digit 4");
        assertTrue(passwordRules.getIncludedDigits().contains(7), 
                   "Should contain included digit 7");
        assertTrue(passwordRules.getIncludedDigits().contains(8), 
                   "Should contain included digit 8");
    }

    // ========== TESTS FOR SYMBOLS SETTERS AND GETTERS ==========

    @Test
    void testMinNumberOfSymbolsSetterAndGetter() {
        passwordRules.setMinNumberOfSymbols(2L);
        assertEquals(2L, passwordRules.getMinNumberOfSymbols(), "Min symbols should be set correctly");
        
        passwordRules.setMinNumberOfSymbols(0L);
        assertEquals(0L, passwordRules.getMinNumberOfSymbols(), "Min symbols should handle zero");
        
        passwordRules.setMinNumberOfSymbols(15L);
        assertEquals(15L, passwordRules.getMinNumberOfSymbols(), "Min symbols should handle larger values");
    }

    @Test
    void testMaxNumberOfSymbolsSetterAndGetter() {
        passwordRules.setMaxNumberOfSymbols(8L);
        assertEquals(8L, passwordRules.getMaxNumberOfSymbols(), "Max symbols should be set correctly");
        
        passwordRules.setMaxNumberOfSymbols(0L);
        assertEquals(0L, passwordRules.getMaxNumberOfSymbols(), "Max symbols should handle zero");
        
        passwordRules.setMaxNumberOfSymbols(25L);
        assertEquals(25L, passwordRules.getMaxNumberOfSymbols(), "Max symbols should handle large values");
    }

    @Test
    void testExcludedSymbolsSetterAndGetter() {
        List<Character> excludedSymbols = Arrays.asList('$', '%', '^', '&');
        passwordRules.setExcludedSymbols(excludedSymbols);
        
        assertEquals(excludedSymbols, passwordRules.getExcludedSymbols(), 
                     "Excluded symbols should be set correctly");
        assertEquals(4, passwordRules.getExcludedSymbols().size(), 
                     "Excluded symbols list size should be correct");
        assertTrue(passwordRules.getExcludedSymbols().contains('$'), 
                   "Should contain excluded symbol '$'");
        assertTrue(passwordRules.getExcludedSymbols().contains('%'), 
                   "Should contain excluded symbol '%'");
        assertTrue(passwordRules.getExcludedSymbols().contains('^'), 
                   "Should contain excluded symbol '^'");
        assertTrue(passwordRules.getExcludedSymbols().contains('&'), 
                   "Should contain excluded symbol '&'");
    }

    @Test
    void testIncludedSymbolsSetterAndGetter() {
        List<Character> includedSymbols = Arrays.asList('@', '#', '!', '*');
        passwordRules.setIncludedSymbols(includedSymbols);
        
        assertEquals(includedSymbols, passwordRules.getIncludedSymbols(), 
                     "Included symbols should be set correctly");
        assertEquals(4, passwordRules.getIncludedSymbols().size(), 
                     "Included symbols list size should be correct");
        assertTrue(passwordRules.getIncludedSymbols().contains('@'), 
                   "Should contain included symbol '@'");
        assertTrue(passwordRules.getIncludedSymbols().contains('#'), 
                   "Should contain included symbol '#'");
        assertTrue(passwordRules.getIncludedSymbols().contains('!'), 
                   "Should contain included symbol '!'");
        assertTrue(passwordRules.getIncludedSymbols().contains('*'), 
                   "Should contain included symbol '*'");
    }

    // ========== TESTS FOR LETTERS SETTERS AND GETTERS ==========

    @Test
    void testExcludedLettersSetterAndGetter() {
        List<Character> excludedLetters = Arrays.asList('a', 'e', 'i', 'o', 'u');
        passwordRules.setExcludedLetters(excludedLetters);
        
        assertEquals(excludedLetters, passwordRules.getExcludedLetters(), 
                     "Excluded letters should be set correctly");
        assertEquals(5, passwordRules.getExcludedLetters().size(), 
                     "Excluded letters list size should be correct");
        assertTrue(passwordRules.getExcludedLetters().contains('a'), 
                   "Should contain excluded letter 'a'");
        assertTrue(passwordRules.getExcludedLetters().contains('e'), 
                   "Should contain excluded letter 'e'");
        assertTrue(passwordRules.getExcludedLetters().contains('i'), 
                   "Should contain excluded letter 'i'");
        assertTrue(passwordRules.getExcludedLetters().contains('o'), 
                   "Should contain excluded letter 'o'");
        assertTrue(passwordRules.getExcludedLetters().contains('u'), 
                   "Should contain excluded letter 'u'");
    }

    @Test
    void testIncludedLettersSetterAndGetter() {
        List<Character> includedLetters = Arrays.asList('A', 'B', 'C', 'x', 'y', 'z');
        passwordRules.setIncludedLetters(includedLetters);
        
        assertEquals(includedLetters, passwordRules.getIncludedLetters(), 
                     "Included letters should be set correctly");
        assertEquals(6, passwordRules.getIncludedLetters().size(), 
                     "Included letters list size should be correct");
        assertTrue(passwordRules.getIncludedLetters().contains('A'), 
                   "Should contain included letter 'A'");
        assertTrue(passwordRules.getIncludedLetters().contains('B'), 
                   "Should contain included letter 'B'");
        assertTrue(passwordRules.getIncludedLetters().contains('C'), 
                   "Should contain included letter 'C'");
        assertTrue(passwordRules.getIncludedLetters().contains('x'), 
                   "Should contain included letter 'x'");
        assertTrue(passwordRules.getIncludedLetters().contains('y'), 
                   "Should contain included letter 'y'");
        assertTrue(passwordRules.getIncludedLetters().contains('z'), 
                   "Should contain included letter 'z'");
    }

    // ========== TESTS FOR EDGE CASES ==========

    @Test
    void testEmptyLists() {
        passwordRules.setExcludedDigits(Arrays.asList());
        passwordRules.setIncludedDigits(Arrays.asList());
        passwordRules.setExcludedSymbols(Arrays.asList());
        passwordRules.setIncludedSymbols(Arrays.asList());
        passwordRules.setExcludedLetters(Arrays.asList());
        passwordRules.setIncludedLetters(Arrays.asList());
        
        assertTrue(passwordRules.getExcludedDigits().isEmpty(), 
                   "Empty excluded digits list should remain empty");
        assertTrue(passwordRules.getIncludedDigits().isEmpty(), 
                   "Empty included digits list should remain empty");
        assertTrue(passwordRules.getExcludedSymbols().isEmpty(), 
                   "Empty excluded symbols list should remain empty");
        assertTrue(passwordRules.getIncludedSymbols().isEmpty(), 
                   "Empty included symbols list should remain empty");
        assertTrue(passwordRules.getExcludedLetters().isEmpty(), 
                   "Empty excluded letters list should remain empty");
        assertTrue(passwordRules.getIncludedLetters().isEmpty(), 
                   "Empty included letters list should remain empty");
    }

    @Test
    void testSingleItemLists() {
        passwordRules.setExcludedDigits(Arrays.asList(5));
        passwordRules.setIncludedDigits(Arrays.asList(3));
        passwordRules.setExcludedSymbols(Arrays.asList('#'));
        passwordRules.setIncludedSymbols(Arrays.asList('@'));
        passwordRules.setExcludedLetters(Arrays.asList('q'));
        passwordRules.setIncludedLetters(Arrays.asList('Z'));
        
        assertEquals(1, passwordRules.getExcludedDigits().size(), 
                     "Single item excluded digits list should have size 1");
        assertEquals(1, passwordRules.getIncludedDigits().size(), 
                     "Single item included digits list should have size 1");
        assertEquals(1, passwordRules.getExcludedSymbols().size(), 
                     "Single item excluded symbols list should have size 1");
        assertEquals(1, passwordRules.getIncludedSymbols().size(), 
                     "Single item included symbols list should have size 1");
        assertEquals(1, passwordRules.getExcludedLetters().size(), 
                     "Single item excluded letters list should have size 1");
        assertEquals(1, passwordRules.getIncludedLetters().size(), 
                     "Single item included letters list should have size 1");
        
        assertTrue(passwordRules.getExcludedDigits().contains(5), 
                   "Should contain single excluded digit");
        assertTrue(passwordRules.getIncludedDigits().contains(3), 
                   "Should contain single included digit");
        assertTrue(passwordRules.getExcludedSymbols().contains('#'), 
                   "Should contain single excluded symbol");
        assertTrue(passwordRules.getIncludedSymbols().contains('@'), 
                   "Should contain single included symbol");
        assertTrue(passwordRules.getExcludedLetters().contains('q'), 
                   "Should contain single excluded letter");
        assertTrue(passwordRules.getIncludedLetters().contains('Z'), 
                   "Should contain single included letter");
    }

    // ========== TESTS FOR BOUNDARY VALUES ==========

    @Test
    void testBoundaryValues() {
        // Test minimum boundary values
        passwordRules.setMinLength(0L);
        passwordRules.setMaxLength(0L);
        passwordRules.setMinNumberOfDigits(0L);
        passwordRules.setMaxNumberOfDigits(0L);
        passwordRules.setMinNumberOfSymbols(0L);
        passwordRules.setMaxNumberOfSymbols(0L);
        
        assertEquals(0L, passwordRules.getMinLength(), "Should handle zero min length");
        assertEquals(0L, passwordRules.getMaxLength(), "Should handle zero max length");
        assertEquals(0L, passwordRules.getMinNumberOfDigits(), "Should handle zero min digits");
        assertEquals(0L, passwordRules.getMaxNumberOfDigits(), "Should handle zero max digits");
        assertEquals(0L, passwordRules.getMinNumberOfSymbols(), "Should handle zero min symbols");
        assertEquals(0L, passwordRules.getMaxNumberOfSymbols(), "Should handle zero max symbols");
        
        // Test large boundary values
        passwordRules.setMinLength(Long.MAX_VALUE);
        passwordRules.setMaxLength(Long.MAX_VALUE);
        passwordRules.setMinNumberOfDigits(Long.MAX_VALUE);
        passwordRules.setMaxNumberOfDigits(Long.MAX_VALUE);
        passwordRules.setMinNumberOfSymbols(Long.MAX_VALUE);
        passwordRules.setMaxNumberOfSymbols(Long.MAX_VALUE);
        
        assertEquals(Long.MAX_VALUE, passwordRules.getMinLength(), "Should handle max long value for min length");
        assertEquals(Long.MAX_VALUE, passwordRules.getMaxLength(), "Should handle max long value for max length");
        assertEquals(Long.MAX_VALUE, passwordRules.getMinNumberOfDigits(), "Should handle max long value for min digits");
        assertEquals(Long.MAX_VALUE, passwordRules.getMaxNumberOfDigits(), "Should handle max long value for max digits");
        assertEquals(Long.MAX_VALUE, passwordRules.getMinNumberOfSymbols(), "Should handle max long value for min symbols");
        assertEquals(Long.MAX_VALUE, passwordRules.getMaxNumberOfSymbols(), "Should handle max long value for max symbols");
    }

    // ========== TESTS FOR SPECIAL CHARACTERS ==========

    @Test
    void testSpecialCharactersInSymbols() {
        List<Character> specialSymbols = Arrays.asList('\\', '/', '"', '\'', '\t', '\n');
        passwordRules.setIncludedSymbols(specialSymbols);
        
        assertEquals(specialSymbols, passwordRules.getIncludedSymbols(), 
                     "Should handle special characters in symbols");
        assertTrue(passwordRules.getIncludedSymbols().contains('\\'), 
                   "Should contain backslash");
        assertTrue(passwordRules.getIncludedSymbols().contains('/'), 
                   "Should contain forward slash");
        assertTrue(passwordRules.getIncludedSymbols().contains('"'), 
                   "Should contain double quote");
        assertTrue(passwordRules.getIncludedSymbols().contains('\''), 
                   "Should contain single quote");
        assertTrue(passwordRules.getIncludedSymbols().contains('\t'), 
                   "Should contain tab character");
        assertTrue(passwordRules.getIncludedSymbols().contains('\n'), 
                   "Should contain newline character");
    }

    @Test
    void testUnicodeCharactersInLetters() {
        List<Character> unicodeLetters = Arrays.asList('ñ', 'ü', 'á', 'é', 'í', 'ó', 'ú');
        passwordRules.setIncludedLetters(unicodeLetters);
        
        assertEquals(unicodeLetters, passwordRules.getIncludedLetters(), 
                     "Should handle Unicode characters in letters");
        assertTrue(passwordRules.getIncludedLetters().contains('ñ'), 
                   "Should contain ñ");
        assertTrue(passwordRules.getIncludedLetters().contains('ü'), 
                   "Should contain ü");
        assertTrue(passwordRules.getIncludedLetters().contains('á'), 
                   "Should contain á");
    }
}
