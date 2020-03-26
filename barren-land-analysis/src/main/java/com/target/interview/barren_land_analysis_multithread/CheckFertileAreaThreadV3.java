package com.target.interview.barren_land_analysis_multithread;

import static com.target.interview.barren_land_analysis_multithread.BarrenLandAnalysisMultiThreadV3.HEIGHT;
import static com.target.interview.barren_land_analysis_multithread.BarrenLandAnalysisMultiThreadV3.WIDTH;
import static com.target.interview.barren_land_analysis_multithread.BarrenLandAnalysisMultiThreadV3.fertileArea;
import static com.target.interview.barren_land_analysis_multithread.BarrenLandAnalysisMultiThreadV3.landGrid;

import java.awt.Point;
import java.util.Stack;

public class CheckFertileAreaThreadV3 extends Thread {

	private Stack<Point> tileStack;

	public CheckFertileAreaThreadV3(Stack<Point> tileStack) {
		super();
		this.tileStack = tileStack;
	}

	@Override
	public void run() {
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
					// If the point is out of the grid's bound or it is a barren tile or visited.
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
	}
}
