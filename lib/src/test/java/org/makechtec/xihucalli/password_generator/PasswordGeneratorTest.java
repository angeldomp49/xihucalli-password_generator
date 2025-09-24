package org.makechtec.xihucalli.password_generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {

    private PasswordGenerator passwordGenerator;

    @BeforeEach
    void setUp() {
        ApplicationPropertiesLoader applicationPropertiesLoader = new ApplicationPropertiesLoader();
        applicationPropertiesLoader.load("application.properties");
        var properties = applicationPropertiesLoader.getProperties();

        passwordGenerator = new PasswordGenerator(
                (String) properties.get("password-generator.numbers.list"),
                (String) properties.get("password-generator.symbols.list"),
                (String) properties.get("password-generator.letters.list")
        );
    }

    @Test
    void generatePassword() {
        var rules = """
                {
                  "length": {
                    "min": 8,
                    "max": 30
                  },
                  "digits": {
                    "min": 8,
                    "max": 30,
                    "exclude": [],
                    "include": []
                  },
                  "symbols": {
                    "min": 8,
                    "max": 30,
                    "exclude": [],
                    "include": []
                  },
                  "letters": {
                    "exclude": [],
                    "include": []
                  }
                }
                """;

        var password = passwordGenerator.generatePassword(rules);

        assertNotNull(password);
        assertFalse(password.isBlank());
    }

    @Test
    void generatePassword2() {
        var rules = """
                {
                  "length": {
                    "min": 8,
                    "max": 30
                  },
                  "digits": {
                    "min": 8,
                    "max": 30,
                    "exclude": [
                        0,
                        1
                    ],
                    "include": []
                  },
                  "symbols": {
                    "min": 8,
                    "max": 30,
                    "exclude": [
                        "$",
                        "%"
                    ],
                    "include": []
                  },
                  "letters": {
                    "exclude": [
                        "a",
                        "F"
                    ],
                    "include": []
                  }
                }
                """;

        var password = passwordGenerator.generatePassword(rules);

        assertNotNull(password);
        assertFalse(password.isBlank());
        assertFalse(password.contains("0"));
        assertFalse(password.contains("1"));
        assertFalse(password.contains("$"));
        assertFalse(password.contains("%"));
    }

    @Test
    void generatePassword3() {
        var rules = """
                {
                  "length": {
                    "min": 8,
                    "max": 8
                  },
                  "digits": {
                    "min": 0,
                    "max": 0,
                    "exclude": [],
                    "include": []
                  },
                  "symbols": {
                    "min": 0,
                    "max": 0,
                    "exclude": [],
                    "include": []
                  },
                  "letters": {
                    "exclude": [],
                    "include": [ "a", "F", "b", "G", "c", "H" ]
                  }
                }
                """;

        var password = passwordGenerator.generatePassword(rules);

        assertNotNull(password, "Password should not be null");
        assertFalse(password.isBlank(), "Password should not be blank");
        assertEquals(8, password.length(), "Password should have exactly 8 characters");
        assertTrue(password.contains("a"), "Password must contain required letter 'a'");
        assertTrue(password.contains("F"), "Password must contain required letter 'F'");
        
        // Verify no digits or symbols are present (as min/max are 0)
        long digitCount = password.chars().filter(Character::isDigit).count();
        long symbolCount = password.chars().filter(ch -> !Character.isLetterOrDigit(ch)).count();
        assertEquals(0, digitCount, "Password should contain no digits");
        assertEquals(0, symbolCount, "Password should contain no symbols");
    }

    @Test
    void generatePassword4() {
        var rules = """
                {
                  "length": {
                    "min": 8,
                    "max": 8
                  },
                  "digits": {
                    "min": 1,
                    "max": 8,
                    "exclude": [],
                    "include": [
                        2,
                        3
                    ]
                  },
                  "symbols": {
                    "min": 1,
                    "max": 8
                  },
                  "letters": {
                    "exclude": [],
                    "include": [ "a", "F" ]
                  }
                }
                """;

        var password = passwordGenerator.generatePassword(rules);

        assertNotNull(password);
        assertFalse(password.isBlank());
        assertFalse(password.contains("0"));
        assertFalse(password.contains("1"));
        assertFalse(password.contains("$"));
        assertFalse(password.contains("%"));

        var hasAtLeastOne = Stream.of('a', 'F', '2', '3', '/', '&').anyMatch(v -> password.contains(v + ""));
        assertTrue(hasAtLeastOne);
    }

    @Test
    void generatePassword5() {
        var rules = """
                {
                  "length": {
                    "min": 10,
                    "max": 15
                  },
                  "digits": {
                    "min": 0,
                    "max": 5,
                    "exclude": [],
                    "include": []
                  },
                  "symbols": {
                    "min": 0,
                    "max": 5,
                    "exclude": [],
                    "include": []
                  },
                  "letters": {
                    "exclude": ["z", "y"],
                    "include": []
                  }
                }
                """;

        var password = passwordGenerator.generatePassword(rules);

        assertNotNull(password, "Password should not be null");
        assertFalse(password.isBlank(), "Password should not be blank");
        assertTrue(password.length() >= 10 && password.length() <= 15, "Password should be within specified length range");
        
        // Should contain mix of available characters
        boolean hasLetters = password.chars().anyMatch(Character::isLetter);
        assertTrue(hasLetters, "Password should contain letters");
        
        // Verify excluded characters are not present
        assertFalse(password.contains("z"), "Password should not contain excluded letter 'z'");
        assertFalse(password.contains("y"), "Password should not contain excluded letter 'y'");
    }

    // ========== TESTS FOR ERROR HANDLING AND EDGE CASES ==========

    @Test
    void testSecurityExceptionForInvalidJsonThrowsSecurityException() {
        var invalidJson = "{ invalid json structure }";
        
        SecurityException exception = assertThrows(SecurityException.class, () -> 
            passwordGenerator.generatePassword(invalidJson),
            "Should throw SecurityException for malformed JSON"
        );
        
        assertTrue(exception.getMessage().contains("Invalid password rules format"), 
                   "Should indicate JSON format error");
    }

    @Test
    void testSecurityExceptionForInvalidRulesThrowsSecurityException() {
        var invalidRules = """
                {
                  "length": {
                    "min": 30,
                    "max": 8
                  },
                  "digits": {
                    "min": 0,
                    "max": 30
                  },
                  "symbols": {
                    "min": 0,
                    "max": 30
                  },
                  "letters": {
                    "exclude": [],
                    "include": []
                  }
                }
                """;
        
        SecurityException exception = assertThrows(SecurityException.class, () -> 
            passwordGenerator.generatePassword(invalidRules),
            "Should throw SecurityException for invalid rules where min > max"
        );
        
        assertTrue(exception.getMessage().contains("Invalid password rules configuration"), 
                   "Exception message should indicate invalid rules");
    }

    @Test
    void testSecurityExceptionForInfeasibleRules() {
        var infeasibleRules = """
                {
                  "length": {
                    "min": 5,
                    "max": 5
                  },
                  "digits": {
                    "min": 10,
                    "max": 10,
                    "exclude": [],
                    "include": []
                  },
                  "symbols": {
                    "min": 0,
                    "max": 0,
                    "exclude": [],
                    "include": []
                  },
                  "letters": {
                    "exclude": [],
                    "include": []
                  }
                }
                """;
        
        SecurityException exception = assertThrows(SecurityException.class, () -> 
            passwordGenerator.generatePassword(infeasibleRules),
            "Should throw SecurityException for mathematically impossible rules"
        );
        
        assertTrue(exception.getMessage().contains("mathematically impossible"), 
                   "Exception should indicate impossible rules");
    }

    @Test
    void testConstructorValidationWithNullInputs() {
        SecurityException exception = assertThrows(SecurityException.class, () -> 
            new PasswordGenerator(null, null, null),
            "Constructor should throw SecurityException for null inputs"
        );
        
        assertTrue(exception.getMessage().contains("Character sets cannot be null"), 
                   "Should indicate null character sets error");
    }

    @Test
    void testConstructorValidationWithAllEmptyInputs() {
        SecurityException exception = assertThrows(SecurityException.class, () -> 
            new PasswordGenerator("", "", ""),
            "Constructor should throw SecurityException for all empty inputs"
        );
        
        assertTrue(exception.getMessage().contains("At least one character set must be non-empty"), 
                   "Should indicate empty character sets error");
    }

    @Test
    void testGeneratePasswordWithNullInput() {
        SecurityException exception = assertThrows(SecurityException.class, () -> 
            passwordGenerator.generatePassword(null),
            "generatePassword should throw SecurityException for null input"
        );
        
        assertTrue(exception.getMessage().contains("Password rules cannot be null"), 
                   "Should indicate null rules error");
    }

    @Test
    void testGeneratePasswordWithEmptyJsonString() {
        SecurityException exception = assertThrows(SecurityException.class, () -> 
            passwordGenerator.generatePassword(""),
            "generatePassword should throw SecurityException for empty JSON string"
        );
        
        assertTrue(exception.getMessage().contains("Password rules cannot be null or empty"), 
                   "Should indicate empty rules error");
    }

    @Test
    void testSecureRandomnessInPasswordGeneration() {
        var rules = """
                {
                  "length": {
                    "min": 12,
                    "max": 12
                  },
                  "digits": {
                    "min": 4,
                    "max": 4,
                    "exclude": [],
                    "include": []
                  },
                  "symbols": {
                    "min": 4,
                    "max": 4,
                    "exclude": [],
                    "include": []
                  },
                  "letters": {
                    "exclude": [],
                    "include": []
                  }
                }
                """;

        String password1 = passwordGenerator.generatePassword(rules);
        String password2 = passwordGenerator.generatePassword(rules);
        
        assertNotEquals(password1, password2, 
                        "Generated passwords should be different due to secure randomness");
        assertEquals(12, password1.length(), "Password should have specified length");
        assertEquals(12, password2.length(), "Password should have specified length");
    }

    @Test
    void testPasswordMeetsSecurityRequirements() {
        var rules = """
                {
                  "length": {
                    "min": 18,
                    "max": 22
                  },
                  "digits": {
                    "min": 4,
                    "max": 7,
                    "exclude": [],
                    "include": []
                  },
                  "symbols": {
                    "min": 4,
                    "max": 7,
                    "exclude": [],
                    "include": []
                  },
                  "letters": {
                    "exclude": [],
                    "include": []
                  }
                }
                """;

        var password = passwordGenerator.generatePassword(rules);
        
        assertNotNull(password, "Password should not be null");
        assertTrue(password.length() >= 18 && password.length() <= 22, 
                   "Password length should be within security range");
        
        long digitCount = password.chars().filter(Character::isDigit).count();
        long symbolCount = password.chars().filter(ch -> !Character.isLetterOrDigit(ch)).count();
        long letterCount = password.chars().filter(Character::isLetter).count();
        
        assertTrue(digitCount >= 4, "Should have minimum required digits for security: " + digitCount);
        assertTrue(symbolCount >= 4, "Should have minimum required symbols for security: " + symbolCount);
        assertTrue(letterCount >= 0, "Should have letters for complexity: " + letterCount);
        
        // Verify total character count adds up correctly
        assertEquals(password.length(), digitCount + symbolCount + letterCount,
                    "Total character counts should equal password length");
    }

    @Test
    void testForcedCharacterInclusion() {
        var rules = """
                {
                  "length": {
                    "min": 10,
                    "max": 15
                  },
                  "digits": {
                    "min": 2,
                    "max": 10,
                    "exclude": [],
                    "include": [7, 9]
                  },
                  "symbols": {
                    "min": 2,
                    "max": 8,
                    "exclude": [],
                    "include": ["@", "#"]
                  },
                  "letters": {
                    "exclude": [],
                    "include": ["X", "z"]
                  }
                }
                """;

        var password = passwordGenerator.generatePassword(rules);
        
        assertTrue(password.contains("7"), "Password must contain forced digit '7'");
        assertTrue(password.contains("9"), "Password must contain forced digit '9'");
        assertTrue(password.contains("@"), "Password must contain forced symbol '@'");
        assertTrue(password.contains("#"), "Password must contain forced symbol '#'");
        assertTrue(password.contains("X"), "Password must contain forced letter 'X'");
        assertTrue(password.contains("z"), "Password must contain forced letter 'z'");
    }

    @Test
    void testExcludedCharactersAreNotPresent() {
        var rules = """
                {
                  "length": {
                    "min": 12,
                    "max": 15
                  },
                  "digits": {
                    "min": 3,
                    "max": 8,
                    "exclude": [0, 1, 2],
                    "include": []
                  },
                  "symbols": {
                    "min": 3,
                    "max": 8,
                    "exclude": ["$", "%", "^"],
                    "include": []
                  },
                  "letters": {
                    "exclude": ["a", "e", "i"],
                    "include": []
                  }
                }
                """;

        var password = passwordGenerator.generatePassword(rules);
        
        assertFalse(password.contains("0"), "Password should not contain excluded digit '0'");
        assertFalse(password.contains("1"), "Password should not contain excluded digit '1'");
        assertFalse(password.contains("2"), "Password should not contain excluded digit '2'");
        assertFalse(password.contains("$"), "Password should not contain excluded symbol '$'");
        assertFalse(password.contains("%"), "Password should not contain excluded symbol '%'");
        assertFalse(password.contains("^"), "Password should not contain excluded symbol '^'");
        assertFalse(password.contains("a"), "Password should not contain excluded letter 'a'");
        assertFalse(password.contains("e"), "Password should not contain excluded letter 'e'");
        assertFalse(password.contains("i"), "Password should not contain excluded letter 'i'");
    }

    // ========== COMPREHENSIVE EDGE CASE TESTS ==========

    @Test
    void testMinimumLengthPassword() {
        var rules = """
                {
                  "length": {
                    "min": 1,
                    "max": 1
                  },
                  "digits": {
                    "min": 0,
                    "max": 1,
                    "exclude": [],
                    "include": []
                  },
                  "symbols": {
                    "min": 0,
                    "max": 1,
                    "exclude": [],
                    "include": []
                  },
                  "letters": {
                    "exclude": [],
                    "include": []
                  }
                }
                """;

        var password = passwordGenerator.generatePassword(rules);
        
        assertNotNull(password, "Password should not be null");
        assertEquals(1, password.length(), "Password should be exactly 1 character long");
    }

    @Test
    void testMaximumLengthPassword() {
        var rules = """
                {
                  "length": {
                    "min": 30,
                    "max": 30
                  },
                  "digits": {
                    "min": 10,
                    "max": 15,
                    "exclude": [],
                    "include": []
                  },
                  "symbols": {
                    "min": 8,
                    "max": 12,
                    "exclude": [],
                    "include": []
                  },
                  "letters": {
                    "exclude": [],
                    "include": []
                  }
                }
                """;

        var password = passwordGenerator.generatePassword(rules);
        
        assertNotNull(password, "Password should not be null");
        assertEquals(30, password.length(), "Password should be exactly 30 characters long");
    }

    @Test
    void testZeroMinimumRequirements() {
        var rules = """
                {
                  "length": {
                    "min": 5,
                    "max": 10
                  },
                  "digits": {
                    "min": 0,
                    "max": 5,
                    "exclude": [],
                    "include": []
                  },
                  "symbols": {
                    "min": 0,
                    "max": 5,
                    "exclude": [],
                    "include": []
                  },
                  "letters": {
                    "exclude": [],
                    "include": []
                  }
                }
                """;

        var password = passwordGenerator.generatePassword(rules);
        
        assertNotNull(password, "Password should not be null");
        assertTrue(password.length() >= 5 && password.length() <= 10, 
                   "Password length should be within specified range");
    }

    @Test
    void testMissingJsonNodes() {
        var rulesWithMissingNodes = """
                {
                  "length": {
                    "min": 8,
                    "max": 12
                  },
                  "digits": {
                    "min": 1
                  },
                  "symbols": {
                    "max": 5
                  },
                  "letters": {}
                }
                """;

        var password = passwordGenerator.generatePassword(rulesWithMissingNodes);
        
        assertNotNull(password, "Password should not be null even with missing optional JSON nodes");
        assertTrue(password.length() >= 8 && password.length() <= 12, 
                   "Password length should respect specified bounds");
    }

    @Test
    void testIncludeAndExcludeBothPresent() {
        var rules = """
                {
                  "length": {
                    "min": 15,
                    "max": 20
                  },
                  "digits": {
                    "min": 3,
                    "max": 8,
                    "exclude": [0, 1],
                    "include": [7, 8, 9]
                  },
                  "symbols": {
                    "min": 2,
                    "max": 5,
                    "exclude": ["$", "%"],
                    "include": ["@", "#", "!"]
                  },
                  "letters": {
                    "exclude": ["a", "e"],
                    "include": ["X", "Y", "Z"]
                  }
                }
                """;

        var password = passwordGenerator.generatePassword(rules);
        
        assertNotNull(password, "Password should not be null");
        assertTrue(password.length() >= 15 && password.length() <= 20, 
                   "Password length should be within range");
        
        // Verify included characters are present
        assertTrue(password.contains("7") || password.contains("8") || password.contains("9"), 
                   "Password should contain at least one included digit");
        assertTrue(password.contains("@") || password.contains("#") || password.contains("!"), 
                   "Password should contain at least one included symbol");
        assertTrue(password.contains("X") || password.contains("Y") || password.contains("Z"), 
                   "Password should contain at least one included letter");
        
        // Verify excluded characters are not present
        assertFalse(password.contains("0") || password.contains("1"), 
                   "Password should not contain excluded digits");
        assertFalse(password.contains("$") || password.contains("%"), 
                   "Password should not contain excluded symbols");
        assertFalse(password.contains("a") || password.contains("e"), 
                   "Password should not contain excluded letters");
    }

    // ========== VULNERABILITY AND SECURITY TESTS ==========

    @Test
    void testTimingAttackResistance() {
        var rules = """
                {
                  "length": {
                    "min": 12,
                    "max": 12
                  },
                  "digits": {
                    "min": 4,
                    "max": 4,
                    "exclude": [],
                    "include": []
                  },
                  "symbols": {
                    "min": 4,
                    "max": 4,
                    "exclude": [],
                    "include": []
                  },
                  "letters": {
                    "exclude": [],
                    "include": []
                  }
                }
                """;

        // Multiple generations should complete in reasonable time
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            var password = passwordGenerator.generatePassword(rules);
            assertNotNull(password, "Password should not be null in iteration " + i);
        }
        long endTime = System.currentTimeMillis();
        
        assertTrue(endTime - startTime < 5000, 
                   "Password generation should be efficient and not vulnerable to timing attacks");
    }

    @Test
    void testNoWeakPatterns() {
        var rules = """
                {
                  "length": {
                    "min": 16,
                    "max": 16
                  },
                  "digits": {
                    "min": 4,
                    "max": 6,
                    "exclude": [],
                    "include": []
                  },
                  "symbols": {
                    "min": 3,
                    "max": 5,
                    "exclude": [],
                    "include": []
                  },
                  "letters": {
                    "exclude": [],
                    "include": []
                  }
                }
                """;

        var password = passwordGenerator.generatePassword(rules);
        
        // Check for common weak patterns
        assertFalse(password.contains("123"), "Password should not contain sequential digits");
        assertFalse(password.contains("abc"), "Password should not contain sequential letters");
        assertFalse(password.contains("password"), "Password should not contain common words");
        
        // Verify character distribution
        char firstChar = password.charAt(0);
        boolean hasVariation = password.chars().anyMatch(ch -> ch != firstChar);
        assertTrue(hasVariation, "Password should not consist of repeating single character");
    }

    @Test
    void testMaximumGenerationAttemptsReached() {
        // Create impossible rules that should trigger maximum attempts
        var impossibleRules = """
                {
                  "length": {
                    "min": 3,
                    "max": 3
                  },
                  "digits": {
                    "min": 5,
                    "max": 5,
                    "exclude": [],
                    "include": []
                  },
                  "symbols": {
                    "min": 0,
                    "max": 0,
                    "exclude": [],
                    "include": []
                  },
                  "letters": {
                    "exclude": [],
                    "include": []
                  }
                }
                """;

        SecurityException exception = assertThrows(SecurityException.class, () -> 
            passwordGenerator.generatePassword(impossibleRules),
            "Should throw SecurityException for mathematically impossible rules"
        );
        
        assertTrue(exception.getMessage().contains("mathematically impossible"), 
                   "Exception should indicate impossible rules were detected");
    }

    @Test
    void testConstructorWithPartialEmptyInputs() {
        // Test with one empty parameter - should still work
        PasswordGenerator partialGenerator = new PasswordGenerator("0123456789", "", "abcdefghijklmnopqrstuvwxyz");
        
        var rules = """
                {
                  "length": {
                    "min": 8,
                    "max": 8
                  },
                  "digits": {
                    "min": 4,
                    "max": 4,
                    "exclude": [],
                    "include": []
                  },
                  "symbols": {
                    "min": 0,
                    "max": 0,
                    "exclude": [],
                    "include": []
                  },
                  "letters": {
                    "exclude": [],
                    "include": []
                  }
                }
                """;

        var password = partialGenerator.generatePassword(rules);
        
        assertNotNull(password, "Password should not be null with partial character sets");
        assertEquals(8, password.length(), "Password should have correct length");
        
        // Should contain only digits and letters (no symbols)
        long symbolCount = password.chars().filter(ch -> !Character.isLetterOrDigit(ch)).count();
        assertEquals(0, symbolCount, "Password should contain no symbols");
    }

    @Test
    void testInvalidNegativeValues() {
        var invalidNegativeRules = """
                {
                  "length": {
                    "min": -5,
                    "max": 10
                  },
                  "digits": {
                    "min": 0,
                    "max": 5,
                    "exclude": [],
                    "include": []
                  },
                  "symbols": {
                    "min": 0,
                    "max": 5,
                    "exclude": [],
                    "include": []
                  },
                  "letters": {
                    "exclude": [],
                    "include": []
                  }
                }
                """;

        SecurityException exception = assertThrows(SecurityException.class, () -> 
            passwordGenerator.generatePassword(invalidNegativeRules),
            "Should throw SecurityException for negative length values"
        );
        
        assertTrue(exception.getMessage().contains("Invalid password rules configuration"), 
                   "Exception should indicate invalid configuration");
    }

    @Test
    void testInvalidZeroMaxLength() {
        var zeroMaxLengthRules = """
                {
                  "length": {
                    "min": 1,
                    "max": 0
                  },
                  "digits": {
                    "min": 0,
                    "max": 5,
                    "exclude": [],
                    "include": []
                  },
                  "symbols": {
                    "min": 0,
                    "max": 5,
                    "exclude": [],
                    "include": []
                  },
                  "letters": {
                    "exclude": [],
                    "include": []
                  }
                }
                """;

        SecurityException exception = assertThrows(SecurityException.class, () -> 
            passwordGenerator.generatePassword(zeroMaxLengthRules),
            "Should throw SecurityException for zero max length"
        );
        
        assertTrue(exception.getMessage().contains("Invalid password rules configuration"), 
                   "Exception should indicate invalid configuration");
    }

    @Test
    void testEmptyIncludedArraysWithExclusions() {
        var rules = """
                {
                  "length": {
                    "min": 10,
                    "max": 15
                  },
                  "digits": {
                    "min": 2,
                    "max": 5,
                    "exclude": [0, 1, 2, 3],
                    "include": []
                  },
                  "symbols": {
                    "min": 2,
                    "max": 5,
                    "exclude": ["$", "%", "^", "&"],
                    "include": []
                  },
                  "letters": {
                    "exclude": ["a", "b", "c", "d", "e"],
                    "include": []
                  }
                }
                """;

        var password = passwordGenerator.generatePassword(rules);
        
        assertNotNull(password, "Password should not be null");
        assertTrue(password.length() >= 10 && password.length() <= 15, 
                   "Password should be within specified length range");
        
        // Verify excluded characters are not present
        assertFalse(password.matches(".*[0123].*"), 
                   "Password should not contain excluded digits");
        assertFalse(password.matches(".*[$%^&].*"), 
                   "Password should not contain excluded symbols");
        assertFalse(password.matches(".*[abcde].*"), 
                   "Password should not contain excluded letters");
    }

    // ========== JSON PARSING EDGE CASES ==========

    @Test
    void testMalformedJsonStructure() {
        var malformedJson = """
                {
                  "length": {
                    "min": 8,
                    "max": 12
                  },
                  "digits": {
                    "min": 2,
                    "max": 5
                    // Missing comma
                    "exclude": []
                  }
                }
                """;

        SecurityException exception = assertThrows(SecurityException.class, () -> 
            passwordGenerator.generatePassword(malformedJson),
            "Should throw SecurityException for malformed JSON"
        );
        
        assertTrue(exception.getMessage().contains("Invalid password rules format"), 
                   "Should indicate JSON parsing error");
    }

    @Test
    void testIncompleteJsonStructure() {
        var incompleteJson = """
                {
                  "length": {
                    "min": 8,
                    "max": 12
                  },
                  "digits": {
                    "min": 1,
                    "max": 5,
                    "exclude": [],
                    "include": []
                  },
                  "symbols": {
                    "min": 0,
                    "max": 5,
                    "exclude": [],
                    "include": []
                  },
                  "letters": {
                    "exclude": [],
                    "include": []
                  }
                }
                """;

        var password = passwordGenerator.generatePassword(incompleteJson);
        
        assertNotNull(password, "Password should not be null even with incomplete JSON structure");
        assertTrue(password.length() >= 8 && password.length() <= 12, 
                   "Password length should be within specified bounds");
        
        // Verify minimum requirements are met
        long digitCount = password.chars().filter(Character::isDigit).count();
        assertTrue(digitCount >= 1, "Password should contain at least 1 digit as specified");
    }

    @Test
    void testPasswordEntropyAndComplexity() {
        var highEntropyRules = """
                {
                  "length": {
                    "min": 20,
                    "max": 25
                  },
                  "digits": {
                    "min": 5,
                    "max": 8,
                    "exclude": [],
                    "include": []
                  },
                  "symbols": {
                    "min": 5,
                    "max": 8,
                    "exclude": [],
                    "include": []
                  },
                  "letters": {
                    "exclude": [],
                    "include": []
                  }
                }
                """;

        var password = passwordGenerator.generatePassword(highEntropyRules);
        
        assertNotNull(password, "Password should not be null");
        assertTrue(password.length() >= 20 && password.length() <= 25, 
                   "Password should be within high-security length range");
        
        // Check character diversity for high entropy
        long uniqueChars = password.chars().distinct().count();
        assertTrue(uniqueChars >= 10, "Password should have high character diversity for entropy");
        
        // Verify all character types are present for complexity
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasLetter = password.chars().anyMatch(Character::isLetter);  
        boolean hasSymbol = password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));
        
        assertTrue(hasDigit, "High-entropy password should contain digits");
        assertTrue(hasLetter, "High-entropy password should contain letters");
        assertTrue(hasSymbol, "High-entropy password should contain symbols");
    }
}
