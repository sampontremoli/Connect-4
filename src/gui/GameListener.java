package gui;
//controllo del mouse

import java.awt.event.*;

import logic.GameLoop;
/**
 * Mouse listener for JPanel Canvas
 * 
 * @author Samuele Pontremoli, Alessio Mecca, Marianna Progano'
 *
 */
public class GameListener implements MouseListener 
{
	private GameLoop game;
	private int top = 30;
	private int margine = 100;
	private int mx = 20;
	private int turn;

	/**
	 * Listener Constructor
	 * 
	 * @param g gameloop The gameloop object, need this reference to return values etc.
	 * @param t actual turn The turn of the game, needed to append the right stats.
	 */
	public GameListener(GameLoop g, int t) 
	{
		turn = t;
		game = g;
	}
	
	/**
	 * Catches mouse events for human players, and performs the right move if so.
	 */
	public void mouseClicked(MouseEvent arg0) 
	{
		int mousex=arg0.getX();		
		int mousey=arg0.getY();	
		//se i e' compreso all'interno del cerchio imposta il giocare
		for(int i=0;i<7;i++)
		{
			if((mousex>=mx) && (mousex<(mx+margine-1) && mousey>top) && !game.getBoard().isFull(i)) 
			{
				game.theMove(i);
				sendStats(i);
				game.endHumanMove();
			}		
			mx+=margine;
		}
		mx=20;
		
	}

	/**
	 * Calls stats append method from StatsHelper object.
	 * 
	 * @param move The move made, which has to be printed on screen
	 */
	private void sendStats(int move) 
	{
		turn = game.getMove();
		game.getStats().appendStats("<h"+(turn%2==0?5:6)+"> - Human player moves to column "+(move+1)+"</h"+(turn%2==0?5:6)+">");

	}

	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}	

}
