package fr.istic.ia.tp1;

import java.util.List;

import org.junit.jupiter.api.Test;

import java.util.HashSet;



import fr.istic.ia.tp1.Game.PlayerId;


import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

public class TestEnglishDraughts {
	static void clearBoard(CheckerBoard board) {
		for (int i=1; i<=board.nbPlayableTiles(); ++i) {
			board.removePawn(i);
		}
	}
	
	static void setBoard(CheckerBoard board, List<Integer> whites,  List<Integer> whiteKings, 
			 List<Integer> blacks,  List<Integer> blackKings) {
		clearBoard(board);
		for (int i : whites) { board.set(i, CheckerBoard.WHITE_CHECKER); }
		for (int i : whiteKings) { board.set(i, CheckerBoard.WHITE_KING); }
		for (int i : blacks) { board.set(i, CheckerBoard.BLACK_CHECKER); }
		for (int i : blackKings) { board.set(i, CheckerBoard.BLACK_KING); }
	}
	
	static EnglishDraughts.DraughtsMove newMove(EnglishDraughts game, List<Integer> steps) {
		EnglishDraughts.DraughtsMove move = game.new DraughtsMove();
		for (int i : steps) {
			move.add(i);
		}
		return move;
	}
	
	@Test
	public void testPossibleMovesInitWhite() {
		EnglishDraughts draughts = new EnglishDraughts(8);
		List<Game.Move> initMoves = asList(
				newMove(draughts, asList(21,17)),
				newMove(draughts, asList(22,17)),
				newMove(draughts, asList(22,18)),
				newMove(draughts, asList(23,18)),
				newMove(draughts, asList(23,19)),
				newMove(draughts, asList(24,19)),
				newMove(draughts, asList(24,20))
			);
		List<Game.Move> moves = draughts.possibleMoves();
		//"Init moves white", 
		//"Duplicate moves",
		assertEquals(new HashSet<Game.Move>(initMoves), new HashSet<Game.Move>(moves));
		assertEquals( initMoves.size(), moves.size());
	}

	@Test
	public void testPossibleMovesInitBlack() {
		EnglishDraughts draughts = new EnglishDraughts(8);
		draughts.play(newMove(draughts, asList(21,17)));
		assertEquals(PlayerId.TWO, draughts.player());
		//"Black should play after white", 
		List<Game.Move> initMoves = asList(
				newMove(draughts, asList(9,13)),
				newMove(draughts, asList(9,14)),
				newMove(draughts, asList(10,14)),
				newMove(draughts, asList(10,15)),
				newMove(draughts, asList(11,15)),
				newMove(draughts, asList(11,16)),
				newMove(draughts, asList(12,16))
			);
		List<Game.Move> moves = draughts.possibleMoves();
		//"Init moves black",
		assertEquals( new HashSet<Game.Move>(initMoves), new HashSet<Game.Move>(moves));
		assertEquals( initMoves.size(), moves.size());
		//"Duplicate moves",
	}
	
	@Test
	public void testPossibleMovesSimpleTake() {
		EnglishDraughts draughts = new EnglishDraughts(8);
		setBoard(draughts.board, asList(16,18,19), asList(7), asList(11,15), asList(24));
		
		List<Game.Move> initMoves = asList( newMove(draughts, asList(19,10)) );
		List<Game.Move> moves = draughts.possibleMoves();
		//"Simple take: 19x10", 
		assertEquals(new HashSet<Game.Move>(initMoves), new HashSet<Game.Move>(moves));
		assertEquals( initMoves.size(), moves.size());
		//"Duplicate moves",
	}
	
	@Test
	public void testPossibleMovesSimpleTakeKing() {
		EnglishDraughts draughts = new EnglishDraughts(8);
		setBoard(draughts.board, asList(16,18,19), asList(7), asList(10,15), asList(24));
		
		List<Game.Move> initMoves = asList( 
				newMove(draughts, asList(18,11)),
				newMove(draughts, asList(7,14)) );
		List<Game.Move> moves = draughts.possibleMoves();
		//"Simple take: pawn and king", 
		assertEquals(new HashSet<Game.Move>(initMoves), new HashSet<Game.Move>(moves));
		assertEquals(initMoves.size(), moves.size());
		//"Duplicate moves", 
	}
	
	@Test
	public void testPossibleMovesMutipleTake() {
		EnglishDraughts draughts = new EnglishDraughts(8);
		setBoard(draughts.board, asList(18,19), asList(10), asList(6,8,15), asList(7));
		
		List<Game.Move> initMoves = asList( 
				newMove(draughts, asList(10,1)),
				newMove(draughts, asList(10,3,12)),
				newMove(draughts, asList(18,11,2)),
				newMove(draughts, asList(18,11,4)) );
		List<Game.Move> moves = draughts.possibleMoves();
		//"Multiple take", 
		assertEquals(new HashSet<Game.Move>(initMoves), new HashSet<Game.Move>(moves));
		assertEquals(initMoves.size(), moves.size());
		//"Duplicate moves", 
	}
	
	@Test
	public void testPossibleMovesMutipleTake2() {
		EnglishDraughts draughts = new EnglishDraughts(8);
		setBoard(draughts.board, asList(18,19), asList(1), asList(6,8,15), asList(7));
		
		List<Game.Move> initMoves = asList( 
				newMove(draughts, asList(1,10,3,12)),
				newMove(draughts, asList(19,10,3)),
				newMove(draughts, asList(18,11,2)),
				newMove(draughts, asList(18,11,4)) );
		List<Game.Move> moves = draughts.possibleMoves();
		/* for (Game.Move move : moves) {
			System.out.println( move );
		} */
		//"Multiple take", 
		assertEquals(new HashSet<Game.Move>(initMoves), new HashSet<Game.Move>(moves));
		assertEquals(initMoves.size(), moves.size());
		//"Duplicate moves", 
	}
	
	@Test
	public void testWinner() {
		EnglishDraughts draughts = new EnglishDraughts(8);
		setBoard(draughts.board, asList(), asList(), asList(18,19), asList(1));
		assertEquals(PlayerId.TWO, draughts.winner());
		//"Only blacks on board", 
	}
	
	@Test
	public void test25MovesEquality() {
		EnglishDraughts draughts = new EnglishDraughts(8);
		setBoard(draughts.board, asList(), asList(22), asList(), asList(10));
		assertEquals(null, draughts.winner());
		//"Game is not finished yet", 
		for (int i=0; i<6; ++i) {
			draughts.play(newMove(draughts, asList(22, 25))); // white king
			draughts.play(newMove(draughts, asList(10, 7))); // black king
			draughts.play(newMove(draughts, asList(25, 22))); // white king
			draughts.play(newMove(draughts, asList(7, 10))); // black king
		}
		draughts.play(newMove(draughts, asList(22, 25))); // white king
		assertEquals(PlayerId.NONE, draughts.winner());
		//"Equality", 
	}
	
	@Test
	public void testPlaySimpleMoves() {
		EnglishDraughts draughts = new EnglishDraughts(8);
		
		draughts.play(newMove(draughts, asList(21, 17)));
		//"Leave 21", 
		assertTrue(draughts.board.isEmpty(21));
		//"Go to 17", 
		assertEquals(CheckerBoard.WHITE_CHECKER, draughts.board.get(17));
		
		draughts.play(newMove(draughts, asList(10, 14)));
		//"Leave 10", 
		assertTrue(draughts.board.isEmpty(10));
		//"Go to 14",
		assertEquals( CheckerBoard.BLACK_CHECKER, draughts.board.get(14));
	}
	
	@Test
	public void testPlaySimpleCapture() {
		EnglishDraughts draughts = new EnglishDraughts(8);
		setBoard(draughts.board, asList(16,18,19), asList(7), asList(11,15), asList(24));
		
		draughts.play(newMove(draughts, asList(19, 10)));
		//"Leave 19", 
		assertTrue(draughts.board.isEmpty(19));
		//"Jump to 10", 
		assertEquals(CheckerBoard.WHITE_CHECKER, draughts.board.get(10));
		//"Remove adversary from 15",
		assertTrue( draughts.board.isEmpty(15));
	}
	
	@Test
	public void testPlayMutipleTakeCrown() {
		EnglishDraughts draughts = new EnglishDraughts(8);
		setBoard(draughts.board, asList(18,19), asList(10), asList(6,8,15), asList(7));
		draughts.play(newMove(draughts, asList(18,11,4)));
		//"Leave 18",
		assertTrue( draughts.board.isEmpty(18));
		//"Remove adversary from 15", 
		assertTrue(draughts.board.isEmpty(15));
		//"Pass by 11", 
		assertTrue(draughts.board.isEmpty(11));
		//"Remove adversary from 8", 
		assertTrue(draughts.board.isEmpty(8));
		//"Finish in 4 and get crowned", 
		assertEquals(CheckerBoard.WHITE_KING, draughts.board.get(4));
	}
}
