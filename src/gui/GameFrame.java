package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.DefaultCaret;

//import test.Debug;

import logic.GameLoop;
import logic.Board;
import static data.Constants.*;

/**
 * Main Frame of the game - Sets up all the JPanels, TextFields, JLabels and so on.
 * 
 * @author Samuele Pontremoli, Alessio Mecca, Marianna Progano'
 * 
 */
public class GameFrame extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private GameLoop game;
	//Logic Board
	private Board griglia;
	//Main JPanel
	private Canvas canvas;
	
	//Dimensions
	private Dimension dimensione = new Dimension(1100, 720);
	private Dimension statbox = new Dimension(100,400);
	
	//Points values
	private static int punti1 = 0;
	private static int punti2 = 0;
	
	//Strings
	private String infostring = "Connect Four is a two players game<br> which takes place on a 7x6 rectangular board.<br><br>One player has 21 yellow men and the other 21 red men.<br>Each player can drop a man at the top of the board in one of the seven columns;<br>the man falls down and fills the lower unoccupied square.<br><br>The goal of the game is to connect four men vertically, horizontally or diagonally.<br>If the board is filled and no one has alligned four men then the game is drawn.";
	private String developstring = "Developed by:<br><br>Alessio Mecca<br>Samuele Pontremoli<br>Marianna Progano'";
	
	//Points Panel
	private JPanel p1 = new JPanel();
	//Sidebar Panel
	private JPanel p2 =new JPanel();
	//Logo Panel
	private JPanel p3 =new JPanel();
	
	//Labels
	private JLabel l1 = new JLabel(" PLAYER 1 ");
	private JLabel l2 = new JLabel(" PLAYER 2 ");
	private JLabel infolabel = new JLabel("<html><div style=\"text-align: center;\">"+infostring+"</html>", JLabel.CENTER);
	private JLabel developlabel = new JLabel("<html><div style=\"text-align: center;\">"+developstring+"</html>", JLabel.CENTER);
	
	//Point and Stats fields
	private JTextArea punt = new JTextArea(" "+punti1+" ");
	private JTextArea punt1 = new JTextArea(" "+punti2+" ");
	private JEditorPane stats = new JEditorPane();

	//Menu items
	private JMenuItem  nuova = new JMenuItem("New Game");
	private JMenuItem  settings = new JMenuItem("Settings");
	private JMenuItem  reset = new JMenuItem("Reset");
	private JMenuItem chiudi = new JMenuItem("Exit");
	private JMenuItem rules = new JMenuItem("Rules");
	private JMenuItem credits = new JMenuItem("Credits");
	
	//Logo image
	private Image img = null;
	
	//No games yet, flag used to display settings if not setted yet
	private boolean settingDone=false;
	
	//CSS style to the log
	private String log = "<style> html {background: #787878;} " +
							"h1 {color: black; text-align: center; font-size: 12px;} " +
							"h5 {color: #FFE600; text-align: center; font-size: 11px;} " +
							"h6 {color: #FF030D; text-align: center; font-size: 11px;}" +
							"table {color: black; text-align: center; font-size: 10px;}" +
							"</style>";
	
	/**
	 * Constructor
	 */
	public GameFrame()
	{	
		initComponents();
	}
	
	/**
	 * Builds and adds all the components to the JFrame
	 */
	private void initComponents()
	{
		//Logic board initialization
		griglia = new Board();
		
		//Setting up canvas(JPanel)
		canvas = new Canvas(griglia);

		game = new GameLoop(canvas, this, griglia);

		//Font
		Font font = new Font("Sans_Serif", Font.BOLD, 18);
		
		//JLabels, JPanels, JTextFields and JEditorPane Properties
		l1.setFont(font);
		l2.setFont(font);
		font = new Font("Sans_Serif", Font.BOLD, 14);
		punt.setFont(font);
		punt.setBackground(LIGHTGRAY);
		punt.setForeground(BLACK);
		punt1.setFont(font);
		punt1.setBackground(LIGHTGRAY);
		punt1.setForeground(BLACK);
		l1.setForeground(YELLOW);
		l2.setForeground(RED);
		l1.setBackground(LIGHTGRAY);
		l2.setBackground(LIGHTGRAY);
		l1.setOpaque(true);
		l2.setOpaque(true);
		p1.setBackground(LIGHTBLUE);
		p2.setBackground(LIGHTBLUE);
		p3.setBackground(LIGHTBLUE);
		canvas.setBackground(LIGHTBLUE);
		getContentPane().setBackground(LIGHTBLUE);
		stats.setFont(font);
		stats.setBackground(LIGHTGRAY);
		stats.setEditable(false);
		stats.setContentType("text/html");
		
		//Setting autoscroll on the jeditorpane
		DefaultCaret caret = (DefaultCaret)stats.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		//Adding jeditorpane to the scroll pane and setting up dimensions
		JScrollPane scroll = new JScrollPane (stats, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getViewport().setPreferredSize(new Dimension(280,540));
		
		//Menu initialization
		setMenu();
		
		//Setting up gridbaglayout for frame
        setLayout(new GridBagLayout());
        
		//Adding elements to panel p1 (Score)
		Border whiteline = BorderFactory.createLineBorder(BLACK);
		Border titledBorder = new TitledBorder(whiteline, "SCORE", TitledBorder.CENTER, TitledBorder.CENTER, font, BLACK);
		p1.setBorder(titledBorder);	
		p1.add(l1, 0);
		punt.setEditable(false);
		p1.add(punt,1);
		punt1.setEditable(false);
		p1.add(punt1, 2);
		p1.add(l2, 3);
		
		//Adding elements to panel p2 (Sidebar)
		Border titledBorder2 = new TitledBorder(whiteline, "GAME LOG", TitledBorder.CENTER, TitledBorder.CENTER, font, BLACK);
		p2.setBorder(titledBorder2);
		stats.setEditable(false);
		
		//Sidebar Layout
		p2.setLayout(new GridBagLayout());
		GridBagConstraints e = new GridBagConstraints();
		e.gridx=0;
		e.gridy=0;
		e.insets = new Insets(5,5,5,5);
		p2.add(scroll, e);
		e.anchor = GridBagConstraints.WEST;
		e.gridy++;
		
		//Save Button and JFileChooser
		p2.add(new JButton(new AbstractAction("Save Game Log")
		{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						JFileChooser chooser = new JFileChooser("./");
						int r = chooser.showSaveDialog(game.getMainFrame());
						File file=chooser.getSelectedFile();
						PrintWriter writer = null; 
						if (r == JFileChooser.APPROVE_OPTION)
						{
				            try  
				            {  
					            writer = new PrintWriter( new File( file.getAbsolutePath()+".html"));  
					            writer.println(stats.getText());
					            writer.close();  
					            JOptionPane.showMessageDialog(null, "Game Log was Saved Successfully!", "Success!", JOptionPane.INFORMATION_MESSAGE);  
				            }  
				            catch (IOException e)  
				            {  
				            	JOptionPane.showMessageDialog(null, "Data could not be Saved!", "Error!", JOptionPane.ERROR_MESSAGE);  
				            }  
						}
					}
				});
				
			}
			
		}), e);
		
		//Save Tree Button
		e.anchor = GridBagConstraints.EAST;
		p2.add(new JButton(new AbstractAction("Save Search Tree")
		{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				JFileChooser chooser = new JFileChooser("./");
				int r = chooser.showSaveDialog(null);
				File file=chooser.getSelectedFile();
				PrintWriter writer = null; 
				if (r == JFileChooser.APPROVE_OPTION)
				{
		            try  
		            {  
		            	File f = new File( file.getAbsolutePath()+".txt");
		            	writer = new PrintWriter(f);  
		            	if(game.getAi()!=null)
		            		game.getAi().getTheGame().printTree(writer);
		            	writer.close();
		            	JOptionPane.showMessageDialog(null, "Search Tree was Saved Successfully!", "Success!", JOptionPane.INFORMATION_MESSAGE);  
		            }  
		            catch (IOException e)  
		            {  
		            	JOptionPane.showMessageDialog(null, "Data could not be Saved!", "Error!", JOptionPane.ERROR_MESSAGE);  
		            }  
				}
			}
			
		}), e);
		p2.setMinimumSize(statbox);
		
		//Adding logo to panel p3
		String path="/images/logo.png";
		URL imgURL = getClass().getResource(path);
		try {
			img = ImageIO.read(imgURL);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if(img!=null)
		{
			Image scaledImage = img.getScaledInstance(170, 42, Image.SCALE_SMOOTH);
			JLabel picLabel = new JLabel();
			picLabel.setIcon(new ImageIcon(scaledImage));
			p3.add(picLabel, BorderLayout.CENTER);
		}
		
		//Score Panel constraints
		GridBagConstraints a = new GridBagConstraints();
		a.insets=new Insets(5,5, 0, 0);
		a.gridx = 0;
		a.gridy = 0;
		a.weightx = 0;
		a.weighty = 1;
		a.anchor = GridBagConstraints.NORTH;
		a.fill = GridBagConstraints.HORIZONTAL;
		//Adding Score Panel
	    add(p1, a);
	    
	    //Canvas constraints
		GridBagConstraints b = new GridBagConstraints();
		b.gridx = 0;
		b.gridy = 1;
		b.weightx = 0;
		b.weighty = 0;
		b.insets=new Insets(0,20,20,20);
		b.anchor = GridBagConstraints.SOUTH;
		b.fill = GridBagConstraints.NORTH;
		//Adding Canvas Panel
		add(canvas, b);
		
		//Sidebar constraints
		GridBagConstraints c = new GridBagConstraints();
		c.insets=new Insets(0, 0, 5, 5);
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 3;
		c.weighty = 3;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.VERTICAL;
		//Adding sidebar
		add(p2, c);
		
		//Logo constraints
		GridBagConstraints d = new GridBagConstraints();
		d.insets=new Insets(5, 0, 0, 0);
		d.gridx = 1;
		d.gridy = 0;
		d.weightx = 1;
		d.weighty = 1;
		d.anchor = GridBagConstraints.EAST;
		d.fill = GridBagConstraints.BOTH;
		//Adding Logo
		add(p3, d);
		
		//Window positioning, title and display
		setTitle("Connect 4");
		setSize(dimensione);
        setResizable(false);
        setLocationRelativeTo(null);
		pack();
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	/**
	 * Creates the main menu with a JMenuBar Object. The menu itself is divided in two
	 * submenus, with dropdowns.
	 */
	private void setMenu() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu menu = new JMenu("Game");
		JMenuItem menuItem;

		//Menu Game
		menuBar.add(menu);
		
		//Game item 1
		menuItem = nuova;
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent a)
			{
				if(settingDone)
				{
					game.killGame();
					newGame();
				}
				else
				{
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							new SettingsFrame(game);
						}
					});
				}
			}
		});
		menu.add(menuItem);
		
		//Game item 2
		menuItem = settings;
		menuItem.addActionListener(new ActionListener()
		{
			
			public void actionPerformed(ActionEvent a)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						new SettingsFrame(game);
					}
				});
			}
		});
		menu.add(menuItem);
		
		//Game item 3
		menuItem = reset;
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent a)
			{
				game.killGame();
				griglia.empty();
				game.loopReset();
				canvas.setMove(-1, -1);
				log = "";
				setStats(log);
				punt.setText(" "+0+" ");
				punt1.setText(" "+0+" ");
				settingDone=false;
				game.getMainFrame().repaint();
			}
		});
		menu.add(menuItem);

		//Game item 4
		menuItem = chiudi;
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a) {
				if(JOptionPane.showConfirmDialog(null, "Exit Game?", "Goodbye" , JOptionPane.YES_NO_OPTION)==0)
					System.exit(0);	
			}});
		menu.add(menuItem);
		
		//Menu Help
		menu=new JMenu("Help");
		menuBar.add(menu);
		
		//Help item 1
		menuItem = rules;
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a) {
				JOptionPane.showMessageDialog(null, infolabel,"Game Rules",JOptionPane.DEFAULT_OPTION);
			}});
		menu.add(menuItem);
		
		//Help item 2
		menuItem = credits;
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a) {
				JOptionPane.showMessageDialog(null, developlabel, "Credits", JOptionPane.DEFAULT_OPTION);		
			}});
		menu.add(menuItem);
		
		//Creates the menu
		setJMenuBar(menuBar);
	}
	
	/**
	 * Main method, runs in the Event Dispatch Thread
	 * 
	 * @param args Nothing to be taken in input
	 */
	public static void main(String args[])
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				new GameFrame();
			}
		});
	}
	
	/**
	 * Shows the dialog box to choose the players and the start order, then starts the main loop.
	 */
	public void newGame()
	{
		griglia.empty();
		game.loopReset();
		canvas.setMove(-1, -1);
		this.repaint();
		game.start();
	}
    
	//Setter-Getter
	
	public void setPunti1(int p1)
	{
		punti1=p1;
		punt.setText(" "+punti1+" ");
	}
	
	public void setPunti2(int p2)
	{
		punti2=p2;
		punt1.setText(" "+punti2+" ");
	}
	
	public int getPunti1()
	{
		return punti1;
	}
	
	public int getPunti2()
	{
		return punti2;
	}
	
	public JTextArea getPunt()
	{
		return punt;
	}
	
	public JTextArea getPunt1()
	{
		return punt1;
	}
	
	public void setStats(String s)
	{	
		log+=s;
		stats.setText(log);
	}
	
	public void setSettingDone(boolean b)
	{
		settingDone=b;
	}
	
	
	
}
