package edu.ucsb.cs56.projects.games.cs56_games_maze;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.lang.Math;


/**
   A class extending JComponent that is used to draw all of the information held in
   a MazeGrid (the markers, as well as the remaining closed walls of each cell)

   @author Jake Staahl
   @author Evan West
   @version 5/14/13 for proj1, cs56, S13
   @see MazeGrid
*/
public class MazeComponent extends JComponent implements MouseListener{
    private MazeGrid grid;
    private int cellWidth;    
    private int colorMode;
    private boolean rect;
    /**
       Construct a MazeComponent to draw this MazeGrid grid, with the width of each
       Cell being drawn at cellWidth pixels wide and tall
       @param grid the MazeGrid this MazeComponent will be drawing
       @param cellWidth the width and height to draw each Cell
    */
    public MazeComponent(MazeGrid grid, int cellWidth, int inputColor, boolean r) {
	this.colorMode = inputColor;
	this.grid = grid;
	this.cellWidth = cellWidth;
	addMouseListener(this);
	this.setFocusable(true);
	this.rect = r;
    }
    
    /**
       Method in JComponent overrided to draw this MazeGrid
    */
    public void paintComponent(Graphics g) {
	Graphics2D g2 = (Graphics2D)g;
	// draw each cell in the grid using drawCell()
	Cell a = new Cell(0, 0);
	for (; a.row < grid.getRows(); a.row++) {
	    for (; a.col < grid.getCols(); a.col++) {
		    drawCell(g2, a);
	    }
	    a.col = 0;
	}
    }

    /**
       Method in JComponent overrided to set the preferred size of window space that this
       component gets
    */
    public Dimension getPreferredSize() {
        return new Dimension(grid.getCols()*cellWidth, grid.getRows()*cellWidth);
    }

    /**
       Method in JComponent overrided to set the minimum size of window space that this
       component gets
    */
    public Dimension getMinSize() {
        return new Dimension(grid.getCols()*cellWidth, grid.getRows()*cellWidth);
    }

    /**
       Draws the markers and closed walls of a given cell a in the Graphics2D object g2
       @param g2 the Graphics2D object within which this Cell is being drawn
       @param a the Cell that is being drawn
    */
    public void drawCell(Graphics2D g2, Cell a) {
	// paint the proper markers for the Cell in the proper order
	if(this.grid.hasMarker(a, MazeGrid.MARKER5)) //do not draw
	    return;
	if (this.grid.hasMarker(a, MazeGrid.MARKER3)) //solution
	    this.paintMarker3(g2, a);
	else if (this.grid.hasMarker(a, MazeGrid.MARKER1)) //finish
	    this.paintMarker1(g2, a);
	else if (this.grid.hasMarker(a, MazeGrid.MARKER2)) //start
	    this.paintMarker2(g2, a);
	if(this.grid.hasMarker(a, MazeGrid.MARKER4)){ //player
	    this.paintMarker4(g2, a);
	}
	
	// paint the walls of the Cell
	short directions = this.grid.getCellDirections(a);
	if(colorMode == 0)
	    g2.setColor(Color.BLACK);
	else if(colorMode == 1)
	    {
		Color c = new Color(46,139,87);	
		g2.setColor(c);
	    }
	else if(colorMode == 2)
	    {
		Color c = new Color(176,23,31);
		g2.setColor(c);
	    }
	else if(colorMode == 3)
	    g2.setColor(Color.white);
	
	if ((directions & MazeGrid.DIR_RIGHT) == 0) {
	    Line2D.Float wall = new Line2D.Float(this.cellWidth*a.col + this.cellWidth-1,
						 this.cellWidth*a.row + 0,
						 this.cellWidth*a.col + this.cellWidth-1,
						 this.cellWidth*a.row + this.cellWidth-1);
	    g2.draw(wall);
	}
	if ((directions & MazeGrid.DIR_UP) == 0) {
	    Line2D.Float wall = new Line2D.Float(this.cellWidth*a.col + 0,
						 this.cellWidth*a.row + 0,
						 this.cellWidth*a.col + this.cellWidth-1,
						 this.cellWidth*a.row + 0);
	    g2.draw(wall);
	}
	if ((directions & MazeGrid.DIR_LEFT) == 0) {
	    Line2D.Float wall = new Line2D.Float(this.cellWidth*a.col + 0,
						 this.cellWidth*a.row + 0,
						 this.cellWidth*a.col + 0,
						 this.cellWidth*a.row + this.cellWidth-1);
	    g2.draw(wall);
	}
	if ((directions & MazeGrid.DIR_DOWN) == 0) {
	    Line2D.Float wall = new Line2D.Float(this.cellWidth*a.col + 0,
						 this.cellWidth*a.row + this.cellWidth-1,
						 this.cellWidth*a.col + this.cellWidth-1,
						 this.cellWidth*a.row + this.cellWidth-1);
	    g2.draw(wall);
	}
    }

    /**
       How MazeGrid.MARKER1 should be painted. Change this if you want marker1 to be painted differently.
    */
    private void paintMarker1(Graphics2D g2, Cell a) {
	
	if(colorMode == 0)
	    g2.setColor(Color.RED);
	else if(colorMode == 1)
	    {
		Color c = new Color(255,182,193);
		g2.setColor(c);
	    }
	else if(colorMode == 2)
	    {
		Color c = new Color(255,153,255);
		g2.setColor(c);
	    }
	else if(colorMode == 3)
	    g2.setColor(Color.CYAN);
	
	g2.fill(new Rectangle2D.Float(this.cellWidth*a.col, this.cellWidth*a.row, this.cellWidth, this.cellWidth));

	//g2.fill(new Rectangle2D.Double(this.cellWidth*a.col + (0.4*this.cellWidth)-1,this.cellWidth*a.row + (0.4*this.cellWidth)-1,0.4*this.cellWidth,0.4*this.cellWidth));
    }

    /**
       How MazeGrid.MARKER2 should be painted. Change this if you want marker2 to be painted differently.
    */
    private void paintMarker2(Graphics2D g2, Cell a) {
	if(colorMode == 0)
	    g2.setColor(Color.CYAN);
	else if(colorMode == 1)
	    {
		Color c = new Color(173,255,46);
		g2.setColor(c);
	    }
	else if(colorMode == 2)
	    {	    
		Color c = new Color(58,213,254);
		g2.setColor(c);
	    }
	else if(colorMode == 3)
	    g2.setColor(Color.RED);
	
	g2.fill(new Rectangle2D.Float(this.cellWidth*a.col, this.cellWidth*a.row, this.cellWidth, this.cellWidth));
	//g2.fill(new Rectangle2D.Double(this.cellWidth*a.col + (0.4*this.cellWidth)-1, this.cellWidth*a.row + (0.4*this.cellWidth)-1, 0.4*this.cellWidth,0.4*this.cellWidth));
    }
    
    /**
       How MazeGrid.MARKER3 should be painted. Change this if you want marker3 to be painted differently.
    */
    private void paintMarker3(Graphics2D g2, Cell a) {
	if(colorMode == 0)
	    g2.setColor(Color.YELLOW);
	else if(colorMode == 1)
	    {
		g2.setColor(Color.YELLOW);
	    }
	else if(colorMode == 2)
	    {
		g2.setColor(Color.YELLOW);
	    }
	else if(colorMode == 3)
	    {
		Color c = new Color (128,0,128);
		g2.setColor(c);
	    }
	g2.fill(new Rectangle2D.Float(this.cellWidth*a.col, this.cellWidth*a.row, this.cellWidth, this.cellWidth));
    }
    
    /**
       How MazeGrid.MARKER4 should be painted. Change this if you want marker4 to be painted differently.
    */
    private void paintMarker4(Graphics2D g2, Cell a) {
	if(colorMode == 0)
	    g2.setColor(Color.BLACK);
	else if(colorMode == 1)
	    {
		Color c = new Color(238,154,0);
		g2.setColor(c);
	    }
	else if(colorMode == 2)
	    {
		g2.setColor(Color.red);
	    }		
	else if(colorMode == 3)
	    g2.setColor(Color.white);
	//g2.fill(new Rectangle2D.Float(this.cellWidth*a.col, this.cellWidth*a.row, this.cellWidth, this.cellWidth));
	if(rect == true)
	    g2.fill(new Rectangle2D.Double(this.cellWidth*a.col + (0.4*this.cellWidth)-1, this.cellWidth*a.row + (0.4*this.cellWidth)-1, 0.4*this.cellWidth,0.4*this.cellWidth));
	else
	    g2.fill(new Ellipse2D.Double(this.cellWidth*a.col + (0.4*this.cellWidth)-1, this.cellWidth*a.row + (0.4*this.cellWidth)-1, 0.4*this.cellWidth,0.4*this.cellWidth));
    }

    /** Sets MazeGrid associated with this MazeComponent
	@param mg New MazeGrid to associate with this component
     */
    public void setMazeGrid(MazeGrid mg){
	this.grid=mg;
    }

    /** MouseListener implementation to catch focus on click
     */
    @Override
    public void mouseClicked(MouseEvent e){
	requestFocusInWindow();
    }

    /** Functionless implementation for MouseListener interface */
    public void mouseExited(MouseEvent e){}
    /** Functionless implementation for MouseListener interface */
    public void mouseEntered(MouseEvent e){}
    /** Functionless implementation for MouseListener interface */
    public void mouseReleased(MouseEvent e){}
    /** Functionless implementation for MouseListener interface */
    public void mousePressed(MouseEvent e){}
}
