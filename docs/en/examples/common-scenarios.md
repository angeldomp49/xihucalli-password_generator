# Common Scenarios - Password Generation Examples

This guide provides practical examples for common password generation scenarios, demonstrating how to configure the Password Generator Library for different business requirements.

## Table of Contents

- [Basic Password Types](#basic-password-types)
- [Security Level Examples](#security-level-examples)
- [Industry-Specific Requirements](#industry-specific-requirements)
- [Special Use Cases](#special-use-cases)
- [Integration Patterns](#integration-patterns)

## Basic Password Types

### Standard User Password

A typical password for user registration systems with balanced security and usability.

```java
PasswordGenerator generator = new PasswordGenerator(
    "0123456789",
    "!@#$%^&*()",
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
);

String userPasswordRules = """
{
  "length": {"min": 8, "max": 16},
  "digits": {"min": 1, "max": 4},
  "symbols": {"min": 1, "max": 3},
  "letters": {"exclude": ["l", "I", "1", "0", "O"]}
}
""";

String password = generator.generatePassword(userPasswordRules);
// Example output: "Kf7@mN2p"
```

### Temporary Password

Short-term passwords for password reset flows or temporary access.

```java
String tempPasswordRules = """
{
  "length": {"min": 12, "max": 12},
  "digits": {"min": 3, "max": 3},
  "symbols": {"min": 2, "max": 2},
  "symbols": {"include": ["@", "#", "$", "%"]}
}
""";

String tempPassword = generator.generatePassword(tempPasswordRules);
// Example output: "aB3@xY9#mNpQ"
```

### Numeric PIN

Simple numeric passwords for low-security applications.

```java
PasswordGenerator pinGenerator = new PasswordGenerator("0123456789", "", "");

String pinRules = """
{
  "length": {"min": 4, "max": 6},
  "digits": {"min": 4, "exclude": [0]}
}
""";

String pin = pinGenerator.generatePassword(pinRules);
// Example output: "7349"
```

## Security Level Examples

### Low Security - Basic Protection

Suitable for non-critical applications with user convenience prioritized.

```java
String lowSecurityRules = """
{
  "length": {"min": 6, "max": 10},
  "digits": {"min": 1},
  "symbols": {"max": 2}
}
""";

String basicPassword = generator.generatePassword(lowSecurityRules);
// Example output: "pass7@"
```

### Medium Security - Balanced Approach

Standard enterprise security for most business applications.

```java
String mediumSecurityRules = """
{
  "length": {"min": 10, "max": 14},
  "digits": {"min": 2, "max": 4},
  "symbols": {"min": 2, "max": 3},
  "letters": {"exclude": ["l", "I", "O", "0"]}
}
""";

String balancedPassword = generator.generatePassword(mediumSecurityRules);
// Example output: "Kf7@mN2p$x"
```

### High Security - Maximum Protection

For administrative accounts and sensitive systems.

```java
String highSecurityRules = """
{
  "length": {"min": 16, "max": 24},
  "digits": {"min": 4, "max": 6},
  "symbols": {"min": 4, "max": 6},
  "symbols": {"exclude": ["<", ">", "&", "\"", "'"]},
  "letters": {"exclude": ["l", "I", "1", "0", "O"]}
}
""";

String strongPassword = generator.generatePassword(highSecurityRules);
// Example output: "A7B#2C@9x$4mN5pQ!wR"
```

### Ultra Security - Government/Military Grade

Maximum security for classified or highly sensitive applications.

```java
String ultraSecurityRules = """
{
  "length": {"min": 20, "max": 32},
  "digits": {"min": 6, "max": 8},
  "symbols": {"min": 6, "max": 8},
  "symbols": {"include": ["!", "@", "#", "$", "%", "^", "&", "*"]},
  "letters": {"exclude": ["l", "I", "1", "0", "O", "Q", "q"]}
}
""";

String ultraPassword = generator.generatePassword(ultraSecurityRules);
// Example output: "A7B#2C@9x$4mN5pQ!wR8zE*6yT"
```

## Industry-Specific Requirements

### Financial Services (PCI-DSS Compliance)

Meeting Payment Card Industry Data Security Standard requirements.

```java
String pciCompliantRules = """
{
  "length": {"min": 12, "max": 16},
  "digits": {"min": 2, "max": 4},
  "symbols": {"min": 2, "max": 4},
  "symbols": {"exclude": ["<", ">", "&", "\"", "'", "/", "\\\\"]},
  "letters": {"exclude": ["l", "I", "1", "0", "O"]}
}
""";

String pciPassword = generator.generatePassword(pciCompliantRules);
// Example output: "Secure@Bank7#2024"
```

### Healthcare (HIPAA Compliance)

Passwords for healthcare systems handling protected health information.

```java
String hipaaCompliantRules = """
{
  "length": {"min": 14, "max": 18},
  "digits": {"min": 3, "max": 5},
  "symbols": {"min": 3, "max": 4},
  "symbols": {"include": ["@", "#", "$", "%", "!", "*"]},
  "letters": {"exclude": ["l", "I", "1", "0", "O", "S", "5"]}
}
""";

String hipaaPassword = generator.generatePassword(hipaaCompliantRules);
// Example output: "Health@Care3#2024$"
```

### Government Systems

Passwords meeting federal security standards.

```java
String govSecurityRules = """
{
  "length": {"min": 15, "max": 20},
  "digits": {"min": 4, "max": 6},
  "symbols": {"min": 4, "max": 5},
  "symbols": {"exclude": ["<", ">", "&", "\"", "'", "(", ")", "{", "}"]},
  "letters": {"exclude": ["l", "I", "1", "0", "O", "Q", "Z", "2"]}
}
""";

String govPassword = generator.generatePassword(govSecurityRules);
// Example output: "Secure@Gov4#2024$Fed"
```

## Special Use Cases

### API Key Generation

Creating secure API keys with specific format requirements.

```java
String apiKeyRules = """
{
  "length": {"min": 32, "max": 32},
  "digits": {"min": 8, "max": 12},
  "letters": {"include": ["A", "B", "C", "D", "E", "F", "G", "H", "J", "K"]},
  "symbols": {"max": 0}
}
""";

String apiKey = generator.generatePassword(apiKeyRules);
// Example output: "ABCD1234EFGH5678JKAB9012CDEF3456"
```

### Session Tokens

Temporary session identifiers for web applications.

```java
String sessionTokenRules = """
{
  "length": {"min": 24, "max": 28},
  "digits": {"min": 6, "max": 8},
  "letters": {"exclude": ["l", "I", "1", "0", "O"]},
  "symbols": {"max": 2, "include": ["-", "_"]}
}
""";

String sessionToken = generator.generatePassword(sessionTokenRules);
// Example output: "Abc7Def2Ghi9Jkm5Nop8Qrs-3"
```

### Database Connection Strings

Passwords for database connections with restricted character sets.

```java
String dbPasswordRules = """
{
  "length": {"min": 16, "max": 20},
  "digits": {"min": 4, "max": 6},
  "symbols": {"min": 2, "max": 3},
  "symbols": {"include": ["@", "#", "$", "%", "!", "*"]},
  "letters": {"exclude": ["'", "\"", "\\\\", "/", ";", ":"]}
}
""";

String dbPassword = generator.generatePassword(dbPasswordRules);
// Example output: "DbSecure@2024#Pass$"
```

### One-Time Passwords (OTP)

Short-lived passwords for two-factor authentication.

```java
PasswordGenerator otpGenerator = new PasswordGenerator("0123456789", "", "");

String otpRules = """
{
  "length": {"min": 6, "max": 8},
  "digits": {"min": 6, "exclude": [0, 1]}
}
""";

String otp = otpGenerator.generatePassword(otpRules);
// Example output: "749823"
```

## Integration Patterns

### Spring Boot Service Integration

```java
@Service
public class PasswordService {
    private final PasswordGenerator passwordGenerator;
    
    public PasswordService() {
        this.passwordGenerator = new PasswordGenerator(
            "0123456789",
            "!@#$%^&*()",
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        );
    }
    
    public String generateUserPassword() {
        String rules = """
        {
          "length": {"min": 8, "max": 16},
          "digits": {"min": 1, "max": 4},
          "symbols": {"min": 1, "max": 3}
        }
        """;
        
        return passwordGenerator.generatePassword(rules);
    }
}
```

### Configuration-Driven Generation

```java
@Component
public class ConfigurablePasswordGenerator {
    
    @Value("${password.rules.user}")
    private String userPasswordRules;
    
    @Value("${password.rules.admin}")
    private String adminPasswordRules;
    
    private final PasswordGenerator generator;
    
    public String generatePassword(String userType) {
        String rules = "admin".equals(userType) ? adminPasswordRules : userPasswordRules;
        return generator.generatePassword(rules);
    }
}
```

### Batch Password Generation

```java
public class BatchPasswordGenerator {
    private final PasswordGenerator generator;
    
    public List<String> generatePasswords(String rules, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> generator.generatePassword(rules))
                .collect(Collectors.toList());
    }
    
    public Map<String, String> generateNamedPasswords(String rules, List<String> names) {
        return names.stream()
                .collect(Collectors.toMap(
                    Function.identity(),
                    name -> generator.generatePassword(rules)
                ));
    }
}
```

### Error Handling Patterns

```java
public class RobustPasswordGenerator {
    private final PasswordGenerator generator;
    private final List<String> fallbackRules;
    
    public String generatePasswordWithFallback(String primaryRules) {
        try {
            return generator.generatePassword(primaryRules);
        } catch (SecurityException e) {
            log.warn("Primary rules failed: {}, trying fallback", e.getMessage());
            
            for (String fallbackRule : fallbackRules) {
                try {
                    return generator.generatePassword(fallbackRule);
                } catch (SecurityException ex) {
                    log.debug("Fallback rule failed: {}", ex.getMessage());
                }
            }
            
            throw new RuntimeException("All password generation strategies failed", e);
        }
    }
}
```

---

**Next**: [Error Handling Guide](../error-handling/security-exceptions.md)  
**Related**: [Business Logic Guide](../business-logic/password-generation-rules.md)
