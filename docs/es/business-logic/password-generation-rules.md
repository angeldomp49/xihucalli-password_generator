# Reglas de Generación de Contraseñas - Guía de Lógica de Negocio

Esta guía explica la lógica de negocio central detrás de la Biblioteca Generador de Contraseñas, incluyendo jerarquía de reglas, algoritmo de generación y estrategias de resolución de conflictos.

## Jerarquía de Reglas y Sistema de Prioridades

El generador de contraseñas sigue una jerarquía estricta al procesar reglas para asegurar un comportamiento predecible y seguro:

### 1. Las Reglas de Inclusión Tienen Precedencia

Cuando se especifican tanto listas de `include` como de `exclude` para el mismo tipo de carácter, las reglas de inclusión siempre tienen prioridad:

```json
{
  "digits": {
    "include": [1, 2, 3],
    "exclude": [2, 4, 5]
  }
}
```

En este caso, solo se considerarán los dígitos `1`, `2` y `3`, aunque `2` aparezca en la lista de exclusión.

### 2. Los Requisitos Mínimos Anulan los Máximos

El generador asegura que se cumplan los requisitos mínimos antes de considerar las restricciones máximas:

```json
{
  "length": {"min": 10, "max": 12},
  "digits": {"min": 8, "max": 3}
}
```

Esta configuración sería marcada como matemáticamente imposible ya que 8 dígitos mínimos no pueden caber dentro de la restricción máxima de 3.

### 3. Validación de Viabilidad Matemática

Antes de que comience la generación, el sistema valida que las reglas sean matemáticamente posibles:

- `minDigits + minSymbols ≤ maxLength`
- Los conjuntos de caracteres disponibles deben contener caracteres suficientes
- Las listas de inclusión deben referenciar caracteres que existan en los conjuntos de caracteres base

## Algoritmo de Generación de Contraseñas

El proceso de generación sigue un algoritmo determinístico de 8 pasos diseñado para seguridad y cumplimiento:

### Paso 1: Validación de Entrada e Hidratación de Reglas

```
1. Analizar entrada JSON usando Jackson ObjectMapper
2. Validar estructura JSON y tipos de datos
3. Crear objeto PasswordRulesInformation
4. Realizar validación inicial de reglas (valores positivos, min ≤ max)
```

### Paso 2: Verificación de Viabilidad Matemática

```
1. Calcular caracteres mínimos requeridos: minDigits + minSymbols
2. Verificar mínimo ≤ maxLength
3. Verificar disponibilidad de conjunto de caracteres para cada tipo
4. Asegurar diversidad de caracteres suficiente (≥4 caracteres disponibles para longitudes >3)
```

### Paso 3: Determinación Segura de Longitud

```java
// Usa ThreadLocalRandom para seguridad criptográfica
int targetLength = ThreadLocalRandom.current().nextInt(minLength, maxLength + 1);
```

### Paso 4: Adición de Caracteres Obligatorios

```
1. Agregar todos los caracteres de las listas de inclusión primero
2. Estos caracteres son obligatorios y no pueden ser removidos
3. Rastrear conteos actuales para cada tipo de carácter
```

### Paso 5: Cumplimiento de Requisitos Mínimos

```
1. Calcular dígitos restantes necesarios: max(0, minDigits - currentDigits)
2. Calcular símbolos restantes necesarios: max(0, minSymbols - currentSymbols)
3. Agregar caracteres aleatorios de grupos disponibles para cumplir mínimos
```

### Paso 6: Llenado de Posiciones Restantes

```
1. Crear grupo combinado de caracteres de todos los tipos de caracteres disponibles
2. Llenar posiciones restantes con selecciones aleatorias
3. Asegurar que se alcance la longitud objetivo
```

### Paso 7: Mezcla Segura (Algoritmo Fisher-Yates)

```java
// Mezcla criptográficamente segura usando SecureRandom
for (int i = password.size() - 1; i > 0; i--) {
    int randomIndex = secureRandom.nextInt(i + 1);
    Collections.swap(password, i, randomIndex);
}
```

### Paso 8: Validación Final y Lógica de Reintento

```
1. Validar contraseña generada contra todas las reglas
2. Si la validación falla, reintentar (hasta 1000 intentos)
3. Lanzar SecurityException si se excede el máximo de intentos
```

## Estrategias de Resolución de Conflictos

### Escenario 1: Requisitos de Longitud Imposibles

**Problema**: `minDigits + minSymbols > maxLength`

```json
{
  "length": {"max": 5},
  "digits": {"min": 4},
  "symbols": {"min": 3}
}
```

**Resolución**: Rechazo inmediato con `SecurityException` durante la verificación de viabilidad.

### Escenario 2: Conjunto de Caracteres Insuficiente

**Problema**: Caracteres requeridos no disponibles en conjuntos de caracteres base

```json
{
  "digits": {"include": [9]},
  // Pero PasswordGenerator inicializado con digitsList = "01234567"
}
```

**Resolución**: Las listas de inclusión se filtran para contener solo caracteres presentes en conjuntos base.

### Escenario 3: Exclusiones Demasiado Restrictivas

**Problema**: Todos los caracteres excluidos de un tipo requerido

```json
{
  "digits": {"min": 1, "exclude": [0,1,2,3,4,5,6,7,8,9]}
}
```

**Resolución**: La verificación de viabilidad detecta conjuntos de caracteres disponibles vacíos y rechaza la configuración.

---

**Siguiente**: [Guía de Escenarios Comunes](../examples/common-scenarios.md)  
**Relacionado**: [Guía de Manejo de Errores](../error-handling/security-exceptions.md)
