package logic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import ai.AI;

import data.StatsHelper;
import static data.Constants.*;

import gui.Canvas;
import gui.GameFrame;
import gui.GameListener;
  
/**
 * Class that represent the main loop of the game. Here take place all the turn exchange.
 * 
 * @author Samuele Pontremoli, Alessio Mecca, Marianna Progano'
 *
 */
public class GameLoop 
{	
	//Executor for AI Thread
	private ExecutorService exec;
	private AiThread aiThread;
	
	//Graphics
	private GameFrame mainframe;
	private Canvas canvas;
	private GameListener myListener;
	
	//Logic
	private Board board;
	
	//Values 
	private int player1=0;
	private int player2=1;
	private int state;
	private int AI1DEPTH;
	private int AI2DEPTH;
	
	//Turn counters
	private int move;
	private int turn;
	
	//String Handler for the GUI
	private StatsHelper stats;
	
	private String playagain="Do you want to play a new game?";
	
	/**
	 * Constructor
	 * 
	 * @param c The canvas to paint on
	 * @param m The mainframe of the game
	 * @param g The logc board
	 */
	public GameLoop(Canvas c, GameFrame m, Board g)
	{
		myListener =  new GameListener(this, move);
		move = -1;
		turn = 1;
		state=-1;
		canvas = c;
		mainframe = m;
		stats = new StatsHelper(mainframe);
		board = g;
	}
	
	/**
	 * Starts the main loop of the game, calls the right move to make
	 */
	synchronized public void start()
	{
		if(move==-1)
			stats.appendStats("<h1>THE GAME STARTS:</h1>");
		move++;
		canvas.repaint();
		state=getNextState();
		if(move%2==0 && turn<22 && state < HUMAN1_WINS)
			stats.appendStats("<h1>TURN "+(turn++)+"</h1>");
		switch(state)
		{
			case HUMAN_TURN1: humanMove();break;
			case HUMAN_TURN2: humanMove();break;
			case AI1_TURN: aiMove(2);break;
			case AI2_TURN: aiMove(3);break;
			default: stop();break;
		}
	}
	
	/**
	 * When a state is not recognized to be a playing case, the only thing that it can be is
	 * draw, ai win, or human win.
	 */
	synchronized private void stop()
	{	
		if(state==DRAW)
		{
			stats.appendStats("<h1>DRAW!</h1>");
			if(JOptionPane.showConfirmDialog(null, "The game ended with a draw!\n"+playagain, "Draw!", JOptionPane.YES_NO_OPTION)==0)
			{
				killGame();
				mainframe.newGame();
			}
		}
		else
			if(state==HUMAN1_WINS || state==HUMAN2_WINS )
			{
				stats.appendStats("<h1>"+(state==HUMAN1_WINS?"HUMAN 1 WON!" : "HUMAN 2 WON!")+"</h1>");
				if(JOptionPane.showConfirmDialog(null, "Congratulations! "+playagain, (state==HUMAN1_WINS?"Human 1 Won" : "Human 2 Won"), JOptionPane.YES_NO_OPTION)==0)
				{	
					stats.setPoints(move%2);
					killGame();
					mainframe.newGame();
				}
				
			}
			else
				if(state==AI1_WINS || state==AI2_WINS)
				{
					stats.appendStats("<h1>"+(state==AI1_WINS?"AI 1 WON!" : "AI 2 WON!")+"</h1>");
					if(JOptionPane.showConfirmDialog(null, (state==AI1_WINS ? "AI 1 Won. " : "AI 2 Won. ")+playagain, (state==AI1_WINS ? "AI 1 Won" : "AI 2 Won") , JOptionPane.YES_NO_OPTION)==0)
					{
						stats.setPoints(move%2);
						killGame();
						mainframe.newGame();
					}
				}
	}
	
	/**
	 * Starts the human move, the only thing that does is activate the listener
	 */
	synchronized private void humanMove()
	{
		activateMouseListener();
	}
	
	/**
	 * When the human player does a move, this method is called by the listener.
	 * It removes all listener from canvas and calls for next turn.
	 */
	synchronized public void endHumanMove()
	{
		deleteMouseListener();
		canvas.repaint();
		start();
	}
	
	/**
	 * Method that starts the AI thread and submits it to the ExecutorService object
	 * 
	 * @param AI The kind of AI to be called
	 */
	synchronized private void aiMove(int AI)
	{
		aiThread = new AiThread(this, board, AI, move, (AI==AI1_TURN ? AI1DEPTH : AI2DEPTH));
		exec.submit(aiThread);
	}

	/**
	 * Useful method called by start(), before performing any move, returns the state of the game.
	 * 
	 * @return An int for the next state value, look constants class to know which can it be
	 */
	private int getNextState() 
	{
		int next = -1;
		int actual=state;
		//If there's a winning state, determines who has played last, because he's the winner
		if(board.epicWin())
		{
			switch(actual)
			{
				case HUMAN_TURN1: next=HUMAN1_WINS; break;
				case HUMAN_TURN2: next=HUMAN2_WINS; break;
				case AI1_TURN: next=AI1_WINS; break;
				case AI2_TURN: next=AI2_WINS; break;
			}	
		}
		
		//Draw case
		else if(move>41)
			next=DRAW;
		
		//Turn 1 case, the choice is made using player1 and 2 values, taken by Settings
		else if(actual==-1)
		{
			switch(player1)
			{
				case HUMAN_TURN1: next=HUMAN_TURN1; break;
				case HUMAN_TURN2: next=HUMAN_TURN2; break;
				case AI1_TURN: next=AI1_TURN; break;
				case AI2_TURN: next=AI2_TURN; break;
			}					
		}
		
		//In any other case next player to play is the one that didn't play last turn
		else
			next=(actual==player1?player2:player1);
		return next;
	}
	
	/**
	 * Performs the move on the logical board itself
	 * 
	 * @param column
	 */
	public void theMove(int column)
	{
		board.insert(move%2, column);
		canvas.setMove(column, move%2);
		board.getCheck().clearCheck();
	}
	
	/**
	 * Resets all the values of the loop
	 */
	synchronized public void loopReset()
	{
		killGame();
		exec = Executors.newSingleThreadExecutor();
		move = -1;
		turn = 1;
		state = -1;
	}
	
	/**
	 * Activates listener of the canvas
	 */
	public void activateMouseListener() 
	{
		canvas.addMouseListener(myListener);
	}
	
	/**
	 * Deactivates listener of the canvas
	 */
	public void deleteMouseListener() 
	{
		canvas.removeMouseListener(myListener);
	}
	
	/**
	 * Sets AI level from user input through settings frame.
	 * 
	 * @param ai The AI
	 * @param depth The desired depth
	 */
	public void setAiDepth(int ai, int depth) 
	{
		if(ai==AI1_TURN)
			AI1DEPTH=depth;
		else
			AI2DEPTH=depth;
	}
	
	/**
	 * Kills everything. If human is playing kills the listener, if any AI is playing kills also that.
	 * Used if reset or newGame button (brutally) pressed during a game.
	 */
	synchronized public void killGame()
	{
		if(aiThread!=null)
		{
			((AiThread) aiThread).stop();
		}
		if(mainframe.getMouseListeners()!=null)
			deleteMouseListener();
	}
	
	//Setter-Getter

	public int getPlayer1()
	{
		return player1;
	}

	public void setPlayer1(int player1)
	{
		this.player1 = player1;
	}

	public int getPlayer2()
	{
		return player2;
	}

	public void setPlayer2(int player2)
	{
		this.player2 = player2;
	}
	
	public Board getBoard()
	{
		return board;
	}
	
	public Canvas getCanvas()
	{
		return canvas;
	}
	
	public GameListener getMyListener()
	{
		return myListener;
	}
	
	public int getMove()
	{
		return move;
	}
	
	public void setMove(int m)
	{
		move = m;
	}
	
	public StatsHelper getStats()
	{
		return stats;
	}
	
	public GameFrame getMainFrame()
	{
		return mainframe;
	}
	
	synchronized public AI getAi()
	{
		AI nu = null;
		if(aiThread!=null)
			nu = aiThread.getAi();
		return nu;
	}

}