# Foire aux questions (FAQ)

Ce document répond aux questions courantes sur la bibliothèque Password Generator, en fournissant des solutions pratiques et des explications basées sur des scénarios réels d'utilisation.

## Table des matières

- Questions générales
- Questions de sécurité
- Questions de configuration
- Questions de gestion des erreurs
- Questions de performance
- Questions d'intégration

## Questions générales

### Qu'est-ce qui distingue ce générateur de mots de passe ?

**R :** Il offre des avantages uniques :
- Validation mathématique des règles
- Sécurité cryptographique
- Fonctionnalités d'entreprise
- Configuration flexible en JSON

### Quelles sont les exigences minimales du système ?

**R :**
- Java 17 ou supérieur
- Utilisation minimale de la mémoire
- Dépendance à Jackson pour JSON
- Compatible avec Gradle ou Maven

### Comment démarrer avec la génération de base ?

**R :** Exemple :
```java
PasswordGenerator generator = new PasswordGenerator(
    "0123456789",
    "!@#$%^&*()",
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
);
String password = generator.generatePassword("{}");
```

Traducción al español:

# Preguntas frecuentes (FAQ)

Este documento responde preguntas comunes sobre la biblioteca Password Generator, proporcionando soluciones prácticas y explicaciones basadas en escenarios reales de uso.

## Tabla de contenidos

- Preguntas generales
- Preguntas de seguridad
- Preguntas de configuración
- Preguntas de manejo de errores
- Preguntas de rendimiento
- Preguntas de integración

## Preguntas generales

### ¿Qué hace diferente a este generador de contraseñas?

**R:** Ofrece ventajas únicas:
- Validación matemática de reglas
- Seguridad criptográfica
- Características empresariales
- Configuración flexible en JSON

### ¿Cuáles son los requisitos mínimos del sistema?

**R:**
- Java 17 o superior
- Uso mínimo de memoria
- Dependencia de Jackson para JSON
- Compatible con Gradle o Maven

### ¿Cómo empezar con la generación básica?

**R:** Ejemplo:
```java
PasswordGenerator generator = new PasswordGenerator(
    "0123456789",
    "!@#$%^&*()",
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
);
String password = generator.generatePassword("{}");
```

