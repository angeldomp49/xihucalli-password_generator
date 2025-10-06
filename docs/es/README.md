# Biblioteca Generador de Contraseñas

Una biblioteca Java segura para generar contraseñas con reglas personalizables a través de configuración JSON. Esta biblioteca proporciona generación de contraseñas criptográficamente segura con validación exhaustiva y configuración flexible basada en reglas.

## Tabla de Contenidos

- [Introducción](#introducción)
- [Instalación y Configuración](#instalación-y-configuración)
- [Uso Básico](#uso-básico)
- [Formato de Reglas JSON](#formato-de-reglas-json)
- [Casos de Uso Avanzados](#casos-de-uso-avanzados)
- [Manejo de Errores](#manejo-de-errores)
- [Ejemplos](#ejemplos)
- [Enlaces de Documentación](#enlaces-de-documentación)

## Introducción

La Biblioteca Generador de Contraseñas está diseñada para aplicaciones empresariales que requieren generación de contraseñas segura y compatible con control granular sobre la composición de contraseñas. A diferencia de los generadores de contraseñas aleatorios simples, esta biblioteca proporciona:

- **Seguridad Criptográfica**: Utiliza `SecureRandom` y `ThreadLocalRandom` para generación criptográficamente segura
- **Validación de Reglas**: La validación de viabilidad matemática previene configuraciones de reglas imposibles
- **Configuración Flexible**: Las reglas basadas en JSON permiten requisitos de contraseña complejos
- **Listo para Empresas**: Maneja casos extremos y proporciona informes de errores completos

### Casos de Uso Empresariales

- **Sistemas de Registro de Usuarios**: Generar contraseñas seguras que cumplan políticas organizacionales
- **Generación de Claves API**: Crear tokens de autenticación fuertes con requisitos de caracteres específicos
- **Sistemas de Contraseñas Temporales**: Generar contraseñas para flujos de restablecimiento de contraseña
- **Requisitos de Cumplimiento**: Cumplir estándares de seguridad específicos (PCI-DSS, SOX, etc.)

## Instalación y Configuración

### Requisitos del Sistema

- Java 17 o superior
- Gradle para construir el proyecto

### Construcción del Proyecto

```bash
./gradlew build
```

### Integración de la Biblioteca

```java
import org.makechtec.xihucalli.password_generator.PasswordGenerator;

// Inicializar con conjuntos de caracteres personalizados
PasswordGenerator generator = new PasswordGenerator(
    "0123456789",                                           // dígitos
    "!@#$%^&*()_+-=[]{}|;':,./<>?",                        // símbolos
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" // letras
);
```

## Uso Básico

### Ejemplo Mínimo

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

### Manejo de Excepciones

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

## Formato de Reglas JSON

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

### Propiedades de Reglas

| Propiedad | Tipo | Descripción | Por Defecto |
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

## Casos de Uso Avanzados

### Política de Seguridad Corporativa

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

### Generación de Claves API

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

## Manejo de Errores

Escenarios comunes de error y sus soluciones:

### Configuración de Reglas Inválida
- **Error**: "Configuración de reglas de contraseña inválida"
- **Causa**: minLength > maxLength, valores negativos
- **Solución**: Validar restricciones de reglas antes de la generación

### Reglas Matemáticamente Imposibles
- **Error**: "Las reglas de contraseña son matemáticamente imposibles de satisfacer"
- **Causa**: minDigits + minSymbols > maxLength
- **Solución**: Ajustar requisitos mínimos o aumentar longitud máxima

### Fallo de Generación
- **Error**: "No se puede generar contraseña que cumpla requisitos de seguridad después del máximo de intentos"
- **Causa**: Reglas muy restrictivas causando fallos repetidos
- **Solución**: Relajar algunas restricciones o verificar disponibilidad del conjunto de caracteres

## Ejemplos

### Ejemplo 1: Contraseña Básica de 8 Caracteres

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

### Ejemplo 2: Contraseña de Alta Seguridad

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

### Ejemplo 3: PIN Numérico

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

## Enlaces de Documentación

- [Guía de Lógica de Negocio](business-logic/password-generation-rules.md)
- [Escenarios Comunes](examples/common-scenarios.md)
- [Guía de Manejo de Errores](error-handling/security-exceptions.md)
- [Matriz de Cobertura de Pruebas](testing/test-coverage-matrix.md)
- [Guía de Migración](migration/upgrade-guide.md)
- [Preguntas Frecuentes](faq.md)

---

**Versión**: 1.3.0  
**Última Actualización**: Diciembre 2024  
**Licencia**: [Información de licencia]
