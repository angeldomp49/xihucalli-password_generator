# Bibliothèque Générateur de Mots de Passe

Une bibliothèque Java sécurisée pour générer des mots de passe avec des règles personnalisables via une configuration JSON. Cette bibliothèque fournit une génération de mots de passe cryptographiquement sécurisée avec une validation exhaustive et une configuration flexible basée sur des règles.

## Table des Matières

- [Introduction](#introduction)
- [Installation et Configuration](#installation-et-configuration)
- [Utilisation de Base](#utilisation-de-base)
- [Format des Règles JSON](#format-des-règles-json)
- [Cas d'Usage Avancés](#cas-dusage-avancés)
- [Gestion des Erreurs](#gestion-des-erreurs)
- [Exemples](#exemples)
- [Liens de Documentation](#liens-de-documentation)

## Introduction

La Bibliothèque Générateur de Mots de Passe est conçue pour les applications d'entreprise qui nécessitent une génération de mots de passe sécurisée et conforme avec un contrôle granulaire sur la composition des mots de passe. Contrairement aux générateurs de mots de passe aléatoires simples, cette bibliothèque fournit :

- **Sécurité Cryptographique** : Utilise `SecureRandom` et `ThreadLocalRandom` pour une génération cryptographiquement sécurisée
- **Validation des Règles** : La validation de faisabilité mathématique empêche les configurations de règles impossibles
- **Configuration Flexible** : Les règles basées sur JSON permettent des exigences de mots de passe complexes
- **Prêt pour l'Entreprise** : Gère les cas limites et fournit des rapports d'erreurs complets

### Cas d'Usage Métier

- **Systèmes d'Enregistrement d'Utilisateurs** : Générer des mots de passe sécurisés respectant les politiques organisationnelles
- **Génération de Clés API** : Créer des jetons d'authentification robustes avec des exigences de caractères spécifiques
- **Systèmes de Mots de Passe Temporaires** : Générer des mots de passe pour les flux de réinitialisation
- **Exigences de Conformité** : Respecter des standards de sécurité spécifiques (PCI-DSS, SOX, etc.)

## Installation et Configuration

### Exigences Système

- Java 17 ou supérieur
- Gradle pour construire le projet

### Construction du Projet

```bash
./gradlew build
```

### Intégration de la Bibliothèque

```java
import org.makechtec.xihucalli.password_generator.PasswordGenerator;

// Initialiser avec des ensembles de caractères personnalisés
PasswordGenerator generator = new PasswordGenerator(
    "0123456789",                                           // chiffres
    "!@#$%^&*()_+-=[]{}|;':,./<>?",                        // symboles
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" // lettres
);
```

## Utilisation de Base

### Exemple Minimal

```java
PasswordGenerator generator = new PasswordGenerator(
    "0123456789",
    "!@#$%^&*()",
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
);

String rules = """
{
  "length": {"min": 8, "max": 12}
}
""";

try {
    String password = generator.generatePassword(rules);
    System.out.println("Mot de passe généré : " + password);
} catch (SecurityException e) {
    System.err.println("La génération du mot de passe a échoué : " + e.getMessage());
}
```

### Gestion des Exceptions

La bibliothèque lance `SecurityException` pour divers scénarios :
- Format JSON invalide
- Règles mathématiquement impossibles
- Génération échouée après le nombre maximum de tentatives (1000)

```java
try {
    String password = generator.generatePassword(rules);
} catch (SecurityException e) {
    // Gérer les cas d'erreur spécifiques basés sur le message
    if (e.getMessage().contains("mathematically impossible")) {
        // Ajuster les règles et réessayer
    }
}
```

## Format des Règles JSON

Les règles de génération de mots de passe sont spécifiées en utilisant un format JSON structuré :

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
    "include": ["A", "B", "C"],
    "exclude": ["l", "I", "O"]
  }
}
```

### Propriétés des Règles

| Propriété | Type | Description | Par Défaut |
|-----------|------|-------------|------------|
| `length.min` | entier | Longueur minimale du mot de passe | 1 |
| `length.max` | entier | Longueur maximale du mot de passe | Long.MAX_VALUE |
| `digits.min` | entier | Nombre minimum de chiffres | 0 |
| `digits.max` | entier | Nombre maximum de chiffres | Long.MAX_VALUE |
| `digits.include` | tableau d'entiers | Chiffres requis | [] |
| `digits.exclude` | tableau d'entiers | Chiffres interdits | [] |
| `symbols.min` | entier | Nombre minimum de symboles | 0 |
| `symbols.max` | entier | Nombre maximum de symboles | Long.MAX_VALUE |
| `symbols.include` | tableau de chaînes | Symboles requis | [] |
| `symbols.exclude` | tableau de chaînes | Symboles interdits | [] |
| `letters.include` | tableau de chaînes | Lettres requises | [] |
| `letters.exclude` | tableau de chaînes | Lettres interdites | [] |

## Cas d'Usage Avancés

### Politique de Sécurité d'Entreprise

```java
// Mot de passe haute sécurité pour comptes administratifs
String corporateRules = """
{
  "length": {"min": 14, "max": 20},
  "digits": {"min": 3, "max": 5},
  "symbols": {"min": 2, "max": 4, "exclude": ["<", ">", "&"]},
  "letters": {"exclude": ["l", "I", "1", "0", "O"]}
}
""";
```

### Génération de Clés API

```java
// Générer des clés API avec des exigences de format spécifiques
String apiKeyRules = """
{
  "length": {"min": 32, "max": 32},
  "digits": {"min": 8},
  "letters": {"include": ["A", "B", "C", "D", "E", "F"]}
}
""";
```

### Génération de Code PIN

```java
PasswordGenerator pinGenerator = new PasswordGenerator("0123456789", "", "");
String pinRules = """
{
  "length": {"min": 4, "max": 6},
  "digits": {"min": 4, "exclude": [0]}
}
""";
```

## Gestion des Erreurs

Scénarios d'erreur courants et leurs solutions :

### Configuration de Règles Invalide
- **Erreur** : "Configuration de règles de mot de passe invalide"
- **Cause** : minLength > maxLength, valeurs négatives
- **Solution** : Valider les contraintes de règles avant la génération

### Règles Mathématiquement Impossibles
- **Erreur** : "Les règles de mot de passe sont mathématiquement impossibles à satisfaire"
- **Cause** : minDigits + minSymbols > maxLength
- **Solution** : Ajuster les exigences minimales ou augmenter la longueur maximale

### Échec de Génération
- **Erreur** : "Impossible de générer un mot de passe respectant les exigences de sécurité après le nombre maximum de tentatives"
- **Cause** : Règles très restrictives causant des échecs répétés
- **Solution** : Assouplir certaines contraintes ou vérifier la disponibilité de l'ensemble de caractères

## Exemples

### Exemple 1 : Mot de Passe Basique de 8 Caractères

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
// Exemple de résultat : "aB3#xY9z"
```

### Exemple 2 : Mot de Passe Haute Sécurité

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
// Exemple de résultat : "A7B#2C@9x$4mN5pQ"
```

### Exemple 3 : Code PIN Numérique

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
// Exemple de résultat : "4729"
```

## Liens de Documentation

- [Guide de Logique Métier](business-logic/password-generation-rules.md)
- [Scénarios Courants](examples/common-scenarios.md)
- [Guide de Gestion des Erreurs](error-handling/security-exceptions.md)
- [Matrice de Couverture de Tests](testing/test-coverage-matrix.md)
- [Guide de Migration](migration/upgrade-guide.md)
- [Questions Fréquemment Posées](faq.md)

---

**Version** : 1.3.0  
**Dernière Mise à Jour** : Décembre 2024  
**Licence** : [Informations de licence]
