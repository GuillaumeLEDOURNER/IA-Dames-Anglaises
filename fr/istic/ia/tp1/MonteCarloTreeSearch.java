package fr.istic.ia.tp1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import fr.istic.ia.tp1.Game.Move;
import fr.istic.ia.tp1.Game.PlayerId;

/**
 * A class implementing a Monte-Carlo Tree Search method (MCTS) for playing two-player games ({@link Game}).
 * @author Le Dourner/Guerin 
 *
 */
public class MonteCarloTreeSearch {

	/**
	 * A class to represent an evaluation node in the MCTS tree.
	 * This is a member class so that each node can access the global statistics of the owning MCTS.
	 * @author Le Dourner/Guerin
	 *
	 */
	class EvalNode {
		/** The number of simulations run through this node */
		int n;

		/** The number of winning runs */
		double w;

		/** The game state corresponding to this node */
		Game game;

		/** The children of the node: the games states accessible by playing a move from this node state */
		ArrayList<EvalNode> children;

		Move m;

		/** 
		 * The only constructor of EvalNode.
		 * @param game The game state corresponding to this node.
		 */
		EvalNode(Game game) {
			this.game = game;
			children = new ArrayList<>();
			w = 0.0;
			n = 0;
		}
		EvalNode(Game game,Move m) {
			this.game = game;
			this.m = m;
			children = new ArrayList<>();
			w = 0.0;
			n = 0;
		}

		/**
		 * Compute the Upper Confidence Bound for Trees (UCT) value for the node.
		 * @return UCT value for the node
		 */
		double uct() {
			double max = 0.0;
			double temp = 0.0;

			for(EvalNode c : children){
				temp = (c.w/c.n)+Math.sqrt(2.0)*Math.sqrt(Math.log(this.n)/c.n);
				if(temp>max){
					max= temp;
				}
			}
			return max;

		}
		/**
		 * Compute the Upper Confidence Bound for Trees (UCT) value for the node.
		 * @return UCT EvalNode for the node
		 */
		EvalNode uctChild() {
			EvalNode choix = new EvalNode(game);
			double max = 0.0;
			double temp =-1.0;
			if(this.game.possibleMoves().size() != this.children.size()) {
				return null;
			}else {
				for(EvalNode c : children){
					temp = (c.w/c.n)+Math.sqrt(2.0)*Math.sqrt(Math.log(this.n)/c.n);
					if(temp>=max){
						max= temp;
						choix = c;
						//comparer avec le noeud vide aussi (
					}
				}
			}

			return choix;

		}

		/**
		 * "Score" of the node, i.e estimated probability of winning when moving to this node
		 * @return Estimated probability of win for the node
		 */
		double score() {
			/*if(n == 0) n = 1;
			if(w == 1) w = 1;*/
			return this.w/this.n;
		}

		/**
		 * Update the stats (n and w) of the node with the provided rollout results
		 * @param res
		 */
		void updateStats(RolloutResults res) {
			this.n = res.n + this.n;
			this.w = res.nbWins(this.game.player());
		}
	}

	/**
	 * A class to hold the results of the rollout phase
	 * Keeps the number of wins for each player and the number of simulations.
	 * @author vdrevell
	 *
	 */
	static class RolloutResults {
		/** The number of wins for player 1 {@link PlayerId#ONE}*/
		double win1;

		/** The number of wins for player 2 {@link PlayerId#TWO}*/
		double win2;

		/** The number of playouts */
		int n;

		/**
		 * The constructor
		 */
		public RolloutResults() {
			reset();
		}

		/**
		 * Reset results
		 */
		public void reset() {
			n = 0;
			win1 = 0.0;
			win2 = 0.0;
		}

		/**
		 * Add other results to this
		 * @param res The results to add
		 */
		public void add(RolloutResults res) {
			win1 += res.win1;
			win2 += res.win2;
			n += res.n;
		}

		/**
		 * Update playout statistics with a win of the player <code>winner</code>
		 * Also handles equality if <code>winner</code>={@link PlayerId#NONE}, adding 0.5 wins to each player
		 * @param winner
		 */
		public void update(PlayerId winner) {

			switch(winner){
			case ONE : win1+=1.0;
			break;
			case TWO : win2+=1.0;
			break;
			case NONE : win1+=0.5;win2+=0.5;
			break;
			}
		}

		/**
		 * Getter for the number of wins of a player
		 * @param playerId
		 * @return The number of wins of player <code>playerId</code>
		 */
		public double nbWins(PlayerId playerId) {
			switch (playerId) {
			case ONE: return win1;
			case TWO: return win2;
			default: return 0.0;
			}
		}

		/**
		 * Getter for the number of simulations
		 * @return The number of playouts
		 */
		public int nbSimulations() {
			return n;
		}
	}

	/**
	 * The root of the MCTS tree
	 */
	EvalNode root;

	/**
	 * The total number of performed simulations (rollouts)
	 */
	int nTotal;


	/**
	 * The constructor
	 * @param game
	 */
	public MonteCarloTreeSearch(Game game) {
		root = new EvalNode(game.clone());
		nTotal = 0;
	}

	/**
	 * Perform a single random playing rollout from the given game state
	 * @param game Initial game state. {@code game} will contain an ended game state when the function returns.
	 * @return The PlayerId of the winner (or NONE if equality or timeout).
	 */
	static PlayerId playRandomlyToEnd(Game game) {
		PlayerRandom rd = new PlayerRandom();
		EnglishDraughts gameCloned = (EnglishDraughts) game.clone();
		while(gameCloned.winner() == null) {
			//System.out.println(gameCloned.board.boardView());
			gameCloned.play(rd.play(gameCloned));
		}
		return gameCloned.winner();
	}


	/**
	 * Perform nbRuns rollouts from a game state, and returns the winning statistics for both players.
	 * @param game The initial game state to start with (not modified by the function)
	 * @param nbRuns The number of playouts to perform
	 * @return A RolloutResults object containing the number of wins for each player and the number of simulations
	 */
	static RolloutResults rollOut(final Game game, int nbRuns) {

		RolloutResults r = new RolloutResults();
		while(nbRuns>0){
			r.update(playRandomlyToEnd(game));
			r.n++;
			nbRuns--;
		}

		//Partir du début, faire un run 

		// maj le nombre de victoire en fonction du winner
		//venir au début et recommencer	
		return r;
	}

	/**
	 * Apply the MCTS algorithm during at most <code>timeLimitMillis</code> milliseconds to compute
	 * the MCTS tree statistics.
	 * @param timeLimitMillis Computation time limit in milliseconds
	 */
	public void evaluateTreeWithTimeLimit(int timeLimitMillis) {
		// Record function entry time
		long startTime = System.nanoTime();
		// Evaluate the tree until timeout
		while(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) < timeLimitMillis) {
			// Perform one MCTS step
			boolean canStop = evaluateTreeOnce();
			// Stop evaluating the tree if there is nothing more to explore
			if (canStop) {
				break;
			}

		}

		// Print some statistics
		System.out.println("Stopped search after " 
				+ TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + " ms. "
				+ "Root stats is " + root.w + "/" + root.n + String.format(" (%.2f%% loss)", 100.0*root.w/root.n));
	}

	/**
	 * Perform one MCTS step (selection, expansion(s), simulation(s), backpropagation
	 * @return <code>true</code> if there is no need for further exploration (to speed up end of games).
	 */
	public boolean evaluateTreeOnce() {

		EvalNode node = root;
		// List of visited nodes
		List<EvalNode> noeudVisite = new ArrayList<>();
		// Start from the root

		noeudVisite.add(node);
		// Selection (with UCT tree policy)
		
		EvalNode temp = null;

		while(!node.children.isEmpty()) {
			temp = node.uctChild();
			noeudVisite.add(node);
			if(temp == null) {
				// il reste des fils a parcourir
				break;
			}else if(!temp.game.possibleMoves().isEmpty()) {
				// tout les fils ont au moins 1 score et le meilleur n'est pas bloquer
				node = temp;
			}else {
				return false;
			}

		}

		List<Move> Poss = node.game.possibleMoves();
		for(EvalNode e : node.children){
			Poss.remove(e.m);
		}
		Random rd = new Random();
		Move m = Poss.get(rd.nextInt(Poss.size()));			
		Game g = node.game.clone();
		g.play(m);
		temp = new EvalNode(g,m);	



		// Simulate from new node(s)
		node.children.add(temp);
		RolloutResults r = rollOut(temp.game,2);
		temp.updateStats(r);
		// Backpropagate results
		for(EvalNode n : noeudVisite) {
			node.updateStats(r);
		}
		// Return false if tree evaluation should continue
		return false;
	}

	/**
	 * Select the best move to play, given the current MCTS tree playout statistics
	 * @return The best move to play from the current MCTS tree state.
	 */
	public Move getBestMove() {
		double max = -1.0;
		EvalNode node = root;
		while(!node.children.isEmpty()) {	
			System.out.println("size + " + node.children.size());
			EvalNode temp = node;
			for(EvalNode n : node.children){
				
				System.out.println("max = " + max);
				System.out.println("score = " + n.score());
				if(max < n.score()) {

					max = n.score();
					temp = n;
				}
			}
			if(temp.equals(node)) {
				break;
			}else {
				node = temp;
				max = -1;
			}
		}
		return node.m;
	}
	/**
	 * Get a few stats about the MTS tree and the possible moves scores
	 * @return A string containing MCTS stats
	 */
	public String stats() {
		String str = "MCTS with " + nTotal + " evals\n";
		Iterator<Move> itMove = root.game.possibleMoves().iterator();
		for (EvalNode node : root.children) {
			Move move = itMove.next();
			double score = node.score();
			str += move + " : " + score + " (" + node.w + "/" + node.n + ")\n";
		}
		return str;
	}
}
