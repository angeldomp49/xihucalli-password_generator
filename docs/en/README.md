# Password Generator Library

A secure Java library for generating passwords with customizable rules through JSON configuration. This library provides cryptographically secure password generation with extensive validation and flexible rule-based configuration.

## Table of Contents

- [Introduction](#introduction)
- [Installation and Configuration](#installation-and-configuration)
- [Basic Usage](#basic-usage)
- [JSON Rules Format](#json-rules-format)
- [Advanced Use Cases](#advanced-use-cases)
- [Error Handling](#error-handling)
- [Examples](#examples)
- [Documentation Links](#documentation-links)

## Introduction

The Password Generator Library is designed for enterprise applications that require secure, compliant password generation with fine-grained control over password composition. Unlike simple random password generators, this library provides:

- **Cryptographic Security**: Uses `SecureRandom` and `ThreadLocalRandom` for cryptographically secure generation
- **Rule Validation**: Mathematical feasibility validation prevents impossible rule configurations
- **Flexible Configuration**: JSON-based rules allow complex password requirements
- **Enterprise Ready**: Handles edge cases and provides comprehensive error reporting

### Business Use Cases

- **User Registration Systems**: Generate secure passwords meeting organizational policies
- **API Key Generation**: Create strong authentication tokens with specific character requirements
- **Temporary Password Systems**: Generate passwords for password reset flows
- **Compliance Requirements**: Meet specific security standards (PCI-DSS, SOX, etc.)

## Installation and Configuration

### System Requirements

- Java 17 or higher
- Gradle for building the project

### Building the Project

```bash
./gradlew build
```

### Library Integration

```java
import org.makechtec.xihucalli.password_generator.PasswordGenerator;

// Initialize with custom character sets
PasswordGenerator generator = new PasswordGenerator(
    "0123456789",                                           // digits
    "!@#$%^&*()_+-=[]{}|;':,./<>?",                        // symbols
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" // letters
);
```

## Basic Usage

### Minimal Example

```java
PasswordGenerator generator = new PasswordGenerator(
    "0123456789",
    "!@#$%^&*()",
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
);

String rules = """
{
  "length": {"min": 8, "max": 12}
}
""";

try {
    String password = generator.generatePassword(rules);
    System.out.println("Generated password: " + password);
} catch (SecurityException e) {
    System.err.println("Password generation failed: " + e.getMessage());
}
```

### Exception Handling

The library throws `SecurityException` for various scenarios:
- Invalid JSON format
- Mathematically impossible rules
- Failed generation after maximum attempts (1000)

```java
try {
    String password = generator.generatePassword(rules);
} catch (SecurityException e) {
    // Handle specific error cases based on message
    if (e.getMessage().contains("mathematically impossible")) {
        // Adjust rules and retry
    }
}
```

## JSON Rules Format

The password generation rules are specified using a structured JSON format:

```json
{
  "length": {
    "min": 8,
    "max": 16
  },
  "digits": {
    "min": 2,
    "max": 4,
    "include": [1, 2, 3],
    "exclude": [0, 5]
  },
  "symbols": {
    "min": 1,
    "max": 3,
    "include": ["@", "#"],
    "exclude": ["&", "%"]
  },
  "letters": {
    "include": ["A", "B", "C"],
    "exclude": ["l", "I", "O"]
  }
}
```

### Rule Properties

| Property | Type | Description | Default |
|----------|------|-------------|---------|
| `length.min` | integer | Minimum password length | 1 |
| `length.max` | integer | Maximum password length | Long.MAX_VALUE |
| `digits.min` | integer | Minimum number of digits | 0 |
| `digits.max` | integer | Maximum number of digits | Long.MAX_VALUE |
| `digits.include` | array of integers | Required digits | [] |
| `digits.exclude` | array of integers | Forbidden digits | [] |
| `symbols.min` | integer | Minimum number of symbols | 0 |
| `symbols.max` | integer | Maximum number of symbols | Long.MAX_VALUE |
| `symbols.include` | array of strings | Required symbols | [] |
| `symbols.exclude` | array of strings | Forbidden symbols | [] |
| `letters.include` | array of strings | Required letters | [] |
| `letters.exclude` | array of strings | Forbidden letters | [] |

## Advanced Use Cases

### Corporate Security Policy

```java
// High-security password for administrative accounts
String corporateRules = """
{
  "length": {"min": 14, "max": 20},
  "digits": {"min": 3, "max": 5},
  "symbols": {"min": 2, "max": 4, "exclude": ["<", ">", "&"]},
  "letters": {"exclude": ["l", "I", "1", "0", "O"]}
}
""";
```

### API Key Generation

```java
// Generate API keys with specific format requirements
String apiKeyRules = """
{
  "length": {"min": 32, "max": 32},
  "digits": {"min": 8},
  "letters": {"include": ["A", "B", "C", "D", "E", "F"]}
}
""";
```

### PIN Generation

```java
PasswordGenerator pinGenerator = new PasswordGenerator("0123456789", "", "");
String pinRules = """
{
  "length": {"min": 4, "max": 6},
  "digits": {"min": 4, "exclude": [0]}
}
""";
```

## Error Handling

Common error scenarios and their solutions:

### Invalid Rules Configuration
- **Error**: "Invalid password rules configuration"
- **Cause**: minLength > maxLength, negative values
- **Solution**: Validate rule constraints before generation

### Mathematically Impossible Rules
- **Error**: "Password rules are mathematically impossible to satisfy"
- **Cause**: minDigits + minSymbols > maxLength
- **Solution**: Adjust minimum requirements or increase maximum length

### Generation Failure
- **Error**: "Unable to generate password meeting security requirements after maximum attempts"
- **Cause**: Very restrictive rules causing repeated failures
- **Solution**: Relax some constraints or verify character set availability

## Examples

### Example 1: Basic 8-Character Password

```java
PasswordGenerator generator = new PasswordGenerator(
    "0123456789",
    "!@#$%^&*()",
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
);

String rules = """
{
  "length": {"min": 8, "max": 8},
  "digits": {"min": 2},
  "symbols": {"min": 1}
}
""";

String password = generator.generatePassword(rules);
// Result example: "aB3#xY9z"
```

### Example 2: High-Security Password

```java
String highSecurityRules = """
{
  "length": {"min": 16, "max": 24},
  "digits": {"min": 4, "max": 6},
  "symbols": {"min": 3, "max": 5},
  "letters": {
    "include": ["A", "B", "C"],
    "exclude": ["l", "I", "O", "0"]
  }
}
""";

String strongPassword = generator.generatePassword(highSecurityRules);
// Result example: "A7B#2C@9x$4mN5pQ"
```

### Example 3: Numeric PIN

```java
PasswordGenerator pinGenerator = new PasswordGenerator(
    "0123456789",
    "",
    ""
);

String pinRules = """
{
  "length": {"min": 4, "max": 6},
  "digits": {"min": 4, "exclude": [0]}
}
""";

String pin = generator.generatePassword(pinRules);
// Result example: "4729"
```

## Documentation Links

- [Business Logic Guide](business-logic/password-generation-rules.md)
- [Common Scenarios](examples/common-scenarios.md)
- [Error Handling Guide](error-handling/security-exceptions.md)
- [Test Coverage Matrix](testing/test-coverage-matrix.md)
- [Migration Guide](migration/upgrade-guide.md)
- [FAQ](faq.md)

---

**Version**: 1.3.0  
**Last Updated**: December 2024  
**License**: [License information]
