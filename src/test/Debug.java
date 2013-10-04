package test;

import java.io.PrintWriter;

import logic.Board;

import data.MyInteger;
import static data.Constants.*;

import ai.GameStates;
import ai.GameStates.State;

/**
 * Test class for testing states, evaluations etc. May contain duplicates
 * 
 * @author Samuele Pontremoli, Alessio Mecca, Marianna Progano'
 *
 */
@SuppressWarnings("unused")
public class Debug
{	
	private MyInteger vuoto = new MyInteger(-1);
	
	private GameStates.State state;
	
	private GameStates.State state1;
	/*	Red to move (X=wrong move)
	 * 	-------
	 * 	-------
	 * 	-------
	 * 	---G---
	 * 	---GX--
	 * 	R--GR--
	 */
	private GameStates.State state2;
	/*	Red to move (X=wrong move)
	 * 	-------
	 * 	-------
	 * 	--R-X--
	 * 	--RRGG-
	 * 	--GGRR-
	 * 	--GRGG-
	 */
	private GameStates.State state3;
	/*	Red to move (X=wrong move)
	 * 	---XR--
	 * 	--GGR--
	 * 	--GRG--
	 * 	--GRGR-
	 * 	--RRGGR
	 * 	--RGRGG
	 */
	private GameStates.State state4;
	
	public Debug(GameStates.State s)
	{
		state = s; 
	}
	
	public Debug()
	{
		Board g = new Board();
		/*STATE 3
		g.insert(1, 2);
		g.insert(1, 2);
		g.insert(0, 2);
		g.insert(0, 2);
		g.insert(0, 2);
		g.insert(0, 3);
		g.insert(1, 3);
		g.insert(1, 3);
		g.insert(1, 3);
		g.insert(0, 3);
		g.insert(1, 4);
		g.insert(0, 4);
		g.insert(0, 4);
		g.insert(0, 4);
		g.insert(1, 4);
		g.insert(1, 4);
		g.insert(0, 5);
		g.insert(0, 5);
		g.insert(1, 5);
		g.insert(0, 6);
		g.insert(1, 6);
		*/
		g.insert(1, 1);
		g.insert(0, 2);
		g.insert(0, 3);
		g.insert(1, 3);
		g.insert(1, 3);
		g.insert(1, 4);
		g.insert(0, 4);
		g.insert(1, 4);
		g.insert(0, 5);
		g.insert(0, 5);
		g.insert(0, 5);
		
		for(int i=0; i<7; i++)
		{
			g.insert(1, i);
			state1 = new GameStates.State(g, 1);
			state = state1;
			System.out.println("Stato alla mossa "+i+"\t --> "+evalTest(1));
			printState();
			g.remove(i);
		}
	}
	
	public static void printState(GameStates.State state)
	{
		for(int i=0; i<HEIGHT; i++)
		{
			for(int j=0; j<BASE; j++)
				System.out.print(state.getBoard().getIndex(i, j).getValue()+"\t");
			System.out.println();
		}
		System.out.println();
	}
	
	public void printState()
	{
		for(int i=0; i<HEIGHT; i++)
		{
			for(int j=0; j<BASE; j++)
				System.out.print(state.getBoard().getIndex(i, j).getValue()+"\t");
			System.out.println();
		}
		System.out.println();
	}
	
	public int evalTest(int player)
	{
		int h=0;
		int opposite = (player+1)%2;
		if(checkWinTest(player))
		{
			if(state.moveCount==1)
				return Integer.MAX_VALUE/3;
			else
				h+=12000;
		}
		if(checkWinTest(opposite))
		{
			if(state.moveCount==2)
			{
				System.out.println("Vittoria Avversario");
				return Integer.MIN_VALUE/3;
			}
			else
				h-=10000;
		}
		int[] lm = state.getBoard().legalMoves();
		for(int i=0; i<state.getBoard().getCheck().getFours().size(); i++)
		{
			/* First heuristic evaluation:
			 * h+=(AI's one in a row * 1)+(AI's two in a row * 4)+(AI's three in a row * 4)
			 * h-=(Opponent's one in a row * 1)+(Opponent's two in a row * 4)+(Opponent's three in a row * 4)
			 */
			int[] app=state.getBoard().getCheck().getFours().get(i).count(player);
			int[] app2=state.getBoard().getCheck().getFours().get(i).count(opposite);
			switch(app[0])
			{
				case 1:
					h+=app[0]; break;
				case 2:
					h+=app[0]*4; break;
				case 3:
					h+=app[0]*32; break;
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
				default:
					break;
			}
			/* Second heuristic evaluation:
			 * Actual move -> influences in a good way opponent's next move?
			 */
			int[] counts = state.getBoard().getCheck().getFours().get(i).count(opposite);
			if(counts[0]==3)
			{
				if(lm[counts[2]]!=-1 && lm[counts[2]]==counts[1])
				{
					h-=10000;
				}
			}
			if(counts[0]==2)
			{
				if(lm[counts[2]]!=-1 && lm[counts[2]]==counts[1])
				{
					h-=500;
				}
			}
		}
		return h;
	}
	
	public boolean checkWinTest(int player)
	{
		for(int i=0; i<state.getBoard().getCheck().getFours().size(); i++)
		{
			int[] counts = state.getBoard().getCheck().getFours().get(i).count(player);
			if(counts[0]==4)
			{
				return true;
			}
		}
		return false;
	}
	
	public int[] legalMovesTest()
	{
		int[] moves = new int[BASE];
		for(int j=0; j<BASE; j++)
		{
			if(!fullTest(j))
			{
				int i=HEIGHT-1;
				while(i>0 && !state.getBoard().getIndex(i, j).equals(vuoto))
				{
					i--;
				}
				moves[j]=i;
			}
			else
				moves[j]=-1;
		}
		return moves;
	}
	
	public boolean fullTest(int column) 
	{
		if(!state.getBoard().getIndex(0, column).equals(vuoto)) 
			return true;
		return false;
	}
	
}
