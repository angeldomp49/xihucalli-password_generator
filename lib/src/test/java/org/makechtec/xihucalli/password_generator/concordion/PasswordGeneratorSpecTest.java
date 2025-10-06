package org.makechtec.xihucalli.password_generator.concordion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.makechtec.xihucalli.password_generator.ApplicationPropertiesLoader;
import org.makechtec.xihucalli.password_generator.PasswordGenerator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Password Generator Integration Tests")
public class PasswordGeneratorSpecTest {

    private PasswordGenerator passwordGenerator;
    private final Map<String, Object> testResults = new ConcurrentHashMap<>();
    private final Set<String> generatedPasswords = ConcurrentHashMap.newKeySet();

    @BeforeEach
    void setUp() {
        ApplicationPropertiesLoader propertiesLoader = new ApplicationPropertiesLoader();
        propertiesLoader.load("application.properties");
        var properties = propertiesLoader.getProperties();

        passwordGenerator = new PasswordGenerator(
                (String) properties.get("password-generator.numbers.list"),
                (String) properties.get("password-generator.symbols.list"),
                (String) properties.get("password-generator.letters.list")
        );
        
        // Clear previous test data
        testResults.clear();
        generatedPasswords.clear();
    }

    @Test
    @DisplayName("Escenario 1: Generar contraseña con longitud específica")
    void testGeneratePasswordWithBasicRules() {
        // Given
        int minLength = 8;
        int maxLength = 12;
        int minDigits = 2;
        int minSymbols = 1;
        
        // When
        String password = generatePasswordWithBasicRules(minLength, maxLength, minDigits, minSymbols);
        
        // Then
        assertTrue(isLengthValid(password, minLength, maxLength), "Password length should be valid");
        assertTrue(hasMinDigits(password, minDigits), "Password should have minimum digits");
        assertTrue(hasMinSymbols(password, minSymbols), "Password should have minimum symbols");
        assertTrue(isNotEmpty(password), "Password should not be empty");
    }

    @Test
    @DisplayName("Escenario 2: Contraseña con caracteres incluidos específicos")
    void testGeneratePasswordWithIncludes() {
        // Given
        List<Integer> includedDigits = Arrays.asList(1, 2, 3);
        List<Character> includedSymbols = Arrays.asList('@', '#');
        
        // When
        String password = generatePasswordWithIncludes(includedDigits, includedSymbols);
        
        // Then
        assertTrue(containsAllRequiredDigits(password, includedDigits), "Password should contain all required digits");
        assertTrue(containsAllRequiredSymbols(password, includedSymbols), "Password should contain all required symbols");
    }

    @Test
    @DisplayName("Escenario 3: Contraseña con exclusiones")
    void testGeneratePasswordWithExcludes() {
        // Given
        List<Integer> excludedDigits = Arrays.asList(0, 1);
        List<Character> excludedSymbols = Arrays.asList('!', '@');
        
        // When
        String password = generatePasswordWithExcludes(excludedDigits, excludedSymbols);
        
        // Then
        assertTrue(doesNotContainExcludedDigits(password, excludedDigits), "Password should not contain excluded digits");
        assertTrue(doesNotContainExcludedSymbols(password, excludedSymbols), "Password should not contain excluded symbols");
    }

    @Test
    @DisplayName("Escenario 4: Validación de límites extremos")
    void testPasswordLengthLimits() {
        // Test minimum length
        String password1 = generatePasswordWithLength(1, 1);
        assertEquals(1, getPasswordLength(password1), "Password should have exact length of 1");
        
        // Test medium length
        String password2 = generatePasswordWithLength(4, 4);
        assertEquals(4, getPasswordLength(password2), "Password should have exact length of 4");
        
        // Test maximum length
        String password3 = generatePasswordWithLength(30, 30);
        assertEquals(30, getPasswordLength(password3), "Password should have exact length of 30");
    }

    @Test
    @DisplayName("Escenario 5: Manejo de errores")
    void testErrorHandling() {
        // Test null rules
        tryGenerateWithNullRules();
        assertTrue(throwsExceptionWithNullRules(), "Should throw exception with null rules");
        
        // Test impossible rules
        tryGenerateWithImpossibleRules();
        assertTrue(throwsExceptionWithImpossibleRules(), "Should throw exception with impossible rules");
    }

    @Test
    @DisplayName("Escenario 6: Validación de seguridad")
    void testSecurityValidation() {
        // Generate multiple passwords to verify uniqueness
        int numPasswords = 50;
        int uniquePasswords = countUniquePasswords(numPasswords);
        
        // Should generate unique passwords (allowing for some statistical overlap)
        assertTrue(uniquePasswords >= numPasswords * 0.9, 
                "At least 90% of passwords should be unique for security");
    }

    public String generatePasswordWithBasicRules(int minLength, int maxLength, int minDigits, int minSymbols) {
        String jsonRules = String.format("""
            {
              "length": {"min": %d, "max": %d},
              "digits": {"min": %d, "max": %d},
              "symbols": {"min": %d, "max": %d},
              "letters": {"exclude": [], "include": []}
            }
            """, minLength, maxLength, minDigits, maxLength, minSymbols, maxLength);
        
        try {
            String password = passwordGenerator.generatePassword(jsonRules);
            testResults.put("lastGeneratedPassword", password);
            generatedPasswords.add(password);
            return password;
        } catch (Exception e) {
            testResults.put("lastException", e);
            return "ERROR: " + e.getMessage();
        }
    }

    public boolean isLengthValid(String password, int minLength, int maxLength) {
        if (password == null || password.startsWith("ERROR:")) {
            return false;
        }
        return password.length() >= minLength && password.length() <= maxLength;
    }

    public boolean hasMinDigits(String password, int minDigits) {
        if (password == null || password.startsWith("ERROR:")) {
            return false;
        }
        long digitCount = password.chars()
                .filter(Character::isDigit)
                .count();
        return digitCount >= minDigits;
    }

    public boolean hasMinSymbols(String password, int minSymbols) {
        if (password == null || password.startsWith("ERROR:")) {
            return false;
        }
        
        String symbolsPattern = "[!@#$%^&*()_+=-\\[\\]{};'`:\"./<>?~¢£¤¥¦§¨©«¬®°±´¶¸»×÷]";
        Pattern pattern = Pattern.compile(symbolsPattern);
        long symbolCount = password.chars()
                .filter(ch -> pattern.matcher(String.valueOf((char) ch)).matches())
                .count();
        return symbolCount >= minSymbols;
    }

    public boolean isNotEmpty(String password) {
        return password != null && !password.isEmpty() && !password.startsWith("ERROR:");
    }

    public String generatePasswordWithIncludes(List<Integer> includedDigits, List<Character> includedSymbols) {
        StringBuilder symbolsArray = new StringBuilder("[");
        for (int i = 0; i < includedSymbols.size(); i++) {
            if (i > 0) symbolsArray.append(",");
            symbolsArray.append("\"").append(includedSymbols.get(i)).append("\"");
        }
        symbolsArray.append("]");
        
        String jsonRules = String.format("""
            {
              "length": {"min": 10, "max": 15},
              "digits": {"min": 1, "max": 10, "include": %s},
              "symbols": {"min": 1, "max": 5, "include": %s},
              "letters": {"exclude": [], "include": []}
            }
            """, includedDigits, symbolsArray);

        try {
            String password = passwordGenerator.generatePassword(jsonRules);
            testResults.put("passwordWithIncludes", password);
            testResults.put("includedDigits", includedDigits);
            testResults.put("includedSymbols", includedSymbols);
            return password;
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public boolean containsAllRequiredDigits(String password, List<Integer> requiredDigits) {
        if (password == null || password.startsWith("ERROR:")) {
            return false;
        }
        
        for (Integer digit : requiredDigits) {
            if (!password.contains(String.valueOf(digit))) {
                return false;
            }
        }
        return true;
    }

    public boolean containsAllRequiredSymbols(String password, List<Character> requiredSymbols) {
        if (password == null || password.startsWith("ERROR:")) {
            return false;
        }
        
        for (Character symbol : requiredSymbols) {
            if (password.indexOf(symbol) == -1) {
                return false;
            }
        }
        return true;
    }

    public String generatePasswordWithExcludes(List<Integer> excludedDigits, List<Character> excludedSymbols) {
        StringBuilder excludedSymbolsArray = new StringBuilder("[");
        for (int i = 0; i < excludedSymbols.size(); i++) {
            if (i > 0) excludedSymbolsArray.append(",");
            excludedSymbolsArray.append("\"").append(excludedSymbols.get(i)).append("\"");
        }
        excludedSymbolsArray.append("]");

        String jsonRules = String.format("""
            {
              "length": {"min": 12, "max": 18},
              "digits": {"min": 2, "max": 8, "exclude": %s},
              "symbols": {"min": 1, "max": 4, "exclude": %s},
              "letters": {"exclude": [], "include": []}
            }
            """, excludedDigits, excludedSymbolsArray);

        try {
            String password = passwordGenerator.generatePassword(jsonRules);
            testResults.put("passwordWithExcludes", password);
            testResults.put("excludedDigits", excludedDigits);
            testResults.put("excludedSymbols", excludedSymbols);
            return password;
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public boolean doesNotContainExcludedDigits(String password, List<Integer> excludedDigits) {
        if (password == null || password.startsWith("ERROR:")) {
            return false;
        }
        
        for (Integer digit : excludedDigits) {
            if (password.contains(String.valueOf(digit))) {
                return false;
            }
        }
        return true;
    }

    public boolean doesNotContainExcludedSymbols(String password, List<Character> excludedSymbols) {
        if (password == null || password.startsWith("ERROR:")) {
            return false;
        }
        
        for (Character symbol : excludedSymbols) {
            if (password.indexOf(symbol) != -1) {
                return false;
            }
        }
        return true;
    }

    public String generatePasswordWithLength(int minLength, int maxLength) {
        String jsonRules = String.format("""
            {
              "length": {"min": %d, "max": %d},
              "digits": {"min": 0, "max": %d},
              "symbols": {"min": 0, "max": %d},
              "letters": {"exclude": [], "include": []}
            }
            """, minLength, maxLength, maxLength, maxLength);

        try {
            return passwordGenerator.generatePassword(jsonRules);
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public int getPasswordLength(String password) {
        if (password == null || password.startsWith("ERROR:")) {
            return -1;
        }
        return password.length();
    }

    public void tryGenerateWithNullRules() {
        try {
            passwordGenerator.generatePassword(null);
            testResults.put("nullRulesException", false);
        } catch (SecurityException e) {
            testResults.put("nullRulesException", true);
            testResults.put("nullRulesExceptionMessage", e.getMessage());
        }
    }

    public void tryGenerateWithImpossibleRules() {
        String impossibleRules = """
            {
              "length": {"min": 5, "max": 5},
              "digits": {"min": 10, "max": 10},
              "symbols": {"min": 10, "max": 10}
            }
            """;
        
        try {
            passwordGenerator.generatePassword(impossibleRules);
            testResults.put("impossibleRulesException", false);
        } catch (SecurityException e) {
            testResults.put("impossibleRulesException", true);
            testResults.put("impossibleRulesExceptionMessage", e.getMessage());
        }
    }

    public boolean throwsExceptionWithNullRules() {
        return (Boolean) testResults.getOrDefault("nullRulesException", false);
    }

    public boolean throwsExceptionWithImpossibleRules() {
        return (Boolean) testResults.getOrDefault("impossibleRulesException", false);
    }

    public int countUniquePasswords(int numberOfPasswords) {
        Set<String> uniquePasswords = new HashSet<>();
        String basicRules = """
            {
              "length": {"min": 12, "max": 16},
              "digits": {"min": 2, "max": 6},
              "symbols": {"min": 1, "max": 4},
              "letters": {"exclude": [], "include": []}
            }
            """;

        for (int i = 0; i < numberOfPasswords; i++) {
            try {
                String password = passwordGenerator.generatePassword(basicRules);
                uniquePasswords.add(password);
            } catch (Exception e) {
                // Continue generating other passwords
            }
        }
        
        testResults.put("uniquePasswordsGenerated", uniquePasswords.size());
        return uniquePasswords.size();
    }
}
