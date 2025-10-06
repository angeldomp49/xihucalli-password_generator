# SPEC: Update Documentation


Status: draft
Owner: @angeldomp49
Source Model: Claude Opus 4.1 (Ask Mode)
Last Sync: 2024-11-26


## Contexto del Proyecto

**Nombre del Proyecto**: Password Generator  
**Descripción**: Biblioteca Java para la generación segura de contraseñas con reglas personalizables a través de configuración JSON.

## Estado Actual de la Documentación

### Estructura Documentada
```
docs/
├── advices/
│   └── concordion_usage.md
├── artifacts/
│   ├── specs/
│   │   ├── SPEC-conflictive_rules_resolution.md
│   │   └── SPEC-fit_behavior_to_test_matrix.md
│   ├── stories/
│   │   ├── STORY-conflictive_rules_resolution.md
│   │   ├── STORY-first_matrix_test.md
│   │   ├── STORY-fit_behavior_to_test_matrix.md
│   │   └── STORY-update_documentation.md
│   └── tests/
│       └── MATRIX-TEXT-first.md
├── en/
│   └── README.md
├── external-references/
│   ├── ioc_container_docs.md
│   └── json_tree_docs.md
└── translations/
    ├── es/
    │   └── README.md
    └── fr/
        └── README.md
```

### Análisis del Código Actual

La clase `PasswordGenerator` implementa las siguientes características principales:

1. **Generación Segura**: Utiliza `SecureRandom` y `ThreadLocalRandom` para generación criptográficamente segura
2. **Validación de Reglas**: Múltiples niveles de validación para garantizar la viabilidad de las reglas
3. **Configuración JSON**: Las reglas se especifican mediante un formato JSON estructurado
4. **Reintentos Inteligentes**: Hasta 1000 intentos para generar una contraseña válida
5. **Conjuntos de Caracteres Personalizables**: Dígitos, símbolos y letras configurables

## Requisitos de Documentación

### 1. Documentación Principal (README.md)

#### Contenido Requerido

**Sección: Introducción**
- Explicación del propósito del generador de contraseñas
- Casos de uso empresariales principales
- Ventajas sobre otras soluciones

**Sección: Instalación y Configuración**
- Requisitos del sistema (Java 17+)
- Proceso de construcción con Gradle
- Configuración inicial del generador

**Sección: Uso Básico**
- Ejemplo mínimo de generación de contraseña
- Explicación del formato JSON de reglas
- Manejo de excepciones SecurityException

**Sección: Formato de Reglas JSON**
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
    "include": ["a", "b", "c"],
    "exclude": ["x", "y", "z"]
  }
}
```

**Sección: Casos de Uso Avanzados**
- Generación de contraseñas para diferentes niveles de seguridad
- Integración con sistemas de autenticación
- Personalización de conjuntos de caracteres

### 2. Guía de Lógica de Negocio

#### Documento: `docs/business-logic/password-generation-rules.md`

**Contenido**:

1. **Jerarquía de Reglas**
    - Prioridad de inclusiones sobre exclusiones
    - Resolución de conflictos entre reglas mínimas y máximas
    - Validación de viabilidad matemática

2. **Algoritmo de Generación**
   ```
   1. Validar entrada JSON y reglas
   2. Determinar longitud objetivo (aleatoria segura entre min y max)
   3. Añadir caracteres obligatorios (included)
   4. Cumplir requisitos mínimos (min digits, min symbols)
   5. Completar hasta longitud objetivo con caracteres disponibles
   6. Mezclar de forma segura (Fisher-Yates)
   7. Validar resultado contra todas las reglas
   8. Reintentar si es necesario (máximo 1000 intentos)
   ```

3. **Escenarios de Conflicto**
    - Reglas matemáticamente imposibles
    - Conjuntos de caracteres insuficientes
    - Requisitos mínimos que exceden la longitud máxima

### 3. Ejemplos Prácticos

#### Documento: `docs/examples/common-scenarios.md`

**Ejemplo 1: Contraseña Básica de 8 Caracteres**
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
// Resultado ejemplo: "aB3#xY9z"
```

**Ejemplo 2: Contraseña de Alta Seguridad**
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
// Resultado ejemplo: "A7B#2C@9x$4mN5pQ"
```

**Ejemplo 3: Contraseña PIN Numérico**
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
// Resultado ejemplo: "4729"
```

### 4. Guía de Manejo de Errores

#### Documento: `docs/error-handling/security-exceptions.md`

**Escenarios de Error**:

1. **Reglas Inválidas**
    - `minLength > maxLength`
    - Valores negativos
    - JSON malformado

2. **Reglas Imposibles**
    - `minDigits + minSymbols > maxLength`
    - Todos los caracteres excluidos
    - Caracteres incluidos que no existen en el conjunto

3. **Fallo de Generación**
    - Imposibilidad después de 1000 intentos
    - Conjuntos de caracteres vacíos

### 5. Matriz de Pruebas Documentada

#### Documento: `docs/testing/test-coverage-matrix.md`

| Escenario | Entrada | Salida Esperada | Validación |
|-----------|---------|-----------------|------------|
| Contraseña mínima | `{"length":{"min":1,"max":1}}` | 1 carácter | Longitud = 1 |
| Solo dígitos | `{"digits":{"min":8},"length":{"min":8,"max":8}}` | 8 dígitos | Todo dígitos |
| Inclusión obligatoria | `{"digits":{"include":[1,2,3]}}` | Contiene 1,2,3 | Verificar presencia |
| Exclusión estricta | `{"symbols":{"exclude":["&","%"]}}` | Sin & ni % | Verificar ausencia |
| Conflicto imposible | `{"length":{"max":2},"digits":{"min":5}}` | SecurityException | Excepción lanzada |

### 6. Guía de Migración

#### Documento: `docs/migration/upgrade-guide.md`

**De versión 1.2.x a 1.3.0**:
- Nueva validación de viabilidad matemática
- Soporte mejorado para caracteres Unicode
- Optimización del algoritmo de mezcla

### 7. Preguntas Frecuentes

#### Documento: `docs/faq.md`

**P: ¿Por qué mi generación falla con SecurityException?**
R: Verifica que tus reglas sean matemáticamente posibles. Por ejemplo, no puedes pedir 10 dígitos en una contraseña de máximo 5 caracteres.

**P: ¿Cómo garantiza la seguridad el generador?**
R: Utiliza `SecureRandom` para la selección de caracteres y Fisher-Yates para el mezclado, ambos criptográficamente seguros.

**P: ¿Puedo usar caracteres especiales Unicode?**
R: Sí, puedes incluir cualquier carácter Unicode en tus conjuntos de caracteres personalizados.

## Requisitos de Traducción

Todos los documentos deben tener versiones en:
- **Inglés** (`docs/en/`)
- **Español** (`docs/es/`)
- **Francés** (`docs/fr/`)

## Estructura Final Esperada

```
docs/
├── en/
│   ├── README.md
│   ├── business-logic/
│   │   └── password-generation-rules.md
│   ├── examples/
│   │   └── common-scenarios.md
│   ├── error-handling/
│   │   └── security-exceptions.md
│   ├── testing/
│   │   └── test-coverage-matrix.md
│   ├── migration/
│   │   └── upgrade-guide.md
│   └── faq.md
├── es/
│   └── [misma estructura]
└── fr/
    └── [misma estructura]
```

## Criterios de Aceptación

1. **Completitud**: Toda funcionalidad del código debe estar documentada
2. **Claridad**: Ejemplos ejecutables para cada caso de uso
3. **Precisión**: Descripción exacta del comportamiento actual (v1.3.0)
4. **Multilingüe**: Traducciones completas y precisas
5. **Navegabilidad**: Enlaces cruzados entre documentos relacionados
6. **Actualización**: Referencias a las últimas características implementadas

## Notas de Implementación

- Utilizar formato Markdown estándar
- Incluir bloques de código con sintaxis highlighting
- Añadir diagramas donde sea necesario (usando Mermaid)
- Mantener consistencia en terminología entre idiomas
- Verificar todos los ejemplos de código antes de documentar
- Actualizar la documentación actual con la nueva información