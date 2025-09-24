# Password Generator Library

## Example

Here are some examples of how to use the library.

### Example 1: Simple Password Generation

This example generates a password with a length between 8 and 30 characters, including at least 8 digits and 8 symbols.

```java
import org.makechtec.xihucalli.password_generator.PasswordGenerator;

// It is necessary to instantiate PasswordGenerator with the character lists.
// This is an example of how it could be done.
PasswordGenerator passwordGenerator = new PasswordGenerator(
    "0123456789",
    "!@#$%^&*()_+-=[]{}|;':,./<>?",
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
);

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
System.out.println(password);
```

### Example 2: Exclude Specific Characters

In this case, a password is generated excluding certain digits, symbols, and letters.

```java
var rules = """
        {
          "length": {
            "min": 8,
            "max": 30
          },
          "digits": {
            "min": 8,
            "max": 30,
            "exclude": [ 0, 1 ],
            "include": []
          },
          "symbols": {
            "min": 8,
            "max": 30,
            "exclude": [ "$", "%" ],
            "include": []
          },
          "letters": {
            "exclude": [ "a", "F" ],
            "include": []
          }
        }
        """;

var password = passwordGenerator.generatePassword(rules);
System.out.println(password); // Will not contain '0', '1', '$', '%', 'a', 'F'
```

### Example 3: Force Character Inclusion

Here, an 8-character password is generated that only contains letters, and must include 'a' and 'F'.

```java
var rules = """
        {
          "length": {
            "min": 8,
            "max": 8
          },
          "digits": {
            "min": 0,
            "max": 0
          },
          "symbols": {
            "min": 0,
            "max": 0
          },
          "letters": {
            "include": [ "a", "F" ]
          }
        }
        """;

var password = passwordGenerator.generatePassword(rules);
System.out.println(password); // Will contain 'a' and 'F'
```

## Changelog
