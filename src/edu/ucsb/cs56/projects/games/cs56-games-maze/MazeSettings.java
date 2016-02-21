package edu.ucsb.cs56.projects.games.cs56_games_maze;

import java.io.Serializable;

/**
 Class to hold settings information for one game.
 Made to be passed into settings editor window.
 @author Evan West
 @author Logan Ortega
 @author Richard Wang
 @version 2/24/14 for proj1, cs56, W14
*/

public class MazeSettings implements Serializable
{
    public int genChainLength;
    public int genChainLengthFlux;
    public int stepGenDistance;
    public int rows;
    public int cols;
    public int cellWidth;
    public int startRow;
    public int startCol;
    public int endRow;
    public int endCol;
    public int genType;
    public boolean progReveal;
    public boolean inverseMode;
    public boolean memoryMode;
    public int progRevealRadius;
    public boolean progDraw;
    public int progDrawSpeed;

    /** No-arg constructor, creates all default values
     */
    public MazeSettings(){
	this.genChainLength=50;
	this.genChainLengthFlux=50;
	this.stepGenDistance=2;
	this.rows=20;
	this.cols=20;
	this.cellWidth=20; // increase size of maze
	this.startRow=0;
	this.startCol=0;
	this.endRow=rows-1;
	this.endCol=cols-1;
	this.genType=1;
	this.progReveal=false;
	this.inverseMode=false;
	this.memoryMode=false;
	this.progRevealRadius=3;
	this.progDraw=true;
	this.progDrawSpeed=10;
    }
    /** Copy constructor, used to clone settings objects for saving.
	@param other Another MazeSettings object to copy all values from;
     */
    public MazeSettings(MazeSettings other){
	this.genChainLength=other.genChainLength;
	this.genChainLengthFlux=other.genChainLengthFlux;
	this.stepGenDistance=other.stepGenDistance;
	this.rows=other.rows;
	this.cols=other.cols;
	this.cellWidth=other.cellWidth;
	this.startRow=other.startRow;
	this.startCol=other.startCol;
	this.endRow=other.endRow;
	this.endCol=other.endCol;
	this.genType=other.genType;
	this.inverseMode = other.inverseMode;
	this.memoryMode = other.memoryMode;
	this.progReveal=other.progReveal;
	this.progRevealRadius=other.progRevealRadius;
	this.progDraw=other.progDraw;
	this.progDrawSpeed=other.progDrawSpeed;
    }
}
