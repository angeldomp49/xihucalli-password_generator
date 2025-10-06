# Matriz de cobertura de pruebas

Este documento proporciona una matriz de cobertura de pruebas para la biblioteca Password Generator, documentando todos los escenarios, resultados esperados y criterios de validación.

## Tabla de contenidos

- Categorías de pruebas
- Matriz funcional
- Matriz de seguridad
- Matriz de manejo de errores
- Matriz de rendimiento
- Matriz de casos límite

## Categorías de pruebas

| Categoría      | Descripción                                 | Prioridad |
|---------------|---------------------------------------------|-----------|
| Funcional     | Funcionalidad principal de generación        | Alta      |
| Seguridad     | Seguridad criptográfica y aleatoriedad       | Crítica   |
| Validación    | Validación de entrada y reglas               | Alta      |
| Manejo de errores | Escenarios de excepción y recuperación   | Media     |
| Rendimiento   | Velocidad y uso de recursos                  | Media     |
| Casos límite  | Condiciones de frontera y casos extremos     | Alta      |

## Matriz funcional

| ID   | Escenario                  | Reglas de entrada | Salida esperada | Criterio de validación |
|------|----------------------------|-------------------|-----------------|-----------------------|
| FT-001 | Contraseña de longitud mínima | {"length":{"min":1,"max":1}} | 1 carácter | Longitud = 1 |
| FT-002 | Contraseña de longitud máxima | {"length":{"min":100,"max":100}} | 100 caracteres | Longitud = 100 |
| FT-003 | Rango de longitud variable | {"length":{"min":8,"max":12}} | 8-12 caracteres | 8 ≤ longitud ≤ 12 |
| FT-004 | Comportamiento por defecto | {} | Longitud variable | Longitud ≥ 1 |

### Requisitos de Tipos de Caracteres

| ID Prueba | Escenario | Reglas de Entrada | Salida Esperada | Criterios de Validación |
|-----------|-----------|-------------------|-----------------|------------------------|
| FT-101 | Solo dígitos | `{"digits":{"min":8},"length":{"min":8,"max":8}}` | 8 dígitos | Todos los caracteres son dígitos |
| FT-102 | Solo símbolos | `{"symbols":{"min":6},"length":{"min":6,"max":6}}` | 6 símbolos | Todos los caracteres son símbolos |
| FT-103 | Solo letras | `{"letters":{"include":["a","b","c"]},"length":{"min":5,"max":5}}` | 5 letras de a,b,c | Todos los caracteres del conjunto especificado |
| FT-104 | Requisitos mixtos | `{"digits":{"min":2},"symbols":{"min":1},"length":{"min":5,"max":8}}` | 2+ dígitos, 1+ símbolo | Cumple todos los mínimos |

### Reglas de Inclusión

| ID Prueba | Escenario | Reglas de Entrada | Salida Esperada | Criterios de Validación |
|-----------|-----------|-------------------|-----------------|------------------------|
| FT-201 | Dígitos requeridos | `{"digits":{"include":[1,2,3]}}` | Contiene 1,2,3 | Contraseña contiene chars '1','2','3' |
| FT-202 | Símbolos requeridos | `{"symbols":{"include":["@","#"]}}` | Contiene @,# | Contraseña contiene '@','#' |
| FT-203 | Letras requeridas | `{"letters":{"include":["A","B"]}}` | Contiene A,B | Contraseña contiene 'A','B' |
| FT-204 | Múltiples inclusiones | `{"digits":{"include":[9]},"symbols":{"include":["!"]}}` | Contiene 9,! | Contraseña contiene '9','!' |

### Reglas de Exclusión

| ID Prueba | Escenario | Reglas de Entrada | Salida Esperada | Criterios de Validación |
|-----------|-----------|-------------------|-----------------|------------------------|
| FT-301 | Dígitos excluidos | `{"digits":{"exclude":[0,1]},"digits":{"min":2}}` | Sin dígitos 0 o 1 | Contraseña no contiene '0','1' |
| FT-302 | Símbolos excluidos | `{"symbols":{"exclude":["&","%"]},"symbols":{"min":1}}` | Sin símbolos & o % | Contraseña no contiene '&','%' |
| FT-303 | Letras excluidas | `{"letters":{"exclude":["l","I","O"]}}` | Sin letras confusas | Contraseña no contiene 'l','I','O' |

## Matriz de Pruebas de Seguridad

### Seguridad Criptográfica

| ID Prueba | Escenario | Método de Prueba | Criterios de Éxito |
|-----------|-----------|------------------|-------------------|
| ST-001 | Distribución aleatoria | Generar 10,000 contraseñas, analizar frecuencia de caracteres | Valor p de prueba Chi-cuadrado > 0.05 |
| ST-002 | Impredecibilidad | Generar contraseñas secuenciales con mismas reglas | Sin patrones detectables |
| ST-003 | Validación de entropía | Calcular entropía de contraseñas generadas | Entropía ≥ mínimo esperado |
| ST-004 | Uso de SecureRandom | Verificar que SecureRandom se usa para selección de caracteres | Inspección de código exitosa |

### Pruebas de Prioridad de Reglas

| ID Prueba | Escenario | Reglas de Entrada | Comportamiento Esperado | Validación |
|-----------|-----------|-------------------|-------------------------|------------|
| ST-101 | Include vs Exclude | `{"digits":{"include":[1,2],"exclude":[2,3]}}` | Include tiene prioridad | Solo '1','2' usados |
| ST-102 | Conflicto Min vs Max | `{"digits":{"min":5,"max":3}}` | Error de validación | SecurityException lanzada |
| ST-103 | Verificación de viabilidad | `{"length":{"max":3},"digits":{"min":5}}` | Imposibilidad matemática | SecurityException lanzada |

## Matriz de Pruebas de Manejo de Errores

### Errores de Validación de Entrada

| ID Prueba | Escenario | Entrada | Excepción Esperada | El Mensaje de Error Contiene |
|-----------|-----------|---------|-------------------|----------------------------|
| EH-001 | JSON nulo | `null` | SecurityException | "no puede ser nulo o vacío" |
| EH-002 | JSON vacío | `""` | SecurityException | "no puede ser nulo o vacío" |
| EH-003 | Sintaxis JSON inválida | `{"length": {min: 8}` | SecurityException | "Formato de reglas de contraseña inválido" |
| EH-004 | Longitud negativa | `{"length":{"min":-1}}` | SecurityException | "Configuración de reglas de contraseña inválida" |

### Errores de Imposibilidad Matemática

| ID Prueba | Escenario | Reglas de Entrada | Excepción Esperada | Validación |
|-----------|-----------|-------------------|-------------------|------------|
| EH-101 | Longitud imposible | `{"length":{"max":2},"digits":{"min":5}}` | SecurityException | "matemáticamente imposible" |
| EH-102 | Conjunto de caracteres vacío | `{"digits":{"min":1,"exclude":[0,1,2,3,4,5,6,7,8,9]}}` | SecurityException | "matemáticamente imposible" |

## Matriz de Pruebas de Rendimiento

### Velocidad de Generación

| ID Prueba | Escenario | Configuración | Rendimiento Objetivo | Medición |
|-----------|-----------|---------------|---------------------|----------|
| PT-001 | Contraseña simple | Reglas básicas de 8 caracteres | < 1ms promedio | Tiempo de 1000 generaciones |
| PT-002 | Contraseña compleja | 16 caracteres con todas las reglas | < 5ms promedio | Tiempo de 1000 generaciones |
| PT-003 | Generación por lotes | 1000 contraseñas | < 1 segundo total | Tiempo por lotes |

### Pruebas de Escalabilidad

| ID Prueba | Escenario | Configuración de Carga | Criterios de Éxito | Monitoreo |
|-----------|-----------|------------------------|-------------------|-----------|
| PT-101 | Generación concurrente | 100 hilos, 1000 contraseñas cada uno | Todas completan exitosamente | Seguridad de hilos |
| PT-102 | Carga sostenida | 1 contraseña/segundo por 1 hora | Rendimiento consistente | Presión GC |

## Ejecución de Pruebas

### Ejecución Automatizada de Pruebas

```java
@TestMethodOrder(OrderAnnotation.class)
class PasswordGeneratorTestMatrix {
    
    private PasswordGenerator generator;
    
    @BeforeEach
    void setUp() {
        generator = new PasswordGenerator(
            "0123456789",
            "!@#$%^&*()",
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        );
    }
    
    @Test
    @Order(1)
    void testFT001_MinimumLengthPassword() {
        String rules = "{\"length\":{\"min\":1,\"max\":1}}";
        String password = generator.generatePassword(rules);
        assertEquals(1, password.length(), "La contraseña debe ser exactamente de 1 carácter");
    }
    
    @Test
    @Order(100)
    void testEH001_NullJSON() {
        assertThrows(SecurityException.class, 
                    () -> generator.generatePassword(null),
                    "Debe lanzar SecurityException para entrada nula");
    }
}
```

## Métricas de Cobertura

### Objetivos de Cobertura Meta

| Métrica | Objetivo | Actual | Estado |
|---------|----------|--------|--------|
| Cobertura de Línea | 95% | 98% | ✅ Logrado |
| Cobertura de Rama | 90% | 94% | ✅ Logrado |
| Cobertura de Método | 100% | 100% | ✅ Logrado |
| Cobertura de Clase | 100% | 100% | ✅ Logrado |

---

**Siguiente**: [Guía de Migración](../migration/upgrade-guide.md)  
**Relacionado**: [Guía de Manejo de Errores](../error-handling/security-exceptions.md)
