# README

Ce projet fournit une bibliothèque pour la génération de mots de passe en Java. La bibliothèque permet de créer des mots de passe en fonction de règles spécifiques concernant la longueur, les chiffres, les symboles et les lettres.

## Exemple d'utilisation

Voici quelques exemples d'utilisation de la bibliothèque.

### Exemple 1 : Génération de mot de passe simple

Cet exemple génère un mot de passe d'une longueur comprise entre 8 et 30 caractères, comprenant au moins 8 chiffres et 8 symboles.

```java
import org.makechtec.xihucalli.password_generator.PasswordGenerator;

// Il est nécessaire d'instancier PasswordGenerator avec les listes de caractères.
// Voici un exemple de la manière dont cela pourrait être fait.
PasswordGenerator passwordGenerator = new PasswordGenerator(
    "0123456789",
    "!@#$%^&*()_+-=[]{}|;':,./<>?",
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
);

var rules = """
        {
          "length": {
            "min": 8,
            "max": 30
          },
          "digits": {
            "min": 8,
            "max": 30,
            "exclude": [],
            "include": []
          },
          "symbols": {
            "min": 8,
            "max": 30,
            "exclude": [],
            "include": []
          },
          "letters": {
            "exclude": [],
            "include": []
          }
        }
        """;

var password = passwordGenerator.generatePassword(rules);
System.out.println(password);
```

### Exemple 2 : Exclure des caractères spécifiques

Dans ce cas, un mot de passe est généré en excluant certains chiffres, symboles et lettres.

```java
var rules = """
        {
          "length": {
            "min": 8,
            "max": 30
          },
          "digits": {
            "min": 8,
            "max": 30,
            "exclude": [ 0, 1 ],
            "include": []
          },
          "symbols": {
            "min": 8,
            "max": 30,
            "exclude": [ "$", "%" ],
            "include": []
          },
          "letters": {
            "exclude": [ "a", "F" ],
            "include": []
          }
        }
        """;

var password = passwordGenerator.generatePassword(rules);
System.out.println(password); // Ne contiendra pas '0', '1', '$', '%', 'a', 'F'
```

### Exemple 3 : Forcer l'inclusion de caractères

Ici, un mot de passe de 8 caractères est généré qui ne contient que des lettres et doit inclure 'a' et 'F'.

```java
var rules = """
        {
          "length": {
            "min": 8,
            "max": 8
          },
          "digits": {
            "min": 0,
            "max": 0
          },
          "symbols": {
            "min": 0,
            "max": 0
          },
          "letters": {
            "include": [ "a", "F" ]
          }
        }
        """;

var password = passwordGenerator.generatePassword(rules);
System.out.println(password); // Contiendra 'a' et 'F'
```

## Changelog

- **1.0.0** - Première version de la bibliothèque avec des fonctionnalités de génération de mots de passe de base.
- **1.1.0** - Ajout de la possibilité d'exclure des caractères spécifiques lors de la génération de mots de passe.
- **1.2.0** - Ajout de règles plus flexibles pour l'inclusion et l'exclusion de caractères.
