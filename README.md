# Password Generator

Generador de contraseñas seguro y configurable para aplicaciones Java.

## Descripción

Esta biblioteca permite generar contraseñas seguras siguiendo reglas personalizables. Ideal para aplicaciones que requieren autenticación robusta.

## Dependencias

- Java 17 o superior
- commons-math3
- guava
- jackson-databind

## Instrucciones de uso

### Maven
```xml
<dependency>
    <groupId>org.makechtec.xihucalli</groupId>
    <artifactId>password_generator</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle
```groovy
dependencies {
    implementation 'org.makechtec.xihucalli:password_generator:1.0.0'
}
```

### Gradle Kotlin DSL
```kotlin
dependencies {
    implementation("org.makechtec.xihucalli:password_generator:1.0.0")
}
```

## Ejemplo de uso

A continuación se muestran algunos ejemplos de cómo utilizar la biblioteca.

### Ejemplo 1: Generación de una contraseña simple

Este ejemplo genera una contraseña con una longitud entre 8 y 30 caracteres, incluyendo al menos 8 dígitos y 8 símbolos.

```java
import org.makechtec.xihucalli.password_generator.PasswordGenerator;

// Es necesario instanciar PasswordGenerator con las listas de caracteres.
// Este es un ejemplo de cómo se podría hacer.
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

### Ejemplo 2: Excluir caracteres específicos

En este caso, se genera una contraseña excluyendo ciertos dígitos, símbolos y letras.

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
System.out.println(password); // No contendrá '0', '1', '$', '%', 'a', 'F'
```

### Ejemplo 3: Forzar la inclusión de caracteres

Aquí se genera una contraseña de 8 caracteres que solo contiene letras, y debe incluir 'a' y 'F'.

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
System.out.println(password); // Contendrá 'a' y 'F'
```

## Changelog

- 1.0.0: Versión inicial con generación de contraseñas y reglas configurables.
