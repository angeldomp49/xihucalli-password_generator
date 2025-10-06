# Password Generation Rules - Business Logic Guide

This guide explains the core business logic behind the Password Generator Library, including rule hierarchy, generation algorithm, and conflict resolution strategies.

## Rule Hierarchy and Priority System

The password generator follows a strict hierarchy when processing rules to ensure predictable and secure behavior:

### 1. Inclusion Rules Take Precedence

When both `include` and `exclude` lists are specified for the same character type, inclusion rules always take priority:

```json
{
  "digits": {
    "include": [1, 2, 3],
    "exclude": [2, 4, 5]
  }
}
```

In this case, only digits `1`, `2`, and `3` will be considered, even though `2` appears in the exclude list.

### 2. Minimum Requirements Override Maximums

The generator ensures minimum requirements are met before considering maximum constraints:

```json
{
  "length": {"min": 10, "max": 12},
  "digits": {"min": 8, "max": 3}
}
```

This configuration would be flagged as mathematically impossible since 8 minimum digits cannot fit within the maximum constraint of 3.

### 3. Mathematical Feasibility Validation

Before generation begins, the system validates that rules are mathematically possible:

- `minDigits + minSymbols ≤ maxLength`
- Available character sets must contain sufficient characters
- Include lists must reference characters that exist in the base character sets

## Password Generation Algorithm

The generation process follows a deterministic 8-step algorithm designed for security and compliance:

### Step 1: Input Validation and Rule Hydration

```
1. Parse JSON input using Jackson ObjectMapper
2. Validate JSON structure and data types
3. Create PasswordRulesInformation object
4. Perform initial rule validation (positive values, min ≤ max)
```

### Step 2: Mathematical Feasibility Check

```
1. Calculate minimum required characters: minDigits + minSymbols
2. Verify minimum ≤ maxLength
3. Check character set availability for each type
4. Ensure sufficient character diversity (≥4 available characters for lengths >3)
```

### Step 3: Secure Length Determination

```java
// Uses ThreadLocalRandom for cryptographic security
int targetLength = ThreadLocalRandom.current().nextInt(minLength, maxLength + 1);
```

### Step 4: Mandatory Character Addition

```
1. Add all characters from include lists first
2. These characters are mandatory and cannot be removed
3. Track current counts for each character type
```

### Step 5: Minimum Requirement Fulfillment

```
1. Calculate remaining digits needed: max(0, minDigits - currentDigits)
2. Calculate remaining symbols needed: max(0, minSymbols - currentSymbols)
3. Add random characters from available pools to meet minimums
```

### Step 6: Remaining Position Filling

```
1. Create combined character pool from all available character types
2. Fill remaining positions with random selections
3. Ensure target length is reached
```

### Step 7: Secure Shuffling (Fisher-Yates Algorithm)

```java
// Cryptographically secure shuffle using SecureRandom
for (int i = password.size() - 1; i > 0; i--) {
    int randomIndex = secureRandom.nextInt(i + 1);
    Collections.swap(password, i, randomIndex);
}
```

### Step 8: Final Validation and Retry Logic

```
1. Validate generated password against all rules
2. If validation fails, retry (up to 1000 attempts)
3. Throw SecurityException if maximum attempts exceeded
```

## Conflict Resolution Strategies

### Scenario 1: Impossible Length Requirements

**Problem**: `minDigits + minSymbols > maxLength`

```json
{
  "length": {"max": 5},
  "digits": {"min": 4},
  "symbols": {"min": 3}
}
```

**Resolution**: Immediate rejection with `SecurityException` during feasibility check.

### Scenario 2: Insufficient Character Set

**Problem**: Required characters not available in base character sets

```json
{
  "digits": {"include": [9]},
  // But PasswordGenerator initialized with digitsList = "01234567"
}
```

**Resolution**: Include lists are filtered to only contain characters present in base sets.

### Scenario 3: Over-Restrictive Exclusions

**Problem**: All characters excluded from a required type

```json
{
  "digits": {"min": 1, "exclude": [0,1,2,3,4,5,6,7,8,9]}
}
```

**Resolution**: Feasibility check detects empty available character sets and rejects the configuration.

### Scenario 4: Short Password Special Handling

**Problem**: Very short passwords (1-3 characters) with strict diversity requirements

```json
{
  "length": {"max": 2},
  "digits": {"min": 1},
  "symbols": {"min": 1}
}
```

**Resolution**: For passwords ≤3 characters with no minimum digit/symbol requirements, the MIN_AVAILABLE_CHARACTERS constraint (4) is relaxed.

## Security Considerations

### Cryptographic Random Number Generation

- **SecureRandom**: Used for character selection and shuffling
- **ThreadLocalRandom**: Used for length determination within ranges
- **No Predictable Patterns**: Algorithm designed to prevent pattern recognition

### Retry Logic and Timing Attacks

- **Maximum Attempts**: 1000 retries prevent infinite loops
- **Constant Time Validation**: Validation time doesn't leak information about rule complexity
- **Secure Failure**: Graceful degradation with meaningful error messages

### Character Set Validation

- **Input Sanitization**: All character sets validated at construction time
- **Unicode Support**: Full Unicode character support with proper encoding
- **Memory Safety**: Immutable character lists prevent modification after initialization

## Performance Characteristics

### Time Complexity

- **Rule Validation**: O(n) where n = total characters in all sets
- **Generation**: O(m × k) where m = target length, k = average attempts
- **Shuffling**: O(m) where m = password length

### Space Complexity

- **Character Storage**: O(n) for base character sets
- **Working Memory**: O(m) for password construction
- **Rule Storage**: O(1) for rule information

### Optimization Strategies

1. **Early Validation**: Impossible rules rejected before generation attempts
2. **Efficient Character Pools**: Pre-computed available character lists
3. **Smart Retry Logic**: Learns from previous failures to adjust strategy
4. **Memory Reuse**: Minimal object allocation during generation

---

**Next**: [Common Scenarios Guide](../examples/common-scenarios.md)  
**Related**: [Error Handling Guide](../error-handling/security-exceptions.md)
