
Status: draft
Owner: @angeldomp49
Source Model: Claude Opus 4.1 (Ask Mode)
Last Sync: 2024-12-20T10:30:00Z

# Especificación de Pruebas de Integración con Concordion

## Información del Proyecto

- **Nombre del Proyecto**: password_generator
- **Tipo**: Generador de contraseñas con Spring Boot
- **Build Tool**: Gradle
- **Lenguajes**: Java, Kotlin

## Rol y Contexto

Como QA tester especializado en generadores de contraseñas, implementarás pruebas de integración robustas usando Concordion para crear especificaciones ejecutables en HTML.

## Configuración de Dependencias

### 1. Agregar dependencias en `lib/build.gradle.kts`:

```gradle
dependencies {
    // Concordion
    testImplementation("org.concordion:concordion:4.1.0")
    testImplementation("org.concordion:concordion-junit-jupiter:4.1.0")
    
    // JaCoCo para cobertura de código
    testImplementation("org.jacoco:jacoco-maven-plugin:0.8.11")
    
    // Mockito para mocks mínimos
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.7.0")
    
    // AssertJ para aserciones fluidas
    testImplementation("org.assertj:assertj-core:3.24.2")
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/test"))
    }
}
```

## Matriz de Pruebas

### Componentes a Probar

| Componente                   | Casos de Prueba                            | Prioridad | Tipo        |
|------------------------------|--------------------------------------------|-----------|-------------|
| **PasswordGenerator**        |                                            |           |             |
| - Generación básica          | Generar contraseña con longitud específica | Alta      | Integración |
| - Caracteres especiales      | Incluir/excluir caracteres especiales      | Alta      | Integración |
| - Mayúsculas/Minúsculas      | Validar mezcla de casos                    | Alta      | Integración |
| - Números                    | Incluir/excluir números                    | Media     | Integración |
| - Longitud mínima/máxima     | Validar límites (min: 4, max: 128)         | Alta      | Integración |
| **PasswordRulesInformation** |                                            |           |             |
| - Validación de reglas       | Verificar aplicación de reglas             | Alta      | Unitaria    |
| - Configuración              | Cargar configuración desde properties      | Media     | Integración |
| - Reglas conflictivas        | Manejar reglas contradictorias             | Alta      | Integración |
| **Spring Boot Integration**  |                                            |           |             |
| - Context Loading            | Verificar carga del contexto               | Alta      | Integración |
| - Bean Injection             | Validar inyección de dependencias          | Alta      | Integración |
| - Configuration Properties   | Verificar propiedades de configuración     | Media     | Integración |

## Estructura de Pruebas Concordion

### Directorio de especificaciones:
```
lib/src/test/resources/concordion/
├── PasswordGeneratorSpec.html
├── PasswordRulesSpec.html
├── IntegrationSpec.html
└── css/
    └── concordion.css
```

### Plantilla de Especificación HTML:

```html
<!DOCTYPE html>
<html xmlns:c="http://www.concordion.org/2007/concordion">
<head>
    <title>Password Generator Specification</title>
    <link href="css/concordion.css" rel="stylesheet" type="text/css"/>
</head>
<body>
    <h1>Password Generator Tests</h1>
    
    <div class="example">
        <h3>Example: Generate Password with Specific Length</h3>
        <p>When generating a password with length 
           <span c:set="#length">12</span>, 
           the result should have exactly 
           <span c:assertEquals="generatePassword(#length).length()">12</span> 
           characters.
        </p>
    </div>
</body>
</html>
```

### Fixture Java correspondiente:

```java
package org.makechtec.xihucalli.password_generator.concordion;

import org.concordion.integration.junit.jupiter.ConcordionRunner;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(ConcordionRunner.class)
@SpringBootTest
public class PasswordGeneratorSpec {
    
    private PasswordGenerator passwordGenerator;
    
    public String generatePassword(int length) {
        // Implementation
    }
}
```

## Principios de Implementación

### Clean Code
- Nombres descriptivos y autoexplicativos
- Funciones pequeñas y con responsabilidad única
- Evitar comentarios redundantes
- DRY (Don't Repeat Yourself)

### SOLID
- **S**: Cada clase de prueba tiene una única responsabilidad
- **O**: Extensible mediante herencia de fixtures base
- **L**: Los fixtures específicos pueden sustituir a los genéricos
- **I**: Interfaces segregadas para diferentes tipos de pruebas
- **D**: Inyección de dependencias mediante Spring

### Aserciones Requeridas
- Cada prueba debe incluir al menos una aserción de validación
- Verificar estado interno cuando sea relevante
- Validar ejecución de métodos críticos

## Configuración de Reportes

### Concordion Reports:
```
build/reports/concordion/
├── index.html
├── org/makechtec/xihucalli/password_generator/
│   ├── PasswordGeneratorSpec.html
│   └── PasswordRulesSpec.html
```

### JaCoCo Coverage:
```
build/reports/jacoco/test/
├── html/index.html
└── jacocoTestReport.xml
```

## Criterios de Éxito

1. ✅ Todas las pruebas pasan exitosamente
2. ✅ Cobertura de código > 80%
3. ✅ Reportes Concordion generados correctamente
4. ✅ Sin violaciones de principios SOLID
5. ✅ Código limpio y mantenible
6. ✅ Documentación completa en especificaciones HTML

## Condiciones de Finalización

El proceso se completará cuando:
- Todos los componentes identificados tengan pruebas completas
- Las pruebas ejecuten exitosamente
- Los reportes se generen en las carpetas esperadas
- La cobertura de código alcance el objetivo
- El código cumpla con los estándares de calidad establecidos
