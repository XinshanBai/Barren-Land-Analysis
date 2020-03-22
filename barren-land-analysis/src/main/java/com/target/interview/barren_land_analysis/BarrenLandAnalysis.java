package com.target.interview.barren_land_analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Barren Land Analysis
 *
 */
public class BarrenLandAnalysis {
	private static final int WIDTH = 600;
	private static final int HEIGHT = 400;

	// TODO change STDIN to actual user input
	private static String[] STDIN = { "0 292 399 307" };
	// private static String[] STDIN = {"48 192 351 207", "48 392 351 407", "120 52
	// 135 547", "260 52 275 547"};

	private static List<BarrenLandPosition> BarrenLandList = new ArrayList<>();
	
	private static int[][] land = new int[600][400];
	
	private static List<Integer> fertileLandList = new ArrayList<>();
	
	private static int fertileArea = 0;

	public static void main(String[] args) {
		
		initBarrenLandList();

		// Create land grid
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				if(isBarrenLand(i, j)) {
					// barren tile 
					land[i][j] = 0;
				} else {
					// fertile tile 
					land[i][j] = 1;
				}
			}
		}
		
		for(int i = 0; i < WIDTH; i++) {
			for(int j = 0; j < HEIGHT; j++) {
				if(land[i][j] == 1) {
					checkAdjacentTiles(i, j);
					fertileLandList.add(fertileArea);
					fertileArea = 0;
				}
			}
		}
		
		Collections.sort(fertileLandList);
		System.out.println(fertileLandList);
	}

	private static void initBarrenLandList() {
		if (STDIN != null && STDIN.length != 0) {
			for (String input : STDIN) {
				String[] barrenLandArray = input.split(" ");
				BarrenLandPosition barrenLandPosition = new BarrenLandPosition();
				barrenLandPosition.setBottom(Integer.valueOf(barrenLandArray[0]));
				barrenLandPosition.setLeft(Integer.valueOf(barrenLandArray[1]));
				barrenLandPosition.setTop(Integer.valueOf(barrenLandArray[2]));
				barrenLandPosition.setRight(Integer.valueOf(barrenLandArray[3]));
				BarrenLandList.add(barrenLandPosition);
			}
		} else {
			System.out.println("Invalid input");
		}
	}

	private static boolean isBarrenLand(int i, int j) {
		for (BarrenLandPosition barrenLandPosition : BarrenLandList) {
			if (i >= barrenLandPosition.getLeft() && i <= barrenLandPosition.getRight()
					&& j >= barrenLandPosition.getBottom() && j <= barrenLandPosition.getTop()) {
				return true;
			}
		}
		return false;
	}
	
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
}
