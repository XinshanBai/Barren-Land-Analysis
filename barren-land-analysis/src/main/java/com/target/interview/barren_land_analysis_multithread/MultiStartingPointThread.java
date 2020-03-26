package com.target.interview.barren_land_analysis_multithread;

import static com.target.interview.barren_land_analysis_multithread.BarrenLandAnalysisMultiThreadV2.HEIGHT;
import static com.target.interview.barren_land_analysis_multithread.BarrenLandAnalysisMultiThreadV2.WIDTH;
import static com.target.interview.barren_land_analysis_multithread.BarrenLandAnalysisMultiThreadV2.connectedThreadList;
import static com.target.interview.barren_land_analysis_multithread.BarrenLandAnalysisMultiThreadV2.landGrid;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class MultiStartingPointThread extends Thread {

	private int customId;
	private int coveredArea;
	private Point startingPoint;

	public MultiStartingPointThread(int customId, Point startingPoint) {
		super();
		this.customId = customId;
		this.startingPoint = startingPoint;
	}

	public int getCoveredArea() {
		return coveredArea;
	}

	public void setCoveredArea(int coveredArea) {
		this.coveredArea = coveredArea;
	}

	public int getCustomId() {
		return customId;
	}

	@Override
	public void run() {
		Stack<Point> tileStack = new Stack<>();
		tileStack.push(this.startingPoint);

		while (!tileStack.isEmpty()) {
			Point currentTile = tileStack.pop();

			int x = (int) currentTile.getX();
			int y = (int) currentTile.getY();

			// If the point is out of bound
			if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT)
				continue;

			synchronized (landGrid[x][y]) {
				// If the point is a barren tile or processed by current thread
				if (landGrid[x][y].get() == 0 || landGrid[x][y].get() == customId)
					continue;

				// If the tile is processed by other thread, add the other thread's id to the
				// connected thread list
				if (landGrid[x][y].get() != 1) {
					addConnectedThreadId(landGrid[x][y].get());
				} else {
					// If the tile is a unprocessed fertile tile, mark the fertile tile as processed
					// by current thread by setting its value to the thread id
					landGrid[x][y].set(customId);

					coveredArea++;

					// Find all adjacent tiles and add them to the stack for checking.
					tileStack.push(new Point(x, y - 1)); // down
					tileStack.push(new Point(x, y + 1)); // up
					tileStack.push(new Point(x - 1, y)); // left
					tileStack.push(new Point(x + 1, y)); // right
				}
			}
		}

		addSelfId();

	}

	private void addSelfId() {
		// Add self to the connected thread list
		synchronized (connectedThreadList) {
			if (connectedThreadList.size() == 0) {
				Set<Integer> connectedThreadSet = new HashSet<>();
				connectedThreadSet.add(customId);
				connectedThreadList.add(connectedThreadSet);
			} else {
				boolean isAdded = false;
				for (Set<Integer> connectedThreadSet : connectedThreadList) {
					// If connected thread id is found, add self id(if self id is already in the
					// list, it won't add a duplicate)
					if (connectedThreadSet.contains(customId)) {
						isAdded = true;
					}
				}
				if (!isAdded) {
					Set<Integer> connectedThreadSet = new HashSet<>();
					connectedThreadSet.add(customId);
					connectedThreadList.add(connectedThreadSet);
				}
			}
		}
	}

	/**
	 * This will add self id and connected thread id into the connectedThreadList
	 * The list contains HashSet objects, each object will store all the threads' id
	 * who processed the same fertile land
	 * 
	 * @param connectedThreadCustomId the connected thread custom id
	 */
	private void addConnectedThreadId(int connectedThreadId) {
		synchronized (connectedThreadList) {
			// If the List is empty, create a new HashSet, add self id and the connected
			// thread id
			if (connectedThreadList.size() == 0) {
				Set<Integer> connectedThreadSet = new HashSet<>();
				connectedThreadSet.add(connectedThreadId);
				connectedThreadSet.add(customId);
				connectedThreadList.add(connectedThreadSet);
			} else {
				boolean isAdded = false;
				// If the list has elements, loop through it try to find self id or the
				// connected thread id
				for (Set<Integer> connectedThreadSet : connectedThreadList) {
					// If connected thread id is found, add self id(if self id is already in the
					// list, it won't add a duplicate)
					if (connectedThreadSet.contains(connectedThreadId)) {
						connectedThreadSet.add(customId);
						isAdded = true;
					}

					// If self id is found, add connected thread id (if connected thread id is
					// already in the list, it won't add a duplicate)
					if (connectedThreadSet.contains(customId)) {
						connectedThreadSet.add(connectedThreadId);
						isAdded = true;
					}
				}

				// After the loop if id is not added, create a new HashSet and add it now
				if (!isAdded) {
					Set<Integer> connectedThreadSet = new HashSet<>();
					connectedThreadSet.add(connectedThreadId);
					connectedThreadSet.add(customId);
					connectedThreadList.add(connectedThreadSet);
				}
			}
		}
	}
}
