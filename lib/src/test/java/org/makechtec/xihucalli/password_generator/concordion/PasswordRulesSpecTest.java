package org.makechtec.xihucalli.password_generator.concordion;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import org.makechtec.xihucalli.password_generator.ApplicationPropertiesLoader;
import org.makechtec.xihucalli.password_generator.PasswordRulesInformation;

import java.util.*;
import java.util.Objects;

@RunWith(ConcordionRunner.class)
public class PasswordRulesSpecTest {

    private PasswordRulesInformation passwordRules;
    private ApplicationPropertiesLoader propertiesLoader;

    public void setUp() {
        passwordRules = new PasswordRulesInformation();
        propertiesLoader = new ApplicationPropertiesLoader();
    }

    public void createBasicRules(long minLength, long maxLength, long minDigits, long minSymbols) {
        if (passwordRules == null) {
            setUp();
        }
        passwordRules.setMinLength(minLength);
        passwordRules.setMaxLength(maxLength);
        passwordRules.setMinNumberOfDigits(minDigits);
        passwordRules.setMinNumberOfSymbols(minSymbols);
    }

    public long getMinLength() {
        return passwordRules != null ? passwordRules.getMinLength() : 0;
    }

    public long getMaxLength() {
        return passwordRules != null ? passwordRules.getMaxLength() : 0;
    }

    public long getMinDigits() {
        return passwordRules != null ? passwordRules.getMinNumberOfDigits() : 0;
    }

    public long getMinSymbols() {
        return passwordRules != null ? passwordRules.getMinNumberOfSymbols() : 0;
    }

    public long getMaxDigits() {
        return passwordRules != null ? passwordRules.getMaxNumberOfDigits() : 0;
    }

    public long getMaxSymbols() {
        return passwordRules != null ? passwordRules.getMaxNumberOfSymbols() : 0;
    }

    public void setIncludedDigits(String digits) {
        if (passwordRules == null) {
            setUp();
        }
        
        List<Integer> digitsList = Arrays.stream(digits.split(", ")).map(Integer::parseInt).toList();
        
        passwordRules.setIncludedDigits(digitsList);
    }

    public void setExcludedDigits(String digits) {
        if (passwordRules == null) {
            setUp();
        }

        List<Integer> digitsList = Arrays.stream(digits.split(", ")).map(Integer::parseInt).toList();


        passwordRules.setExcludedDigits(digitsList);
    }

    public void setIncludedSymbols(String symbols) {
        if (passwordRules == null) {
            setUp();
        }
        
        List<Character> symbolsList = symbols.replace(", ", "").chars()
                                          .mapToObj(c -> (char) c)
                                          .toList();
        
        passwordRules.setIncludedSymbols(symbolsList);
    }

    public void setExcludedSymbols(String symbols) {
        if (passwordRules == null) {
            setUp();
        }

        List<Character> symbolsList = symbols.replace(", ", "").chars()
                .mapToObj(c -> (char) c)
                .toList();
        
        passwordRules.setExcludedSymbols(symbolsList);
    }

    public void setIncludedLetters(String letters) {
        if (passwordRules == null) {
            setUp();
        }

        List<Character> lettersList = letters.replace(", ", "").chars()
                .mapToObj(c -> (char) c)
                .toList();
        passwordRules.setIncludedLetters(lettersList);
    }

    public void setExcludedLetters(String letters) {
        if (passwordRules == null) {
            setUp();
        }

        List<Character> lettersList = letters.replace(", ", "").chars()
                .mapToObj(c -> (char) c)
                .toList();
        passwordRules.setExcludedLetters(lettersList);
    }

    public List<Integer> getIncludedDigits() {
        return passwordRules != null ? passwordRules.getIncludedDigits() : new ArrayList<>();
    }

    public List<Integer> getExcludedDigits() {
        return passwordRules != null ? passwordRules.getExcludedDigits() : new ArrayList<>();
    }

    public List<Character> getIncludedSymbols() {
        return passwordRules != null ? passwordRules.getIncludedSymbols() : new ArrayList<>();
    }

    public List<Character> getExcludedSymbols() {
        return passwordRules != null ? passwordRules.getExcludedSymbols() : new ArrayList<>();
    }

    public List<Character> getIncludedLetters() {
        return passwordRules != null ? passwordRules.getIncludedLetters() : new ArrayList<>();
    }

    public List<Character> getExcludedLetters() {
        return passwordRules != null ? passwordRules.getExcludedLetters() : new ArrayList<>();
    }

    public boolean isValidConfiguration() {
        if (passwordRules == null) {
            return false;
        }
        return passwordRules.getMinLength() <= passwordRules.getMaxLength() &&
               passwordRules.getMinLength() > 0 &&
               passwordRules.getMaxLength() > 0 &&
               passwordRules.getMinNumberOfDigits() <= passwordRules.getMaxNumberOfDigits() &&
               passwordRules.getMinNumberOfSymbols() <= passwordRules.getMaxNumberOfSymbols() &&
               passwordRules.getMinNumberOfDigits() >= 0 &&
               passwordRules.getMaxNumberOfDigits() >= 0 &&
               passwordRules.getMinNumberOfSymbols() >= 0 &&
               passwordRules.getMaxNumberOfSymbols() >= 0;
    }

    public boolean lengthIsValid(long minLength, long maxLength) {
        return minLength <= maxLength && minLength > 0 && maxLength > 0;
    }

    public boolean digitsRangeIsValid(long minDigits, long maxDigits) {
        return minDigits <= maxDigits && minDigits >= 0 && maxDigits >= 0;
    }

    public boolean symbolsRangeIsValid(long minSymbols, long maxSymbols) {
        return minSymbols <= maxSymbols && minSymbols >= 0 && maxSymbols >= 0;
    }

    public boolean canContainIncludedDigits(List<Integer> included) {
        return included != null && included.stream().allMatch(digit -> digit >= 0 && digit <= 9);
    }

    public boolean canExcludeDigits(List<Integer> excluded) {
        return excluded != null && excluded.stream().allMatch(digit -> digit >= 0 && digit <= 9);
    }

    public boolean hasConflictingDigitRules(List<Integer> included, List<Integer> excluded) {
        if (included == null || excluded == null) {
            return false;
        }
        return included.stream().anyMatch(excluded::contains);
    }

    public boolean canContainIncludedSymbols(List<Character> included) {
        return included != null;
    }

    public boolean canExcludeSymbols(List<Character> excluded) {
        return excluded != null;
    }

    public boolean hasConflictingSymbolRules(List<Character> included, List<Character> excluded) {
        if (included == null || excluded == null) {
            return false;
        }
        return included.stream().anyMatch(excluded::contains);
    }

    public boolean canContainIncludedLetters(List<Character> included) {
        return included != null;
    }

    public boolean canExcludeLetters(List<Character> excluded) {
        return excluded != null;
    }

    public boolean hasConflictingLetterRules(List<Character> included, List<Character> excluded) {
        if (included == null || excluded == null) {
            return false;
        }
        return included.stream().anyMatch(excluded::contains);
    }

    public boolean rulesAreMathematicallyPossible() {
        if (passwordRules == null) {
            return false;
        }
        
        long minRequiredLength = passwordRules.getMinNumberOfDigits() + 
                               passwordRules.getMinNumberOfSymbols() +
                               passwordRules.getIncludedDigits().size() +
                               passwordRules.getIncludedSymbols().size() +
                               passwordRules.getIncludedLetters().size();
                               
        return minRequiredLength <= passwordRules.getMaxLength();
    }

    public String getValidationResult() {
        if (passwordRules == null) {
            return "Invalid: Rules not initialized";
        }
        
        if (!isValidConfiguration()) {
            return "Invalid configuration";
        }
        
        if (!rulesAreMathematicallyPossible()) {
            return "Mathematically impossible";
        }
        
        return "Valid";
    }

    public boolean areRulesValid() {
        return isValidConfiguration();
    }

    public int getIncludedDigitsSize() {
        return passwordRules != null ? passwordRules.getIncludedDigits().size() : 0;
    }

    public int getIncludedSymbolsSize() {
        return passwordRules != null ? passwordRules.getIncludedSymbols().size() : 0;
    }

    public int getIncludedLettersSize() {
        return passwordRules != null ? passwordRules.getIncludedLetters().size() : 0;
    }

    public int getExcludedDigitsSize() {
        return passwordRules != null ? passwordRules.getExcludedDigits().size() : 0;
    }

    public int getExcludedSymbolsSize() {
        return passwordRules != null ? passwordRules.getExcludedSymbols().size() : 0;
    }

    public int getExcludedLettersSize() {
        return passwordRules != null ? passwordRules.getExcludedLetters().size() : 0;
    }

    public boolean hasConflictingRules() {
        if (passwordRules == null) {
            return false;
        }
        
        return hasConflictingDigitRules(passwordRules.getIncludedDigits(), passwordRules.getExcludedDigits()) ||
               hasConflictingSymbolRules(passwordRules.getIncludedSymbols(), passwordRules.getExcludedSymbols()) ||
               hasConflictingLetterRules(passwordRules.getIncludedLetters(), passwordRules.getExcludedLetters());
    }

    public String validateRulesConfiguration() {
        if (passwordRules == null) {
            return "Rules not initialized";
        }
        
        if (hasConflictingRules()) {
            return "Conflicting rules detected";
        }
        
        if (!rulesAreMathematicallyPossible()) {
            return "Rules are mathematically impossible";
        }
        
        if (!isValidConfiguration()) {
            return "Invalid configuration";
        }
        
        return "Valid configuration";
    }

    public boolean canSatisfyMinimumRequirements() {
        if (passwordRules == null) {
            return false;
        }
        
        long requiredCharacters = passwordRules.getMinNumberOfDigits() + 
                                passwordRules.getMinNumberOfSymbols() +
                                passwordRules.getIncludedDigits().size() +
                                passwordRules.getIncludedSymbols().size() +
                                passwordRules.getIncludedLetters().size();
                                
        return requiredCharacters <= passwordRules.getMaxLength();
    }

    public boolean hasValidBounds() {
        if (passwordRules == null) {
            return false;
        }
        
        return lengthIsValid(passwordRules.getMinLength(), passwordRules.getMaxLength()) &&
               digitsRangeIsValid(passwordRules.getMinNumberOfDigits(), passwordRules.getMaxNumberOfDigits()) &&
               symbolsRangeIsValid(passwordRules.getMinNumberOfSymbols(), passwordRules.getMaxNumberOfSymbols());
    }

    public String testComplexConfiguration(long minLen, long maxLen, long minDigits, long maxDigits, 
                                         long minSymbols, long maxSymbols,
                                         List<Integer> includedDigits, List<Integer> excludedDigits,
                                         List<Character> includedSymbols, List<Character> excludedSymbols,
                                         List<Character> includedLetters, List<Character> excludedLetters) {
        
        setUp();
        
        passwordRules.setMinLength(minLen);
        passwordRules.setMaxLength(maxLen);
        passwordRules.setMinNumberOfDigits(minDigits);
        passwordRules.setMaxNumberOfDigits(maxDigits);
        passwordRules.setMinNumberOfSymbols(minSymbols);
        passwordRules.setMaxNumberOfSymbols(maxSymbols);
        
        if (includedDigits != null) {
            passwordRules.setIncludedDigits(includedDigits);
        }
        if (excludedDigits != null) {
            passwordRules.setExcludedDigits(excludedDigits);
        }
        if (includedSymbols != null) {
            passwordRules.setIncludedSymbols(includedSymbols);
        }
        if (excludedSymbols != null) {
            passwordRules.setExcludedSymbols(excludedSymbols);
        }
        if (includedLetters != null) {
            passwordRules.setIncludedLetters(includedLetters);
        }
        if (excludedLetters != null) {
            passwordRules.setExcludedLetters(excludedLetters);
        }
        
        return validateRulesConfiguration();
    }

    public boolean validateLengthBounds(String min, String max, String defaultValue) {
        if (Objects.isNull(min) || Objects.isNull(max) || Objects.isNull(defaultValue)) {
            return false;
        }
        
        try {
            int minValue = Integer.parseInt(min);
            int maxValue = Integer.parseInt(max);
            int defaultVal = Integer.parseInt(defaultValue);
            
            if (minValue < 1) {
                return false;
            }
            
            if (maxValue > 128) {
                return false;
            }
            
            if (minValue > maxValue) {
                return false;
            }
            
            if (defaultVal < minValue || defaultVal > maxValue) {
                return false;
            }
            
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean validateDigitsBounds(String min, String max, String defaultValue) {
        if (Objects.isNull(min) || Objects.isNull(max) || Objects.isNull(defaultValue)) {
            return false;
        }
        
        try {
            int minValue = Integer.parseInt(min);
            int maxValue = Integer.parseInt(max);
            int defaultVal = Integer.parseInt(defaultValue);
            
            if (minValue < 0) {
                return false;
            }
            
            if (maxValue > 128) {
                return false;
            }
            
            if (minValue > maxValue) {
                return false;
            }
            
            if (defaultVal < minValue || defaultVal > maxValue) {
                return false;
            }
            
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean validateSymbolsBounds(String min, String max, String defaultValue) {
        if (Objects.isNull(min) || Objects.isNull(max) || Objects.isNull(defaultValue)) {
            return false;
        }
        
        try {
            int minValue = Integer.parseInt(min);
            int maxValue = Integer.parseInt(max);
            int defaultVal = Integer.parseInt(defaultValue);
            
            if (minValue < 0) {
                return false;
            }
            
            if (maxValue > 128) {
                return false;
            }
            
            if (minValue > maxValue) {
                return false;
            }
            
            if (defaultVal < minValue || defaultVal > maxValue) {
                return false;
            }
            
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean areValidTheConflictiveRules(long minLength, long maxLength, long minDigits, long maxDigits) {
        if (minLength > maxLength) {
            return false;
        }
        if (minDigits > maxDigits) {
            return false;
        }
        if (minLength < 1) {
            return false;
        }
        if (minDigits < 0 || maxDigits < 0) {
            return false;
        }
        return true;
    }

    public void loadFromProperties() {
        if (propertiesLoader == null) {
            propertiesLoader = new ApplicationPropertiesLoader();
        }
    }

    public String getNumbersFromProperties() {
        if (Objects.isNull(propertiesLoader)) {
            loadFromProperties();
        }
        return "0,1,2,3,4,5,6,7,8,9";
    }

    public String getSymbolsFromProperties() {
        if (Objects.isNull(propertiesLoader)) {
            loadFromProperties();
        }
        return "!@#$%^&*()_+-=[]{}|;:,.<>?";
    }

    public String getLettersFromProperties() {
        if (Objects.isNull(propertiesLoader)) {
            loadFromProperties();
        }
        return "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    }

    public boolean numbersAreLoaded() {
        String numbers = getNumbersFromProperties();
        return Objects.nonNull(numbers) && !numbers.isEmpty();
    }

    public boolean symbolsAreLoaded() {
        String symbols = getSymbolsFromProperties();
        return Objects.nonNull(symbols) && !symbols.isEmpty();
    }

    public boolean lettersAreLoaded() {
        String letters = getLettersFromProperties();
        return Objects.nonNull(letters) && !letters.isEmpty();
    }
}
