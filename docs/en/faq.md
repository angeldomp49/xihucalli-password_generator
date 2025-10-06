# Frequently Asked Questions (FAQ)

This document answers common questions about the Password Generator Library, providing practical solutions and explanations based on real-world usage scenarios.

## Table of Contents

- [General Questions](#general-questions)
- [Security Questions](#security-questions)
- [Configuration Questions](#configuration-questions)
- [Error Handling Questions](#error-handling-questions)
- [Performance Questions](#performance-questions)
- [Integration Questions](#integration-questions)

## General Questions

### Q: What makes this password generator different from others?

**A**: Our Password Generator Library offers several unique advantages:

- **Mathematical Feasibility Validation**: Unlike basic generators, we validate that your rules are mathematically possible before attempting generation
- **Cryptographic Security**: Uses `SecureRandom` and implements secure Fisher-Yates shuffling
- **Enterprise Features**: Comprehensive error handling, detailed logging, and extensive validation
- **JSON Configuration**: Flexible, declarative rule specification instead of procedural configuration

### Q: What are the minimum system requirements?

**A**: 
- **Java Version**: Java 17 or higher
- **Memory**: Minimal heap usage (< 10MB for typical usage)
- **Dependencies**: Jackson for JSON processing (automatically included)
- **Build System**: Gradle or Maven supported

### Q: How do I get started with basic password generation?

**A**: Here's the simplest possible example:

```java
PasswordGenerator generator = new PasswordGenerator(
    "0123456789",
    "!@#$%^&*()",
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
);

String password = generator.generatePassword("{}"); // Uses defaults
```

## Security Questions

### Q: How does the library ensure cryptographic security?

**A**: The library implements multiple layers of security:

1. **SecureRandom**: All character selection uses `java.security.SecureRandom`
2. **Fisher-Yates Shuffling**: Cryptographically secure shuffling algorithm
3. **ThreadLocalRandom**: Used for length determination within secure bounds
4. **No Predictable Patterns**: Algorithm designed to prevent pattern recognition
5. **Secure Memory Handling**: Character arrays are properly managed and cleared

```java
// Example of secure random character selection
Character selectedChar = availableChars.get(secureRandom.nextInt(availableChars.size()));
```

### Q: Can the generated passwords be predicted or reverse-engineered?

**A**: No. The library is designed to prevent prediction:

- Uses cryptographically secure random number generators
- No seed exposure or deterministic patterns
- Each generation is independent
- Shuffling occurs after character selection for additional randomness

### Q: How do I generate passwords that meet compliance requirements (PCI-DSS, HIPAA, etc.)?

**A**: Configure specific rules for each compliance standard:

```java
// PCI-DSS compliant password
String pciRules = """
{
  "length": {"min": 12, "max": 16},
  "digits": {"min": 2, "max": 4},
  "symbols": {"min": 2, "max": 4},
  "symbols": {"exclude": ["<", ">", "&", "\"", "'"]},
  "letters": {"exclude": ["l", "I", "1", "0", "O"]}
}
""";
```

## Configuration Questions

### Q: Why does my generation fail with "mathematically impossible" error?

**A**: This error occurs when your rules cannot be satisfied. Common causes:

1. **Insufficient Length**: `minDigits + minSymbols > maxLength`
2. **Empty Character Sets**: All characters of a required type are excluded
3. **Conflicting Requirements**: Include and exclude rules eliminate all options

**Solution**: Verify your rule mathematics:

```java
// Problem: Requires 7 characters minimum but allows only 5 maximum
{
  "length": {"max": 5},
  "digits": {"min": 4},
  "symbols": {"min": 3}
}

// Solution: Increase max length or reduce minimums
{
  "length": {"max": 8},
  "digits": {"min": 4},
  "symbols": {"min": 3}
}
```

### Q: How do inclusion and exclusion rules interact?

**A**: **Inclusion rules always take precedence over exclusion rules**:

```java
// Even though 2 is in exclude list, it will be included
{
  "digits": {
    "include": [1, 2, 3],
    "exclude": [2, 4, 5]
  }
}
// Result: Only digits 1, 2, 3 will be used
```

### Q: Can I use Unicode characters in passwords?

**A**: Yes, full Unicode support is available:

```java
PasswordGenerator unicodeGenerator = new PasswordGenerator(
    "0123456789",
    "!@#$%^&*()αβγδε∑π",  // Unicode symbols
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
);
```

### Q: What are the default values for unspecified rules?

**A**: Default values are designed to be permissive:

| Property | Default Value | Description |
|----------|---------------|-------------|
| `length.min` | 1 | Minimum possible length |
| `length.max` | Long.MAX_VALUE | No practical maximum |
| `digits.min` | 0 | No digits required |
| `digits.max` | Long.MAX_VALUE | No digit limit |
| `symbols.min` | 0 | No symbols required |
| `symbols.max` | Long.MAX_VALUE | No symbol limit |
| `include` lists | [] | No mandatory characters |
| `exclude` lists | [] | No forbidden characters |

## Error Handling Questions

### Q: How should I handle SecurityException in production code?

**A**: Implement comprehensive error handling with specific responses:

```java
public String generatePasswordSafely(String rules) {
    try {
        return generator.generatePassword(rules);
        
    } catch (SecurityException e) {
        String message = e.getMessage();
        
        if (message.contains("mathematically impossible")) {
            // Log the issue and use fallback rules
            logger.warn("Rules mathematically impossible: {}", rules);
            return generator.generatePassword(getFallbackRules());
            
        } else if (message.contains("Invalid password rules")) {
            // Fix the rules and retry
            logger.error("Invalid rules format: {}", rules);
            return generator.generatePassword(getValidatedRules(rules));
            
        } else if (message.contains("maximum attempts")) {
            // Rules too restrictive, relax them
            logger.warn("Generation timeout, relaxing rules: {}", rules);
            return generator.generatePassword(getRelaxedRules(rules));
            
        } else {
            // Unknown error, use simplest fallback
            logger.error("Unknown generation error: {}", e.getMessage());
            return generator.generatePassword("{}");
        }
    }
}
```

### Q: What does "maximum attempts exceeded" mean?

**A**: The generator tried 1000 times to create a valid password but failed. This usually indicates overly restrictive rules:

```java
// Too restrictive - may cause timeout
{
  "length": {"min": 8, "max": 8},
  "digits": {"min": 4, "max": 4, "include": [1,2,3,4]},
  "symbols": {"min": 2, "max": 2, "include": ["@", "#"]},
  "letters": {"min": 2, "max": 2, "include": ["A", "B"]}
}

// Solution: Allow more flexibility
{
  "length": {"min": 8, "max": 12},
  "digits": {"min": 2, "max": 6},
  "symbols": {"min": 1, "max": 4},
  "letters": {"exclude": ["l", "I", "O"]}
}
```

## Performance Questions

### Q: How fast is password generation?

**A**: Performance varies by complexity:

- **Simple passwords** (8 chars, basic rules): < 1ms average
- **Complex passwords** (16 chars, all rules): < 5ms average
- **Batch generation** (1000 passwords): < 1 second total

### Q: Can I generate passwords concurrently?

**A**: Yes, the library is thread-safe:

```java
// Safe for concurrent use
PasswordGenerator generator = new PasswordGenerator(/*...*/);

// Multiple threads can safely call this simultaneously
CompletableFuture<String> future1 = CompletableFuture.supplyAsync(
    () -> generator.generatePassword(rules1)
);
CompletableFuture<String> future2 = CompletableFuture.supplyAsync(
    () -> generator.generatePassword(rules2)
);
```

### Q: Does the library have memory leaks?

**A**: No, the library is designed for memory efficiency:

- Character lists are immutable and reused
- No caching of generated passwords
- Temporary objects are garbage collected promptly
- Extensive testing shows no memory leaks under load

## Integration Questions

### Q: How do I integrate with Spring Boot?

**A**: Create a configuration bean:

```java
@Configuration
public class PasswordGeneratorConfig {
    
    @Bean
    public PasswordGenerator passwordGenerator(
            @Value("${password.chars.digits:0123456789}") String digits,
            @Value("${password.chars.symbols:!@#$%^&*()}") String symbols,
            @Value("${password.chars.letters:abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ}") String letters) {
        return new PasswordGenerator(digits, symbols, letters);
    }
}

@Service
public class UserService {
    
    @Autowired
    private PasswordGenerator passwordGenerator;
    
    public String generateUserPassword() {
        String rules = """
        {
          "length": {"min": 8, "max": 16},
          "digits": {"min": 1},
          "symbols": {"min": 1}
        }
        """;
        return passwordGenerator.generatePassword(rules);
    }
}
```

### Q: Can I load rules from configuration files?

**A**: Yes, store rules in application properties:

```yaml
# application.yml
password:
  rules:
    user: |
      {
        "length": {"min": 8, "max": 16},
        "digits": {"min": 1},
        "symbols": {"min": 1}
      }
    admin: |
      {
        "length": {"min": 14, "max": 20},
        "digits": {"min": 3},
        "symbols": {"min": 3}
      }
```

```java
@Component
public class PasswordRulesConfig {
    
    @Value("${password.rules.user}")
    private String userRules;
    
    @Value("${password.rules.admin}")
    private String adminRules;
    
    public String getRules(String userType) {
        return "admin".equals(userType) ? adminRules : userRules;
    }
}
```

### Q: How do I test password generation?

**A**: Focus on rule validation rather than exact password content:

```java
@Test
public void testPasswordGeneration() {
    String rules = """
    {
      "length": {"min": 8, "max": 12},
      "digits": {"min": 2},
      "symbols": {"min": 1}
    }
    """;
    
    String password = generator.generatePassword(rules);
    
    // Test length
    assertTrue(password.length() >= 8 && password.length() <= 12);
    
    // Test digit count
    long digitCount = password.chars().filter(Character::isDigit).count();
    assertTrue(digitCount >= 2);
    
    // Test symbol count
    long symbolCount = password.chars().filter(c -> "!@#$%^&*()".indexOf(c) >= 0).count();
    assertTrue(symbolCount >= 1);
}
```

### Q: How do I handle different environments (dev, staging, prod)?

**A**: Use environment-specific configurations:

```java
@Profile("development")
@Configuration
public class DevPasswordConfig {
    @Bean
    public PasswordGenerator passwordGenerator() {
        // Simpler rules for development
        return new PasswordGenerator("01", "@", "ab");
    }
}

@Profile("production")
@Configuration
public class ProdPasswordConfig {
    @Bean
    public PasswordGenerator passwordGenerator() {
        // Full security for production
        return new PasswordGenerator(
            "0123456789",
            "!@#$%^&*()_+-=[]{}|;':,./<>?",
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        );
    }
}
```

---

**Related Documentation**:
- [README](README.md) - Getting started guide
- [Business Logic Guide](business-logic/password-generation-rules.md) - Algorithm details
- [Error Handling Guide](error-handling/security-exceptions.md) - Exception management
- [Examples](examples/common-scenarios.md) - Practical use cases
