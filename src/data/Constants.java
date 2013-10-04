package data;

import java.awt.Color;
/**
 * Bunch of constants used all over the application
 * 
 * @author Samuele Pontremoli, Alessio Mecca, Marianna Progano'
 *
 */
public final class Constants {
	
	//COLORS
	public static final Color YELLOW = new Color(0xFFE600);
	public static final Color RED = new Color(0xFF030D);
	public static final Color BLUE = new Color(0x003EFF);	
	public static final Color WHITE = Color.white;
	public static final Color BLACK = Color.black;
	public static final Color DARKGREY = new Color(0x787878);
	public static final Color LIGHTGRAY = new Color(0xBEBEBE);
	public static final Color DARKRED = new Color(0xb80000);
	public static final Color DARKYELLOW = new Color(0xf9a500);
	public static final Color LIGHTBLUE = new Color(0xB9D6ED);
	
	//BOARD
	public static final int HEIGHT = 6;
	public static final int BASE = 7;
	
	public static final MyInteger EMPTY = new MyInteger(-1);
	public static final MyInteger ONE = new MyInteger(0);
	public static final MyInteger TWO = new MyInteger(1);
	
	//FLAGS
	public static final int HUMAN_TURN1=0;
	public static final int HUMAN_TURN2=1;
	public static final int AI1_TURN=2;
	public static final int AI2_TURN=3;
	
	public static final int HUMAN1_WINS=4;
	public static final int HUMAN2_WINS=5;
	public static final int AI1_WINS=6;
	public static final int AI2_WINS=7;
	public static final int DRAW=8;

}
