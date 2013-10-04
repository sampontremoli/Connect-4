package logic;

import data.MyInteger;
import static data.Constants.*;

/**
 * Logic board
 * 
 * @author Samuele Pontremoli, Alessio Mecca, Marianna Progano'
 *
 */
public class Board 
{
	//Main logic matrix
	private MyInteger matrix[][]=new MyInteger[HEIGHT][BASE];
	
	//Checklist object contains all the possible 69 fours
	private Checklist check;
	
	/**
	 * Constructor that creates an empty board
	 */
	public Board() 
	{
		for(int i=0; i<HEIGHT; i++) 
			for(int j=0; j<BASE; j++) 
				matrix[i][j] = new MyInteger(EMPTY.getValue());
		check = new Checklist(matrix);
		check.fillCheck();
	}
	
	/**
	 * Constructor that creates a copy of a board
	 * @param g The copied board
	 */
	public Board(Board g) 
	{
		for(int i=0; i<HEIGHT; i++) 
			for(int j=0; j<BASE; j++) 
				matrix[i][j]= new MyInteger(g.matrix[i][j].getValue());
		check = new Checklist(matrix);
		check.fillCheck();
		check.clearCheck();
	}
	
	/**
	 * Empties the board
	 */
	public void empty()
	{
		for(int i=0; i<HEIGHT; i++) 
			for(int j=0; j<BASE; j++) 
				matrix[i][j].setValue(EMPTY.getValue());
	}
	
	/**
	 * Checks if a column is full
	 * @param column The column to be checked
	 * @return	true/false if the column is full/not full
	 */
	public boolean isFull(int col) 
	{
		if(!matrix[0][col].equals(EMPTY)) 
			return true;
		return false;
	}
	
	/**
	 * Inserts a man in a column for a given player
	 * @param player The player that made the move
	 * @param colonna The column of the move
	 * @return true/false if move is performed/not performed
	 */
	public boolean insert(Integer player, int col) 
	{
		if(isFull(col)) 
			return false;
		else 
			for(int i=HEIGHT-1; i>=0;i--) 
			{
				if(matrix[i][col].equals(EMPTY))
				{
					matrix[i][col].setValue(player);
					return true;	
				}
			}
		return false;
	}
	
	/**
	 * Important method that checks if there's a winning state
	 * @return true/false if the state is/isn't a winning state
	 */
	public boolean epicWin()
	{	
		for(int j=0; j<BASE; j++)
			for(int i=HEIGHT-1; i>=0; i--)
			{
				if(matrix[i][j].equals(EMPTY))
					break;
				else
					if(epicWinIJ(i,j))
						return true;
			}
		return false;
	}
	
	/**
	 * Utility method called from epicWin(), checks if there's a winning state
	 * starting from matrix[i][j]
	 * @param i
	 * @param j
	 * @return true/false if there's/there's not a winning state starting from matrix[i][j]
	 */
	public boolean epicWinIJ(int i,int j)
	{	
		
		MyInteger g = new MyInteger(matrix[i][j].getValue());
		//Verticale
		if(i>=3)
			if(matrix[i-1][j].equals(g) && matrix[i-2][j].equals(g) && matrix[i-3][j].equals(g))
				return true;
			
		if(j<=3)
		{
			//Orizzontale
			if(matrix[i][j+1].equals(g) && matrix[i][j+2].equals(g) && matrix[i][j+3].equals(g))
				return true;
			//Diagonale dx
			if(i>=3)
				if(matrix[i-1][j+1].equals(g) && matrix[i-2][j+2].equals(g) && matrix[i-3][j+3].equals(g))
					return true;
		}
		//Diagonale sx
		if(j>=3 && (i>=3))
			if(matrix[i-1][j-1].equals(g) && matrix[i-2][j-2].equals(g) && matrix[i-3][j-3].equals(g))
				return true;
		
		return false;
	}
	
	/**
	 * Checks the board for the first row index for each column that is free
	 * @return An array[] of 7 slots containing the indexes
	 */
	public int[] legalMoves()
	{
		int[] moves = new int[BASE];
		for(int j=0; j<BASE; j++)
		{
			if(!isFull(j))
			{
				int i=HEIGHT-1;
				while(i>0 && !matrix[i][j].equals(EMPTY))
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
	
	/**
	 * Utility method that removes the last man in a given column
	 * @param col The column in which the last man is removed
	 * @return true/false if can/cannot remove
	 */
	public boolean remove(int col) 
	{
		if(isFull(col))
		{
			matrix[0][col].setValue(-1);
			return true;	
		}
		for(int i=HEIGHT-1; i>=0;i--) 
		{
			if(matrix[i][col].equals(EMPTY))
			{
				matrix[i+1][col].setValue(-1);
				return true;	
			}
		}
		return false;
	}
	
	/**
	 * Returns the element in a given position of the board
	 * @param i rows
	 * @param j columns
	 * @return A myInteger object
	 */
	public MyInteger getIndex(int i, int j) {
		return matrix[i][j];
	}

	public String toString()
	{
		String s="";
		for(int i=0; i<HEIGHT; i++)
		{
			for(int j=0; j<BASE; j++)
				s+=matrix[i][j]+"\t";
			s+="  **  ";
		}
		return s;
	}

	//Setter-Getter

	public Checklist getCheck() 
	{
		return check;
	}

}