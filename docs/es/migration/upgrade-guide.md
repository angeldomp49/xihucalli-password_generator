# Guía de Migración y Actualización

Esta guía proporciona instrucciones detalladas para migrar entre versiones de la Biblioteca Generador de Contraseñas, incluyendo cambios disruptivos, nuevas características y estrategias de actualización recomendadas.

## Tabla de Contenidos

- [Historial de Versiones](#historial-de-versiones)
- [Migración de 1.2.x a 1.3.0](#migración-de-12x-a-130)
- [Cambios Disruptivos](#cambios-disruptivos)
- [Nuevas Características](#nuevas-características)
- [Estrategias de Actualización](#estrategias-de-actualización)

## Historial de Versiones

| Versión | Fecha de Lanzamiento | Cambios Clave | Compatibilidad |
|---------|---------------------|---------------|----------------|
| 1.3.0 | Diciembre 2024 | Validación de viabilidad matemática, soporte Unicode | Cambios disruptivos |
| 1.2.x | Octubre 2024 | Optimizaciones de rendimiento | Compatible hacia atrás |
| 1.1.x | Agosto 2024 | Lanzamiento estable inicial | N/A |

## Migración de 1.2.x a 1.3.0

### Resumen

La versión 1.3.0 introduce mejoras significativas en validación de reglas y seguridad, pero incluye cambios disruptivos que requieren actualizaciones de código.

### Cambios Críticos

#### 1. Validación de Reglas Mejorada

**Comportamiento Anterior (1.2.x)**:
```java
// Esto intentaría generación y potencialmente fallaría silenciosamente o después de muchos intentos
String impossibleRules = """
{
  "length": {"max": 5},
  "digits": {"min": 4},
  "symbols": {"min": 3}
}
""";
generator.generatePassword(impossibleRules); // Fallaría impredeciblemente
```

**Nuevo Comportamiento (1.3.0)**:
```java
// Ahora lanza SecurityException inmediatamente con mensaje de error claro
String impossibleRules = """
{
  "length": {"max": 5},
  "digits": {"min": 4},
  "symbols": {"min": 3}
}
""";
// Lanza: SecurityException("Las reglas de contraseña son matemáticamente imposibles de satisfacer")
```

**Acción de Migración**: Actualizar manejo de errores para capturar y manejar excepciones de validación de viabilidad.

#### 2. Requisitos de Conjunto de Caracteres Más Estrictos

**Comportamiento Anterior (1.2.x)**:
```java
// Permitía diversidad de caracteres insuficiente
PasswordGenerator generator = new PasswordGenerator("01", "!", "AB");
// A veces tendría éxito, a veces fallaría aleatoriamente
```

**Nuevo Comportamiento (1.3.0)**:
```java
// Fuerza diversidad mínima de caracteres para contraseñas > 3 caracteres
PasswordGenerator generator = new PasswordGenerator("01", "!", "AB");
String rules = """{"length": {"min": 10, "max": 15}}""";
// Lanza: SecurityException("Las reglas de contraseña son matemáticamente imposibles de satisfacer")
```

**Acción de Migración**: Asegurar que los conjuntos de caracteres tengan suficiente diversidad (≥4 caracteres) para contraseñas más largas.

### Proceso de Migración Paso a Paso

#### Paso 1: Actualizar Dependencias

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

#### Paso 2: Actualizar Manejo de Errores

**Antes (1.2.x)**:
```java
public String generateUserPassword(String rules) {
    try {
        return generator.generatePassword(rules);
    } catch (SecurityException e) {
        // Manejo de errores genérico
        return generateFallbackPassword();
    }
}
```

**Después (1.3.0)**:
```java
public String generateUserPassword(String rules) {
    try {
        return generator.generatePassword(rules);
    } catch (SecurityException e) {
        if (e.getMessage().contains("mathematically impossible")) {
            // Manejar reglas imposibles - ajustar reglas o usar respaldo
            return generateWithAdjustedRules(rules);
        } else if (e.getMessage().contains("Invalid password rules")) {
            // Manejar errores de validación - corregir formato de regla
            return generateWithValidatedRules(rules);
        } else {
            // Manejar otros errores
            return generateFallbackPassword();
        }
    }
}
```

#### Paso 3: Validar Configuraciones de Reglas Existentes

Crear una utilidad de validación para verificar reglas existentes:

```java
public class RuleMigrationValidator {
    
    public static ValidationResult validateRules(String rules) {
        try {
            // Probar análisis de reglas
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rulesNode = mapper.readTree(rules);
            
            // Verificar problemas potenciales
            List<String> warnings = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            
            validateLengthRequirements(rulesNode, warnings, errors);
            validateCharacterRequirements(rulesNode, warnings, errors);
            validateFeasibility(rulesNode, warnings, errors);
            
            return new ValidationResult(errors.isEmpty(), warnings, errors);
            
        } catch (Exception e) {
            return ValidationResult.error("Análisis JSON falló: " + e.getMessage());
        }
    }
}
```

## Cambios Disruptivos

### Cambios en API

| Tipo de Cambio | Antes | Después | Impacto |
|----------------|--------|---------|---------|
| Validación | Validación tardía durante generación | Validación temprana antes de generación | Excepciones lanzadas más temprano |
| Mensajes de Error | Mensajes genéricos | Mensajes específicos y accionables | Mejor manejo de errores posible |
| Conjuntos de Caracteres | Sin requisito de diversidad mínima | ≥4 caracteres para contraseñas largas | Puede requerir conjuntos de caracteres más grandes |

## Nuevas Características

### 1. Validación de Viabilidad Matemática

```java
// Nueva característica: Validación inmediata de reglas imposibles
String rules = """
{
  "length": {"max": 3},
  "digits": {"min": 5}
}
""";
// Lanza inmediatamente SecurityException con mensaje claro
```

### 2. Soporte Mejorado de Unicode

```java
// Soporte mejorado para caracteres Unicode
PasswordGenerator unicodeGenerator = new PasswordGenerator(
    "0123456789",
    "!@#$%^&*()αβγδε",  // Símbolos Unicode soportados
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
);
```

### 3. Algoritmo de Generación Optimizado

- Reducción del tiempo promedio de generación en 30%
- Gestión más eficiente de grupos de caracteres
- Patrones de uso de memoria mejorados

## Estrategias de Actualización

### Estrategia 1: Actualización Inmediata (Recomendada)

Mejor para aplicaciones con buena cobertura de pruebas y recursos de desarrollo.

```java
// 1. Actualizar dependencia
// 2. Ejecutar pruebas existentes para identificar problemas
// 3. Actualizar manejo de errores
// 4. Validar todas las configuraciones de reglas
// 5. Desplegar con pruebas integrales
```

### Estrategia 2: Migración Gradual

Para aplicaciones grandes con muchos puntos de generación de contraseñas.

```java
public class VersionedPasswordGenerator {
    
    private final PasswordGenerator v13Generator;
    private final boolean useV13;
    
    public String generatePassword(String rules) {
        if (useV13) {
            try {
                return v13Generator.generatePassword(rules);
            } catch (SecurityException e) {
                // Registrar problemas de migración
                logger.warn("Generación V1.3 falló, usando respaldo: {}", e.getMessage());
                return generateLegacyPassword(rules);
            }
        } else {
            return generateLegacyPassword(rules);
        }
    }
}
```

## Plan de Reversión

Si surgen problemas después de la migración:

1. **Reversión Inmediata**: Revertir a versión 1.2.x
2. **Ajuste de Reglas**: Modificar reglas problemáticas
3. **Re-migración Gradual**: Usar enfoque versionado

```java
// Configuración de reversión de emergencia
password.generator.version=1.2
password.generator.fallback.enabled=true
```

---

**Siguiente**: [Preguntas Frecuentes](../faq.md)  
**Relacionado**: [Guía de Manejo de Errores](../error-handling/security-exceptions.md)
