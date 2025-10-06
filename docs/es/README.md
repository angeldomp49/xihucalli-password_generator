# Biblioteca generadora de contraseñas

Una biblioteca Java segura para generar contraseñas con reglas personalizables mediante configuración JSON. Proporciona generación criptográficamente segura, validación extensa y configuración flexible basada en reglas.

## Tabla de contenidos

- Introducción
- Instalación y configuración
- Uso básico
- Formato de reglas JSON
- Casos de uso avanzados
- Manejo de errores
- Ejemplos
- Enlaces de documentación

## Introducción

Diseñada para aplicaciones empresariales que requieren generación segura y conforme de contraseñas con control detallado sobre la composición. Ofrece:

- Seguridad criptográfica
- Validación matemática de reglas
- Configuración flexible en JSON
- Preparada para entornos empresariales

### Casos de uso empresariales

- Sistemas de registro de usuarios
- Generación de claves API
- Contraseñas temporales
- Cumplimiento de normativas

## Instalación y configuración

### Requisitos del sistema

- Java 17 o superior
- Gradle para construir el proyecto

### Compilación del proyecto

```bash
./gradlew build
```

### Integración de la biblioteca

```java
import org.makechtec.xihucalli.password_generator.PasswordGenerator;

// Inicializar con conjuntos de caracteres personalizados
PasswordGenerator generator = new PasswordGenerator(
    "0123456789",                                           // dígitos
    "!@#$%^&*()_+-=[]{}|;':,./<>?",                        // símbolos
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" // letras
);
```

## Uso básico

### Ejemplo mínimo

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
    System.out.println("Contraseña generada: " + password);
} catch (SecurityException e) {
    System.err.println("La generación de contraseña falló: " + e.getMessage());
}
```

### Manejo de excepciones

La biblioteca lanza `SecurityException` para varios escenarios:
- Formato JSON inválido
- Reglas matemáticamente imposibles
- Generación fallida después del máximo de intentos (1000)

```java
try {
    String password = generator.generatePassword(rules);
} catch (SecurityException e) {
    // Manejar casos de error específicos basados en el mensaje
    if (e.getMessage().contains("mathematically impossible")) {
        // Ajustar reglas y reintentar
    }
}
```

## Formato de reglas JSON

Las reglas de generación de contraseñas se especifican usando un formato JSON estructurado:

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

### Propiedades de reglas

| Propiedad | Tipo | Descripción | Por defecto |
|-----------|------|-------------|-------------|
| `length.min` | entero | Longitud mínima de contraseña | 1 |
| `length.max` | entero | Longitud máxima de contraseña | Long.MAX_VALUE |
| `digits.min` | entero | Número mínimo de dígitos | 0 |
| `digits.max` | entero | Número máximo de dígitos | Long.MAX_VALUE |
| `digits.include` | array de enteros | Dígitos requeridos | [] |
| `digits.exclude` | array de enteros | Dígitos prohibidos | [] |
| `symbols.min` | entero | Número mínimo de símbolos | 0 |
| `symbols.max` | entero | Número máximo de símbolos | Long.MAX_VALUE |
| `symbols.include` | array de strings | Símbolos requeridos | [] |
| `symbols.exclude` | array de strings | Símbolos prohibidos | [] |
| `letters.include` | array de strings | Letras requeridas | [] |
| `letters.exclude` | array de strings | Letras prohibidas | [] |

## Casos de uso avanzados

### Política de seguridad corporativa

```java
// Contraseña de alta seguridad para cuentas administrativas
String corporateRules = """
{
  "length": {"min": 14, "max": 20},
  "digits": {"min": 3, "max": 5},
  "symbols": {"min": 2, "max": 4, "exclude": ["<", ">", "&"]},
  "letters": {"exclude": ["l", "I", "1", "0", "O"]}
}
""";
```

### Generación de claves API

```java
// Generar claves API con requisitos de formato específicos
String apiKeyRules = """
{
  "length": {"min": 32, "max": 32},
  "digits": {"min": 8},
  "letters": {"include": ["A", "B", "C", "D", "E", "F"]}
}
""";
```

### Generación de PIN

```java
PasswordGenerator pinGenerator = new PasswordGenerator("0123456789", "", "");
String pinRules = """
{
  "length": {"min": 4, "max": 6},
  "digits": {"min": 4, "exclude": [0]}
}
""";
```

## Manejo de errores

Escenarios comunes de error y sus soluciones:

### Configuración de reglas inválida
- **Error**: "Configuración de reglas de contraseña inválida"
- **Causa**: minLength > maxLength, valores negativos
- **Solución**: Validar restricciones de reglas antes de la generación

### Reglas matemáticamente imposibles
- **Error**: "Las reglas de contraseña son matemáticamente imposibles de satisfacer"
- **Causa**: minDigits + minSymbols > maxLength
- **Solución**: Ajustar requisitos mínimos o aumentar longitud máxima

### Fallo de generación
- **Error**: "No se puede generar contraseña que cumpla requisitos de seguridad después del máximo de intentos"
- **Causa**: Reglas muy restrictivas causando fallos repetidos
- **Solución**: Relajar algunas restricciones o verificar disponibilidad del conjunto de caracteres

## Ejemplos

### Ejemplo 1: Contraseña básica de 8 caracteres

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
// Ejemplo de resultado: "aB3#xY9z"
```

### Ejemplo 2: Contraseña de alta seguridad

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
// Ejemplo de resultado: "A7B#2C@9x$4mN5pQ"
```

### Ejemplo 3: PIN numérico

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
// Ejemplo de resultado: "4729"
```

## Enlaces de documentación

- [Guía de lógica de negocio](business-logic/password-generation-rules.md)
- [Escenarios comunes](examples/common-scenarios.md)
- [Guía de manejo de errores](error-handling/security-exceptions.md)
- [Matriz de cobertura de pruebas](testing/test-coverage-matrix.md)
- [Guía de migración](migration/upgrade-guide.md)
- [Preguntas frecuentes](faq.md)

---

**Versión**: 1.3.0  
**Última actualización**: Diciembre 2024  
**Licencia**: [Información de licencia]
