# 🌾 SmartFarm

Application de bureau JavaFX pour la gestion intelligente d'une exploitation agricole.
Visualisez vos champs, gérez vos réserves d'eau et asperseurs, et explorez la géométrie de Voronoï/Delaunay générée automatiquement.

---

## ✨ Fonctionnalités

- **Carte interactive** : visualisation des champs, réserves d'eau et asperseurs
- **Zoom & déplacement** : zoom centré sur la carte + navigation au clic maintenu
- **Diagramme de Voronoï** : calculé automatiquement depuis les réserves d'eau
- **Triangulation de Delaunay** : affichée en surimpression sur la carte
- **Panneau d'informations** : cliquez sur un élément pour afficher ses détails à droite
- **Ajouter des éléments** : formulaires pour créer des champs, réserves d'eau et asperseurs
- **Sauvegarde / chargement** : format texte lisible, éditable manuellement
- **Créer une nouvelle ferme** : saisie et configuration initiale

---

## 🚀 Compilation et Exécution (Ligne de commande)

Ce projet utilise Maven pour la gestion des dépendances (notamment JavaFX) et la compilation. Il n'est pas nécessaire de télécharger ou de configurer le SDK JavaFX manuellement.

Prérequis :
- JDK 17 ou supérieur (le projet est configuré sous Java 21)
- Maven installé et configuré dans les variables d'environnement

Instructions pas à pas :

1. Ouvrez un terminal.
2. Déplacez-vous dans le sous-dossier SmartFarm (le dossier contenant le fichier pom.xml) :
   ```
   cd chemin/vers/le/dossier/Projet-ING1-S2/SmartFarm
   ```

3. Nettoyez et compilez le projet :
   ```
   mvn clean compile
   ```

4. Lancez l'application avec l'interface graphique JavaFX :
   ```
   mvn javafx:run
   ```

5. (Optionnel) Pour lancer la version en ligne de commande (mode Console) :
   ```
   mvn exec:java -Dexec.mainClass="org.example.Cli"
   ```

## 🗂️ Format de sauvegarde

Les fichiers se trouvent dans `SmartFarm/src/main/resources/Saves/`.  
Format texte délimité par des `;`, un élément par ligne :

name;Dupont;Jean;jean@email.com;42
G;10000.0
F;0.0;0.0;500.0;300.0;Champ Nord;150000.0
W;250.0;150.0;1000.0;5.0
S;300.0;200.0;2.0;30.0

| Préfixe | Élément      | Champs                                         |
|---------|--------------|------------------------------------------------|
| `name`  | Propriétaire | nom ; prénom ; email ; âge                     |
| `G`     | Ground       | surface                                        |
| `F`     | Field        | xStart ; yStart ; xStop ; yStop ; nom ; surface|
| `W`     | Water Tank   | x ; y ; capacité ; débit                       |
| `S`     | Sprinkler    | x ; y ; débit ; rayon                          |

---

## 🏗️ Architecture

```
org.example/
├── Launcher              # Point d'entrée
├── SmartFarmUI           # Menu principal + barre de navigation partagée
│
├── — Vues —
├── SaveView              # Liste des sauvegardes
├── MapView               # Carte interactive (zoom, pan, infos)
├── ElementView           # Vue abstraite d'un Point (WaterTank / Sprinkler)
├── WaterTankView         # Détail d'un water tank
├── SprinklerView         # Détail d'un asperseur
├── AddForm               # Formulaires d'ajout d'éléments
├── NewGroundFormView     # Formulaire de création d'une ferme
│
├── — Modèle —
├── Ground                # Conteneur principal (champs, tanks, asperseurs)
├── Field                 # Champ rectangulaire
├── Point                 # Coordonnées x/y (classe de base)
├── WaterTank             # Point avec capacité et débit
├── Sprinkler             # Point avec débit et rayon d'irrigation
├── Person                # Propriétaire de la ferme
│
├── — Géométrie —
├── DelaunayTriangulation # Algorithme de Bowyer-Watson
├── DelaunayTriangle      # Triangle avec calcul du cercle circonscrit
├── VoronoiBuilder        # Construction du diagramme depuis la triangulation
├── VoronoiDiagram        # Résultat du diagramme
├── VoronoiCell           # Cellule Voronoï associée à un water tank
│
└── — Persistance —
    ├── Save              # Lecture / écriture des fichiers de sauvegarde
    └── Cli               # Interface en ligne de commande
```

---

## 👥 Auteurs

Projet réalisé par le groupe **GI5-F**
- Paul Morille
- Tom Lemenand
- Oscar Luiggi
- Jilani Mohamed
- Illya Liganov

