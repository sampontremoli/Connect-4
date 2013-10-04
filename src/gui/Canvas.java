package gui;

import java.awt.*;
import java.awt.geom.Point2D;

import javax.swing.*;

import data.Constants;

import logic.Board;
import static data.Constants.*;

/**
 * JPanel painted on the main frame. Contains the graphics for the board.
 * 
 * @author Samuele Pontremoli, Alessio Mecca, Marianna Progano'
 *
 */
public class Canvas extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	//Logic Board
	private Board board;

	//Board Dimension
	private final int canvHeight = 615;
	private final int canvWidth = 745;
	private final int top = 30;
	private int margin = 100;
	
	//Triangles Settings
	private int[] tx = {62,72,82};
	private int[] ty = {10,20,10};

	//Positioning of the circles and radius
	private int x = 40; 
	private int y = 10;
	private int r = 65;

	//Needed stuff for the arrow that changes color
	private Color MOVECOLOR = WHITE;
	private int moveColumn = -1;
	
	public Canvas(Board b)
	{
		this.board=b;
	}
	
	/**
	 * Sets up the graphics of the board.
	 */
	public void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		setBackground(BLUE);
		
		//Antialiasing on
		Graphics2D g2=(Graphics2D)g;
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    
	    //Draws the board on graphics2d object
		drawBoard(g2);
	 }
	
	 /**
	  * Draws the board, lines, circles, triangles and all that kind of stuff.
	  * 
	  * @param g A Graphics2D object to paint in, it has Antialiasing enabled.
	  */
	public void drawBoard(Graphics2D g) 
	{
		//Top Grey Bar
		g.setColor(LIGHTBLUE);
		for(int i=0; i<top; i++)
		{
			g.drawLine(0, i, canvWidth, i);
		}
		//Circles painting
		for(int i=0; i<Constants.HEIGHT; i++) 
		{ 
			for(int j=0; j<BASE; j++) 
			{
				switch(board.getIndex(i, j).getValue())						
				{
					case -1: g.setColor(WHITE);break;
					case 0: g.setColor(YELLOW);break;
					case 1: g.setColor(RED);break;
				}
				//If we are drawing a man for a player
				if((g.getColor()==RED || g.getColor()==YELLOW) && !(g.getColor()==BLUE))
					//Then we want it to be with a 3D gradient
					paintOval(x, y+top, r, g);
				//Else
				else if(g.getColor()==WHITE)
					//Just draw a simple white circle
					g.fillOval(x,y+top,r,r);
				x+=margin;
			}
			x=40;
			y+=margin;
		}
		y=10;
		//Left border color
		g.setColor(LIGHTBLUE);
		for(int i=0; i<19; i++)
			g.drawLine(i, 0, i, canvHeight);
		//Right border color
		g.setColor(LIGHTBLUE);
		for(int i=722; i<canvWidth; i++)
			g.drawLine(i, 0, i, canvHeight);
		//Board Black Lines
		g.setColor(BLACK);
		for(int i=0,k=20;i<=BASE;i++,k+=100)
		{
			g.drawLine(k-1, top, k-1, canvHeight);
			g.drawLine(k, top, k, canvHeight);
			g.drawLine(k+1, top, k+1, canvHeight);
		}
		//Top & Bottom black lines
		g.drawLine(20, top, canvWidth-25, top);
		g.drawLine(20, top+1, canvWidth-25, top+1);
		g.drawLine(20, top+2, canvWidth-25, top+2);
		g.drawLine(20, canvHeight-2, canvWidth-25, canvHeight-2);
		g.drawLine(20, canvHeight-1, canvWidth-25, canvHeight-1);
		g.drawLine(20, canvHeight, canvWidth-25, canvHeight);
		//Top Triangles
		Polygon p;
		for(int i=0; i<BASE; i++)
		{
			if(i==moveColumn){
				g.setColor(MOVECOLOR);
			}
			else
				g.setColor(WHITE);
			p = new Polygon(tx, ty, 3);
			g.fillPolygon(p);
			tx[0]+=margin;
			tx[1]+=margin;
			tx[2]+=margin;
		}
		tx[0]=62;
		tx[1]=72;
		tx[2]=82;
		g.dispose();
	}
	
	/**
	 * Applies a nice gradient to the drawn mans, gives 3D effect.
	 * 
	 * @param x x-axis position
	 * @param y y-axis position
	 * @param r2 radius
	 * @param g2 Graphics2D object to paint on, passed from drawBoard method
	 */
	private void paintOval(int x, int y, int r2, Graphics2D g2) {
		// Retains the previous state
        Paint oldPaint = g2.getPaint();
        Paint p;
        Color cold = g2.getColor();
        Color cnew = (cold==RED ? DARKRED: DARKYELLOW);
        
        //Little highlight at the bottom 
        p = new GradientPaint(x, y, cold,
                0, r2/2, cnew);
        g2.setPaint(p);
        g2.fillOval(x, y, r2, r2);
        
        //Dark edges on bottom right, gives 3D effect
        p = new RadialGradientPaint(new Point2D.Double(x, y), r2 / 1.8f, new float[] { 0.4f, 1.0f }, new Color[] { cold, cnew });
        g2.setPaint(p);
        g2.fillOval(x, y, r2, r2);
        
        //Restores the previous paint
        g2.setPaint(oldPaint);
	}

	/**
	 * Method that sets the parameters to identify the last move made to change triangle color.
	 * 
	 * @param mc column of the last move
	 * @param p player that made the last move
	 */
	public void setMove(int mc, int p)
	{
		if(mc==-1 && p==-1){
			moveColumn = -1;
			MOVECOLOR = WHITE;
		}
		else{
			moveColumn = mc;
			MOVECOLOR = p==0 ? YELLOW : RED;
		}
	}
	 
	/**
	 * Absolutely needed method to paint the JFrame correctly using GridBagLayout.
	 */
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(canvWidth, canvHeight);
		
	}

}
