# Guía de reglas para la generación de contraseñas

Esta guía explica la lógica de negocio principal detrás de la biblioteca Password Generator, incluyendo la jerarquía de reglas, el algoritmo de generación y las estrategias de resolución de conflictos.

## Jerarquía de reglas y sistema de prioridades

El generador de contraseñas sigue una jerarquía estricta al procesar reglas para garantizar un comportamiento predecible y seguro:

### 1. Las reglas de inclusión tienen prioridad

Cuando se especifican listas de "incluir" y "excluir" para el mismo tipo de carácter, las reglas de inclusión siempre tienen prioridad.

### 2. Los requisitos mínimos prevalecen sobre los máximos

El generador asegura que se cumplan los requisitos mínimos antes de considerar las restricciones máximas.

### 3. Validación de viabilidad matemática

Antes de iniciar la generación, el sistema valida que las reglas sean matemáticamente posibles.

## Algoritmo de generación de contraseñas

El proceso de generación sigue un algoritmo determinista de 8 pasos diseñado para seguridad y cumplimiento.

Ejemplo de uso:

1. Definir las reglas de generación.
2. Validar la configuración.
3. Generar la contraseña.

Traducción al francés:

# Guide des règles de génération de mots de passe

Ce guide explique la logique métier principale derrière la bibliothèque Password Generator, y compris la hiérarchie des règles, l'algorithme de génération et les stratégies de résolution des conflits.

## Hiérarchie des règles et système de priorités

Le générateur de mots de passe suit une hiérarchie stricte lors du traitement des règles pour garantir un comportement prévisible et sécurisé.

### 1. Les règles d'inclusion ont la priorité

Lorsque des listes "inclure" et "exclure" sont spécifiées pour le même type de caractère, les règles d'inclusion ont toujours la priorité.

### 2. Les exigences minimales prévalent sur les maximales

Le générateur garantit que les exigences minimales sont respectées avant de considérer les contraintes maximales.

### 3. Validation de la faisabilité mathématique

Avant de commencer la génération, le système valide que les règles sont mathématiquement possibles.

## Algorithme de génération de mots de passe

Le processus de génération suit un algorithme déterministe en 8 étapes conçu pour la sécurité et la conformité.

Exemple d'utilisation :

1. Définir les règles de génération.
2. Valider la configuration.
3. Générer le mot de passe.
