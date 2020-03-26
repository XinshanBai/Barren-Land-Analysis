package com.target.interview.barren_land_analysis_multithread;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import com.target.interview.barren_land_analysis.BarrenLandPosition;

/**
 * Barren Land Analysis
 *
 */
public class BarrenLandAnalysisMultiThreadV1 {

	private static final int WIDTH = 600;
	private static final int HEIGHT = 400;

	/*
	 * private static final int WIDTH = 6000; private static final int HEIGHT =
	 * 4000;
	 */

	private static final int NUMBER_OF_THREADS = 4;

	private static List<BarrenLandPosition> barrenLandList = new ArrayList<>();
	private static AtomicInteger[][] landGrid = new AtomicInteger[WIDTH][HEIGHT];
	private static List<AtomicInteger> fertileLandList = new ArrayList<>();
	private static AtomicInteger fertileArea = new AtomicInteger(0);

	public static void main(String[] args) {

		String[] input = readInput();

		initBarrenLandList(input);

		createLandGrid();

		countFertileAreaMutiThread();

		displayFertileAreas();
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
					landGrid[i][j] = new AtomicInteger(0);
				} else {
					// fertile tile
					landGrid[i][j] = new AtomicInteger(1);
				}
			}
		}
	}

	/**
	 * Check all the tiles and count the fertile lands' area. Each fertile land's
	 * area is added into the fertile land list.
	 */
	private static void countFertileAreaMutiThread() {
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				// Is fertile tile
				if (landGrid[i][j].get() == 1) {
					checkFertileTilesMutiThread(i, j);
					fertileLandList.add(fertileArea);
					fertileArea = new AtomicInteger(0);
				}
			}
		}
	}

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
	private static void checkFertileTilesMutiThread(int i, int j) {
		Stack<Point> tileStack = new Stack<>();
		tileStack.push(new Point(i, j));

		Runnable checkFertileTilesRunnable = () -> {
			while (true) {
				Point currentTile = null;
				synchronized (tileStack) {
					if (!tileStack.isEmpty()) {
						currentTile = tileStack.pop();
					} else {
						// Following threads created very shortly after the previous ones, the stack
						// might be empty at that moment, need to release the lock on stack and let the
						// other threads push new Point objects into it
						try {
							tileStack.wait(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						// If the thread is still empty after shortly release the lock, the check is
						// done
						if (tileStack.isEmpty()) {
							break;
						} else {
							continue;
						}
					}
				}
				if (currentTile != null) {
					int x = (int) currentTile.getX();
					int y = (int) currentTile.getY();

					if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT)
						continue;

					synchronized (landGrid[x][y]) {
						// If the point is a barren tile or visited.
						if (landGrid[x][y].get() == 0)
							continue;

						// Mark the fertile tile as visited by changing it to a barren tile.
						landGrid[x][y].set(0);
					}

					fertileArea.addAndGet(1);

					// Find all adjacent tiles and add them to the stack for checking.
					tileStack.push(new Point(x, y - 1)); // down
					tileStack.push(new Point(x, y + 1)); // up
					tileStack.push(new Point(x - 1, y)); // left
					tileStack.push(new Point(x + 1, y)); // right
				}
			}
		};

		List<Thread> threadList = new ArrayList<>();

		int numberOfThreads = 0;
		while (numberOfThreads < NUMBER_OF_THREADS) {
			Thread checkFertileTilesThread = new Thread(checkFertileTilesRunnable);
			checkFertileTilesThread.start();
			threadList.add(checkFertileTilesThread);
			numberOfThreads++;
		}

		for (Thread checkFertileTilesThread : threadList) {
			try {
				checkFertileTilesThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method will clean up all static variables It is called by JUnit test
	 * code for testing purpose
	 */
	public static void cleanUp() {
		barrenLandList = new ArrayList<>();
		landGrid = new AtomicInteger[WIDTH][HEIGHT];
		fertileLandList = new ArrayList<>();
		fertileArea = new AtomicInteger(0);
	}
}
