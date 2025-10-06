# Excepciones de Seguridad - Guía de Manejo de Errores

Esta guía proporciona cobertura integral de todos los escenarios de error en la Biblioteca Generador de Contraseñas, incluyendo explicaciones detalladas, causas y estrategias de resolución.

## Tabla de Contenidos

- [Resumen de Excepciones](#resumen-de-excepciones)
- [Errores de Validación de Reglas](#errores-de-validación-de-reglas)
- [Errores de Imposibilidad Matemática](#errores-de-imposibilidad-matemática)
- [Errores de Fallo de Generación](#errores-de-fallo-de-generación)
- [Errores de Procesamiento JSON](#errores-de-procesamiento-json)
- [Mejores Prácticas de Manejo de Errores](#mejores-prácticas-de-manejo-de-errores)

## Resumen de Excepciones

La Biblioteca Generador de Contraseñas utiliza `SecurityException` para todas las condiciones de error para mantener consistencia y manejo de errores enfocado en seguridad. Todas las excepciones incluyen mensajes descriptivos para ayudar en la depuración y resolución.

### Patrón Común de Excepción

```java
try {
    String password = generator.generatePassword(rules);
} catch (SecurityException e) {
    // Analizar mensaje de error para manejo específico
    String message = e.getMessage();
    
    if (message.contains("mathematically impossible")) {
        // Manejar reglas imposibles
    } else if (message.contains("Invalid password rules")) {
        // Manejar errores de validación
    } else if (message.contains("maximum attempts")) {
        // Manejar fallos de generación
    }
}
```

## Errores de Validación de Reglas

### Configuración de Longitud Inválida

**Mensaje de Error**: `"Configuración de reglas de contraseña inválida"`

**Escenarios**:
1. Longitud mínima mayor que longitud máxima
2. Valores de longitud cero o negativos
3. Requisitos mínimos que exceden restricciones máximas

**Ejemplos**:

```java
// Escenario 1: Rango de longitud inválido
String invalidRules1 = """
{
  "length": {"min": 10, "max": 8}
}
""";
// Lanza: SecurityException("Configuración de reglas de contraseña inválida")

// Escenario 2: Valores negativos
String invalidRules2 = """
{
  "length": {"min": -1, "max": 10}
}
""";
// Lanza: SecurityException("Configuración de reglas de contraseña inválida")
```

**Estrategias de Resolución**:

```java
public class RuleValidator {
    
    public static void validateRules(JsonNode rules) {
        if (rules.has("length")) {
            long min = rules.get("length").get("min").asLong(1);
            long max = rules.get("length").get("max").asLong(Long.MAX_VALUE);
            
            if (min > max) {
                throw new IllegalArgumentException("La longitud mínima no puede exceder la longitud máxima");
            }
            if (min <= 0) {
                throw new IllegalArgumentException("La longitud debe ser positiva");
            }
        }
    }
}
```

## Errores de Imposibilidad Matemática

### Longitud Insuficiente para Requisitos

**Mensaje de Error**: `"Las reglas de contraseña son matemáticamente imposibles de satisfacer"`

**Causa**: La suma de caracteres mínimos requeridos excede la longitud máxima permitida.

**Ejemplo**:

```java
String impossibleRules = """
{
  "length": {"max": 5},
  "digits": {"min": 4},
  "symbols": {"min": 3}
}
""";
// Requiere 7 caracteres mínimo pero permite solo 5 máximo
```

**Resolución**:

```java
public class RuleAdjuster {
    
    public static String adjustImpossibleRules(String originalRules) {
        // Analizar y ajustar reglas
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rules = mapper.readTree(originalRules);
        
        long maxLength = rules.path("length").path("max").asLong(Long.MAX_VALUE);
        long minDigits = rules.path("digits").path("min").asLong(0);
        long minSymbols = rules.path("symbols").path("min").asLong(0);
        
        long requiredLength = minDigits + minSymbols;
        
        if (requiredLength > maxLength) {
            // Opción 1: Aumentar longitud máxima
            maxLength = requiredLength + 2;
            
            // Reconstruir reglas con ajustes
            return buildAdjustedRules(maxLength, minDigits, minSymbols);
        }
        
        return originalRules;
    }
}
```

### Conjuntos de Caracteres Vacíos

**Mensaje de Error**: `"Las reglas de contraseña son matemáticamente imposibles de satisfacer"`

**Causa**: Todos los caracteres de un tipo requerido han sido excluidos o el conjunto de caracteres está vacío.

**Ejemplo**:

```java
String emptySetRules = """
{
  "digits": {
    "min": 1,
    "exclude": [0,1,2,3,4,5,6,7,8,9]
  }
}
""";
// Requiere dígitos pero excluye todos los dígitos disponibles
```

## Errores de Fallo de Generación

### Máximo de Intentos Excedido

**Mensaje de Error**: `"No se puede generar contraseña que cumpla requisitos de seguridad después del máximo de intentos"`

**Causa**: El generador falló en crear una contraseña válida después de 1000 intentos, usualmente debido a reglas demasiado restrictivas.

**Escenarios Comunes**:
1. Combinaciones muy específicas de include/exclude
2. Requisitos conflictivos que rara vez se alinean
3. Casos extremos en combinaciones de reglas

**Ejemplo**:

```java
String restrictiveRules = """
{
  "length": {"min": 8, "max": 8},
  "digits": {"min": 4, "max": 4, "include": [1,2,3,4]},
  "symbols": {"min": 2, "max": 2, "include": ["@", "#"]},
  "letters": {"min": 2, "max": 2, "include": ["A", "B"]}
}
""";
// Requisitos muy específicos que pueden ser difíciles de satisfacer aleatoriamente
```

**Estrategias de Resolución**:

```java
public class GenerationOptimizer {
    
    public static String generateWithRetryStrategy(PasswordGenerator generator, String rules) {
        String[] fallbackStrategies = {
            rules,                    // Reglas originales
            relaxMinimums(rules),     // Reducir requisitos mínimos
            increaseLength(rules),    // Permitir contraseñas más largas
            expandCharacterSet(rules) // Permitir más opciones de caracteres
        };
        
        for (String strategy : fallbackStrategies) {
            try {
                return generator.generatePassword(strategy);
            } catch (SecurityException e) {
                if (!e.getMessage().contains("maximum attempts")) {
                    throw e; // Re-lanzar errores que no son de generación
                }
                // Continuar a la siguiente estrategia
            }
        }
        
        throw new SecurityException("Todas las estrategias de generación fallaron");
    }
}
```

## Errores de Procesamiento JSON

### JSON Malformado

**Mensaje de Error**: `"Formato de reglas de contraseña inválido"`

**Causa**: El análisis JSON falla debido a errores de sintaxis.

**Ejemplos**:

```java
// Llave de cierre faltante
String malformedJson1 = """
{
  "length": {"min": 8, "max": 16"
}
""";

// Sintaxis inválida
String malformedJson2 = """
{
  length: {min: 8, max: 16}
}
""";
```

**Resolución**:

```java
public class JsonValidator {
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    public static void validateJsonSyntax(String json) {
        try {
            MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Sintaxis JSON inválida: " + e.getMessage(), e);
        }
    }
}
```

## Mejores Prácticas de Manejo de Errores

### Manejo Integral de Errores

```java
public class RobustPasswordService {
    
    private final PasswordGenerator generator;
    
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
            return GenerationResult.failure("REGLAS_IMPOSIBLES", 
                "Las reglas de contraseña son matemáticamente imposibles. Por favor ajuste los requisitos mínimos o aumente la longitud máxima.", 
                suggestRuleAdjustments(originalRules));
                
        } else if (message.contains("Invalid password rules")) {
            return GenerationResult.failure("REGLAS_INVALIDAS", 
                "Las reglas de contraseña contienen configuraciones inválidas. Por favor verifique valores min/max y tipos de datos.", 
                validateAndSuggestFixes(originalRules));
                
        } else {
            return GenerationResult.failure("ERROR_DESCONOCIDO", 
                "Ocurrió un error inesperado: " + message, 
                null);
        }
    }
}
```

---

**Siguiente**: [Matriz de Cobertura de Pruebas](../testing/test-coverage-matrix.md)  
**Relacionado**: [Guía de Lógica de Negocio](../business-logic/password-generation-rules.md)
