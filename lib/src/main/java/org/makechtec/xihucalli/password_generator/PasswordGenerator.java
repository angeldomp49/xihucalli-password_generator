package org.makechtec.xihucalli.password_generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PasswordGenerator {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final List<Character> DIGITS = Stream.of(
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9'
    ).toList();
    private static final List<Character> SYMBOLS = Stream.of(
            '!', '@', '#', '$', '%', '^', '&', '*', '(',
            ')', '_', '+', '=', '-', '[', ']', '{', '}', ';', '\'',
            ':', '\\', '"', ',', '.', '/', '<', '>', '?'
    ).toList();

    private static final List<Character> LETTERS = Stream.of(
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    ).toList();

    public String generatePassword(String jsonRules) {

        try {

            var passwordRules = hydrateRules(jsonRules);

            if (!areValidRules(passwordRules)) {
                throw new RuntimeException("Rules not valid");
            }

            boolean notValidPassword = true;
            List<Character> validPassword = new ArrayList<>();

            do {

                var possiblyPassword =
                        IntStream.range(0, Runtime.getRuntime().availableProcessors())
                                .mapToObj(index -> createPossiblePassword((int) passwordRules.getMaxLength(), passwordRules))
                                .filter(freshPassword -> isValidPassword(freshPassword, passwordRules))
                                .findFirst();

                if (possiblyPassword.isEmpty()) {
                    continue;
                }

                notValidPassword = false;
                validPassword = possiblyPassword.get();

            } while (notValidPassword);


            return validPassword.stream()
                    .map(character -> character + "")
                    .reduce("", (prev, next) -> prev + next);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private List<Character> createPossiblePassword(int maxLength, PasswordRulesInformation passwordRulesInformation) {

        var random = new SecureRandom();

        var password = new ArrayList<Character>();

        var digits2 = passwordRulesInformation.getIncludedDigits().stream().map(digit -> ("" + digit).charAt(0)).toList();
        var range = new ArrayList<>(List.copyOf(digits2));
        range.addAll(passwordRulesInformation.getIncludedSymbols());
        range.addAll(passwordRulesInformation.getIncludedLetters());

        if (range.isEmpty()) {
            range.addAll(DIGITS);
            range.addAll(LETTERS);
            range.addAll(SYMBOLS);
        }

        for (int i = 0; i < maxLength; i++) {
            var randomIndex = random.nextInt(range.size());
            var randomChar = range.get(randomIndex);

            password.add(randomChar);
        }

        return password;
    }

    private boolean isValidPassword(List<Character> password, PasswordRulesInformation passwordRulesInformation) {

        var lengthValidation =
                (password.size() >= passwordRulesInformation.getMinLength())
                        && (password.size() <= passwordRulesInformation.getMaxLength());


        return lengthValidation
                && areDigitsValid(password, passwordRulesInformation)
                && areSymbolsValid(password, passwordRulesInformation)
                && areLettersValid(password, passwordRulesInformation);

    }

    private boolean areDigitsValid(List<Character> password, PasswordRulesInformation passwordRulesInformation) {
        var digitsCount = password.stream().filter(DIGITS::contains).count();

        var allDigitsValidity = false;

        if (passwordRulesInformation.getExcludedDigits().isEmpty() && passwordRulesInformation.getIncludedDigits().isEmpty()) {
            allDigitsValidity = true;
        } else if (passwordRulesInformation.getIncludedDigits().isEmpty()) {
            allDigitsValidity = password.stream()
                    .noneMatch(
                            character ->
                                    passwordRulesInformation.getExcludedDigits()
                                            .stream()
                                            .map(d -> "" + d)
                                            .anyMatch(("" + character)::equals)
                    );

        } else if (passwordRulesInformation.getExcludedDigits().isEmpty()) {
            allDigitsValidity = password.stream()
                    .anyMatch(
                            character ->
                                    passwordRulesInformation.getIncludedDigits()
                                            .stream()
                                            .map(d -> "" + d)
                                            .anyMatch(("" + character)::equals)
                    );

        } else {
            allDigitsValidity = password.stream()
                    .anyMatch(
                            character ->
                                    passwordRulesInformation.getIncludedDigits()
                                            .stream()
                                            .map(d -> "" + d)
                                            .anyMatch(("" + character)::equals)
                    );
        }

        return
                (digitsCount >= passwordRulesInformation.getMinNumberOfDigits())
                        && (digitsCount <= passwordRulesInformation.getMaxNumberOfDigits())
                        && allDigitsValidity;
    }

    private boolean areSymbolsValid(List<Character> password, PasswordRulesInformation passwordRulesInformation) {
        var symbolsCount = password.stream().filter(SYMBOLS::contains).count();

        var allSymbolsValidity = false;

        if (passwordRulesInformation.getExcludedSymbols().isEmpty() && passwordRulesInformation.getIncludedSymbols().isEmpty()) {
            allSymbolsValidity = true;
        } else if (passwordRulesInformation.getIncludedSymbols().isEmpty()) {
            allSymbolsValidity = password.stream()
                    .noneMatch(
                            character ->
                                    passwordRulesInformation.getExcludedSymbols()
                                            .stream()
                                            .anyMatch(d -> character == d)
                    );

        } else if (passwordRulesInformation.getExcludedSymbols().isEmpty()) {
            allSymbolsValidity = password.stream()
                    .anyMatch(
                            character ->
                                    passwordRulesInformation.getIncludedSymbols()
                                            .stream()
                                            .anyMatch(d -> character == d)
                    );

        } else {
            allSymbolsValidity = password.stream()
                    .anyMatch(
                            character ->
                                    passwordRulesInformation.getIncludedSymbols()
                                            .stream()
                                            .anyMatch(d -> character == d)
                    );
        }

        return
                (symbolsCount >= passwordRulesInformation.getMinNumberOfSymbols())
                        && (symbolsCount <= passwordRulesInformation.getMaxNumberOfSymbols())
                        && allSymbolsValidity;
    }

    private boolean areLettersValid(List<Character> password, PasswordRulesInformation passwordRulesInformation) {

        var allLettersValidity = false;

        if (passwordRulesInformation.getExcludedLetters().isEmpty() && passwordRulesInformation.getIncludedLetters().isEmpty()) {
            allLettersValidity = true;
        } else if (passwordRulesInformation.getIncludedLetters().isEmpty()) {
            allLettersValidity = password.stream()
                    .noneMatch(
                            character ->
                                    passwordRulesInformation.getExcludedLetters()
                                            .stream()
                                            .anyMatch(d -> character == d)
                    );

        } else if (passwordRulesInformation.getExcludedLetters().isEmpty()) {
            allLettersValidity = password.stream()
                    .anyMatch(
                            character ->
                                    passwordRulesInformation.getIncludedLetters()
                                            .stream()
                                            .anyMatch(d -> character == d)
                    );

        } else {
            allLettersValidity = password.stream()
                    .anyMatch(
                            character ->
                                    passwordRulesInformation.getIncludedLetters()
                                            .stream()
                                            .anyMatch(d -> character == d)
                    );
        }

        return allLettersValidity;
    }


    private boolean areValidRules(PasswordRulesInformation rulesInformation) {

        return rulesInformation.getMinLength() <= rulesInformation.getMaxLength()
                && rulesInformation.getMinNumberOfDigits() <= rulesInformation.getMaxNumberOfDigits()
                && rulesInformation.getMinNumberOfSymbols() <= rulesInformation.getMaxNumberOfSymbols();

    }

    private PasswordRulesInformation hydrateRules(String jsonRules) throws JsonProcessingException {
        var json = OBJECT_MAPPER.readTree(jsonRules);
        var passwordRulesInformation = new PasswordRulesInformation();

        if (json.get("length").hasNonNull("min")) {
            passwordRulesInformation.setMinLength(json.get("length").get("min").asLong());
        }

        if (json.get("length").hasNonNull("max")) {
            passwordRulesInformation.setMaxLength(json.get("length").get("max").asLong());
        }

        if (json.get("digits").hasNonNull("min")) {
            passwordRulesInformation.setMinNumberOfDigits(json.get("digits").get("min").asLong());
        }

        if (json.get("digits").hasNonNull("max")) {
            passwordRulesInformation.setMaxNumberOfDigits(json.get("digits").get("max").asLong());
        }


        if (json.get("symbols").hasNonNull("min")) {
            passwordRulesInformation.setMinNumberOfSymbols(json.get("symbols").get("min").asLong());
        }

        if (json.get("symbols").hasNonNull("max")) {
            passwordRulesInformation.setMaxNumberOfSymbols(json.get("symbols").get("max").asLong());
        }


        if (json.get("digits").hasNonNull("exclude")) {
            for (var excluded : json.get("digits").get("exclude")) {
                passwordRulesInformation.getExcludedDigits().add(excluded.asInt());
            }
        }

        if (json.get("digits").hasNonNull("include")) {
            for (var included : json.get("digits").get("include")) {
                passwordRulesInformation.getIncludedDigits().add(included.asInt());
            }
        }

        if (json.get("symbols").hasNonNull("exclude")) {
            for (var excluded : json.get("symbols").get("exclude")) {
                passwordRulesInformation.getExcludedSymbols().add(excluded.asText().charAt(0));
            }
        }

        if (json.get("symbols").hasNonNull("include")) {
            for (var included : json.get("symbols").get("include")) {
                passwordRulesInformation.getIncludedSymbols().add(included.asText().charAt(0));
            }
        }

        if (json.get("letters").hasNonNull("exclude")) {
            for (var excluded : json.get("letters").get("exclude")) {
                passwordRulesInformation.getExcludedLetters().add(excluded.asText().charAt(0));
            }
        }

        if (json.get("letters").hasNonNull("include")) {
            for (var included : json.get("letters").get("include")) {
                passwordRulesInformation.getIncludedLetters().add(included.asText().charAt(0));
            }
        }

        return passwordRulesInformation;
    }

}
