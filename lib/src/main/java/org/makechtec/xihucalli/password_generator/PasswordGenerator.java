package org.makechtec.xihucalli.password_generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class PasswordGenerator {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final int MAX_GENERATION_ATTEMPTS = 1000;
    private static final int MIN_AVAILABLE_CHARACTERS = 4;

    private final List<Character> digitsList;
    private final List<Character> symbolsList;
    private final List<Character> lettersList;
    private final SecureRandom secureRandom;

    public PasswordGenerator(String digitsList, String symbolsList, String lettersList) {
        validateConstructorInputs(digitsList, symbolsList, lettersList);
        this.digitsList = List.copyOf(fromString(digitsList));
        this.symbolsList = List.copyOf(fromString(symbolsList));
        this.lettersList = List.copyOf(fromString(lettersList));
        this.secureRandom = new SecureRandom();
    }

    public String generatePassword(String jsonRules) throws SecurityException {
        validateJsonInput(jsonRules);

        try {
            var passwordRules = hydrateRules(jsonRules);
            validatePasswordRules(passwordRules);

            for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
                var candidatePassword = createSecurePassword(passwordRules);
                if (isValidPassword(candidatePassword, passwordRules)) {
                    return convertPasswordToString(candidatePassword);
                }
            }

            throw new SecurityException("Unable to generate password meeting security requirements after maximum attempts");

        } catch (JsonProcessingException e) {
            throw new SecurityException("Invalid password rules format", e);
        }
    }

    private void validateConstructorInputs(String digits, String symbols, String letters) {
        if (digits == null || symbols == null || letters == null) {
            throw new SecurityException("Character sets cannot be null");
        }
        if (digits.isEmpty() && symbols.isEmpty() && letters.isEmpty()) {
            throw new SecurityException("At least one character set must be non-empty");
        }
    }

    private void validateJsonInput(String jsonRules) {
        if (jsonRules == null || jsonRules.trim().isEmpty()) {
            throw new SecurityException("Password rules cannot be null or empty");
        }
    }

    private void validatePasswordRules(PasswordRulesInformation rules) throws SecurityException {
        if (!areValidRules(rules)) {
            throw new SecurityException("Invalid password rules configuration");
        }

        if (!areRulesFeasible(rules)) {
            throw new SecurityException("Password rules are mathematically impossible to satisfy");
        }
    }

    private boolean areRulesFeasible(PasswordRulesInformation rules) {
        long minRequiredLength = rules.getMinNumberOfDigits() + rules.getMinNumberOfSymbols();
        if (minRequiredLength > rules.getMaxLength()) {
            return false;
        }

        var availableDigits = getAvailableCharacters(digitsList, rules.getExcludedDigits(), rules.getIncludedDigits());
        var availableSymbols = getAvailableCharacters(symbolsList, rules.getExcludedSymbols(), rules.getIncludedSymbols());
        var availableLetters = getAvailableCharacters(lettersList, rules.getExcludedLetters(), rules.getIncludedLetters());

        if (rules.getMinNumberOfDigits() > 0 && availableDigits.isEmpty()) {
            return false;
        }
        if (rules.getMinNumberOfSymbols() > 0 && availableSymbols.isEmpty()) {
            return false;
        }
        if (!rules.getIncludedLetters().isEmpty() && availableLetters.isEmpty()) {
            return false;
        }

        return (availableDigits.size() + availableSymbols.size() + availableLetters.size()) >= MIN_AVAILABLE_CHARACTERS;
    }

    private List<Character> createSecurePassword(PasswordRulesInformation rules) {
        int targetLength = generateSecureLength(rules.getMinLength(), rules.getMaxLength());
        List<Character> password = new ArrayList<>(targetLength);

        addMandatoryIncludedCharacters(password, rules);
        addMinimumRequiredCharacters(password, rules, targetLength);
        fillRemainingPositions(password, targetLength, rules);
        shufflePasswordSecurely(password);

        return password;
    }

    private void addMandatoryIncludedCharacters(List<Character> password, PasswordRulesInformation rules) {
        rules.getIncludedDigits().forEach(digit -> password.add(Character.forDigit(digit, 10)));
        password.addAll(rules.getIncludedSymbols());
        password.addAll(rules.getIncludedLetters());
    }

    private void addMinimumRequiredCharacters(List<Character> password, PasswordRulesInformation rules, int targetLength) {
        int currentDigits = countDigitsInPassword(password);
        int currentSymbols = countSymbolsInPassword(password);

        var availableDigits = getAvailableCharacters(digitsList, rules.getExcludedDigits(), rules.getIncludedDigits());
        var availableSymbols = getAvailableCharacters(symbolsList, rules.getExcludedSymbols(), rules.getIncludedSymbols());

        while (currentDigits < rules.getMinNumberOfDigits() && password.size() < targetLength && !availableDigits.isEmpty()) {
            password.add(availableDigits.get(secureRandom.nextInt(availableDigits.size())));
            currentDigits++;
        }

        while (currentSymbols < rules.getMinNumberOfSymbols() && password.size() < targetLength && !availableSymbols.isEmpty()) {
            password.add(availableSymbols.get(secureRandom.nextInt(availableSymbols.size())));
            currentSymbols++;
        }
    }

    private int countDigitsInPassword(List<Character> password) {
        return (int) password.stream().filter(digitsList::contains).count();
    }

    private int countSymbolsInPassword(List<Character> password) {
        return (int) password.stream().filter(symbolsList::contains).count();
    }

    private int generateSecureLength(long minLength, long maxLength) {
        if (minLength == maxLength) {
            return (int) minLength;
        }
        return ThreadLocalRandom.current().nextInt((int) minLength, (int) maxLength + 1);
    }

    private void fillRemainingPositions(List<Character> password, int targetLength,
                                        PasswordRulesInformation rules) {
        var allAvailableCharacters = createCombinedCharacterPool(rules);

        while (password.size() < targetLength && !allAvailableCharacters.isEmpty()) {
            password.add(allAvailableCharacters.get(secureRandom.nextInt(allAvailableCharacters.size())));
        }
    }

    private List<Character> createCombinedCharacterPool(PasswordRulesInformation rules) {
        List<Character> combined = new ArrayList<>();
        combined.addAll(getAvailableCharacters(digitsList, rules.getExcludedDigits(), rules.getIncludedDigits()));
        combined.addAll(getAvailableCharacters(symbolsList, rules.getExcludedSymbols(), rules.getIncludedSymbols()));
        combined.addAll(getAvailableCharacters(lettersList, rules.getExcludedLetters(), rules.getIncludedLetters()));
        return combined;
    }

    private <T> List<Character> getAvailableCharacters(List<Character> sourceList,
                                                     List<T> excluded, List<T> included) {
        if (!included.isEmpty()) {
            return included.stream()
                    .map(Object::toString)
                    .map(s -> s.charAt(0))
                    .filter(sourceList::contains)
                    .toList();
        }

        return sourceList.stream()
                .filter(ch -> !excluded.contains(getComparableValue(ch, excluded)))
                .toList();
    }

    private <T> T getComparableValue(Character ch, List<T> excludedList) {
        if (excludedList.isEmpty()) {
            return null;
        }

        T firstItem = excludedList.get(0);
        if (firstItem instanceof Integer) {
            return (T) Integer.valueOf(Character.getNumericValue(ch));
        }
        return (T) ch;
    }

    private void shufflePasswordSecurely(List<Character> password) {
        for (int i = password.size() - 1; i > 0; i--) {
            int randomIndex = secureRandom.nextInt(i + 1);
            Collections.swap(password, i, randomIndex);
        }
    }

    private String convertPasswordToString(List<Character> password) {
        return password.stream()
                .map(String::valueOf)
                .reduce("", String::concat);
    }

    private boolean isValidPassword(List<Character> password, PasswordRulesInformation rules) {
        return isValidLength(password, rules) &&
                hasValidDigitCount(password, rules) &&
                hasValidSymbolCount(password, rules) &&
                hasValidLetterRequirements(password, rules);
    }

    private boolean isValidLength(List<Character> password, PasswordRulesInformation rules) {
        return password.size() >= rules.getMinLength() && password.size() <= rules.getMaxLength();
    }

    private boolean hasValidDigitCount(List<Character> password, PasswordRulesInformation rules) {
        long digitCount = password.stream().filter(digitsList::contains).count();
        return digitCount >= rules.getMinNumberOfDigits() &&
                digitCount <= rules.getMaxNumberOfDigits() &&
                hasValidDigitRequirements(password, rules);
    }

    private boolean hasValidSymbolCount(List<Character> password, PasswordRulesInformation rules) {
        long symbolCount = password.stream().filter(symbolsList::contains).count();
        return symbolCount >= rules.getMinNumberOfSymbols() &&
                symbolCount <= rules.getMaxNumberOfSymbols() &&
                hasValidSymbolRequirements(password, rules);
    }

    private boolean hasValidDigitRequirements(List<Character> password, PasswordRulesInformation rules) {
        if (!rules.getExcludedDigits().isEmpty()) {
            boolean hasExcludedDigits = password.stream()
                    .filter(digitsList::contains)
                    .map(Character::getNumericValue)
                    .anyMatch(rules.getExcludedDigits()::contains);
            if (hasExcludedDigits) {
                return false;
            }
        }

        if (!rules.getIncludedDigits().isEmpty()) {
            return rules.getIncludedDigits().stream()
                    .allMatch(digit -> password.contains(Character.forDigit(digit, 10)));
        }

        return true;
    }

    private boolean hasValidSymbolRequirements(List<Character> password, PasswordRulesInformation rules) {
        if (!rules.getExcludedSymbols().isEmpty()) {
            boolean hasExcludedSymbols = password.stream()
                    .filter(symbolsList::contains)
                    .anyMatch(rules.getExcludedSymbols()::contains);
            if (hasExcludedSymbols) {
                return false;
            }
        }

        if (!rules.getIncludedSymbols().isEmpty()) {
            return rules.getIncludedSymbols().stream()
                    .allMatch(password::contains);
        }

        return true;
    }

    private boolean hasValidLetterRequirements(List<Character> password, PasswordRulesInformation rules) {
        if (!rules.getExcludedLetters().isEmpty()) {
            boolean hasExcludedLetters = password.stream()
                    .filter(lettersList::contains)
                    .anyMatch(rules.getExcludedLetters()::contains);
            if (hasExcludedLetters) {
                return false;
            }
        }

        if (!rules.getIncludedLetters().isEmpty()) {
            return rules.getIncludedLetters().stream()
                    .allMatch(password::contains);
        }

        return true;
    }

    private boolean areValidRules(PasswordRulesInformation rules) {
        return rules.getMinLength() <= rules.getMaxLength() &&
                rules.getMinLength() > 0 &&
                rules.getMaxLength() > 0 &&
                rules.getMinNumberOfDigits() <= rules.getMaxNumberOfDigits() &&
                rules.getMinNumberOfSymbols() <= rules.getMaxNumberOfSymbols() &&
                rules.getMinNumberOfDigits() >= 0 &&
                rules.getMaxNumberOfDigits() >= 0 &&
                rules.getMinNumberOfSymbols() >= 0 &&
                rules.getMaxNumberOfSymbols() >= 0;
    }

    private PasswordRulesInformation hydrateRules(String jsonRules) throws JsonProcessingException {
        var json = OBJECT_MAPPER.readTree(jsonRules);
        var passwordRules = new PasswordRulesInformation();

        if (json.has("length") && json.get("length").hasNonNull("min")) {
            passwordRules.setMinLength(json.get("length").get("min").asLong());
        }

        if (json.has("length") && json.get("length").hasNonNull("max")) {
            passwordRules.setMaxLength(json.get("length").get("max").asLong());
        }

        if (json.has("digits") && json.get("digits").hasNonNull("min")) {
            passwordRules.setMinNumberOfDigits(json.get("digits").get("min").asLong());
        }

        if (json.has("digits") && json.get("digits").hasNonNull("max")) {
            passwordRules.setMaxNumberOfDigits(json.get("digits").get("max").asLong());
        }

        if (json.has("symbols") && json.get("symbols").hasNonNull("min")) {
            passwordRules.setMinNumberOfSymbols(json.get("symbols").get("min").asLong());
        }

        if (json.has("symbols") && json.get("symbols").hasNonNull("max")) {
            passwordRules.setMaxNumberOfSymbols(json.get("symbols").get("max").asLong());
        }

        if (json.has("digits") && json.get("digits").hasNonNull("exclude")) {
            for (var excluded : json.get("digits").get("exclude")) {
                passwordRules.getExcludedDigits().add(excluded.asInt());
            }
        }

        if (json.has("digits") && json.get("digits").hasNonNull("include")) {
            for (var included : json.get("digits").get("include")) {
                passwordRules.getIncludedDigits().add(included.asInt());
            }
        }

        if (json.has("symbols") && json.get("symbols").hasNonNull("exclude")) {
            for (var excluded : json.get("symbols").get("exclude")) {
                passwordRules.getExcludedSymbols().add(excluded.asText().charAt(0));
            }
        }

        if (json.has("symbols") && json.get("symbols").hasNonNull("include")) {
            for (var included : json.get("symbols").get("include")) {
                passwordRules.getIncludedSymbols().add(included.asText().charAt(0));
            }
        }

        if (json.has("letters") && json.get("letters").hasNonNull("exclude")) {
            for (var excluded : json.get("letters").get("exclude")) {
                passwordRules.getExcludedLetters().add(excluded.asText().charAt(0));
            }
        }

        if (json.has("letters") && json.get("letters").hasNonNull("include")) {
            for (var included : json.get("letters").get("include")) {
                passwordRules.getIncludedLetters().add(included.asText().charAt(0));
            }
        }

        return passwordRules;
    }

    private List<Character> fromString(String str) {
        Objects.requireNonNull(str, "Character set string cannot be null");
        return str.chars()
                .mapToObj(c -> (char) c)
                .toList();
    }
}
