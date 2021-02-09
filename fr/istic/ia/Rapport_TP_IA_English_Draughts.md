# Rapport TP IA : English Draughts

par Le Dourner Guillaume - Guerin Alexys

## Choix d'implémentation

**Pour la classe EnglishDraughts.java** nous avons suivi la javadoc, toutesfois pour certaines fonctions nous avons diviser la tâche pour faire des fonctions plus simples et efficaces.

Par exemple pour les dépacements nous avons réalisé différentes fonctions :

* ==public List<Move> possibleMoves()== qui calcule tous les mouvement possible dans un état du jeu donné, et est composée de toutes les politiques de mouvement possible. Si une capture est possible on ignore tous les autres déplacements sinon on les calcul. On a ici choisi d'utiliser un historique pour éviter les redondance de mouvements.
* ==public List<DraughtsMove> DeplacementSimplesPossibles2(int pawn)== calcule les déplacements simples pour un pion ou un roi, c'est a dire les déplacment sans capture en utilisant les fonctions : 
* ==checkUpRightDepl(pawn,destPossibles);==
* ==checkUpLeftDepl(pawn,destPossibles);==
* ==checkDownRightDepl(pawn,destPossibles);==
* ==checkDownLeftDepl(pawn,destPossibles);==

celles-ci vérifient si le déplacment du pions est possible dans les différentes directions,c'est a dire si la case d'arrivée est bien vide. La direction est Up pour les blancs et Douwn pour les noirs.On construit une liste des destinations possible qui servira à la construction de la liste des mouvement possibles.

La capture étant prioritaire,nous avons aussi réaliser une fonction qui cherche les déplacment possible pour un pion et qui implique une capture simple ou multiple.

* Encore une fois on a utilisé des fonctions du type 
==public void checkUpRightCapt(int upRight,List<Integer> emplCapturePossibles);==
Ces fonctions vérifient qu'il s'agit d'une capture c'est à dire si la case visée est occupée et si la case suivante est bien libre, elle renvoie une liste des emplacement de capture ce qui permet de l'utiliser récursivement pour effectuer une capure multiple.

Ensuite la fonction ==DeplacementAvecCapturePossibles(pawn,historique,king);== et la fonction ==prisesPossibles(int pawn,List<Integer> historique,boolean king);== réalise la pour la première la liste des destinations de capture et pour la seconde la liste des prises possibles. Elle s'appuie toutes deux sur un système d'historique qui permet d'éviter les sauts multiples au dessus du même pions et qui garde en mémoire les cases visitées, de plus les mouvement d'un roi étant plus complexes nous avons choisi d'ajouter à leurs paramètres un ==booléen king== qui permet de choisir le comportement de capture à adopter rapidement.

Toutes ses



**Pour MonteCarloTreeSearch :**
* Un nouveau constructeur de EvalNode => ==EvalNode(Game g, Move m)==. Afin de garder en mémoire le move qui a permis d'obtenir la game du fils, on garde le move désormais dans le node. Cela nous permet d'obtenir facilement le dernier move dans notre recherche de ==getBestMove()==.
* ==utcChild()== méthode dans EvalNode permettant de demander directement au père qu'elle est son meilleur fils ou s'il reste des moves possibles non joués.
   
 




## Difficultées rencontrées

* Pour EnglishDraughts, le plus dur a été de ne pas se perdre dans toutes les conditions des différents moves/captures possibles.

* Pour MonteCarloTreeSearch, nous avons été confronté à plusieurs problèmes, voici ceux qui ont été instructifs  :
    * "Un bâton plutôt qu'un arbre."* Nous ne nous soustrayions pas le nombre de move possible par le nombre d'enfants. Du coup, on regardait 1 move par noeud et continuait de descendre dans l'arbre, résultant à avoir un bâton.
    
    * Le joueur MCTS jouait les moves du joueur adverse, donc nous nous retrouvions avec des scénarios où les pions du joueur MCTS ne bougeaient quasiment jamais et ceux du joueur adverse quasiment tout le temps. Le problème venait du fait que nous faisions un play (donc la partie passe au joueur adverse) mais que ==updateStat()== prenait le joueur de la partie courante(donc le joueur adverse). Nous avons donc décidé de forcer le joueur dans ==updateStat()==.
    
    * Le joueur MCTS jouait plusieurs coups en un tour. Ici, le problème venait de notre ==getBestMove()==, nous cherchions à descendre tout en bas de l'arbre au lieu de prendre seulement un des fils du noeud root.

    
## Un coup marquant !

Lors d'un test entre un MCTS 5 secondes et un MCTS 1 seconde, nous avons été surpris par une action que nous n'avions même pas vue et voulions seulement la partager et l'analyser rapidement.

Le joueur blanc a décidé de sacrifier son pion en 10 forçant la capture par l'adversaire mais empêchant la capture multiple en effectuant le déplacement 17-13 (Là où notre première pensée en tant qu'humain aurait été de sauver le pion en 10). L'adverse a donc été forcé de tomber dans le piège mettant fin à la partie.

Ce choix est en somme tout à fait logique mais sur le coup nous ne l'avions ni vu, ni compris, jusqu'à voir la victoire des blancs. Cet étonnement et incompréhension de notre part vis-à-vis d'un simple programme rendent le concept d'IA encore plus impressionnant et un poil effrayant
![](https://i.imgur.com/9ccxMGU.png)
![](https://i.imgur.com/dFO1NXL.png)
