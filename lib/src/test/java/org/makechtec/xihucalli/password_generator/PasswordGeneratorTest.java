package org.makechtec.xihucalli.password_generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {
    
    private ApplicationPropertiesLoader applicationPropertiesLoader;
    private PasswordGenerator passwordGenerator;
    
    @BeforeEach
    void setUp() {
        applicationPropertiesLoader = new ApplicationPropertiesLoader();
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

        System.out.println(password);

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
                    "include": [ "a", "F" ]
                  }
                }
                """;

        var password = passwordGenerator.generatePassword(rules);

        System.out.println(password);

        assertNotNull(password);
        assertFalse(password.isBlank());
        assertFalse(password.contains("0"));
        assertFalse(password.contains("1"));
        assertFalse(password.contains("$"));
        assertFalse(password.contains("%"));

        assertTrue(password.contains("a"));
        assertTrue(password.contains("F"));

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
                    "max": 8,
                    "exclude": [],
                    "include": [
                        "&",
                        "/"
                    ]
                  },
                  "letters": {
                    "exclude": [],
                    "include": [ "a", "F" ]
                  }
                }
                """;

        var password = passwordGenerator.generatePassword(rules);

        System.out.println(password);

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
                    "include": []
                  }
                }
                """;

        var password = passwordGenerator.generatePassword(rules);

        System.out.println(password);

        assertNotNull(password);
        assertFalse(password.isBlank());
        assertFalse(password.contains("0"));
        assertFalse(password.contains("1"));
        assertFalse(password.contains("$"));
        assertFalse(password.contains("%"));

    }

}