# Guide de gestion des exceptions de sécurité

Ce guide couvre tous les scénarios d'erreur dans la bibliothèque Password Generator, y compris des explications détaillées, des causes et des stratégies de résolution.

## Table des matières

- Vue d'ensemble des exceptions
- Erreurs de validation des règles
- Erreurs d'impossibilité mathématique
- Erreurs d'échec de génération
- Erreurs de traitement JSON
- Erreurs de validation du constructeur
- Bonnes pratiques de gestion des erreurs

## Vue d'ensemble des exceptions

La bibliothèque utilise `SecurityException` pour toutes les conditions d'erreur, assurant la cohérence et la sécurité. Toutes les exceptions incluent des messages descriptifs pour faciliter le débogage et la résolution.

### Modèle d'exception courant

```java
try {
    String password = generator.generatePassword(rules);
} catch (SecurityException e) {
    String message = e.getMessage();
    if (message.contains("mathématiquement impossible")) {
        // Gérer les règles impossibles
    } else if (message.contains("Règles de mot de passe invalides")) {
        // Gérer les erreurs de validation
    } else if (message.contains("tentatives maximales")) {
        // Gérer les échecs de génération
    }
}
```

## Erreurs de validation des règles

### Configuration de longueur invalide

**Message d'erreur** : "Configuration des règles de mot de passe invalide"

**Scénarios** :
1. Longueur minimale supérieure à la maximale
2. Valeurs de longueur nulles ou négatives
3. Exigences minimales dépassant les contraintes maximales

Traducción al español:

# Guía de manejo de excepciones de seguridad

Esta guía cubre todos los escenarios de error en la biblioteca Password Generator, incluyendo explicaciones detalladas, causas y estrategias de resolución.

## Tabla de contenidos

- Descripción general de excepciones
- Errores de validación de reglas
- Errores de imposibilidad matemática
- Errores de fallo de generación
- Errores de procesamiento JSON
- Errores de validación de constructores
- Buenas prácticas de manejo de errores

## Descripción general de excepciones

La biblioteca utiliza `SecurityException` para todas las condiciones de error, manteniendo la consistencia y el enfoque en la seguridad. Todas las excepciones incluyen mensajes descriptivos para facilitar la depuración y resolución.

### Patrón común de excepción

```java
try {
    String password = generator.generatePassword(rules);
} catch (SecurityException e) {
    String mensaje = e.getMessage();
    if (mensaje.contains("matemáticamente imposible")) {
        // Manejar reglas imposibles
    } else if (mensaje.contains("Reglas de contraseña inválidas")) {
        // Manejar errores de validación
    } else if (mensaje.contains("máximos intentos")) {
        // Manejar fallos de generación
    }
}
```

## Errores de validación de reglas

### Configuración de longitud inválida

**Mensaje de error**: "Configuración de reglas de contraseña inválida"

**Escenarios**:
1. Longitud mínima mayor que la máxima
2. Valores de longitud cero o negativos
3. Requisitos mínimos que exceden las restricciones máximas

