package org.makechtec.xihucalli.password_generator.concordion;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import org.makechtec.xihucalli.password_generator.ApplicationPropertiesLoader;
import org.makechtec.xihucalli.password_generator.PasswordGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        
        ObjectNode jsonRules = objectMapper.createObjectNode();
        
        ObjectNode lengthNode = objectMapper.createObjectNode();
        lengthNode.put("min", minLength);
        lengthNode.put("max", maxLength);
        jsonRules.set("length", lengthNode);
        
        ObjectNode digitsNode = objectMapper.createObjectNode();
        digitsNode.put("min", minDigits);
        digitsNode.put("max", maxLength);
        digitsNode.set("exclude", objectMapper.createArrayNode());
        digitsNode.set("include", objectMapper.createArrayNode());
        jsonRules.set("digits", digitsNode);
        
        ObjectNode symbolsNode = objectMapper.createObjectNode();
        symbolsNode.put("min", minSymbols);
        symbolsNode.put("max", maxLength);
        symbolsNode.set("exclude", objectMapper.createArrayNode());
        symbolsNode.set("include", objectMapper.createArrayNode());
        jsonRules.set("symbols", symbolsNode);
        
        ObjectNode lettersNode = objectMapper.createObjectNode();
        lettersNode.set("exclude", objectMapper.createArrayNode());
        lettersNode.set("include", objectMapper.createArrayNode());
        jsonRules.set("letters", lettersNode);

        System.err.println( "s1:" + jsonRules.toString() );

        String s = passwordGenerator.generatePassword(jsonRules.toString());
        
        System.err.println( "s1:" + s);
        
        return s;
        
        
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
        
        ObjectNode jsonRules = objectMapper.createObjectNode();
        
        ObjectNode lengthNode = objectMapper.createObjectNode();
        lengthNode.put("min", 12);
        lengthNode.put("max", 16);
        jsonRules.set("length", lengthNode);
        
        ObjectNode digitsNode = objectMapper.createObjectNode();
        digitsNode.put("min", 0);
        digitsNode.put("max", 16);
        digitsNode.set("exclude", objectMapper.createArrayNode());
        ArrayNode includeDigitsArray = objectMapper.createArrayNode();
        for (Integer digit : includedDigits) {
            includeDigitsArray.add(digit);
        }
        digitsNode.set("include", includeDigitsArray);
        jsonRules.set("digits", digitsNode);
        
        ObjectNode symbolsNode = objectMapper.createObjectNode();
        symbolsNode.put("min", 0);
        symbolsNode.put("max", 16);
        symbolsNode.set("exclude", objectMapper.createArrayNode());
        ArrayNode includeSymbolsArray = objectMapper.createArrayNode();
        for (Character symbol : includedSymbols) {
            includeSymbolsArray.add(symbol.toString());
        }
        symbolsNode.set("include", includeSymbolsArray);
        jsonRules.set("symbols", symbolsNode);
        
        ObjectNode lettersNode = objectMapper.createObjectNode();
        lettersNode.set("exclude", objectMapper.createArrayNode());
        lettersNode.set("include", objectMapper.createArrayNode());
        jsonRules.set("letters", lettersNode);
        
        return passwordGenerator.generatePassword(jsonRules.toString());
    }

    public boolean containsAllRequiredDigits(String password, List<Integer> requiredDigits) {
        return requiredDigits.stream()
                .allMatch(digit -> password.contains(digit.toString()));
    }

    public boolean containsAllRequiredDigits(String password, String requiredDigitsString) {
        List<Integer> requiredDigits = parseIntegerList(requiredDigitsString);
        return containsAllRequiredDigits(password, requiredDigits);
    }

    public boolean containsAllRequiredSymbols(String password, List<Character> requiredSymbols) {
        return requiredSymbols.stream()
                .allMatch(symbol -> password.indexOf(symbol) >= 0);
    }

    public boolean containsAllRequiredSymbols(String password, String requiredSymbolsString) {
        List<Character> requiredSymbols = parseCharacterList(requiredSymbolsString);
        return containsAllRequiredSymbols(password, requiredSymbols);
    }

    public String generatePasswordWithIncludes(String includedDigitsString, String includedSymbolsString) throws Exception {
        List<Integer> includedDigits = parseIntegerList(includedDigitsString);
        List<Character> includedSymbols = parseCharacterList(includedSymbolsString);
        return generatePasswordWithIncludes(includedDigits, includedSymbols);
    }

    public String generatePasswordWithExcludes(String excludedDigitsString, String excludedSymbolsString) throws Exception {
        List<Integer> excludedDigits = parseIntegerList(excludedDigitsString);
        List<Character> excludedSymbols = parseCharacterList(excludedSymbolsString);
        return generatePasswordWithExcludes(excludedDigits, excludedSymbols);
    }

    public boolean doesNotContainExcludedDigits(String password, String excludedDigitsString) {
        List<Integer> excludedDigits = parseIntegerList(excludedDigitsString);
        return doesNotContainExcludedDigits(password, excludedDigits);
    }

    public boolean doesNotContainExcludedSymbols(String password, String excludedSymbolsString) {
        List<Character> excludedSymbols = parseCharacterList(excludedSymbolsString);
        return doesNotContainExcludedSymbols(password, excludedSymbols);
    }

    public String generatePasswordWithExcludes(List<Integer> excludedDigits, List<Character> excludedSymbols) throws Exception {
        if (passwordGenerator == null) {
            setUp();
        }
        
        ObjectNode jsonRules = objectMapper.createObjectNode();
        
        ObjectNode lengthNode = objectMapper.createObjectNode();
        lengthNode.put("min", 10);
        lengthNode.put("max", 14);
        jsonRules.set("length", lengthNode);
        
        ObjectNode digitsNode = objectMapper.createObjectNode();
        digitsNode.put("min", 0);
        digitsNode.put("max", 14);
        ArrayNode excludeDigitsArray = objectMapper.createArrayNode();
        for (Integer digit : excludedDigits) {
            excludeDigitsArray.add(digit);
        }
        digitsNode.set("exclude", excludeDigitsArray);
        digitsNode.set("include", objectMapper.createArrayNode());
        jsonRules.set("digits", digitsNode);
        
        ObjectNode symbolsNode = objectMapper.createObjectNode();
        symbolsNode.put("min", 0);
        symbolsNode.put("max", 14);
        ArrayNode excludeSymbolsArray = objectMapper.createArrayNode();
        for (Character symbol : excludedSymbols) {
            excludeSymbolsArray.add(symbol.toString());
        }
        symbolsNode.set("exclude", excludeSymbolsArray);
        symbolsNode.set("include", objectMapper.createArrayNode());
        jsonRules.set("symbols", symbolsNode);
        
        ObjectNode lettersNode = objectMapper.createObjectNode();
        lettersNode.set("exclude", objectMapper.createArrayNode());
        lettersNode.set("include", objectMapper.createArrayNode());
        jsonRules.set("letters", lettersNode);
        
        return passwordGenerator.generatePassword(jsonRules.toString());
    }

    public boolean doesNotContainExcludedDigits(String password, List<Integer> excludedDigits) {
        return excludedDigits.stream()
                .noneMatch(digit -> password.contains(digit.toString()));
    }

    public boolean doesNotContainExcludedSymbols(String password, List<Character> excludedSymbols) {
        return excludedSymbols.stream()
                .noneMatch(symbol -> password.indexOf(symbol) >= 0);
    }

    public String generatePasswordWithLength(int minLength, int maxLength) {
        if (passwordGenerator == null) {
            setUp();
        }
        
        ObjectNode jsonRules = objectMapper.createObjectNode();
        
        ObjectNode lengthNode = objectMapper.createObjectNode();
        lengthNode.put("min", minLength);
        lengthNode.put("max", maxLength);
        jsonRules.set("length", lengthNode);
        
        ObjectNode digitsNode = objectMapper.createObjectNode();
        digitsNode.put("min", 0);
        digitsNode.put("max", maxLength);
        digitsNode.set("exclude", objectMapper.createArrayNode());
        digitsNode.set("include", objectMapper.createArrayNode());
        jsonRules.set("digits", digitsNode);
        
        ObjectNode symbolsNode = objectMapper.createObjectNode();
        symbolsNode.put("min", 0);
        symbolsNode.put("max", maxLength);
        symbolsNode.set("exclude", objectMapper.createArrayNode());
        symbolsNode.set("include", objectMapper.createArrayNode());
        jsonRules.set("symbols", symbolsNode);
        
        ObjectNode lettersNode = objectMapper.createObjectNode();
        lettersNode.set("exclude", objectMapper.createArrayNode());
        lettersNode.set("include", objectMapper.createArrayNode());
        jsonRules.set("letters", lettersNode);
        
        try {
            return passwordGenerator.generatePassword(jsonRules.toString());
        } catch (Exception e) {
            return null;
        }
    }

    public int getPasswordLength(String password) {
        return password != null ? password.length() : 0;
    }

    public String tryGenerateWithNullRules() {
        if (passwordGenerator == null) {
            setUp();
        }
        
        try {
            passwordGenerator.generatePassword(null);
            return "No exception thrown";
        } catch (SecurityException e) {
            return "SecurityException thrown as expected";
        } catch (Exception e) {
            return "Unexpected exception: " + e.getClass().getSimpleName();
        }
    }

    public String tryGenerateWithImpossibleRules() {
        if (passwordGenerator == null) {
            setUp();
        }
        
        ObjectNode jsonRules = objectMapper.createObjectNode();
        
        ObjectNode lengthNode = objectMapper.createObjectNode();
        lengthNode.put("min", 100);
        lengthNode.put("max", 1);
        jsonRules.set("length", lengthNode);
        
        ObjectNode digitsNode = objectMapper.createObjectNode();
        digitsNode.put("min", 50);
        digitsNode.put("max", 100);
        digitsNode.set("exclude", objectMapper.createArrayNode());
        digitsNode.set("include", objectMapper.createArrayNode());
        jsonRules.set("digits", digitsNode);
        
        ObjectNode symbolsNode = objectMapper.createObjectNode();
        symbolsNode.put("min", 50);
        symbolsNode.put("max", 100);
        symbolsNode.set("exclude", objectMapper.createArrayNode());
        symbolsNode.set("include", objectMapper.createArrayNode());
        jsonRules.set("symbols", symbolsNode);
        
        ObjectNode lettersNode = objectMapper.createObjectNode();
        lettersNode.set("exclude", objectMapper.createArrayNode());
        lettersNode.set("include", objectMapper.createArrayNode());
        jsonRules.set("letters", lettersNode);
        
        try {
            passwordGenerator.generatePassword(jsonRules.toString());
            return "No exception thrown";
        } catch (SecurityException e) {
            return "SecurityException thrown as expected";
        } catch (Exception e) {
            return "Unexpected exception: " + e.getClass().getSimpleName();
        }
    }

    public boolean throwsExceptionWithNullRules() {
        if (passwordGenerator == null) {
            setUp();
        }
        
        try {
            passwordGenerator.generatePassword(null);
            return false;
        } catch (SecurityException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean throwsExceptionWithImpossibleRules() {
        String result = tryGenerateWithImpossibleRules();
        return result.contains("SecurityException thrown as expected");
    }

    public int countUniquePasswords(int numPasswords) {
        if (passwordGenerator == null) {
            setUp();
        }
        
        ObjectNode jsonRules = objectMapper.createObjectNode();
        
        ObjectNode lengthNode = objectMapper.createObjectNode();
        lengthNode.put("min", 12);
        lengthNode.put("max", 16);
        jsonRules.set("length", lengthNode);
        
        ObjectNode digitsNode = objectMapper.createObjectNode();
        digitsNode.put("min", 2);
        digitsNode.put("max", 16);
        digitsNode.set("exclude", objectMapper.createArrayNode());
        digitsNode.set("include", objectMapper.createArrayNode());
        jsonRules.set("digits", digitsNode);
        
        ObjectNode symbolsNode = objectMapper.createObjectNode();
        symbolsNode.put("min", 1);
        symbolsNode.put("max", 16);
        symbolsNode.set("exclude", objectMapper.createArrayNode());
        symbolsNode.set("include", objectMapper.createArrayNode());
        jsonRules.set("symbols", symbolsNode);
        
        ObjectNode lettersNode = objectMapper.createObjectNode();
        lettersNode.set("exclude", objectMapper.createArrayNode());
        lettersNode.set("include", objectMapper.createArrayNode());
        jsonRules.set("letters", lettersNode);
        
        Set<String> uniquePasswords = new HashSet<>();
        
        try {
            for (int i = 0; i < numPasswords; i++) {
                String password = passwordGenerator.generatePassword(jsonRules.toString());
                if (password != null) {
                    uniquePasswords.add(password);
                }
            }
        } catch (Exception e) {
            return 0;
        }
        
        return uniquePasswords.size();
    }

    private List<Integer> parseIntegerList(String listString) {
        if (listString == null || listString.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        listString = listString.trim();
        if (listString.startsWith("[") && listString.endsWith("]")) {
            listString = listString.substring(1, listString.length() - 1);
        }
        
        if (listString.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Integer> result = new ArrayList<>();
        String[] items = listString.split(",");
        
        for (String item : items) {
            item = item.trim();
            if (!item.isEmpty()) {
                try {
                    result.add(Integer.parseInt(item));
                } catch (NumberFormatException e) {
                    // Skip invalid numbers
                }
            }
        }
        
        return result;
    }

    private List<Character> parseCharacterList(String listString) {
        if (listString == null || listString.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        listString = listString.trim();
        if (listString.startsWith("[") && listString.endsWith("]")) {
            listString = listString.substring(1, listString.length() - 1);
        }
        
        if (listString.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Character> result = new ArrayList<>();
        String[] items = listString.split(",");
        
        for (String item : items) {
            item = item.trim();
            if (item.startsWith("\"") && item.endsWith("\"") && item.length() > 2) {
                item = item.substring(1, item.length() - 1);
            }
            if (!item.isEmpty()) {
                result.add(item.charAt(0));
            }
        }
        
        return result;
    }
}
