# Scénarios courants - Exemples de génération de mots de passe

Ce guide fournit des exemples pratiques pour les scénarios courants de génération de mots de passe, montrant comment configurer la bibliothèque pour différents besoins métier.

## Table des matières

- Types de mots de passe de base
- Exemples de niveaux de sécurité
- Exigences spécifiques à l'industrie
- Cas d'utilisation spéciaux
- Modèles d'intégration

## Types de mots de passe de base

### Mot de passe utilisateur standard

Exemple de mot de passe pour les systèmes d'inscription avec un équilibre entre sécurité et convivialité.

```java
PasswordGenerator generator = new PasswordGenerator(
    "0123456789",
    "!@#$%^&*()",
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
);

String reglesUtilisateur = """
{
  "length": {"min": 8, "max": 16},
  "digits": {"min": 1, "max": 4},
  "symbols": {"min": 1, "max": 3},
  "letters": {"exclude": ["l", "I", "1", "0", "O"]}
}
""";

String password = generator.generatePassword(reglesUtilisateur);
// Exemple de sortie : "Kf7@mN2p"
```

### Mot de passe temporaire

Mots de passe de courte durée pour les flux de réinitialisation ou d'accès temporaire.

```java
String reglesTemporaires = """
{
  "length": {"min": 12, "max": 12},
  "digits": {"min": 3, "max": 3},
  "symbols": {"min": 2, "max": 2},
  "symbols": {"include": ["@", "#", "$", "%"]}
}
""";

String password = generator.generatePassword(reglesTemporaires);
```

Traducción al español:

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
```

