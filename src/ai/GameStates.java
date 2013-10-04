package ai;

import java.io.PrintWriter;
import java.util.*;

import logic.Board;
/**
 * Game tree class, contains all the utility methods to manage the tree without entering inside the State itself.
 * 
 * @author Samuele Pontremoli, Alessio Mecca, Marianna Progano'
 *
 * @param <State> The inner-class representing the State of the game, the tree is made of this object.
 */
public class GameStates<State>
{
	//The main game tree, made from a state root, an Hash map for parent associations
	//and another hashmap for childhood associations
	private State root;
	private final Map<State,State> parent = new HashMap<State,State>();
	private final Map<State,ArrayList<State>> children = new HashMap<State,ArrayList<State>>();
	
	//Printing object
	private PrintWriter pw;
	
	/**
	 * Creates a tree with root as the only node.
	 * 
	 * @param root The tree root
	 */
	public GameStates(State root) 
	{
		this.root = root;
		parent.put(root, null);
		children.put(root, new ArrayList<State>());
	}
	
	/**
	 * Adds a child to a specific node of the tree.
	 * 
	 * @param p Parent state
	 * @param child Child state to be added
	 * @return true/false
	 */
	public boolean addChild(State p, State child) 
	{
		if (p == null || child == null || !parent.containsKey(p) || parent.containsKey(child))
			throw new IllegalArgumentException();
		if (children.get(p).contains(child))
			return false;
		parent.put(child, p);
		children.get(p).add(child);
		children.put(child, new ArrayList<State>());
		return true;
	}

	/**
	 * Returns tree root
	 * 
	 * @return The root
	 */
	public State getRoot() 
	{
		return root;
	}

	/**
	 * Controls if a given state is a node of the tree. Unused but useful.
	 * 
	 * @param u The state to be checked
	 * @return true/false
	 */
	public boolean isStateNode(State u) 
	{
		return parent.containsKey(u);
	}

	/**
	 * Returns the parent of a given state node.
	 * 
	 * @param u The state in input
	 * @return The parent state if contained in the tree
	 */
	public State getParent(State u) 
	{
		//SHOULD NEVER HAPPEN HERE
		if (!parent.containsKey(u))
			throw new IllegalArgumentException();
		return parent.get(u);
	}
	
	/**
	 * Return an arraylist copy containing all the children of a given state node.
	 * 
	 * @param u Parent node
	 * @return The parent state if contained in the tree
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<State> getChildren(State u) 
	{
		//SHOULD NEVER HAPPEN HERE
		if (!parent.containsKey(u))
			throw new IllegalArgumentException();
		return (ArrayList<GameStates.State>) children.get(u).clone();
	}
	
	/**
	 * Function called by the print-tree button mainly, but used for debug also. Prints the game tree
	 * in two ways. Calling printTreeRecursive prints all the nodes with the heuristic values and column
	 * number of the last move. Calling printTreeRecursiveExtended prints also the values in the board.
	 * The last option could be a little messy to read. Not that the first one is easy.
	 * 
	 * @param writer A PrintWriter object, in the application is used to write on a .txt file.
	 */
	public void printTree(PrintWriter writer)
	{
		pw = writer;
		if(pw!=null)
			printTreeRecursive(getRoot(), "",false);
		//printTreeRecursiveExtended(getRoot(), "",false);
		pw.close();
	}

	//Recursive method that prints all the search tree, including all the board values
	@SuppressWarnings("unused")
	private void printTreeRecursiveExtended(State u, String prefix, boolean isLast) {
		if (!prefix.isEmpty()) 
			pw.print(prefix + ">>>>>");
		pw.println(u.getBoard()+" - Column: "+u.actionColumn+" - State Value: "+u.val);
		ArrayList<ai.GameStates.State> children = getChildren(u);
		if(isLast) {
			prefix = prefix.substring(0, prefix.length() - 1)+ " ";
			if
				(children.size() == 0) pw.println(prefix);
		}
		prefix += " |";
		for(Iterator<State> i = children.iterator() ; i.hasNext() ; )
			printTreeRecursive(i.next(), prefix, !i.hasNext());
	}
	
	//Recursive method that prints some values for each state
	private void printTreeRecursive(State u, String prefix, boolean isLast) {
		if (!prefix.isEmpty()) 
			pw.print(prefix + ">>>>>");
		pw.println("Column: "+u.actionColumn+" - State Value: "+u.val);
		ArrayList<State> children = getChildren(u);
		if(isLast) {
			prefix = prefix.substring(0, prefix.length() - 1)+ " ";
			if
				(children.size() == 0) pw.println(prefix);
		}
		prefix += " |";
		for(Iterator<State> i = children.iterator() ; i.hasNext() ; )
			printTreeRecursive(i.next(), prefix, !i.hasNext());
	}
	
	/**
	 * Inner-Class State, contains all the information for the state of the game it wants to represent
	 * (h value, depth counter, last move, player, board matrix).
	 * 
	 * @author Samuele Pontremoli, Alessio Mecca, Marianna Progano'
	 *
	 */
	public static class State implements Comparable<State>
	{
		
		private Board board;					//Problem state matrix
		private int player;						//Current player
		public int val;                       	//Heurist
		public int moveCount;					//Counter for tree depth
		public int actionColumn;				//Last move made
		
		/**
		 * Constructor to build up a state from scratch.
		 * 
		 * @param g Game board
		 * @param p Actual player
		 */
		public State(Board g, int p)
		{	
			board = new Board(g);
			player=p;
			moveCount = 0;
			actionColumn = -1;
		}

		/**
		 * Constructor to build up a state from another one.
		 * 
		 * @param gs State to be copied
		 * @param c The column of the move that differs from the state gs
		 * @param p The player who made the move
		 */
		public State(State gs, int c, int p)
		{
			player=p;
			board = new Board(gs.getBoard());
			board.insert(player, c);
			moveCount = gs.moveCount+1;
			actionColumn = c;
		}
		
		@Override
		public int compareTo(State o)
		{
			int delta=val-o.val;
			if(delta>0)
				return 1;
			if(delta<0)
				return -1;
			return 0;
		}
		
		//Setter-Getter
		public Board getBoard()
		{
			return board;
		}
		public int getPlayer() {
			return player;
		}
	}
	
}
	
