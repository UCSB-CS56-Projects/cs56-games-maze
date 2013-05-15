# cs56-games-maze

A program that generates at random a solvable rectangular maze. 

* [Archive page](https://foo.cs.ucsb.edu/cs56/issues/0000769/)
* [Mantis page](https://foo.cs.ucsb.edu/56mantis/view.php?id=769)

## Basic Description

The game creates a maze (slower than actual maze-generation time so that you can watch it grow) then solves it. The algorithm I use to generate the maze is based off of the recursive back-tracker algorithm. I modified the algorithm, however, to run first off of the newest added cell, then the oldest added cell in each iteration. This form of the algorithm generates a sort of branching effect from the point of origin. I also modified the algorithm to be able to randomly spawn new points of origin at a certain specified frequency (from command line arguments; jws uses default values) as the algorithm runs. This creates a sort of "bushy", more complex maze with more grequent path branching.

To see how the recursive backtracker works, visit http://weblog.jamisbuck.org/2011/2/7/maze-generation-algorithm-recap

Changes made to settings take effect on generation of a new maze.

## Gameplay

The player always start in the top left corner, and must always traverse to the lower right corner. Use WASD keys to move the player.

In Progressive Reveal mode, only a small portion of the maze is revelaed to the player at a time.

