# Escenarios comunes - Ejemplos de generación de contraseñas

Esta guía proporciona ejemplos prácticos para escenarios comunes de generación de contraseñas, mostrando cómo configurar la biblioteca para diferentes requisitos empresariales.

## Tabla de contenidos

- Tipos básicos de contraseñas
- Ejemplos de niveles de seguridad
- Requisitos específicos de la industria
- Casos de uso especiales
- Patrones de integración

## Tipos básicos de contraseñas

### Contraseña estándar de usuario

Ejemplo de contraseña para sistemas de registro de usuarios con equilibrio entre seguridad y usabilidad.

```java
PasswordGenerator generator = new PasswordGenerator(
    "0123456789",
    "!@#$%^&*()",
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
);

String reglasUsuario = """
{
  "length": {"min": 8, "max": 16},
  "digits": {"min": 1, "max": 4},
  "symbols": {"min": 1, "max": 3},
  "letters": {"exclude": ["l", "I", "1", "0", "O"]}
}
""";

String password = generator.generatePassword(reglasUsuario);
// Ejemplo de salida: "Kf7@mN2p"
```

### Contraseña temporal

Contraseñas de corta duración para flujos de restablecimiento o acceso temporal.

```java
String reglasTemporales = """
{
  "length": {"min": 12, "max": 12},
  "digits": {"min": 3, "max": 3},
  "symbols": {"min": 2, "max": 2},
  "symbols": {"include": ["@", "#", "$", "%"]}
}
""";

String password = generator.generatePassword(reglasTemporales);
// Ejemplo de salida: "aB3@xY9#mNpQ"
```

### PIN Numérico

Contraseñas numéricas simples para aplicaciones de baja seguridad.

```java
PasswordGenerator pinGenerator = new PasswordGenerator("0123456789", "", "");

String pinRules = """
{
  "length": {"min": 4, "max": 6},
  "digits": {"min": 4, "exclude": [0]}
}
""";

String pin = pinGenerator.generatePassword(pinRules);
// Ejemplo de salida: "7349"
```

## Ejemplos por Nivel de Seguridad

### Seguridad Baja - Protección Básica

Adecuado para aplicaciones no críticas con conveniencia del usuario priorizada.

```java
String lowSecurityRules = """
{
  "length": {"min": 6, "max": 10},
  "digits": {"min": 1},
  "symbols": {"max": 2}
}
""";

String basicPassword = generator.generatePassword(lowSecurityRules);
// Ejemplo de salida: "pass7@"
```

### Seguridad Media - Enfoque Equilibrado

Seguridad empresarial estándar para la mayoría de aplicaciones de negocio.

```java
String mediumSecurityRules = """
{
  "length": {"min": 10, "max": 14},
  "digits": {"min": 2, "max": 4},
  "symbols": {"min": 2, "max": 3},
  "letters": {"exclude": ["l", "I", "O", "0"]}
}
""";

String balancedPassword = generator.generatePassword(mediumSecurityRules);
// Ejemplo de salida: "Kf7@mN2p$x"
```

### Seguridad Alta - Protección Máxima

Para cuentas administrativas y sistemas sensibles.

```java
String highSecurityRules = """
{
  "length": {"min": 16, "max": 24},
  "digits": {"min": 4, "max": 6},
  "symbols": {"min": 4, "max": 6},
  "symbols": {"exclude": ["<", ">", "&", "\"", "'"]},
  "letters": {"exclude": ["l", "I", "1", "0", "O"]}
}
""";

String strongPassword = generator.generatePassword(highSecurityRules);
// Ejemplo de salida: "A7B#2C@9x$4mN5pQ!wR"
```

## Requisitos Específicos por Industria

### Servicios Financieros (Cumplimiento PCI-DSS)

Cumpliendo los requisitos del Estándar de Seguridad de Datos de la Industria de Tarjetas de Pago.

```java
String pciCompliantRules = """
{
  "length": {"min": 12, "max": 16},
  "digits": {"min": 2, "max": 4},
  "symbols": {"min": 2, "max": 4},
  "symbols": {"exclude": ["<", ">", "&", "\"", "'", "/", "\\\\"]},
  "letters": {"exclude": ["l", "I", "1", "0", "O"]}
}
""";

String pciPassword = generator.generatePassword(pciCompliantRules);
// Ejemplo de salida: "Secure@Bank7#2024"
```

### Salud (Cumplimiento HIPAA)

Contraseñas para sistemas de salud que manejan información de salud protegida.

```java
String hipaaCompliantRules = """
{
  "length": {"min": 14, "max": 18},
  "digits": {"min": 3, "max": 5},
  "symbols": {"min": 3, "max": 4},
  "symbols": {"include": ["@", "#", "$", "%", "!", "*"]},
  "letters": {"exclude": ["l", "I", "1", "0", "O", "S", "5"]}
}
""";

String hipaaPassword = generator.generatePassword(hipaaCompliantRules);
// Ejemplo de salida: "Health@Care3#2024$"
```

## Casos de Uso Especiales

### Generación de Claves API

Creando claves API seguras con requisitos de formato específicos.

```java
String apiKeyRules = """
{
  "length": {"min": 32, "max": 32},
  "digits": {"min": 8, "max": 12},
  "letters": {"include": ["A", "B", "C", "D", "E", "F", "G", "H", "J", "K"]},
  "symbols": {"max": 0}
}
""";

String apiKey = generator.generatePassword(apiKeyRules);
// Ejemplo de salida: "ABCD1234EFGH5678JKAB9012CDEF3456"
```

### Tokens de Sesión

Identificadores de sesión temporales para aplicaciones web.

```java
String sessionTokenRules = """
{
  "length": {"min": 24, "max": 28},
  "digits": {"min": 6, "max": 8},
  "letters": {"exclude": ["l", "I", "1", "0", "O"]},
  "symbols": {"max": 2, "include": ["-", "_"]}
}
""";

String sessionToken = generator.generatePassword(sessionTokenRules);
// Ejemplo de salida: "Abc7Def2Ghi9Jkm5Nop8Qrs-3"
```

## Patrones de Integración

### Integración con Servicio Spring Boot

```java
@Service
public class PasswordService {
    private final PasswordGenerator passwordGenerator;
    
    public PasswordService() {
        this.passwordGenerator = new PasswordGenerator(
            "0123456789",
            "!@#$%^&*()",
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        );
    }
    
    public String generateUserPassword() {
        String rules = """
        {
          "length": {"min": 8, "max": 16},
          "digits": {"min": 1, "max": 4},
          "symbols": {"min": 1, "max": 3}
        }
        """;
        
        return passwordGenerator.generatePassword(rules);
    }
}
```

### Generación Dirigida por Configuración

```java
@Component
public class ConfigurablePasswordGenerator {
    
    @Value("${password.rules.user}")
    private String userPasswordRules;
    
    @Value("${password.rules.admin}")
    private String adminPasswordRules;
    
    private final PasswordGenerator generator;
    
    public String generatePassword(String userType) {
        String rules = "admin".equals(userType) ? adminPasswordRules : userPasswordRules;
        return generator.generatePassword(rules);
    }
}
```

---

**Siguiente**: [Guía de Manejo de Errores](../error-handling/security-exceptions.md)  
**Relacionado**: [Guía de Lógica de Negocio](../business-logic/password-generation-rules.md)
