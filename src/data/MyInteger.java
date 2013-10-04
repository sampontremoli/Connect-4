package data;

/**
 * Custom Integer-like class needed to maintain reference to values between the Checklist (list of possible 69 fours)
 * and the game board.
 * 
 * @author Samuele Pontremoli, Alessio Mecca, Marianna Progano'
 *
 */
public class MyInteger
{
	private int value;
	
	public MyInteger(int value)
	{
		this.value=value;
	}

	@Override
	public boolean equals(Object arg)
	{
		if(arg instanceof MyInteger)
		{
			return (((MyInteger)arg).getValue()==value);
		}
		return false;
	}
	
	public String toString()
	{
		return value+"";
	}
	
	@Override
	public int hashCode(){return value;}
	
	public int getValue() {return value;}

	public void setValue(int value) {this.value = value;}	
}