# Especificación Detallada: Ajustar el Comportamiento a la Matriz de Pruebas

Status: draft
Owner: @angeldomp49
Source Model: Claude Opus 4.1 (Ask Mode)
Last Sync: 2024-11-14T10:00:00Z


## Información del Proyecto

- **Nombre del Proyecto**: Password Generator
- **Lenguaje**: Java 17 (Java puro, sin frameworks)
- **Build Tool**: Gradle
- **Arquitectura**: Clean Architecture
- **Framework**: **NINGUNO** - Aplicación Java pura

## Especificación de la Funcionalidad

### Rol

Como desarrollador backend senior, tu tarea es corregir el código fuente actual para cumplir con el comportamiento esperado según la matriz de pruebas.

### Contexto del Proyecto

El proyecto Password Generator es una aplicación **Java pura** que genera contraseñas seguras siguiendo diferentes patrones y configuraciones. El sistema debe:

1. Generar contraseñas con diferentes niveles de complejidad
2. Permitir configuración de longitud y caracteres permitidos
3. Validar las contraseñas generadas contra reglas de seguridad
4. Proporcionar diferentes estrategias de generación

### Estructura del Proyecto

```
password_generator/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/passwordgenerator/
│   │           ├── domain/
│   │           ├── application/
│   │           ├── infrastructure/
│   │           └── interfaces/
│   └── test/
│       ├── java/
│       └── resources/
│           └── fixtures/
├── docs/
└── build.gradle
```

### Restricciones Importantes

⚠️ **NO usar Spring Boot ni ningún framework**
⚠️ **NO usar la librería ioc_container**
⚠️ **NO usar reflexión**
⚠️ **Implementar con Java 17 puro**

### Tareas Específicas

#### 1. Análisis Inicial
- Revisar toda la documentación en la carpeta `docs/`
- Identificar los componentes principales del sistema
- Mapear las dependencias entre módulos usando composición

#### 2. Revisión de Fixtures de Prueba
- Examinar todos los archivos en `src/test/resources/fixtures/`
- Documentar el comportamiento esperado para cada caso de prueba
- Crear una matriz de casos de prueba con:
    - Entrada esperada
    - Salida esperada
    - Reglas de validación

#### 3. Ejecución y Análisis de Pruebas
- Ejecutar el conjunto completo de pruebas: `gradle test`
- Generar reporte de pruebas: `gradle test --info`
- Documentar todas las pruebas fallidas con:
    - Nombre de la prueba
    - Error específico
    - Comportamiento actual vs esperado

#### 4. Corrección del Código

Para cada prueba fallida:

1. **Identificar la causa raíz**
    - Localizar el componente defectuoso
    - Determinar si es un problema de lógica o configuración

2. **Implementar la corrección**
    - Aplicar principios SOLID y Clean Code
    - Usar composición en lugar de herencia
    - Implementar guard clauses
    - Utilizar `Objects.isNull()` y `Objects.nonNull()`
    - Inyección manual de dependencias vía constructor

3. **Validar la corrección**
    - Ejecutar la prueba específica
    - Verificar que no se rompen otras pruebas
    - Confirmar que se mantiene la funcionalidad

### Criterios de Aceptación

- ✅ Todas las pruebas unitarias pasan exitosamente
- ✅ Todas las pruebas de integración pasan exitosamente
- ✅ Sin uso de frameworks (Spring, etc.)
- ✅ Sin uso de reflexión en el código
- ✅ Sin comentarios dentro del código
- ✅ Código autoexplicativo siguiendo Clean Code
- ✅ Uso de composición sobre herencia

### Notas de Implementación

#### Manejo de Dependencias (Java Puro)
```java
// Ejemplo de inyección manual de dependencias
public class PasswordService {
    private final PasswordGenerator generator;
    private final PasswordValidator validator;
    
    public PasswordService(PasswordGenerator generator, 
                          PasswordValidator validator) {
        if (Objects.isNull(generator)) {
            throw new IllegalArgumentException("Generator cannot be null");
        }
        if (Objects.isNull(validator)) {
            throw new IllegalArgumentException("Validator cannot be null");
        }
        this.generator = generator;
        this.validator = validator;
    }
}
```

#### Estándares de Código
```java
// Ejemplo de implementación con guard clauses
public class PasswordGenerator {
    private final CharacterPool characterPool;
    private final SecureRandom random;
    
    public PasswordGenerator(CharacterPool characterPool) {
        if (Objects.isNull(characterPool)) {
            throw new IllegalArgumentException("Character pool required");
        }
        this.characterPool = characterPool;
        this.random = new SecureRandom();
    }
    
    public String generate(PasswordConfig config) {
        if (Objects.isNull(config)) {
            throw new IllegalArgumentException("Config cannot be null");
        }
        
        if (config.getLength() < 8) {
            throw new IllegalArgumentException("Minimum length is 8");
        }
        
        return buildPassword(config);
    }
    
    private String buildPassword(PasswordConfig config) {
        // Lógica de generación
        return "";
    }
}
```

### Flujo de Trabajo

1. **Fase de Análisis** (30 min)
    - Leer documentación completa
    - Entender arquitectura sin frameworks

2. **Fase de Diagnóstico** (1 hora)
    - Ejecutar pruebas con `gradle test`
    - Documentar fallos
    - Priorizar correcciones

3. **Fase de Implementación** (Variable)
    - Corregir componente por componente
    - Mantener arquitectura limpia sin frameworks
    - Validar después de cada corrección

4. **Fase de Validación** (30 min)
    - Ejecutar suite completa de pruebas
    - Verificar que no hay dependencias de frameworks

### Configuración de Gradle (Kotlin DSL)

```kotlin
// build.gradle.kts
plugins {
    java
    application
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.assertj:assertj-core:3.24.2")
}

tasks.test {
    useJUnitPlatform()
}
```

### Condiciones de Finalización

El trabajo se considera completo cuando:
- `gradle test` ejecuta sin errores
- `gradle build` completa exitosamente
- Todos los reportes de prueba muestran estado SUCCESS
- No hay regresiones en funcionalidades existentes
- **NO hay dependencias de frameworks en el código**
- El código usa solo características nativas de Java 17