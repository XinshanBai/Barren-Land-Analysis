package com.target.interview.barren_land_analysis_multithread;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import com.target.interview.barren_land_analysis.BarrenLandPosition;

/**
 * Barren Land Analysis
 *
 */
public class BarrenLandAnalysisMultiThreadV2 {

	public static final int WIDTH = 600;
	public static final int HEIGHT = 400;

	/*
	 * public static final int WIDTH = 6000; public static final int HEIGHT = 4000;
	 */

	private static final int NUMBER_OF_THREADS = 2;

	// The list contains HashSet objects, each object will store all the threads' id
	// who processed the same fertile land
	public static List<Set<Integer>> connectedThreadList = new ArrayList<>();

	private static List<BarrenLandPosition> barrenLandList = new ArrayList<>();
	public static AtomicInteger[][] landGrid = new AtomicInteger[WIDTH][HEIGHT];
	private static List<AtomicInteger> fertileLandList = new ArrayList<>();
	public static AtomicInteger fertileArea = new AtomicInteger(0);

	public static void main(String[] args) {

		String[] input = readInput();

		initBarrenLandList(input);

		createLandGridMutiThread();

		countFertileAreaMutiThread();

		countRestFertileAreaMutiThread();

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
	private static void createLandGridMutiThread() {
		Runnable runnable = () -> {
			Queue<Point> tileQueue = new ArrayDeque<>();
			tileQueue.add(findRandomPoint());
			while (!tileQueue.isEmpty()) {
				Point currentTile = tileQueue.poll();
				int x = (int) currentTile.getX();
				int y = (int) currentTile.getY();
				if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT || landGrid[x][y] != null)
					continue;

				if (isBarrenLand(x, y))
					landGrid[x][y] = new AtomicInteger(0);
				else
					landGrid[x][y] = new AtomicInteger(1);

				// Find all adjacent tiles and add them to the stack for checking.
				tileQueue.add(new Point(x, y - 1)); // down
				tileQueue.add(new Point(x, y + 1)); // up
				tileQueue.add(new Point(x - 1, y)); // left
				tileQueue.add(new Point(x + 1, y)); // right
			}

		};

		List<Thread> createGridThreadList = new ArrayList<>();
		int numberOfThreads = 0;
		while (numberOfThreads < NUMBER_OF_THREADS) {
			Thread createLandGridThread = new Thread(runnable);
			createLandGridThread.start();
			createGridThreadList.add(createLandGridThread);
			numberOfThreads++;
		}

		for (Thread createLandGridThread : createGridThreadList) {
			try {
				createLandGridThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		/*
		 * for (int i = 0; i < WIDTH; i++) { for (int j = 0; j < HEIGHT; j++) { if
		 * (isBarrenLand(i, j)) { // barren tile landGrid[i][j] = new AtomicInteger(0);
		 * } else { // fertile tile landGrid[i][j] = new AtomicInteger(1); } } }
		 */
	}

	/**
	 * Check all the tiles and count the fertile lands' area. Each fertile land's
	 * area is added into the fertile land list.
	 */
	private static void countRestFertileAreaMutiThread() {
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				// Is unprocessed fertile tile
				if (landGrid[i][j].get() == 1) {
					checkRestFertileTilesMutiThread(i, j);
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
	private static void checkRestFertileTilesMutiThread(int i, int j) {
		Stack<Point> tileStack = new Stack<>();
		tileStack.push(new Point(i, j));

		List<Thread> threadList = new ArrayList<>();

		int numberOfThreads = 0;
		while (numberOfThreads < NUMBER_OF_THREADS) {
			CheckFertileAreaThread checkFertileTilesThread = new CheckFertileAreaThread(tileStack);
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

	private static void countFertileAreaMutiThread() {
		Map<Integer, MultiStartingPointThread> threadMap = new HashMap<>();
		int numberOfThreads = 0;
		while (numberOfThreads < NUMBER_OF_THREADS) {
			// 0 indicates barren tile, 1 indicate fertile tile, starts with 2, each number
			// represents the thread id of the thread who processed the tile
			int newThreadCustomId = numberOfThreads + 2;
			MultiStartingPointThread multiStartingPointThread = new MultiStartingPointThread(newThreadCustomId,
					findRandomNonBarrenLand());
			multiStartingPointThread.start();
			threadMap.put(newThreadCustomId, multiStartingPointThread);
			numberOfThreads++;
		}

		// Wait for all thread to finish their work
		for (Map.Entry<Integer, MultiStartingPointThread> multiStartingPointThread : threadMap.entrySet()) {
			try {
				multiStartingPointThread.getValue().join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		int currentArea = 0;

		// Merge all connected threads ids
		mergeConnectedThreadRecursively();

		// Now add all the connected fertile area together
		for (Set<Integer> connectedFertileAreaThreadIds : connectedThreadList) {
			for (int threadCustomId : connectedFertileAreaThreadIds) {
				currentArea += threadMap.get(threadCustomId).getCoveredArea();
			}
			fertileLandList.add(new AtomicInteger(currentArea));
			currentArea = 0;
		}
	}

	private static Point findRandomNonBarrenLand() {
		return findRandomPoint(true);
	}

	private static Point findRandomPoint() {
		return findRandomPoint(false);
	}

	private static Point findRandomPoint(boolean requireNonBarrenLand) {
		Point randomPoint = new Point();
		Random random = new Random();
		int x = random.nextInt(WIDTH);
		int y = random.nextInt(HEIGHT);
		if (requireNonBarrenLand) {
			while (landGrid[x][y].get() == 0) {
				x = random.nextInt(WIDTH);
				y = random.nextInt(HEIGHT);
			}
		}
		randomPoint.x = x;
		randomPoint.y = y;
		return randomPoint;
	}

	private static void mergeConnectedThreadRecursively() {
		boolean isKeepMerging = true;
		while (isKeepMerging) {
			isKeepMerging = mergeConnectedThreads();
		}
	}

	private static boolean mergeConnectedThreads() {

		boolean isSetMerged = false;
		boolean isSetAdded = false;
		List<Set<Integer>> mergedSetList = new ArrayList<>();
		for (Set<Integer> idSet : connectedThreadList) {
			isSetMerged = false;
			isSetAdded = false;
			if (mergedSetList.size() == 0) {
				mergedSetList.add(idSet);
				continue;
			}
			for (int id : idSet) {
				for (Set<Integer> mergedIdSet : mergedSetList) {
					if (mergedIdSet.contains(id)) {
						mergedIdSet.addAll(idSet);
						isSetMerged = true;
						break;
					}
				}
				if (isSetMerged) {
					break;
				}
			}
			if (!isSetMerged) {
				mergedSetList.add(idSet);
				isSetAdded = true;
			}
		}
		connectedThreadList = mergedSetList;
		// The merge action needs to be called recursively until there is no new Set
		// being added or merged
		// When both actions are false, set return value "isKeepMerging" to false
		return isSetMerged || isSetAdded;
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
		connectedThreadList = new ArrayList<>();
	}
}
