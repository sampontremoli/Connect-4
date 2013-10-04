package ai;

import java.util.*;

import logic.Board;
import logic.Checklist;

import ai.GameStates.State;

import static data.Constants.*;

/**
 * Main AI class, contains all the research methods. The main one is AlphaBetaPruning(), which returns
 * the move to be made by the AI Player. It calls the recursive methods minValue and maxValue to do so.
 * 
 * @author Samuele Pontremoli, Alessio Mecca, Marianna Progano'
 *
 */
public class AI
{
	//Game Tree object
	private GameStates<State> theGame;
	
	//Actual Player & Opposite
	private int player;
	private int opponent;
	
	//Search Cut-Level
	private int MAXDEPTH;
	
	//Column weights[1=val*1, 2=val*2, 3=val*3, 4=val*4, 5=val*3, 6=val*2, 7=val*1]
	public static final int[] weights={1,2,3,4,3,2,1};	
	
	//Node count
	private int nodes=1;
	
	//Used to call heuristic2() if the player is AI2
	private int AI_VALUE;
	
	//Synchronized flag to stop the research if reset/newgame button are pressed
	private volatile boolean interrupted;
	
	/**
	 * Ai constructor.
	 * 
	 * @param g The logic board before the move
	 * @param p The actual player
	 * @param d The maximum depth
	 * @param aiv Determines the actual ai player(1 or 2)
	 */
	public AI(Board g, int p, int d, int aiv)
	{	
		interrupted=false;
		player = p;
		opponent = (player+1)%2;
		GameStates.State root = new GameStates.State(g, opponent);
		theGame = new GameStates<State>(root);
		MAXDEPTH = d;
		AI_VALUE = aiv;
	}

	/**
	 * The method that starts the research. After getting the maximum value j from the research, scans all the first
	 * level of the tree(which are AI move possibilities), and if there are two values >= j it chooses randomly between them
	 * which move to make.
	 * 
	 * @return A int which stands for the move
	 */
    public int AlphaBetaPruning()
    {
        int j = maxValue(theGame.getRoot(), Integer.MIN_VALUE, Integer.MAX_VALUE); //Start recursively the travel through the three
    	//int j = maxValue(theGame.getRoot());			//PURE MINMAX CALL
        
        ArrayList<GameStates.State> child = theGame.getChildren(theGame.getRoot());
        ArrayList<GameStates.State> bestOnes=new ArrayList<GameStates.State>();
        
        //Multiply the highest heuristic state vals with weights
        for(GameStates.State elem : child)
        	{
        	if (elem.val >= j)
        	{
        		elem.val=Math.abs(weights[elem.actionColumn]*elem.val);
                bestOnes.add(elem);
            }
        }
        GameStates.State best=bestOnes.get(0);
        
        //If more than one best-state, chooses randomly
        if(bestOnes.size()>1)
        {
        	Collections.sort(bestOnes);
        	if(bestOnes.get(bestOnes.size()-1).val == bestOnes.get(bestOnes.size()-2).val){
        		best=(Math.random()>0.5 ? bestOnes.get(bestOnes.size()-1) : bestOnes.get(bestOnes.size()-2));
        	}
        	else
        	{
        		best=bestOnes.get(bestOnes.size()-1);
        	}
        }
        if(!interrupted)
        	return best.actionColumn;
        return -1;
    }
    
    /*PURE MINIMAX CALL (NO PRUNING)
    
    public int maxValue(GameStates.State curr)
	{
		if(terminationTest(curr))
			return curr.val;
		for(int i=0; i<BASE; i++)
		{
			if(!curr.getBoard().isFull(i))
			{	
				nodes++;
				GameStates.State gsmax = new GameStates.State(curr, i, player);
				theGame.addChild(curr, gsmax);
				int min=minValue(gsmax);
				curr.val=Math.max(curr.val, min);
			}
		}
		return curr.val;
	}
	
	public int minValue(GameStates.State curr)
	{
		if(terminationTest(curr))
			return eval(curr);
		for(int i=0; i<BASE; i++)
		{
			if(!curr.getBoard().isFull(i))
			{
				nodes++;
				GameStates.State gsmin = new GameStates.State(curr, i, opponent);
				theGame.addChild(curr, gsmin);
				curr.val=Math.min(curr.val, maxValue(gsmin));
			}
		}
		return curr.val;
	}
   */
    
    /**
     * First Minimax function (in which AI evaluates states from the actual player point of view)
     * 
     * @param curr The state to be expanded (could come frome alphabeta(root) or from minValue()
     * @param alpha Minimum value for pruning
     * @param beta Maximum value for pruning
     * @return the maximum value for a level of the tree
     */
	private int maxValue(GameStates.State curr, int alpha, int beta)
	{
		if(terminationTest(curr))
			return curr.val;
		curr.val = Integer.MIN_VALUE;
		for(int i=0; i<BASE; i++)
		{
			if(!curr.getBoard().isFull(i))
			{	
				nodes++;
				GameStates.State gsmax = new GameStates.State(curr, i, player);
				theGame.addChild(curr, gsmax);
				int min=minValue(gsmax, alpha, beta);
				curr.val=Math.max(curr.val, min);
				if (curr.val >= beta)
					return curr.val;
                alpha = Math.max(alpha, curr.val);
			}
		}
		return curr.val;
	}
	
	/**
	 * Second Minimax function (in which AI evaluates states from the opponent point of view)
	 * 
	 * @param curr The state to be expanded (could come only from maxValue())
	 * @param alpha Minimum value for pruning
	 * @param beta Maximum value for pruning
	 * @return the minimum value for a level of the tree
	 */
	private int minValue(GameStates.State curr, int alpha, int beta)
	{
		if(terminationTest(curr))
			return curr.val;
		curr.val = Integer.MAX_VALUE;
		for(int i=0; i<BASE; i++)
		{
			if(!curr.getBoard().isFull(i))
			{
				nodes++;
				GameStates.State gsmin = new GameStates.State(curr, i, opponent);
				theGame.addChild(curr, gsmin);
				curr.val=Math.min(curr.val, maxValue(gsmin, alpha, beta));
				if (curr.val <= alpha) 
					return curr.val;
                beta = Math.min(beta, curr.val);
			}
		}
		return curr.val;
	}
    
	/**
	 * Recursion blocker. It stops if we have a terminal state, or if we reached the maximum depth
	 * 
	 * @param state The state to be evaluated
	 * @return true or false if terminal/not terminal state is found
	 */
    private boolean terminationTest(GameStates.State state)
    {
    	//If state is tree-root don't cut
    	if(state.actionColumn==-1)
    		return false;
        state.val = eval(state); 					//Evaluation
        //Since we use MAXVALUE/3-MINVALUE/3 to force moves, MAXVALUE/4 should be good to cutoff
        if(Math.abs(state.val)>=Integer.MAX_VALUE/4)
        	return true; 							//We have a terminal state
        //If we have reached the MAXDEPTH then cut
        if(MAXDEPTH <= state.moveCount)
        	return true;  							//We have a terminal state
        return false;
    }

    /**
     * Starting evaluation function. Follows Allis' advices. First checks if there's a winning move for the player,
     * if so, chooses that move. Then check if there's a winning move for the opponent. Even in this case it chooses
     * to block. Last it calls the heuristic function if none of this cases is encountered.
     * 
     * @param curr The state to be evaluated
     * @return The evaluation for the current state
     */
	private int eval(GameStates.State curr)
	{	
		//MAXVALUE/3-MINVALUE/3 Seems to be enough to force moves
		if(checkWinning(curr, player))
		{
			if(curr.moveCount==1)
				return Integer.MAX_VALUE/3;
		}
		if(checkWinning(curr, opponent))
		{
			if(curr.moveCount==2)
				return Integer.MIN_VALUE/3;
		}
		int h=0;
		//If the player is the AI2, it calls heuristic2()
		if(AI_VALUE == AI2_TURN)
			h+=heuristic2(curr, player, opponent);
		//If the player is the AI1, it calls heuristic1()
		else
			h+=heuristic(curr, player, opponent);
		return h;
	}
	
	/**
	 * Heuristic function, common to both AI1 and AI2. It tries to remain admissible and consistent with the problem.
	 * For each possible four in the board, it scans for the ones taken by the player and the ones taken by the opponent.
	 * Then it evaluates only the ones that could really be closed and that can take to a win/loss.
	 * 
	 * @param curr The state to be evaluated
	 * @param player Actual player
	 * @param opponent The opponent
	 * @return The evaluation for the current state
	 */
	private int heuristic(GameStates.State curr, int player, int opponent) 
	{
		int h=0;
		int[] lm = curr.getBoard().legalMoves();
		
		//For each four-in-a-row in the state(69 at the beginning)
		for(int i=0; i<curr.getBoard().getCheck().getFours().size(); i++)
		{
			Checklist.FourInARow four = curr.getBoard().getCheck().getFours().get(i);
			
			/* First heuristic evaluation:
			 * h+=(AI's one in a row * 1)+(AI's two in a row * 4)+(AI's three in a row * 4)
			 * h-=(Opponent's one in a row * 1)+(Opponent's two in a row * 4)+(Opponent's three in a row * 4)
			 * if there's a vertical 3 threat it has less value than the others
			 */
			int[] app=four.count(player);
			int[] app2=four.count(opponent);
			switch(app[0])
			{
				case 1:
					h+=app[0]; break;
				case 2:
					h+=app[0]*4; break;
				case 3:
					if(four.getControl()==0)
						h+=app[0]*4;
					else
						h+=app[0]*32; break;
				case 4:
					h+=app[0]*64; break;
				default:
					break;
			}
			switch(app2[0])
			{
				case 1:
					h-=app2[0]; break;
				case 2:
					h-=app2[0]*4; break;
				case 3:
					if(four.getControl()==0)
						h-=app2[0]*4;
					else
						h-=app2[0]*32; break;
				case 4:
					h-=app2[0]*64; break;
				default:
					break;
			}
			
			/* Second heuristic evaluation:
			 * 	- h-=10000 if (Actual move --> makes opponent close a four in the next move)
			 * 	- h-=500 if (Actual move --> makes opponent close a three in the next move)
			 */
			int[] counts = four.count(opponent);
			if(counts[0]==2)
			{
				if(lm[counts[2]]!=-1 && lm[counts[2]]==counts[1])
				{
					h-=500;
				}
			}
			if(counts[0]==3)
			{
				if(lm[counts[2]]!=-1 && lm[counts[2]]==counts[1])
				{
					h-=10000;
				}
			}
		}
		return h;
	}
	
	/**
	 * Second heuristic function. A little bit more risky, introduces even and odd rows concept for AI2
	 * 
	 * @param curr The state to be evaluated
	 * @param player Actual player(could only be AI2)
	 * @param opponent Actual player
	 * @return The evaluation for the current state
	 */
	private int heuristic2(GameStates.State curr, int player, int opponent)
	{
		int h=0;
		int[] lm = curr.getBoard().legalMoves();
		
		//For each four-in-a-row in the state(69 at the beginning)
		for(int i=0; i<curr.getBoard().getCheck().getFours().size(); i++)
		{
			Checklist.FourInARow four = curr.getBoard().getCheck().getFours().get(i);
			
			/* First heuristic evaluation:
			 * h+=(AI's one in a row * 1)+(AI's two in a row * 4)+(AI's three in a row * 4)
			 * h-=(Opponent's one in a row * 1)+(Opponent's two in a row * 4)+(Opponent's three in a row * 4)
			 */
			int[] app=four.count(player);
			int[] app2=four.count(opponent);
			switch(app[0])
			{
				case 1:
					h+=app[0]; break;
				case 2:
					h+=app[0]*4; break;
				case 3:
					h+=app[0]*32; break;
				case 4:
					h+=app[0]*64; break;
				default:
					break;
			}
			switch(app2[0])
			{
				case 1:
					h-=app2[0]; break;
				case 2:
					h-=app2[0]*4; break;
				case 3:
					h-=app2[0]*32; break;
				case 4:
					h-=app2[0]*64; break;
				default:
					break;
			}
			
			/* Second heuristic evaluation:
			 * 	- h-=10000 if (Actual move --> makes opponent close a four in the next move)
			 * 	- h-=500 if (Actual move --> makes opponent close a three in the next move)
			 */
			int[] counts = four.count(opponent);
			if(counts[0]==2)
			{
				if(lm[counts[2]]!=-1 && lm[counts[2]]==counts[1])
				{
					h-=500;
				}
			}
			if(counts[0]==3)
			{
				if(lm[counts[2]]!=-1 && lm[counts[2]]==counts[1])
				{
					h-=10000;
				}
			}
		}

		int[] lmm = curr.getBoard().legalMoves();
		int row = (lmm[curr.actionColumn])+1;
		//EVEN ROW
		if(row%2 == 0)
		{
			if(player==0)
				h+=5000;
			else
				h-=5000;
		}
		//ODD ROW
		else
		{
			if(player==1)
				h+=5000;
			else
				h-=5000;
		}
		return h;
	}
	
	/**
	 * Returns true if there's a four that have been cloused (there are four men of the same color in it)
	 * 
	 * @param curr The state to be checked
	 * @param player Actual player
	 * @return true/false is curr is a winning state or not
	 */
	private boolean checkWinning(GameStates.State curr, int player)
	{
		for(int i=0; i<curr.getBoard().getCheck().getFours().size(); i++)
		{
			int[] counts = curr.getBoard().getCheck().getFours().get(i).count(player);
			if(counts[0]==4)
			{
				return true;
			}
		}
		return false;
	}
	
	//Setter-Getter
	
	public synchronized void setInterrupted(boolean i)
	{
		interrupted = i;
	}
	
	public synchronized boolean getInterrupted()
	{
		return interrupted;
	}
	
	public GameStates<State> getTheGame()
	{
		return theGame;
	}
	
	public int getNodes()
	{
		return nodes;
	}
	
}