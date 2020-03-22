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

	private static final int WIDTH = 600;
	private static final int HEIGHT = 400;

	private static List<BarrenLandPosition> barrenLandList = new ArrayList<>();
	private static int[][] landGrid = new int[WIDTH][HEIGHT];
	private static List<Integer> fertileLandList = new ArrayList<>();
	private static int fertileArea = 0;

	public static void main(String[] args) {

		String[] input = readInput();

		initBarrenLandList(input);

		createLandGrid();

		countFertileArea();

		displayFertileAreas();
	}

	private static String[] readInput() {
		try (Scanner sc = new Scanner(System.in)) {
			String stdinput = sc.nextLine();
			return stdinput.replaceAll("^\\{|\"|\\}$", "").split(", ");
		}
	}

	private static void initBarrenLandList(String[] input) {
		if (input != null && input.length != 0) {
			try {
				for (String points : input) {
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
		}
	}

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

	private static void countFertileArea() {
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				if (landGrid[i][j] == 1) {
					checkFertileTiles(i, j);
					fertileLandList.add(fertileArea);
					fertileArea = 0;
				}
			}
		}
	}

	private static void displayFertileAreas() {
		Collections.sort(fertileLandList);
		StringBuilder results = new StringBuilder();
		for (int fertileArea : fertileLandList) {
			results.append(fertileArea);
			results.append(" ");
		}
		System.out.print(results.toString().trim());
	}

	private static boolean isBarrenLand(int i, int j) {
		for (BarrenLandPosition barrenLandPosition : barrenLandList) {
			if (i >= barrenLandPosition.getLeft() && i <= barrenLandPosition.getRight()
					&& j >= barrenLandPosition.getBottom() && j <= barrenLandPosition.getTop()) {
				return true;
			}
		}
		return false;
	}

	private static void checkFertileTiles(int i, int j) {
		Stack<Point> tileStack = new Stack<>();
		tileStack.push(new Point(i, j));

		while (!tileStack.isEmpty()) {
			Point currentTile = tileStack.pop();
			int x = (int) currentTile.getX();
			int y = (int) currentTile.getY();

			if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT || landGrid[x][y] == 0)
				continue;

			landGrid[x][y] = 0;
			fertileArea++;
			tileStack.push(new Point(x, y - 1));
			tileStack.push(new Point(x, y + 1));
			tileStack.push(new Point(x - 1, y));
			tileStack.push(new Point(x + 1, y));
		}
	}
	
	public static void cleanUp() {
		barrenLandList = new ArrayList<>();
		landGrid = new int[WIDTH][HEIGHT];
		fertileLandList = new ArrayList<>();
		fertileArea = 0;
	}
}
