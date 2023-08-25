# Projet de Systèmes Distribués (Master 1 - Semestre 1)

Il s'agit d'une solution comprenant plusieurs applications clients-serveurs resprenant les modèles Producteurs – Consommateurs et Rédacteurs – Lecteurs. Chacune de ces solutions est implémentée en deux parties distinctes afin de respecter le modèle Client–Serveur, la première partie étant un serveur permettant la connexion des clients et la gestion des ressources, tandis que la seconde partie des solutions est un modèle de clients se connectant au serveur et gérant l’affichage et les saisies de l’utilisateur. 

## Producteurs - Consommateurs

### Fonctionnement Client - Serveur

***Côté Client***

Dans notre solution, le client va, au début de son exécution, chercher à se connecter au serveur par le port 5 000. Si la connexion est établie alors il créé 2 objets : un PrintWriter pour envoyer des messages au serveur et un BufferedReader pour recevoir des messages en provenance du serveur.

Une fois cet étage effectué, le client va attendre que l’utilisateur saisisse une entrée au clavier déterminant si ce client est un Producteur ou un Consommateur. Si le client est un Consommateur alors il ne pourra que recevoir des messages du serveur, s’il s’agit par contre d’un Producteur alors il pourra aussi bien en envoyer qu’en recevoir.

Normalement le producteur ne fait qu’envoyer des messages et il n’est pas supposé en recevoir,
mais dans notre solution les Producteur reçoivent un message du serveur si le message qu’ils ont
envoyé n’a pas pu être ajouté au buffer du serveur, faute de place.

***Côté Serveur***

Quand le serveur démarre, il commence par créer un Thread qui va récupérer en continue la première valeur stocké dans le buffer et l’envoyer aux Consommateurs à condition que ni le buffer ni la liste des clients de type Consommateur ne soient vides.

Ensuite, le serveur écoute en continue sur le port 5 000, et chaque fois qu’un client se connecte il créé un Thread ConnexionClient qui s’occupera des transfert de données entre le client et le serveur. Ensuite, le serveur ajoute le Thread à la liste des Producteurs ou des Consommateurs selon le type de client.

### Fonctionnement Producteurs - Consommateurs

Les méthodes liées au modèle Producteurs – Consommateurs sont implémentées au niveau du serveur.

Dans le cas des clients Producteurs, les Threads créés par le serveur vont être exécutés en parallèle. Chacun de ces Threads va alors lire en continue les messages envoyés par le client Producteur auquel il est assigné et ajouter les messages dans le buffer à condition que cela ne lui fasse dépasser une certaine taille. Si le buffer est déjà plein le message est ignoré et le serveur envoie un avertissement au client pour signaler que le buffer est plein.

Dans le cas des clients Consommateurs, le serveur s’occupe, grâce à un Thread créé au lancement du serveur, de vérifier en continue le contenue du buffer, et s’il contient un message alors il le sort du buffer et l’envoie à l’ensemble des clients consommateurs.

### Description des jeux de données et tests de validation

Nous avons testé notre serveur avec différents nombres de Producteurs et de Consommateurs et n’avons rencontré aucun problème lors des exécutions. 

Lorsqu’un Producteur reçoit une entrée clavier d’un utilisateur, il la transmet au serveur qui va à son tour la transmettre à l’ensemble des Consommateurs, sauf s’il n’y en a aucun qui soit connecté au serveur auquel cas le serveur les stockent jusqu’à ce qu’un Consommateur se connecte.

## Rédacteurs - Lecteurs

### Fonctionnement Client - Serveur

***Côté Client***

Dans cette solution, le client va, comme pour la solution précédente, chercher à se connecter au serveur par le port 5 000 dès son exécution. Si la connexion est établie alors il créera les objets PrintWriter et BufferedReader pour envoyer et recevoir des messages.

Une fois cette étape effectuée, le client va attendre que l’utilisateur saisisse au clavier si ce client est un Lecteur ou un Rédacteur. Si le client est un Lecteur alors il pourra seulement recevoir des messages du serveur, et s’il s’agit d’un Rédacteur alors il pourra seulement en envoyer.

***Côté Serveur***

Quand le serveur démarre, il écoute en continue sur le port 5 000, et chaque fois qu’un client se connecte il créé un Thread ConnexionClient qui s’occupera des transfert de données entre le client qui s’est connecté et le serveur. Ensuite, le serveur ajoute le Thread à la liste des Lecteur ou des Rédacteur selon le type de client.

### Fonctionnement Lecteurs - Rédacteurs

Les méthodes liées au modèle Lecteurs – Rédacteurs sont implémentées au niveau du serveur grâce aux Threads crées par le serveur qui permettent l’exécution en parallèle des requêtes des clients pour la lecture et l’écriture.

Dans le cas d’un client Lecteur, le Thread crée côté serveur va récupérer la taille du fichier F puis charger son contenu dans un buffer avant de l’envoyer au client.

Pour un client Rédacteur, le Thread va récupérer le message envoyé par le client et l’ajouter à la suite du fichier.

La synchronisation des Threads est assurée par l’utilisation de variables partagée entre les Threads qui ont pour valeurs respective le nombre de lectures en cours et le nombre d’écritures en cours.

Grâce à ces variables, si on a une écriture en cours alors les autres Thread doivent attendre la fin de l’écriture, et si on a au moins une lecture alors seul les clients Lecteurs peuvent accéder au fichier.

Nous avons également ajouté une file d’attente partagée entre les Threads pour compléter la synchronisation, de cette façon lorsqu’une requête client se termine alors seul la ou les suivante(s) sur la liste d’attente peuvent être exécutée(s).

### Description des jeux de données et tests de validation

Nous avons testé le serveur avec des séries de Lecteurs et de Rédacteurs lancés successivement en variant les combinaisons et n’avons pas rencontré de problèmes au cours des exécutions.

Une fois le serveur lancé, on peut exécuter autant de client que l’on souhaite, l’exécution des requêtes des clients se fait dans l’ordre d’arrivée. Toutefois plusieurs lectures simultanées sont permises si elles se suivent dans la file d’attente des exécutions.