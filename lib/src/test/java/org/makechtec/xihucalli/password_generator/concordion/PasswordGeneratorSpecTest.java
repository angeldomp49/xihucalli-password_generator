package org.makechtec.xihucalli.password_generator.concordion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.makechtec.xihucalli.password_generator.ApplicationPropertiesLoader;
import org.makechtec.xihucalli.password_generator.PasswordGenerator;
import org.makechtec.xihucalli.password_generator.PasswordRulesInformation;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Password Generator Specification Tests")
public class PasswordGeneratorSpecTest {

    private PasswordGenerator passwordGenerator;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        ApplicationPropertiesLoader propertiesLoader = new ApplicationPropertiesLoader();
        propertiesLoader.load("application.properties");
        var properties = propertiesLoader.getProperties();

        passwordGenerator = new PasswordGenerator(
                (String) properties.get("password-generator.numbers.list"),
                (String) properties.get("password-generator.symbols.list"),
                (String) properties.get("password-generator.letters.list")
        );
    }

    @Test
    @DisplayName("Escenario 1: Generar contraseña con longitud específica")
    public void testGeneratePasswordWithBasicRules() throws Exception {
        int minLength = 8;
        int maxLength = 12;
        int minDigits = 2;
        int minSymbols = 1;
        
        String password = generatePasswordWithBasicRules(minLength, maxLength, minDigits, minSymbols);
        
        System.out.println("Generated Password: " + password);
        assertTrue(isLengthValid(password, minLength, maxLength), "Password length should be valid");
        assertTrue(hasMinDigits(password, minDigits), "Password should have minimum digits");
        assertTrue(hasMinSymbols(password, minSymbols), "Password should have minimum symbols");
        assertTrue(isNotEmpty(password), "Password should not be empty");
    }

    @Test
    @DisplayName("Escenario 2: Contraseña con caracteres incluidos específicos")
    public void testGeneratePasswordWithIncludes() throws Exception {
        List<Integer> includedDigits = Arrays.asList(1, 2, 3);
        List<Character> includedSymbols = Arrays.asList('@', '#');
        
        String password = generatePasswordWithIncludes(includedDigits, includedSymbols);
        
        assertTrue(containsAllRequiredDigits(password, includedDigits), "Password should contain all required digits");
        assertTrue(containsAllRequiredSymbols(password, includedSymbols), "Password should contain all required symbols");
    }

    @Test
    @DisplayName("Escenario 3: Contraseña con exclusiones")
    public void testGeneratePasswordWithExcludes() throws Exception {
        List<Integer> excludedDigits = Arrays.asList(0, 1);
        List<Character> excludedSymbols = Arrays.asList('!', '@');
        
        String password = generatePasswordWithExcludes(excludedDigits, excludedSymbols);
        
        assertTrue(doesNotContainExcludedDigits(password, excludedDigits), "Password should not contain excluded digits");
        assertTrue(doesNotContainExcludedSymbols(password, excludedSymbols), "Password should not contain excluded symbols");
    }

    public String generatePasswordWithBasicRules(int minLength, int maxLength, int minDigits, int minSymbols) throws Exception {
        var jsonRules = """
                {
                  "length": {
                    "min": %d,
                    "max": %d
                  },
                  "digits": {
                    "min": %d
                  },
                  "symbols": {
                    "min": %d
                  }
                }
                """.formatted(minLength, maxLength, minDigits, minSymbols);
        
        return passwordGenerator.generatePassword(jsonRules);
    }

    public boolean isLengthValid(String password, int minLength, int maxLength) {
        return password.length() >= minLength && password.length() <= maxLength;
    }

    public boolean hasMinDigits(String password, int minDigits) {
        long digitCount = password.chars()
                .filter(Character::isDigit)
                .count();
        return digitCount >= minDigits;
    }

    public boolean hasMinSymbols(String password, int minSymbols) {
        long symbolCount = password.chars()
                .filter(ch -> !Character.isLetterOrDigit(ch))
                .count();
        return symbolCount >= minSymbols;
    }

    public boolean isNotEmpty(String password) {
        return password != null && !password.isEmpty();
    }

    public String generatePasswordWithIncludes(List<Integer> includedDigits, List<Character> includedSymbols) throws Exception {
        
        var jsonRules2 = objectMapper.createObjectNode();
        
        var lengthNode = objectMapper.createObjectNode();
        lengthNode.put("min", 12);
        lengthNode.put("max", 16);
        
        var digitsNode = objectMapper.createObjectNode();
        var includeArray = objectMapper.createArrayNode();
        for (Integer digit : includedDigits) {
            includeArray.add(digit);
        }
        digitsNode.set("include", includeArray);
        
        var symbolsNode = objectMapper.createObjectNode();
        
        var includeSymbolsArray = objectMapper.createArrayNode();
        for (Character symbol : includedSymbols) {
            includeSymbolsArray.add(symbol.toString());
        }
        symbolsNode.set("include", includeSymbolsArray);
        
        jsonRules2.set("length", lengthNode);
        jsonRules2.set("digits", digitsNode);
        jsonRules2.set("symbols", symbolsNode);
        
        
        String stringRules = objectMapper.writeValueAsString(jsonRules2);
        return passwordGenerator.generatePassword(stringRules);
    }

    public boolean containsAllRequiredDigits(String password, List<Integer> requiredDigits) {
        return requiredDigits.stream()
                .allMatch(digit -> password.contains(digit.toString()));
    }

    public boolean containsAllRequiredSymbols(String password, List<Character> requiredSymbols) {
        return requiredSymbols.stream()
                .allMatch(symbol -> password.indexOf(symbol) >= 0);
    }

    public String generatePasswordWithExcludes(List<Integer> excludedDigits, List<Character> excludedSymbols) throws Exception {
        PasswordRulesInformation rules = new PasswordRulesInformation();
        rules.setMinLength(10);
        rules.setMaxLength(14);
        rules.setExcludedDigits(excludedDigits);
        rules.setExcludedSymbols(excludedSymbols);
        
        String jsonRules = objectMapper.writeValueAsString(rules);
        return passwordGenerator.generatePassword(jsonRules);
    }

    public boolean doesNotContainExcludedDigits(String password, List<Integer> excludedDigits) {
        return excludedDigits.stream()
                .noneMatch(digit -> password.contains(digit.toString()));
    }

    public boolean doesNotContainExcludedSymbols(String password, List<Character> excludedSymbols) {
        return excludedSymbols.stream()
                .noneMatch(symbol -> password.indexOf(symbol) >= 0);
    }
}
