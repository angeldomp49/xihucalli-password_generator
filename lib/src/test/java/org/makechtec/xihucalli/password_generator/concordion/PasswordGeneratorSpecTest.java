package org.makechtec.xihucalli.password_generator.concordion;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import org.makechtec.xihucalli.password_generator.ApplicationPropertiesLoader;
import org.makechtec.xihucalli.password_generator.PasswordGenerator;
import org.makechtec.xihucalli.password_generator.PasswordRulesInformation;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@RunWith(ConcordionRunner.class)
public class PasswordGeneratorSpecTest {

    private PasswordGenerator passwordGenerator;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    public String generatePasswordWithBasicRules(int minLength, int maxLength, int minDigits, int minSymbols) throws Exception {
        if (passwordGenerator == null) {
            setUp();
        }
        
        PasswordRulesInformation rules = new PasswordRulesInformation();
        rules.setMinLength(minLength);
        rules.setMaxLength(maxLength);
        rules.setMinNumberOfDigits(minDigits);
        rules.setMinNumberOfSymbols(minSymbols);
        
        String jsonRules = objectMapper.writeValueAsString(rules);
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
        if (passwordGenerator == null) {
            setUp();
        }
        
        PasswordRulesInformation rules = new PasswordRulesInformation();
        rules.setMinLength(12);
        rules.setMaxLength(16);
        rules.setIncludedDigits(includedDigits);
        rules.setIncludedSymbols(includedSymbols);
        
        String jsonRules = objectMapper.writeValueAsString(rules);
        return passwordGenerator.generatePassword(jsonRules);
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
        if (passwordGenerator == null) {
            setUp();
        }
        
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
