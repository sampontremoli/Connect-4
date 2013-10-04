package logic;

import ai.AI;
import ai.GameStates.State;

/**
 * AiThread class, it implements Runnable to be able to run over the Event Dispatch Thread,
 * so that user can interact with the UI, including menus, without crashing everything.
 * 
 * @author Samuele Pontremoli, Alessio Mecca, Marianna Progano'
 *
 */
public class AiThread implements Runnable
{	
	private GameLoop game;
	private Board board;
	
	private final int AI_VALUE;
	private int turn;
	private int depth;
	private AI ai;
	
	//Used to display time needed to perform a move
	private double time;
	
	/**
	 * Constructor of AiThread.
	 * 
	 * @param g The gameloop to return values.
	 * @param b The board needs to be passed to AI
	 * @param aiv The type of AI (1 or 2)
	 * @param t The actual turn
	 * @param d Tree depth
	 */
	public AiThread(GameLoop g, Board b, int aiv, int t, int d)
	{
		game = g;
		board=b;
		AI_VALUE=aiv;
		turn = t;
		depth = d;
	}

	/**
	 * Runs the search
	 */
	public void run()
	{
		game.deleteMouseListener();
		ai = new AI(board, turn%2, depth, AI_VALUE);
		double start = (double)System.currentTimeMillis();
		int move=ai.AlphaBetaPruning();
		time = (System.currentTimeMillis()-start)/1000;
		
		//Waits until one second if the move is made in <1s time. This because
		//during the late game 2 AI in VS mode are really fast, and you can't enjoy the match
		synchronized(this)
		{
			if(time<1.0)
			{
				try
				{
					this.wait((long) (1000-time*1000));
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		//Moves ONLY if not interrupted. Interrupt happens if, for example, user clicked
		//on reset button during a match involving some AI, causing the match to stop.
		if(move!=-1 && ai.getInterrupted()!=true)
		{
			game.theMove(move);
			sendStats(move);
			game.start();
		}
	}
	
	/**
	 * Calls stats append method from StatsHelper object.
	 * 
	 * @param move The move made, which has to be printed on screen
	 */
	private void sendStats(int move) 
	{
		game.getStats().appendStats("<table align='center'><tr><td><h"+(turn%2==0?5:6)+"> - AI "+(AI_VALUE-1)+" moves to column "+(move+1)+" in "+time+" s"+"</h"+(turn%2==0?5:6)+"></td>");
		for(State elem : ai.getTheGame().getChildren(ai.getTheGame().getRoot()))
		{
			game.getStats().appendStats("</tr><p><tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Column "+(elem.actionColumn+1)+": Heuristic = "+elem.val+"</td></tr>");
		}
		game.getStats().appendStats("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Tree nodes analyzed: "+ai.getNodes()+"</td></tr></p></table>");
	}

	/**
	 * Synchronized method that sets the value of interrupted flag in AI class tu true
	 */
	public synchronized void stop()
	{
		if(ai != null){
			ai.setInterrupted(true);
		}
	}
	
	//Setter-Getter
	
	synchronized public AI getAi()
	{
		return ai;
	}
	
}
