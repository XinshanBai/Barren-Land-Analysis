## Notes
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
The problem describes the land as `a farm of 400m by 600m where coordinates of the field are from (0, 0) to (399, 599)`. This reminds me of 2 dimensional array.
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
This method compares the current coordinates with the barren land coordinates from the input data to check if `(i, j)` is within the barrenland very straight forward.  
### Count the fertile tiles with DFS
There is not specific reason for me to pick DFS over BFS, I just thought it is simple to implement with recursive... Which is a problem, will explain that later.  
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
Now inside the DFS method, instead of creating a object and have a visited boolean property, we can just set the fertile tile from 1 to 0, so we do not count them again. Here is the recursive call. Keep track of the `fertileAreas` we found, once the recursive call is done, which means we found our first fertile land, add that `fertileAreas` to the `fertileLandList`.  
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

### BUT, there was a problem...
