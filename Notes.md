# Notes
This file provides some details about how the design decisions were made and some challenges in the process.
##  First draft of Sample1
I tried to draw a grid based on the sample data, the first grid came out my mind has the origin at the top left. But based on the description of the problem. This is not correct.  
<img src="https://github.com/XinshanBai/Barren-Land-Analysis/blob/master/images/First%20draft%20of%20Sample1.png?raw=true" width="70%">
## Sample1
I redraw it and make sure `The first two integers are the coordinates of the bottom left corner in the given rectangle, and the last two integers are the coordinates of the top right corner.` This is the image draft for Sample1.  
<img src="https://github.com/XinshanBai/Barren-Land-Analysis/blob/master/images/Sample1.png?raw=true" width="70%">
## Sample2
This is the image draft for Sample2.  
<img src="https://github.com/XinshanBai/Barren-Land-Analysis/blob/master/images/Sample2.png?raw=true" width="70%">
## Sample2_1
Here, visually we can see that there might be a way to count the blue rectangles and calculate their area. but it is really difficult to figure out where the blue rectangles start and end, especially when there are a lot of barren lands.  
<img src="https://github.com/XinshanBai/Barren-Land-Analysis/blob/master/images/Sample2_1.png?raw=true" width="70%">
## Depth-frst search or Breadth-first search is the solution
It is not easy to calculate the areas, but we can count them.
### Create the grid
The problem describes the land as `a farm of 400m by 600m where coordinates of the field are from (0, 0) to (399, 599)`. This reminds me of the 2-dimensional array.
``` java
	/**
	 * Create a two dimensional array which represents the whole land based on the
	 * console input. 0 indicates the barren tiles. 1 indicates the fertile tiles.
	 */
	private static void createLandGrid() {
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				if (isBarrenLand(i, j)) {
					// barren tile
					landGrid[i][j] = 0;
				} else {
					// fertile tile
					landGrid[i][j] = 1;
				}
			}
		}
	}
```
**isBarrenLand(i, j)**  
This method compares the current coordinates with the barren land coordinates from the input data to check if `(i, j)` is within the barren land very straight forward.  
### Count the fertile tiles with DFS
There is no specific reason for me to pick DFS over BFS, I just thought it is simple to implement with recursive... Which is a problem, will explain that later.  
Now we use DFS to traversal the grid. Start to call DFS when we find a fertile tile.  
```java
	/**
	 * Check all the tiles and count the fertile lands' area. Each fertile land's
	 * area is added into the fertile land list.
	 */
	private static void countFertileArea() {
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				// Is fertile tile
				if (landGrid[i][j] == 1) {
					checkAdjacentTiles(i, j);
					fertileLandList.add(fertileArea);
					fertileArea = 0;
				}
			}
		}
	}
```
Now inside the DFS method, instead of creating an object and have a visited boolean property, we can just set the fertile tile from 1 to 0, so we do not count them again.  
Here is the recursive call. Keep track of the `fertileAreas` we found, once the recursive call is done, which means we found our first fertile land, add that `fertileAreas` to the `fertileLandList`.  
```java
private static void checkAdjacentTiles(int i, int j) {
		if(i < 0 || i >= WIDTH || j < 0 || j >= HEIGHT || land[i][j] == 0)
			return;
		land[i][j] = 0;
		fertileArea++;
		checkAdjacentTiles(i, j - 1);
		checkAdjacentTiles(i, j + 1);
		checkAdjacentTiles(i - 1, j);
		checkAdjacentTiles(i + 1, j);
	}
```
And the end, format the value in `fertileLandList`, and display them as required in the problem description.
```java
	/**
	 * Sort the results in the fertile land list and display the results as
	 * requested format.
	 */
	private static void displayFertileAreas() {
		fertileLandList.sort((AtomicInteger o1, AtomicInteger o2) -> o1.get() - o2.get());
		StringBuilder results = new StringBuilder();
		// Convert the result list to required format which is fertile lands areas
		// separated by whitespace.
		for (AtomicInteger fertileArea : fertileLandList) {
			results.append(fertileArea);
			results.append(" ");
		}
		// Remove the extra whitespace at the end then display the results.
		System.out.print(results.toString().trim());
	}
```
### BUT, there was a problem...
**StackOverflowException** :sweat_smile:  
I realize that the recursive call went to deep, the default JVM stack size is not enough for this. Changing the JVM default thread stack size apparently is not a good idea.  
So I decided to use a Stack object to replace the recursive call, looks like the default JVM heap size is enough to handle this Stack.  
```java
	/**
	 * Check current fertile tile, mark it as visited by changing it from
	 * <code>1</code> to <code>0</code>. Increase the fertile tile area for each
	 * valid fertile tile found. Continue using DFS to go through all connected
	 * fertile tiles.
	 * 
	 * @param i current fertile tile's horizontal coordinate
	 * @param j current fertile tile's vertical coordinate
	 */
	private static void checkFertileTiles(int i, int j) {
		Stack<Point> tileStack = new Stack<>();
		tileStack.push(new Point(i, j));

		while (!tileStack.isEmpty()) {
			Point currentTile = tileStack.pop();
			int x = (int) currentTile.getX();
			int y = (int) currentTile.getY();

			// If the point is out of the grid's bound or it is a barren tile or visited.
			if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT || landGrid[x][y] == 0)
				continue;

			// Mark the fertile tile as visited by changing it to a barren tile.
			landGrid[x][y] = 0;
			fertileArea++;
			// Find all adjacent tiles and add them to the stack for checking.
			tileStack.push(new Point(x, y - 1));
			tileStack.push(new Point(x, y + 1));
			tileStack.push(new Point(x - 1, y));
			tileStack.push(new Point(x + 1, y));
		}
	}
```
### Unit Test
During the unit test, run a single test always pass, but run them together only the first one passes.  
This was because the global  variables I have used were  not cleared between tests. So I created a cleanup method at the end just to set all of them back to initial states. Then call this method in the unit test before each test.
### Challenges
There was no actual challenge while implementing this.  
* At first, I was surprised that the recursive method did not work because I recently worked on some questions with this method, and it was perfectly fine. But it is easy to see the stack size was the problem.  
* I haven't done unit test for console input and output before. But internet is always here to help.
