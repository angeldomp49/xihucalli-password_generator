# Migration and Upgrade Guide

This guide provides detailed instructions for migrating between versions of the Password Generator Library, including breaking changes, new features, and recommended upgrade strategies.

## Table of Contents

- [Version History](#version-history)
- [Migration from 1.2.x to 1.3.0](#migration-from-12x-to-130)
- [Breaking Changes](#breaking-changes)
- [New Features](#new-features)
- [Deprecated Features](#deprecated-features)
- [Upgrade Strategies](#upgrade-strategies)

## Version History

| Version | Release Date | Key Changes | Compatibility |
|---------|--------------|-------------|---------------|
| 1.3.0 | December 2024 | Mathematical feasibility validation, Unicode support | Breaking changes |
| 1.2.x | October 2024 | Performance optimizations | Backward compatible |
| 1.1.x | August 2024 | Initial stable release | N/A |

## Migration from 1.2.x to 1.3.0

### Overview

Version 1.3.0 introduces significant improvements in rule validation and security, but includes breaking changes that require code updates.

### Critical Changes

#### 1. Enhanced Rule Validation

**Previous Behavior (1.2.x)**:
```java
// This would attempt generation and potentially fail silently or after many attempts
String impossibleRules = """
{
  "length": {"max": 5},
  "digits": {"min": 4},
  "symbols": {"min": 3}
}
""";
generator.generatePassword(impossibleRules); // Would fail unpredictably
```

**New Behavior (1.3.0)**:
```java
// Now throws SecurityException immediately with clear error message
String impossibleRules = """
{
  "length": {"max": 5},
  "digits": {"min": 4},
  "symbols": {"min": 3}
}
""";
// Throws: SecurityException("Password rules are mathematically impossible to satisfy")
```

**Migration Action**: Update error handling to catch and handle feasibility validation exceptions.

#### 2. Stricter Character Set Requirements

**Previous Behavior (1.2.x)**:
```java
// Allowed insufficient character diversity
PasswordGenerator generator = new PasswordGenerator("01", "!", "AB");
// Would sometimes succeed, sometimes fail randomly
```

**New Behavior (1.3.0)**:
```java
// Enforces minimum character diversity for passwords > 3 characters
PasswordGenerator generator = new PasswordGenerator("01", "!", "AB");
String rules = """{"length": {"min": 10, "max": 15}}""";
// Throws: SecurityException("Password rules are mathematically impossible to satisfy")
```

**Migration Action**: Ensure character sets have sufficient diversity (≥4 characters) for longer passwords.

#### 3. Improved Fisher-Yates Shuffling

**Previous Behavior (1.2.x)**:
```java
// Used basic Collections.shuffle()
// Less cryptographically secure
```

**New Behavior (1.3.0)**:
```java
// Implements secure Fisher-Yates with SecureRandom
// Enhanced cryptographic security
```

**Migration Action**: No code changes required, but passwords will have improved randomness.

### Step-by-Step Migration Process

#### Step 1: Update Dependencies

**Maven**:
```xml
<dependency>
    <groupId>org.makechtec.xihucalli</groupId>
    <artifactId>password-generator</artifactId>
    <version>1.3.0</version>
</dependency>
```

**Gradle**:
```groovy
implementation 'org.makechtec.xihucalli:password-generator:1.3.0'
```

#### Step 2: Update Error Handling

**Before (1.2.x)**:
```java
public String generateUserPassword(String rules) {
    try {
        return generator.generatePassword(rules);
    } catch (SecurityException e) {
        // Generic error handling
        return generateFallbackPassword();
    }
}
```

**After (1.3.0)**:
```java
public String generateUserPassword(String rules) {
    try {
        return generator.generatePassword(rules);
    } catch (SecurityException e) {
        if (e.getMessage().contains("mathematically impossible")) {
            // Handle impossible rules - adjust rules or use fallback
            return generateWithAdjustedRules(rules);
        } else if (e.getMessage().contains("Invalid password rules")) {
            // Handle validation errors - fix rule format
            return generateWithValidatedRules(rules);
        } else {
            // Handle other errors
            return generateFallbackPassword();
        }
    }
}
```

#### Step 3: Validate Existing Rule Configurations

Create a validation utility to check existing rules:

```java
public class RuleMigrationValidator {
    
    public static ValidationResult validateRules(String rules) {
        try {
            // Test rule parsing
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rulesNode = mapper.readTree(rules);
            
            // Check for potential issues
            List<String> warnings = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            
            validateLengthRequirements(rulesNode, warnings, errors);
            validateCharacterRequirements(rulesNode, warnings, errors);
            validateFeasibility(rulesNode, warnings, errors);
            
            return new ValidationResult(errors.isEmpty(), warnings, errors);
            
        } catch (Exception e) {
            return ValidationResult.error("JSON parsing failed: " + e.getMessage());
        }
    }
    
    private static void validateLengthRequirements(JsonNode rules, 
                                                  List<String> warnings, 
                                                  List<String> errors) {
        if (rules.has("length")) {
            long min = rules.get("length").path("min").asLong(1);
            long max = rules.get("length").path("max").asLong(Long.MAX_VALUE);
            
            if (min > max) {
                errors.add("Minimum length (" + min + ") exceeds maximum length (" + max + ")");
            }
        }
    }
    
    private static void validateCharacterRequirements(JsonNode rules, 
                                                     List<String> warnings, 
                                                     List<String> errors) {
        long minDigits = rules.path("digits").path("min").asLong(0);
        long minSymbols = rules.path("symbols").path("min").asLong(0);
        long maxLength = rules.path("length").path("max").asLong(Long.MAX_VALUE);
        
        if (minDigits + minSymbols > maxLength) {
            errors.add("Minimum character requirements (" + (minDigits + minSymbols) + 
                      ") exceed maximum length (" + maxLength + ")");
        }
    }
    
    private static void validateFeasibility(JsonNode rules, 
                                          List<String> warnings, 
                                          List<String> errors) {
        // Check for common patterns that might cause issues
        if (rules.path("digits").path("exclude").size() >= 10) {
            warnings.add("All digits excluded - may cause generation issues");
        }
        
        if (rules.path("symbols").path("exclude").size() > 20) {
            warnings.add("Most symbols excluded - may limit password strength");
        }
    }
}

public class ValidationResult {
    private final boolean valid;
    private final List<String> warnings;
    private final List<String> errors;
    
    public static ValidationResult error(String message) {
        return new ValidationResult(false, Collections.emptyList(), List.of(message));
    }
    
    // Constructor and getters...
}
```

#### Step 4: Update Character Set Initialization

**Before (1.2.x)**:
```java
// Minimal character sets were sometimes acceptable
PasswordGenerator generator = new PasswordGenerator("01", "@", "AB");
```

**After (1.3.0)**:
```java
// Ensure sufficient character diversity
PasswordGenerator generator = new PasswordGenerator(
    "0123456789",                                           // At least 10 digits
    "!@#$%^&*()_+-=[]{}|;':,./<>?",                        // Rich symbol set
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" // Full alphabet
);
```

## Breaking Changes

### API Changes

| Change Type | Before | After | Impact |
|-------------|---------|-------|--------|
| Validation | Late validation during generation | Early validation before generation | Exceptions thrown earlier |
| Error Messages | Generic messages | Specific, actionable messages | Better error handling possible |
| Character Sets | No minimum diversity requirement | ≥4 characters for long passwords | May require larger character sets |

### Behavioral Changes

1. **Immediate Rule Validation**: Rules are now validated for mathematical feasibility before generation attempts
2. **Stricter Character Requirements**: Character sets must provide sufficient diversity
3. **Enhanced Security**: Improved random number generation and shuffling algorithms

## New Features

### 1. Mathematical Feasibility Validation

```java
// New feature: Immediate validation of impossible rules
String rules = """
{
  "length": {"max": 3},
  "digits": {"min": 5}
}
""";
// Immediately throws SecurityException with clear message
```

### 2. Enhanced Unicode Support

```java
// Improved support for Unicode characters
PasswordGenerator unicodeGenerator = new PasswordGenerator(
    "0123456789",
    "!@#$%^&*()αβγδε",  // Unicode symbols supported
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
);
```

### 3. Optimized Generation Algorithm

- Reduced average generation time by 30%
- More efficient character pool management
- Improved memory usage patterns

### 4. Better Error Reporting

```java
// More specific error messages
try {
    generator.generatePassword(rules);
} catch (SecurityException e) {
    // Messages now include specific guidance:
    // "Password rules are mathematically impossible to satisfy"
    // "Invalid password rules configuration"
    // "Unable to generate password meeting security requirements after maximum attempts"
}
```

## Deprecated Features

### None in 1.3.0

Version 1.3.0 does not deprecate any existing features, but enhances existing functionality with stricter validation.

## Upgrade Strategies

### Strategy 1: Immediate Upgrade (Recommended)

Best for applications with good test coverage and development resources.

```java
// 1. Update dependency
// 2. Run existing tests to identify issues
// 3. Update error handling
// 4. Validate all rule configurations
// 5. Deploy with comprehensive testing
```

### Strategy 2: Gradual Migration

For large applications with many password generation points.

```java
public class VersionedPasswordGenerator {
    
    private final PasswordGenerator v13Generator;
    private final boolean useV13;
    
    public VersionedPasswordGenerator(boolean useV13) {
        this.useV13 = useV13;
        this.v13Generator = new PasswordGenerator(/* ... */);
    }
    
    public String generatePassword(String rules) {
        if (useV13) {
            try {
                return v13Generator.generatePassword(rules);
            } catch (SecurityException e) {
                // Log migration issues
                logger.warn("V1.3 generation failed, using fallback: {}", e.getMessage());
                return generateLegacyPassword(rules);
            }
        } else {
            return generateLegacyPassword(rules);
        }
    }
}
```

### Strategy 3: Blue-Green Deployment

Deploy both versions and gradually shift traffic.

```java
@Component
public class PasswordGeneratorService {
    
    @Value("${password.generator.version:1.3}")
    private String version;
    
    public String generatePassword(String rules) {
        if ("1.3".equals(version)) {
            return generateV13Password(rules);
        } else {
            return generateLegacyPassword(rules);
        }
    }
}
```

## Testing Migration

### Pre-Migration Testing

```java
@Test
public void testMigrationCompatibility() {
    List<String> existingRules = loadExistingRuleConfigurations();
    
    for (String rules : existingRules) {
        ValidationResult result = RuleMigrationValidator.validateRules(rules);
        
        if (!result.isValid()) {
            System.out.println("Rule needs migration: " + rules);
            System.out.println("Errors: " + result.getErrors());
            System.out.println("Suggested fix: " + suggestFix(rules));
        }
    }
}
```

### Post-Migration Validation

```java
@Test
public void testPasswordQuality() {
    // Generate many passwords and verify they meet quality standards
    for (int i = 0; i < 1000; i++) {
        String password = generator.generatePassword(standardRules);
        assertTrue(validatePasswordStrength(password), 
                  "Password should meet strength requirements");
    }
}
```

## Rollback Plan

If issues arise after migration:

1. **Immediate Rollback**: Revert to version 1.2.x
2. **Rule Adjustment**: Modify problematic rules
3. **Gradual Re-migration**: Use versioned approach

```java
// Emergency rollback configuration
password.generator.version=1.2
password.generator.fallback.enabled=true
```

---

**Next**: [FAQ](../faq.md)  
**Related**: [Error Handling Guide](../error-handling/security-exceptions.md)
