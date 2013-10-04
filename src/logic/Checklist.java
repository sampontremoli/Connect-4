package logic;

import java.util.*;

import data.MyInteger;
import static data.Constants.*;

/**
 * Checklist is a class that represents all the possible fours(line of four men) in the board.
 * The possible fours are 69, in various directions(vertical, horizontal, 2xdiagonal)
 * 
 * @author Samuele Pontremoli, Alessio Mecca, Marianna Progano'
 *
 */
public class Checklist 
{
	//Matrix synchronized with game matrix
	private MyInteger[][] matrix=new MyInteger[HEIGHT][BASE];
	
	//List of fours synchronized with matrix(if a four is not possible, then is not present here) --> boosts performance
	private ArrayList<FourInARow> fours = new ArrayList<FourInARow>();
	
	/**
	 * Constructor, takes matrix as input
	 * 
	 * @param matrix Refers to the game matrix
	 */
	public Checklist(MyInteger[][] matrix)
	{
		this.matrix=matrix;
	}
	
	/**
	 * Creates all fours in the checklist
	 */
	public void fillCheck()
	{
		for(int j=0; j<BASE; j++)
			for(int i=HEIGHT-1; i>=HEIGHT-3; i--)
			{
				int[] rows = {i, i-1, i-2, i-3};
				int[] cols = {j, j, j, j};
				fours.add(new FourInARow(matrix[i][j], matrix[i-1][j], matrix[i-2][j], matrix[i-3][j], 0, rows, cols));
			}
		for(int i=HEIGHT-1; i>=0; i--)
			for(int j=0; j<BASE-3; j++)
			{
				int[] rows = {i, i, i, i};
				int[] cols = {j, j+1, j+2, j+3};
				fours.add(new FourInARow(matrix[i][j], matrix[i][j+1], matrix[i][j+2], matrix[i][j+3], 1, rows, cols));
				if(i>=3)
				{
					int[] rows2 = {i,i-1,i-2,i-3};
					int[] cols2 = {j,j+1,j+2,j+3};
					fours.add(new FourInARow(matrix[i][j], matrix[i-1][j+1], matrix[i-2][j+2], matrix[i-3][j+3], 2, rows2, cols2));
				}
			}
		for(int j=BASE-1; j>=3; j--)
			for(int i=HEIGHT-1; i>=HEIGHT-3; i--)
			{
				int[] rows = {i, i-1, i-2, i-3};
				int[] cols = {j, j-1, j-2, j-3};
				fours.add(new FourInARow(matrix[i][j], matrix[i-1][j-1], matrix[i-2][j-2], matrix[i-3][j-3], 3, rows, cols));
			}
	}
	
	/**
	 * Removes unuseful fours in the checklist!!! VERY IMPORTANT
	 * This is possible thanks to myInteger class.
	 */
	public void clearCheck()
	{
		Iterator<FourInARow> i = fours.iterator();
		while (i.hasNext()) 
		{
			FourInARow s = i.next();
			if(s.mismatch())
				i.remove();
		}
	}
	
	public String toString()
	{
		String s="";
		for(FourInARow elem : fours)
			s+=elem+"\n";
		return s;
	}
	
	//Setter-Getter
	
	public ArrayList<FourInARow> getFours()
	{
		return fours;
	}
	
	/**
	 * Inner-Class that represents a single four
	 * 
	 * @author Samuele Pontremoli, Alessio Mecca, Marianna Progano'
	 *
	 */
	public class FourInARow
	{
		private MyInteger points[] = new MyInteger[4];
		private int[] rows = new int[4];
		private int[] cols = new int[4];
		
		//control assumes value 0 as vertical, 1 as horizontal, 2 as right diagonal, 3 as left diagonal
		private int control;
		
		public FourInARow(MyInteger one, MyInteger two, MyInteger three, MyInteger four, int c, int i[], int[] j)
		{
			points[0] = one;
			points[1] = two;
			points[2] = three;
			points[3] = four;
			rows=i;
			cols=j;
			control = c;
		}
		
		public boolean mismatch()
		{
			int[] app = {points[0].getValue(), points[1].getValue(), points[2].getValue(), points[3].getValue()};
			Arrays.sort(app);
			int i=0;
			while(i<app.length && app[i]==-1)
				i++;
			if(i>=app.length)
				return false;
			for(int a=app[i++]; i<app.length; i++)
				if(i<app.length && app[i]!=a)
					return true;
			
			return false;
		}
		
		/**
		 * 
		 * @param player
		 * @return ret[0]=numero di pedine di player in quella possibile vittoria<br>
		 * 		   ret[1]=l'ultimo indice i vuoto (se ret[0]!=3 è superfluo) 
		 *         ret[2]=l'ultimo indice j vuoto (se ret[0]!=3 è superfluo) 
		 */
		public int[] count(int player)
		{
			int count = 0;
			int[] ret = new int[3];
			int[] app = {points[0].getValue(), points[1].getValue(), points[2].getValue(), points[3].getValue()};
			for(int i=0; i<app.length; i++)
			{
				if(app[i]!=-1)
				{
					//se è mio aumento il count
					if(app[i]==player)
						count++;
					else
					{
						//altrimenti e' del mio avversario e non mi interessa
						ret[0]=-1;
						ret[1]=-1;
						ret[2]=-1;
						return ret;
					}
				}
				else
				{
					ret[1]=rows[i];
					ret[2]=cols[i];
				}
			}
			ret[0]=count;
			return ret;
		}

		@Override
		public String toString()
		{
			return rows[0]+" "+cols[0]+": "+points[0].getValue()+" "+points[1].getValue()+" "+points[2].getValue()+" "+points[3].getValue()+" control: "+control;
		}
		
		//Setter-Getter
		
		public int getRow()
		{
			return rows[0];
		}
		
		public int getCol()
		{
			return cols[0];
		}
		
		public int getControl()
		{
			return control;
		}
		
	}
	
}
