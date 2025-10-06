# Preguntas Frecuentes (FAQ)

Este documento responde preguntas comunes sobre la Biblioteca Generador de Contraseñas, proporcionando soluciones prácticas y explicaciones basadas en escenarios de uso del mundo real.

## Tabla de Contenidos

- [Preguntas Generales](#preguntas-generales)
- [Preguntas de Seguridad](#preguntas-de-seguridad)
- [Preguntas de Configuración](#preguntas-de-configuración)
- [Preguntas de Manejo de Errores](#preguntas-de-manejo-de-errores)
- [Preguntas de Rendimiento](#preguntas-de-rendimiento)
- [Preguntas de Integración](#preguntas-de-integración)

## Preguntas Generales

### P: ¿Qué hace diferente a este generador de contraseñas de otros?

**R**: Nuestra Biblioteca Generador de Contraseñas ofrece varias ventajas únicas:

- **Validación de Viabilidad Matemática**: A diferencia de los generadores básicos, validamos que tus reglas sean matemáticamente posibles antes de intentar la generación
- **Seguridad Criptográfica**: Usa `SecureRandom` e implementa mezcla Fisher-Yates segura
- **Características Empresariales**: Manejo integral de errores, registro detallado y validación exhaustiva
- **Configuración JSON**: Especificación de reglas flexible y declarativa en lugar de configuración procedimental

### P: ¿Cuáles son los requisitos mínimos del sistema?

**R**: 
- **Versión de Java**: Java 17 o superior
- **Memoria**: Uso mínimo de heap (< 10MB para uso típico)
- **Dependencias**: Jackson para procesamiento JSON (incluido automáticamente)
- **Sistema de Construcción**: Gradle o Maven soportados

### P: ¿Cómo empiezo con la generación básica de contraseñas?

**R**: Aquí está el ejemplo más simple posible:

```java
PasswordGenerator generator = new PasswordGenerator(
    "0123456789",
    "!@#$%^&*()",
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
);

String password = generator.generatePassword("{}"); // Usa valores por defecto
```

## Preguntas de Seguridad

### P: ¿Cómo asegura la biblioteca la seguridad criptográfica?

**R**: La biblioteca implementa múltiples capas de seguridad:

1. **SecureRandom**: Toda selección de caracteres usa `java.security.SecureRandom`
2. **Mezcla Fisher-Yates**: Algoritmo de mezcla criptográficamente seguro
3. **ThreadLocalRandom**: Usado para determinación de longitud dentro de límites seguros
4. **Sin Patrones Predecibles**: Algoritmo diseñado para prevenir reconocimiento de patrones
5. **Manejo Seguro de Memoria**: Arrays de caracteres son gestionados y limpiados apropiadamente

### P: ¿Pueden las contraseñas generadas ser predichas o sometidas a ingeniería inversa?

**R**: No. La biblioteca está diseñada para prevenir predicción:

- Usa generadores de números aleatorios criptográficamente seguros
- Sin exposición de semilla o patrones determinísticos
- Cada generación es independiente
- La mezcla ocurre después de la selección de caracteres para aleatoriedad adicional

### P: ¿Cómo genero contraseñas que cumplan requisitos de cumplimiento (PCI-DSS, HIPAA, etc.)?

**R**: Configura reglas específicas para cada estándar de cumplimiento:

```java
// Contraseña compatible con PCI-DSS
String pciRules = """
{
  "length": {"min": 12, "max": 16},
  "digits": {"min": 2, "max": 4},
  "symbols": {"min": 2, "max": 4},
  "symbols": {"exclude": ["<", ">", "&", "\"", "'"]},
  "letters": {"exclude": ["l", "I", "1", "0", "O"]}
}
""";
```

## Preguntas de Configuración

### P: ¿Por qué mi generación falla con error "matemáticamente imposible"?

**R**: Este error ocurre cuando tus reglas no pueden ser satisfechas. Causas comunes:

1. **Longitud Insuficiente**: `minDigits + minSymbols > maxLength`
2. **Conjuntos de Caracteres Vacíos**: Todos los caracteres de un tipo requerido están excluidos
3. **Requisitos Conflictivos**: Las reglas de inclusión y exclusión eliminan todas las opciones

**Solución**: Verificar las matemáticas de tus reglas:

```java
// Problema: Requiere 7 caracteres mínimo pero permite solo 5 máximo
{
  "length": {"max": 5},
  "digits": {"min": 4},
  "symbols": {"min": 3}
}

// Solución: Aumentar longitud máxima o reducir mínimos
{
  "length": {"max": 8},
  "digits": {"min": 4},
  "symbols": {"min": 3}
}
```

### P: ¿Cómo interactúan las reglas de inclusión y exclusión?

**R**: **Las reglas de inclusión siempre tienen precedencia sobre las reglas de exclusión**:

```java
// Aunque 2 está en la lista de exclusión, será incluido
{
  "digits": {
    "include": [1, 2, 3],
    "exclude": [2, 4, 5]
  }
}
// Resultado: Solo se usarán los dígitos 1, 2, 3
```

### P: ¿Puedo usar caracteres Unicode en las contraseñas?

**R**: Sí, soporte completo de Unicode está disponible:

```java
PasswordGenerator unicodeGenerator = new PasswordGenerator(
    "0123456789",
    "!@#$%^&*()αβγδε∑π",  // Símbolos Unicode
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
);
```

## Preguntas de Manejo de Errores

### P: ¿Cómo debo manejar SecurityException en código de producción?

**R**: Implementa manejo integral de errores con respuestas específicas:

```java
public String generatePasswordSafely(String rules) {
    try {
        return generator.generatePassword(rules);
        
    } catch (SecurityException e) {
        String message = e.getMessage();
        
        if (message.contains("mathematically impossible")) {
            // Registrar el problema y usar reglas de respaldo
            logger.warn("Reglas matemáticamente imposibles: {}", rules);
            return generator.generatePassword(getFallbackRules());
            
        } else if (message.contains("Invalid password rules")) {
            // Corregir las reglas y reintentar
            logger.error("Formato de reglas inválido: {}", rules);
            return generator.generatePassword(getValidatedRules(rules));
            
        } else {
            // Error desconocido, usar respaldo más simple
            logger.error("Error de generación desconocido: {}", e.getMessage());
            return generator.generatePassword("{}");
        }
    }
}
```

### P: ¿Qué significa "máximo de intentos excedido"?

**R**: El generador intentó 1000 veces crear una contraseña válida pero falló. Esto usualmente indica reglas demasiado restrictivas:

```java
// Demasiado restrictivo - puede causar timeout
{
  "length": {"min": 8, "max": 8},
  "digits": {"min": 4, "max": 4, "include": [1,2,3,4]},
  "symbols": {"min": 2, "max": 2, "include": ["@", "#"]},
  "letters": {"min": 2, "max": 2, "include": ["A", "B"]}
}

// Solución: Permitir más flexibilidad
{
  "length": {"min": 8, "max": 12},
  "digits": {"min": 2, "max": 6},
  "symbols": {"min": 1, "max": 4},
  "letters": {"exclude": ["l", "I", "O"]}
}
```

## Preguntas de Rendimiento

### P: ¿Qué tan rápida es la generación de contraseñas?

**R**: El rendimiento varía por complejidad:

- **Contraseñas simples** (8 chars, reglas básicas): < 1ms promedio
- **Contraseñas complejas** (16 chars, todas las reglas): < 5ms promedio
- **Generación por lotes** (1000 contraseñas): < 1 segundo total

### P: ¿Puedo generar contraseñas concurrentemente?

**R**: Sí, la biblioteca es thread-safe:

```java
// Seguro para uso concurrente
PasswordGenerator generator = new PasswordGenerator(/*...*/);

// Múltiples hilos pueden llamar esto simultáneamente de forma segura
CompletableFuture<String> future1 = CompletableFuture.supplyAsync(
    () -> generator.generatePassword(rules1)
);
CompletableFuture<String> future2 = CompletableFuture.supplyAsync(
    () -> generator.generatePassword(rules2)
);
```

## Preguntas de Integración

### P: ¿Cómo integro con Spring Boot?

**R**: Crea un bean de configuración:

```java
@Configuration
public class PasswordGeneratorConfig {
    
    @Bean
    public PasswordGenerator passwordGenerator(
            @Value("${password.chars.digits:0123456789}") String digits,
            @Value("${password.chars.symbols:!@#$%^&*()}") String symbols,
            @Value("${password.chars.letters:abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ}") String letters) {
        return new PasswordGenerator(digits, symbols, letters);
    }
}

@Service
public class UserService {
    
    @Autowired
    private PasswordGenerator passwordGenerator;
    
    public String generateUserPassword() {
        String rules = """
        {
          "length": {"min": 8, "max": 16},
          "digits": {"min": 1},
          "symbols": {"min": 1}
        }
        """;
        return passwordGenerator.generatePassword(rules);
    }
}
```

### P: ¿Puedo cargar reglas desde archivos de configuración?

**R**: Sí, almacena reglas en propiedades de aplicación:

```yaml
# application.yml
password:
  rules:
    user: |
      {
        "length": {"min": 8, "max": 16},
        "digits": {"min": 1},
        "symbols": {"min": 1}
      }
    admin: |
      {
        "length": {"min": 14, "max": 20},
        "digits": {"min": 3},
        "symbols": {"min": 3}
      }
```

---

**Documentación Relacionada**:
- [README](README.md) - Guía de inicio
- [Guía de Lógica de Negocio](business-logic/password-generation-rules.md) - Detalles del algoritmo
- [Guía de Manejo de Errores](error-handling/security-exceptions.md) - Gestión de excepciones
- [Ejemplos](examples/common-scenarios.md) - Casos de uso prácticos
