package gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import logic.GameLoop;

/**
 * Frame and Handler for Settings
 * 
 * @author Samuele Pontremoli, Alessio Mecca, Marianna Progano'
 *
 */
public class SettingsFrame extends JFrame implements ItemListener
{
	private static final long serialVersionUID = 1L;
	
	private GameLoop game;
	
	//Various Panels
	private JPanel p1;
	private JPanel p2;
	private JPanel p3;
	private JPanel p4;
	private JPanel p5;
	
	//Buttons
	private JButton applybtn = new JButton("Apply");
	private JButton discardbtn = new JButton("Cancel");
	
	//Labels
	private final JLabel label1 = new JLabel("SELECT PLAYER 1:");
	private final JLabel label2 = new JLabel("SELECT PLAYER 2:");
	private final JLabel diff1 = new JLabel("A.I. 1 DEPTH:");
	private final JLabel diff2 = new JLabel("A.I. 2 DEPTH:");
	
	//Values
	private final String AI1 = "A.I. 1";
	private final String AI2 = "A.I. 2";
	private final String HUMAN1 = "Human";
	private final Integer[] levels = {1,2,3,4,5,6,7};
	
	//Player 1 possibilities
	private final ButtonGroup radioGroup1= new ButtonGroup();
	private JRadioButton[] playGroup1 = new JRadioButton[3];

	//Player 2 possibilities
	private final ButtonGroup radioGroup2= new ButtonGroup();
	private JRadioButton[] playGroup2 = new JRadioButton[3];
	
	//AI 1 Difficulty levels
	private final ButtonGroup groupBtn1 = new ButtonGroup();
	private JRadioButton[] arrGroup1 = new JRadioButton[7];

	//AI 2 Difficulty levels
	private final ButtonGroup groupBtn2 = new ButtonGroup();
	private JRadioButton[] arrGroup2 = new JRadioButton[7];
	
	private JLabel warninglbl1 = new JLabel("Warning! Level 7 may cause");
	private JLabel warninglbl2 = new JLabel("high CPU and memory usage");
	
	private int ai2level = 0;
	private int ai1level = 0;

	/**
	 * Constructor that creates instances of the buttonsm add listeners and calls initComponents().
	 * 
	 * @param g The gameloop object in which the values are being setted
	 */
	public SettingsFrame(GameLoop g)
	{	
		super("Settings");
		game=g;
		
		for(int i=0;i<arrGroup1.length;i++)
		{
			//aggiunge pulsanti per il livello AI da 1 a 7.
			arrGroup1[i]=new JRadioButton(levels[i].toString());
			arrGroup2[i]=new JRadioButton(levels[i].toString());
			groupBtn1.add(arrGroup1[i]);
			groupBtn2.add(arrGroup2[i]);
			if(i<playGroup1.length)
			{
				switch(i)
				{
					case 0: playGroup1[i]=new JRadioButton(HUMAN1);
							playGroup2[i]=new JRadioButton(HUMAN1);break;
					case 1: playGroup1[i]=new JRadioButton(AI1);
							playGroup2[i]=new JRadioButton(AI1);break;
					case 2:	playGroup1[i]=new JRadioButton(AI2);
							playGroup2[i]=new JRadioButton(AI2);break;
				}
				radioGroup1.add(playGroup1[i]);
				radioGroup2.add(playGroup2[i]);
				playGroup1[i].addItemListener(this);
				playGroup2[i].addItemListener(this);
				
			}
		}
		initComponents();
	}

	/**
	 * Builds up all the graphics components.
	 */
	private void initComponents()
	{	
		p1 = new JPanel();
		p2 = new JPanel();
		p3 = new JPanel();
		p4 = new JPanel();
		p5 = new JPanel();
				
		//Font assignment
		Font boldUnderline = new Font("Sans-Serif",Font.BOLD, 14);
		Font plainFont = new Font("Sans-Serif",Font.PLAIN, 13);
		label1.setFont(boldUnderline);
		label2.setFont(boldUnderline);
		diff1.setFont(boldUnderline);
		diff2.setFont(boldUnderline);
		
		//Adding all buttons to panels
		p1.setLayout(new GridLayout(12,1));
		p1.add(label1);
		p1.add(playGroup1[0]);
		p1.add(playGroup1[1]);
		p1.add(playGroup1[2]);
		p2.setLayout(new GridLayout(12,1));
		p2.add(label2);
		p2.add(playGroup2[0]);
		p2.add(playGroup2[1]);
		p2.add(playGroup2[2]);
		p1.add(diff1);
		p2.add(diff2);
		p3.add(applybtn);
		p3.add(discardbtn);
		p4.add(warninglbl1);
		p5.add(warninglbl2);
		for(int i=0; i<arrGroup1.length;i++)
		{
			arrGroup1[i].addItemListener(this);
			arrGroup2[i].addItemListener(this);
			p1.add(arrGroup1[i]);
			arrGroup1[i].setEnabled(false);
			p2.add(arrGroup2[i]);
			arrGroup2[i].setEnabled(false);
			if(i<playGroup1.length)
			{
				playGroup1[i].setFont(plainFont);
				playGroup2[i].setFont(plainFont);
			}
		}
		
		//Apply listener, sends values to the game and starts a new game
		applybtn.addActionListener(new ActionListener() 
		{
            public void actionPerformed(ActionEvent e)
            {   
        		for(int i=0; i<arrGroup1.length; i++)
        		{
        			if(arrGroup1[i].isSelected())
        				ai1level = i+1;
        			if(arrGroup2[i].isSelected())
        				ai2level = i+1;		
        		}
            	for(int i=0;i<playGroup1.length;i++)
        		{
        			if(playGroup1[i].isSelected())
        			{
        				switch(i)
        				{
        					case 0:game.setPlayer1(i);break;
        					case 1: game.setPlayer1(i+1);
        							game.setAiDepth(i+1, getAi1Level());break;
        					case 2:game.setPlayer1(i+1);
								   game.setAiDepth(i+1, getAi1Level());break;
        				}       				
        			}
        			if (playGroup2[i].isSelected())
        			{
        				switch(i)
        				{
        					case 0:game.setPlayer2(i+1);break;
        					case 1: game.setPlayer2(i+1);
        							game.setAiDepth(i+1, getAi2Level());break;
        					case 2:game.setPlayer2(i+1);
								   game.setAiDepth(i+1, getAi2Level());break;
        				}  
        			}
            				
            	}
				game.getMainFrame().getPunt().setText(" "+0+" ");
				game.getMainFrame().getPunt1().setText(" "+0+" ");
        		game.getMainFrame().setSettingDone(true);
            	game.getMainFrame().newGame();
            	dispose();
            }
    		
        });  
		
		//Cancel button listener, closes settings window
		discardbtn.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        });     
		
		playGroup1[0].setSelected(true);
		playGroup2[1].setSelected(true);
		
		//Gridbag layout to this frame
		setLayout(new GridBagLayout());
	    
		//Player 1 possibilities panel
	    GridBagConstraints a = new GridBagConstraints();
	    a.insets = new Insets(20,10,20,0);
		a.gridx = 0;
		a.gridy = 0;
		a.weightx = 4;
		a.weighty = 0;
		a.anchor = GridBagConstraints.NORTH;
		add(p1, a);

		//Player 2 possibilities panel
	    GridBagConstraints b = new GridBagConstraints();
	    b.insets = new Insets(20,10,20,0);
		b.gridx = 1;
		b.gridy = 0;
		b.weightx = 4;
		b.weighty = 0;
		b.anchor = GridBagConstraints.NORTH;
		add(p2, b);
		
		//Apply and Cancel buttons panel
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 2;
		c.anchor = GridBagConstraints.SOUTH;
		add(p3, c);
		
		//Warning message first part
		GridBagConstraints d = new GridBagConstraints();
		d.insets = new Insets(20,10,20,0);
		d.gridx = 0;
		d.gridy = 1;
		d.anchor = GridBagConstraints.SOUTH;
		add(p4, d);
		
		//Warning message second part
		GridBagConstraints e = new GridBagConstraints();
		e.insets = new Insets(20,0,20,10);
		e.gridx = 1;
		e.gridy = 1;
		e.anchor = GridBagConstraints.SOUTH;
		add(p5, e);
		
		//Setting size, title, and display
		setSize(400, 400);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
		setVisible(true);
	}
	
	/**
	 * ItemListener which interacts whith radiobuttons
	 */
	@Override
	public void itemStateChanged(ItemEvent e)
	{
		playGroup2[1].setEnabled((playGroup1[1].isSelected()?false:true));
		playGroup2[2].setEnabled((playGroup1[1].isSelected()?true:false));
		
		playGroup1[1].setEnabled((playGroup2[1].isSelected()?false:true));
		playGroup1[2].setEnabled((playGroup2[1].isSelected()?true:false));
		
		if (playGroup1[1].isSelected() || playGroup1[2].isSelected())
		{
			for(int i = 0; i < arrGroup1.length; i++)
					arrGroup1[i].setEnabled(true);
		
			//Sets the default value
			if(groupBtn1.getSelection()==null)
				arrGroup1[3].setSelected(true);
        }
		else
		{
			playGroup2[1].setEnabled(true);
			playGroup2[2].setEnabled(true);
			for(int i = 0; i < arrGroup1.length; i++)
					arrGroup1[i].setEnabled(false);
			
			groupBtn1.clearSelection();
		}
		
		if (playGroup2[1].isSelected() || playGroup2[2].isSelected())
		{
			for(int i = 0; i < arrGroup2.length; i++)
					arrGroup2[i].setEnabled(true);
			//Sets the default value
			if(groupBtn2.getSelection()==null)
				arrGroup2[3].setSelected(true);
		}
		else
		{
			playGroup1[1].setEnabled(true);
			playGroup1[2].setEnabled(true);
			for(int i = 0; i < arrGroup2.length; i++)
					arrGroup2[i].setEnabled(false);
			
			groupBtn2.clearSelection();
		}
	}
	
	//Setter-Getter
	
	public int getAi1Level()
	{
		return ai1level;
	}
	
	public int getAi2Level()
	{
		return ai2level;
	}
	
}
