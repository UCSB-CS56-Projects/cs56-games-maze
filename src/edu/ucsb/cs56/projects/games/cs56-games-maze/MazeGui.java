package edu.ucsb.cs56.projects.games.cs56_games_maze;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.event.*;
import java.awt.*;
import java.beans.*;
import java.util.ArrayList;

import java.io.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
/**
   Class where the MazeGui is constructed.  This is also the main class and contains the main method
   @author Jake Staahl
   @author Evan West
   @author Logan Ortega
   @author Richard Wang
   @author Zak Blake
   @version 2/26/15 for proj1, cs56, W15
*/

public class MazeGui implements ActionListener{

    private JFrame frame;
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenu colorMenu;
    private MazeTimerBar timerBar;
    private MazeGrid grid;
    private MazeComponent mc;
    private MazeGenerator mg;
    private MazePlayer player;
    private Timer drawTimer;
    private MazeSettings settings;
    private MazeSettings oldSettings;
    private Action playerMoveAction;
    private MazeGameSave gameSave;
    private long realTime;
    private JMenu shapeMenu;
    private boolean rect = true;
    private int colorMode = 0;
    
    private JFileChooser fc;
    private javax.swing.filechooser.FileFilter fileFilter;
    private MazeSettingsDialog settingsDialog;

    public static final int MULTI_CHAIN_GEN = 1;
    public static final int ALT_STEP_GEN = 2;
    public static final int NEW_STEP_GEN = 3;
    /** Main method spins off thread to run controller and create Maze game
     */
    public static void main(final String[] args){
	SwingUtilities.invokeLater(new Runnable(){
		public void run(){
		    new MazeGui(args).run();
		}
		
	    });
    }

    /** Sole constructor for MazeGui (controller)
	@param args Command-line arguments for settings (optional)
    */
    public class Sound {
	private Clip clip;
	public Sound(String fileName) {
	    // specify the sound to play
	    // (assuming the sound can be played by the audio system)
	    // from a wave File
	    try {
		File file = new File(fileName);
		if (file.exists()) {
		    AudioInputStream sound = AudioSystem.getAudioInputStream(file);
		    // load the sound into memory (a Clip)
		    clip = AudioSystem.getClip();
		    clip.open(sound);
		}
		else {
		    throw new RuntimeException("Sound: file not found: " + fileName);
		}
	    }
	    catch (MalformedURLException e) {
		e.printStackTrace();
		throw new RuntimeException("Sound: Malformed URL: " + e);
	    }
	    catch (UnsupportedAudioFileException e) {
		e.printStackTrace();
		throw new RuntimeException("Sound: Unsupported Audio File: " + e);
	    }
	    catch (IOException e) {
		e.printStackTrace();
		throw new RuntimeException("Sound: Input/Output Error: " + e);
	    }
	    catch (LineUnavailableException e) {
		e.printStackTrace();
		throw new RuntimeException("Sound: Line Unavailable Exception Error: " + e);
	    }
	    // play, stop, loop the sound clip
	}
	public void play(){
	    clip.setFramePosition(0);  // Must always rewind!
	    clip.start();
	}
	public void loop(){
	    clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	public void stop(){
            clip.stop();
        }
    }


    public MazeGui(String[] args){
	this.settings = new MazeSettings(); // instantiate MazeSettings object to hold command line args
	this.oldSettings = new MazeSettings(); // instantiate MazeSettings object to hold settings to be serialized
	this.gameSave = null; // for cmd line purposes, say that the game is new and has no saved game attributed to it
	// check for command line arguments, initialize variables accordingly


	if (args.length != 0 && args.length != 2 && args.length != 5 && args.length != 9) {
	    System.out.println("Improper number of command line arguments: " + args.length);
	    System.out.println("Type ant run for proper usage.");
	    return;
	}
	switch (args.length) { // NOTE: no break statements.. cases flow through
	case 9:
	    settings.startRow = Integer.parseInt(args[5]);
	    settings.startCol = Integer.parseInt(args[6]);
	    settings.endRow = Integer.parseInt(args[7]);
	    settings.endCol = Integer.parseInt(args[8]);
	case 5:
	    settings.rows = Integer.parseInt(args[2]);
	    settings.cols = Integer.parseInt(args[3]);
	    settings.cellWidth = Integer.parseInt(args[4]);
	    // set endRow and endCol to rows-1 and cols-1 respectively, but
	    // only if they weren't already explicitly defined under (case 9:)
	    settings.endRow = args.length != 9 ? settings.rows-1 : settings.endRow;
	    settings.endCol = args.length != 9 ? settings.cols-1 : settings.endCol;
	case 2:
	    settings.genChainLength = Integer.parseInt(args[0]);
	    settings.genChainLengthFlux = Integer.parseInt(args[1]);
	}

	// initialize the JFrame
	this.frame = new JFrame();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setTitle("Maze Game");
	
	//initialize timer/controls bar
	this.timerBar = new MazeTimerBar(this);
	frame.add(timerBar, BorderLayout.SOUTH);
	
	//initialize menu bar and menus
	this.menuBar = new JMenuBar();
	this.menu = new JMenu("Menu");
	ButtonGroup group = new ButtonGroup();
	JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem("Multi Chain Generator");
	rbMenuItem.setSelected(true);
	rbMenuItem.setActionCommand("multi_chain_gen");
	rbMenuItem.addActionListener(this);
	group.add(rbMenuItem);
	menu.add(rbMenuItem);
	rbMenuItem = new JRadioButtonMenuItem("Alt Step Generator");
	rbMenuItem.setActionCommand("alt_step_gen");
	rbMenuItem.addActionListener(this);
	group.add(rbMenuItem);
	menu.add(rbMenuItem);
	rbMenuItem = new JRadioButtonMenuItem("New Step Generator");
	rbMenuItem.setActionCommand("new_step_gen");
	rbMenuItem.addActionListener(this);
	group.add(rbMenuItem);
	menu.add(rbMenuItem);
	menu.addSeparator();
	JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem("Progressive Reveal");
	cbMenuItem.setActionCommand("prog_reveal");
	cbMenuItem.addActionListener(this);
	menu.add(cbMenuItem);
	menu.addSeparator();
        JCheckBoxMenuItem cbMenuItem2 = new JCheckBoxMenuItem("Inverse Mode");
        cbMenuItem2.setActionCommand("inverse_mode");
        cbMenuItem2.addActionListener(this);
        menu.add(cbMenuItem2);
	menu.addSeparator();
        JCheckBoxMenuItem cbMenuItem3 = new JCheckBoxMenuItem("Memory Mode");
        cbMenuItem3.setActionCommand("memory_mode");
        cbMenuItem3.addActionListener(this);
        menu.add(cbMenuItem3);
        menu.addSeparator();
	JMenuItem menuItem = new JMenuItem("Settings");
	menuItem.setActionCommand("settings");
	menuItem.addActionListener(this);
	menu.add(menuItem);
	
	menuItem = new JMenuItem("Save...");
	menuItem.setActionCommand("save");
	menuItem.addActionListener(this);
	menu.add(menuItem);

	menuItem = new JMenuItem("Load...");
	menuItem.setActionCommand("load");
	menuItem.addActionListener(this);
	menu.add(menuItem);

	this.menuBar.add(this.menu);
	
	this.colorMenu = new JMenu("Colors");
	ButtonGroup cGroup = new ButtonGroup();
	JRadioButtonMenuItem colorItem = new JRadioButtonMenuItem("Default");
	colorItem.setSelected(true);
	colorItem.setActionCommand("default_color");
	colorItem.addActionListener(this);
	cGroup.add(colorItem);
	colorMenu.add(colorItem);
	colorItem = new JRadioButtonMenuItem("Cool");
	colorItem.setActionCommand("cool_color");
	colorItem.addActionListener(this);
	cGroup.add(colorItem);
	colorMenu.add(colorItem);
	colorItem = new JRadioButtonMenuItem("Warm");
	colorItem.setActionCommand("warm_color");
	colorItem.addActionListener(this);
	cGroup.add(colorItem);
	colorMenu.add(colorItem);
	colorItem = new JRadioButtonMenuItem("Dark");
	colorItem.setActionCommand("dark_color");
	colorItem.addActionListener(this);
	cGroup.add(colorItem);
	colorMenu.add(colorItem);
	this.menuBar.add(this.colorMenu);
	
	this.shapeMenu = new JMenu("Shapes");
	ButtonGroup sGroup = new ButtonGroup();
	JRadioButtonMenuItem sItem = new JRadioButtonMenuItem("Rectangle");
	sItem.setSelected(true);
	sItem.setActionCommand("rect_shape");
	sItem.addActionListener(this);
	sGroup.add(sItem);
	shapeMenu.add(sItem);
	
	sItem = new JRadioButtonMenuItem("Circle");
	sItem.setActionCommand("circle_shape");
	sItem.addActionListener(this);
	sGroup.add(sItem);
	shapeMenu.add(sItem);
	this.menuBar.add(this.shapeMenu);
	
	frame.setJMenuBar(this.menuBar);

	// initialize the MazeGrid, MazeComponent, and MazeGenerator
	this.grid = new MazeGrid(settings.rows, settings.cols);
	this.mc = new MazeComponent(grid, settings.cellWidth, colorMode,rect);
	if(colorMode == 0)
	    frame.getContentPane().setBackground(Color.white);
	else if(colorMode == 1)
	    {
		Color c = new Color(0, 191, 255);
		frame.getContentPane().setBackground(c);
	    }
	else if(colorMode == 2)
	    {
		Color c = new Color (238,201,0);
		frame.getContentPane().setBackground(c);
	    }
	else if(colorMode == 3)
	    frame.getContentPane().setBackground(Color.black);
	frame.add(mc);
	frame.pack();
	frame.setVisible(true);
	this.mg = new MultipleChainGenerator(grid, settings.genChainLength, settings.genChainLengthFlux);

	//initialize the player
	this.player = new MazePlayer(this.grid, new Cell(settings.startRow,settings.startCol));
	grid.setPlayer(player);

	//set up player keybinds
	this.playerMoveAction = new KeyBoardAction();
	remapPlayerKeys(this.playerMoveAction);

	//init settings Dialog
	settingsDialog = new MazeSettingsDialog(settings, this);
	settingsDialog.setLocationRelativeTo(frame);

	//init file chooser window
	fc = new JFileChooser();
	fileFilter = new FileNameExtensionFilter("MazeGame saves (*.mzgs)", "mzgs");
	fc.addChoosableFileFilter(fileFilter);
	fc.setFileFilter(fileFilter);
    }


    /** Stepwise generates and displays maze
     */
    public void run() {
	//	Sound soundPlayer = new Sound("Music/the_wave.mp3");
	//	soundPlayer.play();
		    
	// generate the maze in steps if asked (rather than all at once using MazeGenerator.generate())
	// repaint() in between each step to watch it grow
	if(settings.progDraw){ // if the user chooses to watch the drawing of the maze
	    if(drawTimer!=null)
		drawTimer.stop();
	    drawTimer = new Timer(1, new ActionListener() {

		    int i=0;
		    public void actionPerformed(ActionEvent e){
			++i;
			if(mg.step() && i%(settings.progDrawSpeed)==0)
			    {			    
				frame.repaint();
			    }
			else if(i%(settings.progDrawSpeed)==0){
			    //done drawing
			    ((Timer)e.getSource()).stop();
			    timerBar.startTimer();
			    grid.markStartFinish(new Cell(settings.startRow,settings.startCol),
						 new Cell(settings.endRow,settings.endCol));
			    if(settings.progReveal) { // if the user chooses to enable Progressive Reveal
				grid.setProgReveal(player, settings.progRevealRadius);
				if (gameSave != null) { // if the game is new and has no saved game attributed to it
				    gameSave.getGrid().unmarkVisitedCoordinates(gameSave);
				}
				else {
				    grid.updatePlayerPosition();
				    mc.repaint();
				}
			    }
			    else {
				grid.updatePlayerPosition();
				mc.repaint();		
			    }
			}
		    }
		});
	    drawTimer.start();
	}
	else{ //quick draw, the user chooses not to watch the drawing of the maze
	    mg.generate();
	    timerBar.startTimer();
	    grid.markStartFinish(new Cell(settings.startRow,settings.startCol),new Cell(settings.endRow,settings.endCol));
	    if(settings.progReveal) { // if the user chooses to enable Progressive Reveal
		grid.setProgReveal(player, settings.progRevealRadius);
		if (gameSave != null) { // if the game is new and has no saved game attributed to it
		    gameSave.getGrid().unmarkVisitedCoordinates(gameSave);
		}
	    }
	    else {
		grid.updatePlayerPosition();
		mc.repaint();
	    }
	}
    }
    
    /**
       An alternative to the no-arg run() method that deals with
       the case of using newGame(game) where game had progressive
       reveal enabled.
    */
    public void run(boolean progOn) {
	// generate the maze in steps if asked (rather than all at once using MazeGenerator.generate())
	// repaint() in between each step to watch it grow
	if(settings.progDraw){
	    if(drawTimer!=null)
		drawTimer.stop();
	    drawTimer = new Timer(1, new ActionListener() {
		    int i=0;
		    public void actionPerformed(ActionEvent e){
			++i;
			if(mg.step() && i%(settings.progDrawSpeed)==0)
			    {
				frame.repaint();
			     }
			 else if(i%(settings.progDrawSpeed)==0){
			     //done drawing
			     ((Timer)e.getSource()).stop();
			     timerBar.startTimer();
			     grid.markStartFinish(new Cell(settings.startRow,settings.startCol),new Cell(settings.endRow,settings.endCol));
			     if(settings.progReveal) {
				 if (gameSave != null) gameSave.getGrid().unmarkVisitedCoordinates(gameSave);
			     }
			     else {
				 grid.updatePlayerPosition();
				 mc.repaint();
			    }
			}
		    }
		});
	    drawTimer.start();
	}
	else{ //quick draw
	    mg.generate();
	    timerBar.startTimer();
	    grid.markStartFinish(new Cell(settings.startRow,settings.startCol),new Cell(settings.endRow,settings.endCol));
	    if(settings.progReveal) {
		if (gameSave != null) gameSave.getGrid().unmarkVisitedCoordinates(gameSave);
	    }
	    else {
		grid.updatePlayerPosition();
		mc.repaint();
	    }
	}
    }




    /** Creates new maze with current options, then displays and restarts game
     */
    public void newMaze() {
	timerBar.stopTimer();

	frame.remove(mc);
	this.gameSave = null;
	this.oldSettings=new MazeSettings(settings);
	//settingsDialog.getPanel().writeback();//
	this.grid = new MazeGrid(settings.rows, settings.cols);
	this.mc = new MazeComponent(grid, settings.cellWidth,colorMode,rect);
	mc.setVisible(true);
if(colorMode == 0)
	    frame.getContentPane().setBackground(Color.white);
	else if(colorMode == 1)
	    {
		Color c = new Color(0, 191, 255);
		frame.getContentPane().setBackground(c);
	    }
	else if(colorMode == 2)
	    {
		Color c = new Color (238,201,0);
		frame.getContentPane().setBackground(c);
	    }
	else if(colorMode == 3)
	    frame.getContentPane().setBackground(Color.black);	
	frame.add(mc);
        frame.pack();
	frame.setVisible(true);
	switch(settings.genType){
	case MazeGui.MULTI_CHAIN_GEN:
	    this.mg = new MultipleChainGenerator(grid, settings.genChainLength, settings.genChainLengthFlux);
	    break;
	case MazeGui.ALT_STEP_GEN:
	    this.mg = new AltStepGenerator(grid, settings.stepGenDistance);
	    break;
	case MazeGui.NEW_STEP_GEN:
	    this.mg = new NewStepGenerator(grid, settings.stepGenDistance);
	    break;
	}
	this.player = new MazePlayer(this.grid, new Cell(settings.startRow,settings.startCol));


	Action playerMoveAction = new KeyBoardAction();
	//settingsDialog.getPanel().writeback();//
	run();
    }

    /** Creates a new maze from saved game state, possibly including grid,
	settings, time, player position
	@param game Game state to resume
    */
    public void newMaze(MazeGameSave game){
	if(game == null){
	    System.err.println("Error reading MazeSaveGame object");
	    //settingsDialog.getPanel().writeback();//
	    newMaze();
	}
	else{ // restore the settings of an old game, instead with a new player and
	    // 0 elapsed time
	    timerBar.stopTimer();
	    frame.remove(mc);
	    this.settings=game.getSettings();
	    this.grid=game.getGrid();
	    this.mc=new MazeComponent(grid, settings.cellWidth,colorMode,rect);
	    if(colorMode == 0)
		frame.getContentPane().setBackground(Color.white);
	    else if(colorMode == 1)
		{
		    Color c = new Color(0, 191, 255);
		    frame.getContentPane().setBackground(c);
		}
	    else if(colorMode == 2)
		{
		    Color c = new Color (238,201,0);
		    frame.getContentPane().setBackground(c);
		}
	    else if(colorMode == 3)
		frame.getContentPane().setBackground(Color.black);
	    timerBar.setTimeElapsed(game.getTimeElapsed());
	    mc.setVisible(true);
	    frame.add(mc);
	    frame.pack();
	    frame.setVisible(true);
	    this.player=game.getPlayer();
	    if(game.hasHighScores()){
		JOptionPane.showMessageDialog(this.frame,gameSave.getAllScoresString()+"Press OK to begin!");
	    }
	    if (settings.progReveal) run(true);
	    else run();
	}
    }



    /** Will reveal maze if necessary and show solution
     */
    public void solveMaze() {
	timerBar.stopTimer();
	//reveal maze if hidden
	grid.unmarkCellsInRadius(new Cell(0,0), grid.getCols()+grid.getRows(), MazeGrid.MARKER5);
	// display the solution to the maze
	mg.solve(new Cell(settings.startRow, settings.startCol), (short)0x0,
		 new Cell(settings.endRow, settings.endCol));
	this.player=null;
	mc.repaint();
    }

    /** Callback for menu choice changes
     */
    public void actionPerformed(ActionEvent e) {
	if("multi_chain_gen".equals(e.getActionCommand())){
	    settings.genType = MazeGui.MULTI_CHAIN_GEN;
	}
	else if("alt_step_gen".equals(e.getActionCommand())){
	    settings.genType = MazeGui.ALT_STEP_GEN;
	}
	else if("new_step_gen".equals(e.getActionCommand())){
	    settings.genType = MazeGui.NEW_STEP_GEN;
	}
	else if("settings".equals(e.getActionCommand())){
	    settingsDialog.setVisible(true);
	}
	else if("prog_reveal".equals(e.getActionCommand())){
	    AbstractButton button = (AbstractButton)e.getSource();
	    settings.progReveal=button.getModel().isSelected();
	}
	else if("inverse_mode".equals(e.getActionCommand())){
	    AbstractButton button = (AbstractButton)e.getSource();
	    settings.inverseMode=button.getModel().isSelected();
	}
        else if("memory_mode".equals(e.getActionCommand())){
            AbstractButton button = (AbstractButton)e.getSource();
            settings.memoryMode=button.getModel().isSelected();
        }
	else if("default_color".equals(e.getActionCommand())){
	    AbstractButton button = (AbstractButton)e.getSource();   
	    colorMode = 0;
	}
	else if("cool_color".equals(e.getActionCommand())){
	    AbstractButton button = (AbstractButton)e.getSource();   
	    colorMode = 1;
	}
	else if("warm_color".equals(e.getActionCommand())){
	    AbstractButton button = (AbstractButton)e.getSource();   
	    colorMode = 2;
	}
	else if("dark_color".equals(e.getActionCommand())){
	    AbstractButton button = (AbstractButton)e.getSource();   
	    colorMode = 3;
	}
	else if("rect_shape".equals(e.getActionCommand())){
	    AbstractButton button = (AbstractButton)e.getSource();
	    this.rect = true;
	}
	else if("circle_shape".equals(e.getActionCommand())){
	    AbstractButton button = (AbstractButton)e.getSource();
	    this.rect = false;
	}
	else if("save".equals(e.getActionCommand())){ // user chooses to save mid-game
	    timerBar.stopTimer();
	    realTime = timerBar.getTimeElapsed(); // records ACTUAL time when save button is pressed
	    //prompt user and write to file
	    int returnVal = fc.showSaveDialog(this.frame);
	    if(returnVal == JFileChooser.APPROVE_OPTION){
		File file;
		//System.out.println(fc.getSelectedFile().toString());
		if(fc.getSelectedFile().toString().length()>=5 && fc.getSelectedFile().toString().substring(fc.getSelectedFile().toString().length()-5).equals(".mzgs"))
		    file = fc.getSelectedFile();
		else
		    file = new File(fc.getSelectedFile()+".mzgs");
		FileOutputStream fout;
		ObjectOutputStream oout;
		try{
		    fout = new FileOutputStream(file);
		    oout = new ObjectOutputStream(fout);
		    if(this.gameSave == null){
			oout.writeObject(new MazeGameSave(this.grid, this.oldSettings,this.player,realTime));
		    }
		    else{
			this.gameSave.setTimeElapsed(realTime); // saves ACTUAL time to file
			oout.writeObject(this.gameSave);
		    }
		    oout.close();
		    fout.close();
		}
		catch(IOException ioe){ ioe.printStackTrace(); }
		player=null;
		this.mc.setVisible(false);

		// Set an onscreen message to guide user after
		// previous Maze is saved.
		JOptionPane.showMessageDialog(frame,"To start new game press OK then New.",
					      "Maze Saved", JOptionPane.INFORMATION_MESSAGE);
	    }
	}
	else if("load".equals(e.getActionCommand())){ // user selects to load a game
	    int returnVal = fc.showOpenDialog(this.frame);
	    if(returnVal == JFileChooser.APPROVE_OPTION){
		File file = fc.getSelectedFile();
		FileInputStream fin;
		ObjectInputStream oin;
		try{
		    fin = new FileInputStream(file);
		    oin = new ObjectInputStream(fin);
		    MazeGameSave game = (MazeGameSave)oin.readObject();
		    oin.close();
		    fin.close();
		    game.getGrid().unmarkFinish(); // remove old player
		    this.gameSave = game;
		    newMaze(game); // this "restarts" the game from load point
		}
		catch(IOException | ClassNotFoundException ex){
		    System.err.println("Invalid file specified.");
		    ex.printStackTrace(); }
	    }
	}

    }

    /** Call when user has successfully navigated the maze.
	Eventually want to show win dialog.
    */
    private void wonMaze(){
	timerBar.stopTimer();
	realTime = timerBar.getTimeElapsed();
	String message = "Congratulations, you won!\nIt took you " +player.getNumMoves()+" moves and "+realTime/1000.0+" seconds.\n";

	// check if the user loaded a maze or if it was new
	if(this.gameSave != null && this.gameSave.hasHighScores() ){
	    if(realTime<gameSave.getHighScore().getTime()){ // beat the saved score
		message+="New High Score! You beat "+gameSave.getHighScore().getName()+ " by "+(gameSave.getHighScore().getTime()-realTime)/1000.0+" s.\n";
	    }
	    else{ // did not beat saved score
		message+="Not as good as "+gameSave.getHighScore().getName()+" with"+gameSave.getHighScore().getTime()/1000.0+"\n";
	    }
	    message+=gameSave.getAllScoresString();
	}

	message+="Would you like to save this score to this maze?\n";
	int choice = JOptionPane.showConfirmDialog(frame, message, "Victory",JOptionPane.YES_NO_OPTION);

	if(choice == JOptionPane.YES_OPTION){
	    String name = JOptionPane.showInputDialog(this.frame,"Enter Name","Enter your name:");
	    //prompt user and write to file
	    int returnVal = fc.showSaveDialog(this.frame);
	    if(returnVal == JFileChooser.APPROVE_OPTION){
		File file = fc.getSelectedFile();
		FileOutputStream fout;
		ObjectOutputStream oout;

		try{ // Save Game object to file
		    fout = new FileOutputStream(file);
		    oout = new ObjectOutputStream(fout);
		    this.timerBar.setTimeElapsed(realTime);
		    if(this.gameSave == null){
			this.gameSave = new MazeGameSave(this.grid, this.oldSettings);
		    }

		    gameSave.addHighScore(new MazeHighScore(name, realTime, settings.rows, settings.cols));
		    gameSave.setTimeElapsed(0);
		    gameSave.resetPlayer();
		    oout.writeObject(gameSave);
		    oout.close();
		    fout.close();

		} catch(IOException ioe){ ioe.printStackTrace(); }

	    }

	    try{ // save high score to for table creation
		HighScoreSaver mySaver = new HighScoreSaver("HighScores.ser");

		ArrayList<MazeHighScore> currentScoreList = new ArrayList<MazeHighScore>();
		if (mySaver.hasEmptyFile()==false) { // if the .ser file=empty, then don't read
		    currentScoreList = mySaver.getHighScoreList();
		}
		currentScoreList.add(new MazeHighScore(name,realTime,settings.rows,settings.cols));
		mySaver.writeHighScoreList(currentScoreList);

	    }catch(IOException ioe){ ioe.printStackTrace(); }

	    // prompt user to try again
	    String message2 = "Try this maze again?";
	    int choice2 = JOptionPane.showConfirmDialog(frame, message2, "Victory",JOptionPane.YES_NO_OPTION);
	    if (choice2 == JOptionPane.YES_OPTION){
		gameSave.getGrid().unmarkFinish();
		if(gameSave!=null){
		    newMaze(gameSave); // reload them to the beginning of the maze
		}
	    }
	    else { // user chooses not to play again
		JOptionPane.showMessageDialog(this.frame,"Press 'New' to start a new maze!");
		this.player=null;
	    }
	} // end of block (user chooses to save)
	else{ // The user does not choose to save the game
	    JOptionPane.showMessageDialog(this.frame,"Press 'New' to start a new maze!");
	    this.player=null;
	}
    }

    /** Maps current player movement keys to an action
	@param a Action object to map all keys to
    */
    private void remapPlayerKeys(Action a){
	if(settings.inverseMode){
	    InputMap inputMap = ((JPanel)this.frame.getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	    inputMap.put(KeyStroke.getKeyStroke("S"),"player_up");
	    inputMap.put(KeyStroke.getKeyStroke("W"),"player_down");
	    inputMap.put(KeyStroke.getKeyStroke("D"),"player_left");
	    inputMap.put(KeyStroke.getKeyStroke("A"),"player_right");
	    inputMap.put(KeyStroke.getKeyStroke("P"),"pause_game");
	    ActionMap actionmap = ((JPanel)this.frame.getContentPane()).getActionMap();
	    actionmap.put("player_up",a);
	    actionmap.put("player_down",a);
	    actionmap.put("player_left",a);
	    actionmap.put("player_right",a);
	    actionmap.put("pause_game",a);
	}
	else {
	    InputMap inputMap = ((JPanel)this.frame.getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	    inputMap.put(KeyStroke.getKeyStroke("W"),"player_up");
	    inputMap.put(KeyStroke.getKeyStroke("S"),"player_down");
	    inputMap.put(KeyStroke.getKeyStroke("A"),"player_left");
	    inputMap.put(KeyStroke.getKeyStroke("D"),"player_right");
	    inputMap.put(KeyStroke.getKeyStroke("P"),"pause_game");
	    ActionMap actionmap = ((JPanel)this.frame.getContentPane()).getActionMap();
	    actionmap.put("player_up",a);
	    actionmap.put("player_down",a);
	    actionmap.put("player_left",a);
	    actionmap.put("player_right",a);
	    actionmap.put("pause_game",a);
	}
    }

    /** Action object that responds to player move keyboard inputs
     */
    class KeyBoardAction extends AbstractAction{
	public boolean isPaused = false;
	Font font = new Font("Verdana", Font.BOLD, 30);
	JTextArea pauseArea =
	    new JTextArea("\n\n\n       GAME PAUSED:\n\n    Press 'P' to Resume");
	public void actionPerformed(ActionEvent e){
	    if(player!=null){
		if(!settings.inverseMode){
		    switch(e.getActionCommand()){
		    case "w":
			if(isPaused == false) {player.move(MazeGrid.DIR_UP);}
			break;
		    case"s":
			if(isPaused == false) {player.move(MazeGrid.DIR_DOWN);}
			break;
		    case "a":
			if(isPaused == false) {player.move(MazeGrid.DIR_LEFT);}
			break;
		    case "d":
			if(isPaused == false) {player.move(MazeGrid.DIR_RIGHT);}
			break;
		    case "p":
			pauseArea.setEditable(false);
			pauseArea.setFont(font);
			//Game is Paused
			if (isPaused == true){
			    frame.remove(pauseArea);
			    frame.add(mc);
			    timerBar.resumeTimer();
			}
			//Game is not Paused
			else {
			    timerBar.stopTimer();
			    frame.remove(mc);
			    frame.add(pauseArea);
			}
			frame.repaint();
			frame.setVisible(true);
			if(!isPaused) isPaused = true;
			else isPaused = false;
			return;
		    }
		}
		else{
                    switch(e.getActionCommand()){
                    case "s":
                        if(isPaused == false) {player.move(MazeGrid.DIR_UP);}
                        break;
                    case "w":
                        if(isPaused == false) {player.move(MazeGrid.DIR_DOWN);}
                        break;
                    case "d":
                        if(isPaused == false) {player.move(MazeGrid.DIR_LEFT);}
                        break;
                    case "a":
                        if(isPaused == false) {player.move(MazeGrid.DIR_RIGHT);}
                        break;
                    case "p":
                        pauseArea.setEditable(false);
                        pauseArea.setFont(font);
                        //Game is Paused                                                                                                
                        if (isPaused == true){
                            frame.remove(pauseArea);
                            frame.add(mc);
                            timerBar.resumeTimer();
                        }
                        //Game is not Paused                                                                                            
                        else {
                            timerBar.stopTimer();
                            frame.remove(mc);
                            frame.add(pauseArea);
                        }
			frame.repaint();
			frame.setVisible(true);
                        if(!isPaused) isPaused = true;
                        else isPaused = false;
                        return;
                    }


		}
		if(settings.memoryMode){
		    if (player.getNumMoves()%5==0)
			mc.repaint();
		}
		else
		    mc.repaint();
		     
		if(grid.isAtFinish(player.getPosition())) wonMaze();
	    }
	    
	    else{
		System.err.println("NULL player!");
	    }
	}
    }
}
