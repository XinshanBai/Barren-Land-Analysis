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
My thoughts would be second thread only dies when I added printings. This is common in multi-thread issues, if we slow down the execution, sometimes the problem just appears or goes away.
But to solve this I tried to add a `tileStack.wait(1);` on the stack lock, let the other threads push new Points into it, if the stack is still empty after this, then `break;`.
Also V1 only performaces slightly better when the thread number is very limited, for example 2 or 4 threads. When it is high, like 50, the speed slows significantly. I think this is because when there are too many threads, more of them will be waiting for the lock on the stack, and the reason it slows down also have something to do with using `AtomicInteger` instead of `int`.
### Start threads at random points in the grid `BarrenLandAnalysisMultiThreadV2.java`
V1's performance was not good at all.  
So I try to think hard and came out the second solution -> pick random points in the grid, start threads from there.  
Here comes a few challenges with this idea:  
1. How to find the connected threads
2. How to add them together
3. How to find next fertile land  
Here are the anwsers:
1. Give each thread an ID, while the threads are working, they mark the tiles with their own IDs instead of `0`. So when the other threads sees the tile has a different number, it can record that number as a connected thread. 
2. If we know who are connected, at the end we just need to add all the areas counted by the connected threads. But this was a little trick, I had to think about it eventally I came out the way of how to mark them and how to count at the end. More on this later.
3. I can't... Because it is randome, I am not checking each tile in order like the one I did before. This was the biggest challenge. So in the end, I have to kept the looping part of original program to handle the rest fertile land which is not being covered by the random starting points threads. But if you think about it, with more random starting point threads created, the chance of having a fertile land not being covered is smaller, also if the threads handles the most of them, the looping checking will only see the land is check and goes through it really fast. So this is not too bad.  
*I have also added using randome points to start multi-thread to fill the grid*
#### On how to calculate fertile lands with connected threads
I created global variable `List<Set<Integer>> connectedThreadList`. I know global variable is not good, but here the problems are becoming more and more complicated, using them can help me to focus on the actual problem.  
This list holds a list of `HashSet` which contains the connected thread IDs.  
But the problem is thread 1 can meet thread 2, thread 2 can connect to thread 3, if thread 2 handles all the points between thread 1 and 3, thread 1 will not see thread 3 as connected. The list will look like this  
`[[1, 2],[2, 3]]`  
But and the end it needs to be `[[1, 2, 3]]`  
So I created a merge method to merge them.  
Below is an actual example of the value I captured while running the program. And it requires recrusive calls to complete the merge.  
It has 20 threads, so here are 20 numbers  
`[[16, 3, 8, 10, 11, 14], [19, 20, 6, 7, 9, 12, 15], [18, 2, 4, 5, 13], [17, 21, 8, 9, 11, 12, 14, 15], [2, 19, 5, 6, 7, 13]]`
result should be  
`[2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21]`
### `BarrenLandAnalysisMultiThreadV3`
Since sharing the stack was slowing things down. I decided to try to have V3, but remove the share stack part to see if it gets faster. Here are the results with 50 threads. Strangely it actually got a bit slower... For this one I really have no idea why this could be happening.:question:


