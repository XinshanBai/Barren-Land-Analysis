package com.target.interview.barren_land_analysis;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

/**
 * Barren Land Analysis
 *
 */
public class BarrenLandAnalysis {

	private static int WIDTH = 600;
	private static int HEIGHT = 400;

	private static List<BarrenLandPosition> barrenLandList = new ArrayList<>();
	private static int[][] landGrid;
	private static List<Integer> fertileLandList = new ArrayList<>();
	private static int fertileArea = 0;

	public static void main(String[] args) {

		initLandGrid(args);

		String[] input = readInput();

		initBarrenLandList(input);

		createLandGrid();

		countFertileArea();

		displayFertileAreas();
	}

	/**
	 * Set a different land size for testing
	 * 
	 * @param args String array, the first element is WIDTH, second is HEIGHT
	 */
	private static void initLandGrid(String[] args) {
		if (args != null && args.length != 0) {
			WIDTH = Integer.valueOf(args[0]);
			HEIGHT = Integer.valueOf(args[1]);
		}

		landGrid = new int[WIDTH][HEIGHT];
	}

	/**
	 * Read user input from console and reformat the input from a single string to a
	 * string array of barren land rectangles' positions.
	 * 
	 * @return the string array contains barren land rectangles' positions
	 */
	private static String[] readInput() {
		try (Scanner sc = new Scanner(System.in)) {
			String stdinput = sc.nextLine();
			// This will remove the starting "{", ending "}" and all double quotes in the
			// string (Example: {"48 192 351 207", "48 392 351 407"} to 48 192 351 207, 48
			// 392 351 407) then split the string into string array separated by comma plus
			// a whitespace.
			return stdinput.replaceAll("^\\{|\"|\\}$", "").split(", ");
		}
	}

	/**
	 * Initialize the list of {@link BarrenLandPosition}, each element in the list
	 * contains a barren land rectangle's bottom, left, top and right positions.
	 * 
	 * @param input the reformatted string array of the console input
	 */
	private static void initBarrenLandList(String[] input) {
		if (input != null && input.length != 0) {
			try {
				for (String points : input) {
					// The requirement indicates the first two integers are the coordinates of the
					// bottom left corner in the given rectangle, and the last two integers are the
					// coordinates of the top right corner.
					// Thus use 0 as bottom, 1 as left, 2 as top and 3 as right makes the problem
					// easier to solve.
					String[] barrenLandArray = points.split(" ");
					BarrenLandPosition barrenLandPosition = new BarrenLandPosition();
					barrenLandPosition.setBottom(Integer.valueOf(barrenLandArray[0]));
					barrenLandPosition.setLeft(Integer.valueOf(barrenLandArray[1]));
					barrenLandPosition.setTop(Integer.valueOf(barrenLandArray[2]));
					barrenLandPosition.setRight(Integer.valueOf(barrenLandArray[3]));
					barrenLandList.add(barrenLandPosition);
				}
			} catch (Exception e) {
				System.out.println("Invalid input");
				System.exit(0);
			}
		} else {
			System.out.println("Invalid input");
			System.exit(0);
		}
	}

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

	/**
	 * Check all the tiles and count the fertile lands' area. Each fertile land's
	 * area is added into the fertile land list.
	 */
	private static void countFertileArea() {
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				// Is fertile tile
				if (landGrid[i][j] == 1) {
					checkFertileTiles(i, j);
					fertileLandList.add(fertileArea);
					fertileArea = 0;
				}
			}
		}
	}

	/**
	 * Sort the results in the fertile land list and display the results as
	 * requested format.
	 */
	private static void displayFertileAreas() {
		Collections.sort(fertileLandList);
		StringBuilder results = new StringBuilder();
		// Convert the result list to required format which is fertile lands areas
		// separated by whitespace.
		for (int fertileArea : fertileLandList) {
			results.append(fertileArea);
			results.append(" ");
		}
		// Remove the extra whitespace at the end then display the results.
		System.out.print(results.toString().trim());
	}

	/**
	 * This method checks each tile against the barren land rectangles' position
	 * list.
	 * 
	 * @param i current tile's the horizontal coordinate
	 * @param j current tile's the vertical coordinate
	 * @return <code>true</code> if the current coordinates is within any of the
	 *         barren lands rectangles <code>false</code> otherwise.
	 */
	private static boolean isBarrenLand(int i, int j) {
		for (BarrenLandPosition barrenLandPosition : barrenLandList) {
			if (i >= barrenLandPosition.getLeft() && i <= barrenLandPosition.getRight()
					&& j >= barrenLandPosition.getBottom() && j <= barrenLandPosition.getTop()) {
				return true;
			}
		}
		return false;
	}

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

	/**
	 * This method will clean up all static variables It is called by JUnit test
	 * code for testing purpose
	 */
	public static void cleanUp() {
		WIDTH = 600;
		HEIGHT = 400;
		barrenLandList = new ArrayList<>();
		fertileLandList = new ArrayList<>();
		fertileArea = 0;
	}
}
