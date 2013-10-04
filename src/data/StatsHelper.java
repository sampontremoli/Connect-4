package data;

import gui.GameFrame;
/**
 * Pretty much unuseful class, it's use is just to append log and points to the main frame
 * 
 * @author Samuele Pontremoli, Alessio Mecca, Marianna Progano'
 *
 */
public class StatsHelper
{
	//The main frame
	private GameFrame frame;
	
	public StatsHelper(GameFrame f)
	{
		frame=f;
	}
	
	public void appendStats(String s)
	{
		frame.setStats(s);
	}
	
	public void setPoints(int turn)
	{
		if(turn==1)
			frame.setPunti1(frame.getPunti1()+1);
		else
			frame.setPunti2(frame.getPunti2()+1);
	}
	
}