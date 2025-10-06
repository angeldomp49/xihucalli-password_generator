# Security Exceptions - Error Handling Guide

This guide provides comprehensive coverage of all error scenarios in the Password Generator Library, including detailed explanations, causes, and resolution strategies.

## Table of Contents

- [Exception Overview](#exception-overview)
- [Rule Validation Errors](#rule-validation-errors)
- [Mathematical Impossibility Errors](#mathematical-impossibility-errors)
- [Generation Failure Errors](#generation-failure-errors)
- [JSON Processing Errors](#json-processing-errors)
- [Constructor Validation Errors](#constructor-validation-errors)
- [Error Handling Best Practices](#error-handling-best-practices)

## Exception Overview

The Password Generator Library uses `SecurityException` for all error conditions to maintain consistency and security-focused error handling. All exceptions include descriptive messages to aid in debugging and resolution.

### Common Exception Pattern

```java
try {
    String password = generator.generatePassword(rules);
} catch (SecurityException e) {
    // Analyze error message for specific handling
    String message = e.getMessage();
    
    if (message.contains("mathematically impossible")) {
        // Handle impossible rules
    } else if (message.contains("Invalid password rules")) {
        // Handle validation errors
    } else if (message.contains("maximum attempts")) {
        // Handle generation failures
    }
}
```

## Rule Validation Errors

### Invalid Length Configuration

**Error Message**: `"Invalid password rules configuration"`

**Scenarios**:
1. Minimum length greater than maximum length
2. Zero or negative length values
3. Minimum requirements exceed maximum constraints

**Examples**:

```java
// Scenario 1: Invalid length range
String invalidRules1 = """
{
  "length": {"min": 10, "max": 8}
}
""";
// Throws: SecurityException("Invalid password rules configuration")

// Scenario 2: Negative values
String invalidRules2 = """
{
  "length": {"min": -1, "max": 10}
}
""";
// Throws: SecurityException("Invalid password rules configuration")

// Scenario 3: Conflicting digit constraints
String invalidRules3 = """
{
  "digits": {"min": 5, "max": 3}
}
""";
// Throws: SecurityException("Invalid password rules configuration")
```

**Resolution Strategies**:

```java
public class RuleValidator {
    
    public static void validateRules(JsonNode rules) {
        if (rules.has("length")) {
            long min = rules.get("length").get("min").asLong(1);
            long max = rules.get("length").get("max").asLong(Long.MAX_VALUE);
            
            if (min > max) {
                throw new IllegalArgumentException("Minimum length cannot exceed maximum length");
            }
            if (min <= 0) {
                throw new IllegalArgumentException("Length must be positive");
            }
        }
        
        // Similar validation for digits and symbols...
    }
}
```

### Invalid Character Requirements

**Error Message**: `"Invalid password rules configuration"`

**Scenarios**:
1. Negative minimum/maximum character counts
2. Include/exclude lists containing invalid characters
3. Conflicting character type requirements

**Examples**:

```java
// Negative character counts
String invalidRules = """
{
  "digits": {"min": -2, "max": 5}
}
""";

// Invalid character in include list
String invalidInclude = """
{
  "digits": {"include": ["a", "b"]}
}
""";
// Note: This won't fail at validation but will be filtered out during processing
```

## Mathematical Impossibility Errors

### Insufficient Length for Requirements

**Error Message**: `"Password rules are mathematically impossible to satisfy"`

**Cause**: The sum of minimum required characters exceeds the maximum allowed length.

**Example**:

```java
String impossibleRules = """
{
  "length": {"max": 5},
  "digits": {"min": 4},
  "symbols": {"min": 3}
}
""";
// Requires 7 characters minimum but allows only 5 maximum
```

**Resolution**:

```java
public class RuleAdjuster {
    
    public static String adjustImpossibleRules(String originalRules) {
        // Parse and analyze rules
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rules = mapper.readTree(originalRules);
        
        long maxLength = rules.path("length").path("max").asLong(Long.MAX_VALUE);
        long minDigits = rules.path("digits").path("min").asLong(0);
        long minSymbols = rules.path("symbols").path("min").asLong(0);
        
        long requiredLength = minDigits + minSymbols;
        
        if (requiredLength > maxLength) {
            // Option 1: Increase max length
            maxLength = requiredLength + 2;
            
            // Option 2: Reduce minimum requirements
            // minDigits = Math.min(minDigits, maxLength / 2);
            // minSymbols = Math.min(minSymbols, maxLength / 2);
            
            // Rebuild rules with adjustments
            return buildAdjustedRules(maxLength, minDigits, minSymbols);
        }
        
        return originalRules;
    }
}
```

### Empty Character Sets

**Error Message**: `"Password rules are mathematically impossible to satisfy"`

**Cause**: All characters of a required type have been excluded or the character set is empty.

**Example**:

```java
String emptySetRules = """
{
  "digits": {
    "min": 1,
    "exclude": [0,1,2,3,4,5,6,7,8,9]
  }
}
""";
// Requires digits but excludes all available digits
```

**Resolution**:

```java
public class CharacterSetValidator {
    
    public static void validateCharacterAvailability(PasswordGenerator generator, String rules) {
        // This would require access to generator's character sets
        // Implementation would check if required characters are available
        
        JsonNode rulesNode = new ObjectMapper().readTree(rules);
        
        if (rulesNode.path("digits").path("min").asLong(0) > 0) {
            List<Integer> excluded = getExcludedDigits(rulesNode);
            List<Integer> included = getIncludedDigits(rulesNode);
            
            if (included.isEmpty() && excluded.size() >= 10) {
                throw new IllegalArgumentException("No digits available for generation");
            }
        }
    }
}
```

### Insufficient Character Diversity

**Error Message**: `"Password rules are mathematically impossible to satisfy"`

**Cause**: Less than 4 total available characters for passwords longer than 3 characters.

**Example**:

```java
PasswordGenerator limitedGenerator = new PasswordGenerator("01", "!", "AB");
// Only 4 characters total: 0,1,!,A,B

String diversityRules = """
{
  "length": {"min": 10, "max": 15},
  "digits": {"exclude": [1]},
  "symbols": {"exclude": ["!"]},
  "letters": {"exclude": ["B"]}
}
""";
// Leaves only 2 characters (0, A) for 10-15 character password
```

## Generation Failure Errors

### Maximum Attempts Exceeded

**Error Message**: `"Unable to generate password meeting security requirements after maximum attempts"`

**Cause**: The generator failed to create a valid password after 1000 attempts, usually due to overly restrictive rules.

**Common Scenarios**:
1. Very specific include/exclude combinations
2. Conflicting requirements that rarely align
3. Edge cases in rule combinations

**Example**:

```java
String restrictiveRules = """
{
  "length": {"min": 8, "max": 8},
  "digits": {"min": 4, "max": 4, "include": [1,2,3,4]},
  "symbols": {"min": 2, "max": 2, "include": ["@", "#"]},
  "letters": {"min": 2, "max": 2, "include": ["A", "B"]}
}
""";
// Very specific requirements that may be hard to satisfy randomly
```

**Resolution Strategies**:

```java
public class GenerationOptimizer {
    
    public static String generateWithRetryStrategy(PasswordGenerator generator, String rules) {
        String[] fallbackStrategies = {
            rules,                    // Original rules
            relaxMinimums(rules),     // Reduce minimum requirements
            increaseLength(rules),    // Allow longer passwords
            expandCharacterSet(rules) // Allow more character options
        };
        
        for (String strategy : fallbackStrategies) {
            try {
                return generator.generatePassword(strategy);
            } catch (SecurityException e) {
                if (!e.getMessage().contains("maximum attempts")) {
                    throw e; // Re-throw non-generation errors
                }
                // Continue to next strategy
            }
        }
        
        throw new SecurityException("All generation strategies failed");
    }
    
    private static String relaxMinimums(String rules) {
        // Implementation to reduce minimum requirements by 1
        return rules;
    }
    
    private static String increaseLength(String rules) {
        // Implementation to increase maximum length
        return rules;
    }
    
    private static String expandCharacterSet(String rules) {
        // Implementation to remove some exclusions
        return rules;
    }
}
```

## JSON Processing Errors

### Malformed JSON

**Error Message**: `"Invalid password rules format"`

**Cause**: JSON parsing fails due to syntax errors.

**Examples**:

```java
// Missing closing brace
String malformedJson1 = """
{
  "length": {"min": 8, "max": 16"
}
""";

// Invalid syntax
String malformedJson2 = """
{
  length: {min: 8, max: 16}
}
""";

// Invalid data types
String malformedJson3 = """
{
  "length": {"min": "eight", "max": "sixteen"}
}
""";
```

**Resolution**:

```java
public class JsonValidator {
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    public static void validateJsonSyntax(String json) {
        try {
            MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON syntax: " + e.getMessage(), e);
        }
    }
    
    public static String sanitizeJson(String json) {
        // Remove common issues
        return json.trim()
                  .replaceAll("([{,]\\s*)([a-zA-Z_][a-zA-Z0-9_]*):", "$1\"$2\":") // Add quotes to keys
                  .replaceAll(":\\s*([a-zA-Z]+)([,}])", ": \"$1\"$2");           // Add quotes to string values
    }
}
```

## Constructor Validation Errors

### Null Character Sets

**Error Message**: `"Character sets cannot be null"`

**Cause**: One or more character set parameters passed to constructor are null.

**Example**:

```java
PasswordGenerator generator = new PasswordGenerator(null, "!@#", "abc");
// Throws: SecurityException("Character sets cannot be null")
```

### Empty Character Sets

**Error Message**: `"At least one character set must be non-empty"`

**Cause**: All character sets passed to constructor are empty strings.

**Example**:

```java
PasswordGenerator generator = new PasswordGenerator("", "", "");
// Throws: SecurityException("At least one character set must be non-empty")
```

**Resolution**:

```java
public class PasswordGeneratorFactory {
    
    public static PasswordGenerator createWithDefaults(String digits, String symbols, String letters) {
        // Provide defaults for null or empty sets
        digits = (digits != null && !digits.isEmpty()) ? digits : "0123456789";
        symbols = (symbols != null && !symbols.isEmpty()) ? symbols : "!@#$%^&*()";
        letters = (letters != null && !letters.isEmpty()) ? letters : "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        
        return new PasswordGenerator(digits, symbols, letters);
    }
}
```

## Error Handling Best Practices

### Comprehensive Error Handling

```java
public class RobustPasswordService {
    
    private final PasswordGenerator generator;
    private final List<String> fallbackRules;
    
    public GenerationResult generatePassword(String rules) {
        try {
            String password = generator.generatePassword(rules);
            return GenerationResult.success(password);
            
        } catch (SecurityException e) {
            return handleSecurityException(e, rules);
        }
    }
    
    private GenerationResult handleSecurityException(SecurityException e, String originalRules) {
        String message = e.getMessage();
        
        if (message.contains("mathematically impossible")) {
            return GenerationResult.failure("IMPOSSIBLE_RULES", 
                "The password rules are mathematically impossible. Please adjust minimum requirements or increase maximum length.", 
                suggestRuleAdjustments(originalRules));
                
        } else if (message.contains("Invalid password rules")) {
            return GenerationResult.failure("INVALID_RULES", 
                "The password rules contain invalid configurations. Please check min/max values and data types.", 
                validateAndSuggestFixes(originalRules));
                
        } else if (message.contains("maximum attempts")) {
            return GenerationResult.failure("GENERATION_FAILED", 
                "Password generation failed after maximum attempts. Rules may be too restrictive.", 
                suggestLessRestrictiveRules(originalRules));
                
        } else if (message.contains("Invalid password rules format")) {
            return GenerationResult.failure("JSON_ERROR", 
                "Invalid JSON format in password rules.", 
                suggestJsonFixes(originalRules));
                
        } else {
            return GenerationResult.failure("UNKNOWN_ERROR", 
                "An unexpected error occurred: " + message, 
                null);
        }
    }
}

public class GenerationResult {
    private final boolean success;
    private final String password;
    private final String errorCode;
    private final String errorMessage;
    private final String suggestion;
    
    public static GenerationResult success(String password) {
        return new GenerationResult(true, password, null, null, null);
    }
    
    public static GenerationResult failure(String code, String message, String suggestion) {
        return new GenerationResult(false, null, code, message, suggestion);
    }
    
    // Constructor and getters...
}
```

### Logging and Monitoring

```java
public class MonitoredPasswordGenerator {
    
    private static final Logger logger = LoggerFactory.getLogger(MonitoredPasswordGenerator.class);
    private final MeterRegistry meterRegistry;
    private final PasswordGenerator generator;
    
    public String generatePassword(String rules) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            String password = generator.generatePassword(rules);
            
            meterRegistry.counter("password.generation.success").increment();
            logger.debug("Password generated successfully with rules: {}", sanitizeRulesForLogging(rules));
            
            return password;
            
        } catch (SecurityException e) {
            String errorType = categorizeError(e.getMessage());
            meterRegistry.counter("password.generation.failure", "type", errorType).increment();
            
            logger.warn("Password generation failed: {} | Rules: {}", 
                e.getMessage(), sanitizeRulesForLogging(rules));
            
            throw e;
        } finally {
            sample.stop(Timer.builder("password.generation.duration").register(meterRegistry));
        }
    }
    
    private String categorizeError(String message) {
        if (message.contains("mathematically impossible")) return "impossible_rules";
        if (message.contains("Invalid password rules configuration")) return "invalid_rules";
        if (message.contains("maximum attempts")) return "generation_timeout";
        if (message.contains("Invalid password rules format")) return "json_error";
        return "unknown";
    }
    
    private String sanitizeRulesForLogging(String rules) {
        // Remove sensitive information but keep structure for debugging
        return rules.replaceAll("\"include\":\\s*\\[[^\\]]*\\]", "\"include\":[***]")
                   .replaceAll("\"exclude\":\\s*\\[[^\\]]*\\]", "\"exclude\":[***]");
    }
}
```

---

**Next**: [Test Coverage Matrix](../testing/test-coverage-matrix.md)  
**Related**: [Business Logic Guide](../business-logic/password-generation-rules.md)
