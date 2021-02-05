package fr.istic.ia.tp1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import fr.istic.ia.tp1.Game.PlayerId;

/**
 * Implementation of the English Draughts game.
 * 
 * @author Le Dourner/Guerin
 *
 */
public class EnglishDraughts extends Game {
	/**
	 * The checker board
	 */
	CheckerBoard board;

	/**
	 * The {@link PlayerId} of the current player {@link PlayerId#ONE} corresponds
	 * to the whites {@link PlayerId#TWO} corresponds to the blacks
	 */
	PlayerId playerId;
	boolean gameNullWhiteWin =false;
	boolean gameNullBlackWin =false;
	/**
	 * The current game turn (incremented each time the whites play)
	 */
	int nbTurn;

	/**
	 * The number of consecutive moves played only with kings and without capture
	 * (used to decide equality)
	 */
	int nbKingMovesWithoutCapture;

	/**
	 * Class representing a move in the English draughts game A move is an ArrayList
	 * of Integers, corresponding to the successive tile numbers (Manouri notation)
	 * toString is overrided to provide Manouri notation output.
	 * 
	 * @author vdrevell
	 *
	 */
	class DraughtsMove extends ArrayList<Integer> implements Game.Move {

		private static final long serialVersionUID = -8215846964873293714L;

		@Override
		public String toString() {
			Iterator<Integer> it = this.iterator();
			Integer from = it.next();
			StringBuffer sb = new StringBuffer();
			sb.append(from);
			while (it.hasNext()) {
				Integer to = it.next();
				if (board.neighborDownLeft(from) == to || board.neighborUpLeft(from) == to
						|| board.neighborDownRight(from) == to || board.neighborUpRight(from) == to) {
					sb.append('-');
				} else {
					sb.append('x');
				}
				sb.append(to);
				from = to;
			}
			return sb.toString();
		}
	}

	/**
	 * The default constructor: initializes a game on the standard 8x8 board.
	 */
	public EnglishDraughts() {
		this(8);
	}

	/**
	 * Constructor with custom boardSize (to play on a boardSize x boardSize
	 * checkerBoard).
	 * 
	 * @param boardSize See {@link CheckerBoard#CheckerBoard(int)} for valid board
	 *                  sizes.
	 */
	public EnglishDraughts(int boardSize) {
		this.board = new CheckerBoard(boardSize);
		this.playerId = PlayerId.ONE;
		this.nbTurn = 1;
		this.nbKingMovesWithoutCapture = 0;
	}

	/**
	 * Copy constructor
	 * 
	 * @param d The game to copy
	 */
	EnglishDraughts(EnglishDraughts d) {
		this.board = d.board.clone();
		this.playerId = d.playerId;
		this.nbTurn = d.nbTurn;
		this.nbKingMovesWithoutCapture = d.nbKingMovesWithoutCapture;
	}

	@Override
	public EnglishDraughts clone() {
		return new EnglishDraughts(this);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(nbTurn);
		sb.append(". ");
		sb.append(this.playerId == PlayerId.ONE ? "W" : "B");
		sb.append(":");
		sb.append(board.toString());
		return sb.toString();
	}

	@Override
	public String playerName(PlayerId playerId) {
		switch (playerId) {
		case ONE:
			return "Player with the whites";
		case TWO:
			return "Player with the blacks";
		case NONE:
		default:
			return "Nobody";
		}
	}

	@Override
	public String view() {
		return board.boardView() + "Turn #" + nbTurn + ". " + playerName(playerId) + " plays.\n";
	}

	/**
	 * Check if a tile is empty
	 * 
	 * @param square Tile number
	 * @return
	 */
	boolean isEmpty(int square) {
		return board.isEmpty(square);
	}

	/**
	 * Check if a tile is owned by adversary
	 * 
	 * @param square Tile number
	 * @return
	 */
	boolean isAdversary(int square) {
		switch (playerId) {
		case ONE:
			return board.isBlack(square);
		case TWO:
			return board.isWhite(square);
		default:
			return false;
		}
	}

	/**
	 * Check if a tile is owned by the current player
	 * 
	 * @param square Tile number
	 * @return
	 */
	boolean isMine(int square) {
		switch (playerId) {
		case ONE:
			return board.isWhite(square);
		case TWO:
			return board.isBlack(square);
		default:
			return false;
		}
	}

	/**
	 * Retrieve the list of positions of the pawns owned by the current player
	 * 
	 * @return The list of current player pawn positions
	 */
	ArrayList<Integer> myPawns() {
		ArrayList<Integer> res = new ArrayList<Integer>();
		for (int i = 1; i <= board.nbPlayableTiles(); ++i) {
			if (isMine(i)) {
				res.add(i);
			}
		}
		return res;
	}

	/**
	 * Generate the list of possible moves - first check moves with captures - if no
	 * capture possible, return displacement moves
	 */
	@Override
	public List<Move> possibleMoves() {
		ArrayList<Move> moves = new ArrayList<>();
		ArrayList<Integer> pawns = myPawns();
		List<Integer> historique = new ArrayList<>();
		List<DraughtsMove> prisePossible = new ArrayList<>();
		List<DraughtsMove> deplPossible = new ArrayList<>();
		boolean capture = false;
		boolean king = false;
		for(Integer i : pawns) {
			king = board.isKing(i);
			historique.clear();
			prisePossible.addAll(prisesPossibles(i, historique, king));
			if (!prisePossible.isEmpty()) {
				capture = true;
			}
		}
		if(!capture){
			for(Integer i : pawns){
				deplPossible.addAll(DeplacementSimplesPossibles2(i));
			}
			moves.addAll(deplPossible);
		}else{
			moves.addAll(prisePossible);
		}

		return moves;
	}
	//
	// TODO generate the list of possible moves
	//
	// Advice:
	// create two auxiliary functions :
	// - one for jump moves from a given position, with capture (and multi-capture).
	// Use recursive calls to explore all multiple capture possibilities
	// - one function that returns the displacement moves from a given position
	// (without capture)
	//
	/**
	 * Enregistre les differents deplacements simples selon l'emplacement d'un pion
	 * @param pawn emplacement du pion
	 * @return list des deplacements possibles
	 */
	public List<DraughtsMove> DeplacementSimplesPossibles2(int pawn){
		ArrayList<DraughtsMove> destPossibles = new ArrayList<>();
		
		if(board.isKing(pawn)){
			checkUpRightDepl(pawn,destPossibles);
			checkUpLeftDepl(pawn,destPossibles);
			checkDownRightDepl(pawn,destPossibles);
			checkDownLeftDepl(pawn,destPossibles);
			
		}else if(board.isBlack(pawn)){
			checkDownRightDepl(pawn,destPossibles);
			checkDownLeftDepl(pawn,destPossibles);
		}else{
			checkUpRightDepl(pawn,destPossibles);
			checkUpLeftDepl(pawn,destPossibles);
		}		
		return destPossibles;
	}
	/**
	 * Regarde si case Haute/Droite est vide et non au bord, si oui on ajoute le move a destPossibles
	 * @param pawn	emplacement du pion
	 * @param destPossibles list des deplacements disponibles a mettre a jour
	 */
	public void checkUpRightDepl(int pawn,List<DraughtsMove> destPossibles) {
		int dest = board.neighborUpRight(pawn);
		if(dest>0){
			boolean destB = board.isEmpty(dest);
			if(destB) {
				DraughtsMove temp = new DraughtsMove();
				temp.add(pawn);
				temp.add(dest);
				destPossibles.add(temp);
			}
		}
	}
	/**
	 * Regarde si case Haute/Gauche est vide et non au bord, si oui on ajoute le move a destPossibles
	 * @param pawn	emplacement du pion
	 * @param destPossibles list des deplacements disponibles a mettre a jour
	 */
	public void checkUpLeftDepl(int pawn,List<DraughtsMove> destPossibles) {
		int dest = board.neighborUpLeft(pawn);
		if(dest>0){
			boolean destB = board.isEmpty(dest);
			if(destB) {
				DraughtsMove temp = new DraughtsMove();
				temp.add(pawn);
				temp.add(dest);
				destPossibles.add(temp);
			}
		}

	}
	/**
	 * Regarde si case Bas/Droite est vide et non au bord, si oui on ajoute le move a destPossibles
	 * @param pawn	emplacement du pion
	 * @param destPossibles list des deplacements disponibles a mettre a jour
	 */
	public void checkDownRightDepl(int pawn,List<DraughtsMove> destPossibles) {
		int dest = board.neighborDownRight(pawn);
		if(dest>0){
			boolean destB = board.isEmpty(dest);
			if(destB) {
				DraughtsMove temp = new DraughtsMove();
				temp.add(pawn);
				temp.add(dest);
				destPossibles.add(temp);
			}
		}
	}
	/**
	 * Regarde si case Bas/Gauche est vide et non au bord, si oui on ajoute le move a destPossibles
	 * @param pawn	emplacement du pion
	 * @param destPossibles list des deplacements disponibles a mettre a jour
	 */
	public void checkDownLeftDepl(int pawn,List<DraughtsMove> destPossibles) {
		int dest = board.neighborDownLeft(pawn);
		if(dest>0){
			boolean destB = board.isEmpty(dest);
			if(destB) {
				DraughtsMove temp = new DraughtsMove();
				temp.add(pawn);
				temp.add(dest);
				destPossibles.add(temp);
			}
		}
	}
	/**
	 * Regarde si une capture depuis un pion est possible et n'est pas deja present dans historique
	 * @param pawn emplacement du pion
	 * @param historique case precedente du move
	 * @return list des emplacements apres capture possibles
	 */
	public List<Integer> DeplacementAvecCapturePossibles(int pawn,List<Integer> historique,boolean king){
		int upRight = board.neighborUpRight(pawn);
		int upLeft = board.neighborUpLeft(pawn);
		int downRight = board.neighborDownRight(pawn);
		int downLeft = board.neighborDownLeft(pawn);
		List<Integer> emplCapturePossibles = new ArrayList<>();
		if(king) {
			//Si Reine, on regarde dans toutes les diagonales
			checkUpRightCapt(upRight,emplCapturePossibles);
			checkUpLeftCapt(upLeft,emplCapturePossibles);
			checkDownRightCapt(downRight,emplCapturePossibles);
			checkDownLeftCapt(downLeft,emplCapturePossibles);
		}else if(board.isBlack(pawn)) {
			//Si Noir, on regarde diagonales basses
			checkDownRightCapt(downRight,emplCapturePossibles);
			checkDownLeftCapt(downLeft,emplCapturePossibles);
		}else {
			//Si Blanc, on regarde diagonales Hautes
			checkUpRightCapt(upRight,emplCapturePossibles);
			checkUpLeftCapt(upLeft,emplCapturePossibles);
		}
		SuppDoublon(historique,emplCapturePossibles);
		return emplCapturePossibles;
	}
	/**
	 * Regarde si une capture est possible vers Haut/Droite depuis un pion et met a jout res
	 * @param upRight emplacement du pion
	 * @param emplCapturePossibles
	 */
	public void checkUpRightCapt(int upRight,List<Integer> emplCapturePossibles) {

		if(upRight > 0) {
			if (isAdversary(upRight) && !board.isEmpty(upRight) && board.neighborUpRight(upRight)>0&&board.isEmpty(board.neighborUpRight(upRight))) {
				emplCapturePossibles.add(board.neighborUpRight(upRight));
			}
		}
	}
	
	/**
	 * Regarde si une capture est possible vers Haut/Gauche depuis un pion et met a jout res
	 * @param upLeft emplacement du pion
	 * @param emplCapturePossibles
	 */
	public void checkUpLeftCapt(int upLeft,List<Integer> emplCapturePossibles) {
		if(upLeft > 0){
			if(isAdversary(upLeft) && !board.isEmpty(upLeft) && board.neighborUpLeft(upLeft) > 0 && board.isEmpty(board.neighborUpLeft(upLeft))){
				emplCapturePossibles.add(board.neighborUpLeft(upLeft));
			}
		}
	}
	
	/**
	 * Regarde si une capture est possible vers Bas/Droite depuis un pion et met a jout res
	 * @param downRight emplacement du pion
	 * @param emplCapturePossibles
	 */
	public void checkDownRightCapt(int downRight,List<Integer> emplCapturePossibles) {
		if(downRight > 0) {
			if (isAdversary(downRight) &&!board.isEmpty(downRight) && board.neighborDownRight(downRight)>0 && board.isEmpty(board.neighborDownRight(downRight))) {
				emplCapturePossibles.add(board.neighborDownRight(downRight));
			}
		}
	}
	
	/**
	 * Regarde si une capture est possible vers Bas/Droite depuis un pion et met a jout res
	 * @param downLeft emplacement du pion
	 * @param emplCapturePossibles
	 */
	public void checkDownLeftCapt(int downLeft,List<Integer> emplCapturePossibles) {
		if(downLeft > 0) {
			if (isAdversary(downLeft) && !board.isEmpty(downLeft) && board.neighborDownLeft(downLeft)>0&& board.isEmpty(board.neighborDownLeft(downLeft))) {
				emplCapturePossibles.add(board.neighborDownLeft(downLeft));
			}
		}
	}
	/**
	 * Supprime dans resultat les elements deja present dans historique
	 * @param historique list integer
	 * @param resultat list integer
	 * @return list integer resultat sans les elements de historique 
	 */
	public List<Integer> SuppDoublon (List<Integer> historique, List<Integer> resultat){
		for(Integer i : historique) {
			if(resultat.contains(i)) {
				resultat.remove(i);
			}
		}
		return resultat;
	}
	
	/**
	 * Ajoute de maniere recursive les prises possibles depuis un pion
	 * @param pawn emplacement du pion
	 * @param historique list Integer, historique des deplacement du pion
	 * @return list DraughtMove des prises possibles
	 */
	public List<DraughtsMove> prisesPossibles(int pawn,List<Integer> historique,boolean king){
		ArrayList<DraughtsMove> moves = new ArrayList<>();
		List<Integer> destPossibles = DeplacementAvecCapturePossibles(pawn,historique,king);
		historique.add(pawn);
		for(int dest : destPossibles){
			List<DraughtsMove> movesPriseDest = prisesPossibles(dest,historique,king);
			if(movesPriseDest.isEmpty()){
				DraughtsMove temp = new DraughtsMove();
				temp.add(pawn);temp.add(dest);
				moves.add(temp);
			}else{
				for(DraughtsMove moveDest : movesPriseDest){
					DraughtsMove concat = new DraughtsMove();
					concat.add(pawn);concat.addAll(moveDest);
					moves.add(concat);
				}
			}
		}
		return moves;
	}

	@Override
	public void play(Move aMove) {
		if(aMove == null && playerId == PlayerId.ONE) {
			gameNullBlackWin = true;
			return;
		}
		if(aMove == null && playerId == PlayerId.TWO) {
			gameNullWhiteWin = true;
			return;
		}
		// Player should be valid
		if (playerId == PlayerId.NONE)
			return;
		// We will cast Move to DraughtsMove (kind of ArrayList<Integer>
		if (!(aMove instanceof DraughtsMove))
			return;
		// Cast and apply the move
		boolean capture = false;
		DraughtsMove move = (DraughtsMove) aMove;
		boolean king = board.isKing(move.get(0));
		
		// Move pawn and capture opponents
		for(int i =0;i<move.size()-1;i++){
			
			board.movePawn(move.get(i), move.get(i+1));
			int bitwin = board.squareBetween(move.get(i), move.get(i+1));
			if(bitwin > 0 && !board.isEmpty(bitwin)){
				board.removePawn(bitwin);
				capture = true;
			}
			if(capture && king) {
				nbKingMovesWithoutCapture = 0;
			}
			// Keep track of successive moves with kings wthout capture
			if(!capture && king ){
				nbKingMovesWithoutCapture++;
			}
			
		}

		// Promote to king if the pawn ends on the opposite of the board
		int lastPos = move.get(move.size()-1);
		if(playerId == PlayerId.ONE && board.inTopRow(lastPos)){
			board.crownPawn(lastPos);
		}
		if(playerId == PlayerId.TWO && board.inBottomRow(lastPos)){
			board.crownPawn(lastPos);
		}
		
		// Next player
		if(playerId == PlayerId.ONE){
			playerId = PlayerId.TWO;
		}else{
			playerId = PlayerId.ONE;
		}
		
		// Update nbTurn
		nbTurn++;
	}

	@Override
	public PlayerId player() {
		return playerId;
	}

	/**
	 * Get the winner (or null if the game is still going) Victory conditions are :
	 * - adversary with no more pawns or no move possibilities Null game condition
	 * (return PlayerId.NONE) is - more than 25 successive moves of only kings and
	 * without any capture
	 */
	@Override
	public PlayerId winner() {
		// || (playerId == PlayerId.TWO && possibleMoves().isEmpty())
		// || (playerId == PlayerId.ONE && possibleMoves().isEmpty())
		// return the winner ID if possible
		if(board.getBlackPawns().isEmpty() || gameNullWhiteWin){
			return PlayerId.ONE;
		}else if(board.getWhitePawns().isEmpty() || gameNullBlackWin ){
			return PlayerId.TWO;
		}else if(nbKingMovesWithoutCapture >= 25){
			// return PlayerId.NONE if the game is null
			return PlayerId.NONE;
		}else{
			// Return null is the game has not ended yet
			return null;
		}		
	}
}
