# Performance Discussion
This file discusses what did I try to make this program performance better, what are the outcomes and challenges.
## Multi-Thread
Since the fertile areas are hard to be calculated, I decided to count them. The time complexity O(mn) shows that this method is efficient enough.  
To make it faster, multi-thread is the easiest thing I can think of.
### Share the stack `BarrenLandAnalysisMultiThreadV1.java`
This is the first thought, try to add multi-thread to the DFS algorithm  
So what I did was try to use multi-thread to share the same Stack, they all can push and pop from it.  
This is the first version, the only change here is the `checkFertileTiles` method:  
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
```
#### Challenges
This was quite easy to write, but getting errors everywhere  
* `EmptyStackException` This was because of the race conditions on the stack. After the `if` statement checks if the stack is empty, before the `pop()`, some other threads already popped the last element in the stack.  
Need to `synchronize` the stack for checking and popping.
```java
	synchronized (tileStack) {
		if (!tileStack.isEmpty()) {
			currentTile = tileStack.pop();
		}
	}
```
#### I kept getting inconsistent results - more race condition issues
All these trivial race conditions are not easy to find, these took me a lot of time.
* Same as the stack, checking the `landGrid[x][y]` value and updating it needs to be `synchronized`
```java
	synchronized (landGrid[x][y]) {
		// If the point is a barren tile or visited.
		if (landGrid[x][y].get() == 0)
			continue;

		// Mark the fertile tile as visited by changing it to a barren tile.
		landGrid[x][y].set(0);
	}
```
* But it did not end there. `landGrid[x][y] = 0;` Updating the `landGrid[x][y]` to barren tile, this operation is not atomic, need to use `AtomicInteger` instead of `int`.  
* Race conditions are still happening. So I also updated the `fertileArea` to use `AtomicInteger`  
* I try to verify if the mult-thread is working by printing some values to the console. When I have 2 threads, it shows the second thread immediately dies after it starts.  
This was due to the second thread started too fast, the first thread just picked up the only Point in the stack. The stack went empty, then the second thread checks the empty stack and dies.  
But this is really strange, because if the second thread truelly dead right after starts, where did the race condition came from? 
### Start threads at random points in the grid `BarrenLandAnalysisMultiThreadV2.java`



