# Guide de migration et de mise à niveau

Ce guide fournit des instructions détaillées pour migrer entre les versions de la bibliothèque Password Generator, y compris les changements majeurs, les nouvelles fonctionnalités et les stratégies recommandées de mise à niveau.

## Table des matières

- Historique des versions
- Migration de 1.2.x à 1.3.0
- Changements majeurs
- Nouvelles fonctionnalités
- Fonctionnalités obsolètes
- Stratégies de mise à niveau

## Historique des versions

| Version | Date de sortie | Changements clés | Compatibilité |
|---------|---------------|------------------|--------------|
| 1.3.0   | Décembre 2024 | Validation mathématique, support Unicode | Changements majeurs |
| 1.2.x   | Octobre 2024  | Optimisation des performances | Compatible |
| 1.1.x   | Août 2024     | Version stable initiale | N/A |

## Migration de 1.2.x à 1.3.0

### Résumé

La version 1.3.0 introduit des améliorations significatives dans la validation des règles et la sécurité, mais inclut des changements majeurs nécessitant une mise à jour du code.

### Changements critiques

#### 1. Validation améliorée des règles

**Comportement précédent (1.2.x) :**
```java
// Tentative de génération pouvant échouer silencieusement
String reglesImpossibles = """
{
  "length": {"max": 5},
  "digits": {"min": 4},
  "symbols": {"min": 3}
}
""";
generator.generatePassword(reglesImpossibles); // Peut échouer de façon imprévisible
```

**Nouveau comportement (1.3.0) :**
```java
// Lance maintenant SecurityException immédiatement avec un message clair
String reglesImpossibles = """
{
  "length": {"max": 5},
  "digits": {"min": 4},
  "symbols": {"min": 3}
}
""";
generator.generatePassword(reglesImpossibles); // Lance une exception
```

Traducción al español:

# Guía de migración y actualización

Esta guía proporciona instrucciones detalladas para migrar entre versiones de la biblioteca Password Generator, incluyendo cambios importantes, nuevas funcionalidades y estrategias recomendadas de actualización.

## Tabla de contenidos

- Historial de versiones
- Migración de 1.2.x a 1.3.0
- Cambios importantes
- Nuevas funcionalidades
- Funcionalidades obsoletas
- Estrategias de actualización

## Historial de versiones

| Versión | Fecha de lanzamiento | Cambios clave | Compatibilidad |
|---------|---------------------|--------------|---------------|
| 1.3.0   | Diciembre 2024      | Validación matemática, soporte Unicode | Cambios importantes |
| 1.2.x   | Octubre 2024        | Optimización de rendimiento | Compatible |
| 1.1.x   | Agosto 2024         | Versión estable inicial | N/A |

## Migración de 1.2.x a 1.3.0

### Resumen

La versión 1.3.0 introduce mejoras significativas en la validación de reglas y seguridad, pero incluye cambios importantes que requieren actualización de código.

### Cambios críticos

#### 1. Validación mejorada de reglas

**Comportamiento anterior (1.2.x):**
```java
// Intento de generación que puede fallar silenciosamente
String reglasImposibles = """
{
  "length": {"max": 5},
  "digits": {"min": 4},
  "symbols": {"min": 3}
}
""";
generator.generatePassword(reglasImposibles); // Puede fallar de forma impredecible
```

**Nuevo comportamiento (1.3.0):**
```java
// Ahora lanza SecurityException inmediatamente con mensaje claro
String reglasImposibles = """
{
  "length": {"max": 5},
  "digits": {"min": 4},
  "symbols": {"min": 3}
}
""";
generator.generatePassword(reglasImposibles); // Lanza excepción
```

