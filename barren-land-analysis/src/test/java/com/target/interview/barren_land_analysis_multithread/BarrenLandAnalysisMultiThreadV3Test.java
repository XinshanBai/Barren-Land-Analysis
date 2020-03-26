package com.target.interview.barren_land_analysis_multithread;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BarrenLandAnalysisMultiThreadV3Test {

	/**
	 * Need to clean up all static variables before each testing otherwise the
	 * results are effected by previous tests.
	 */
	@Before
	public void cleanUp() {
		BarrenLandAnalysisMultiThreadV3.cleanUp();
	}

	/**
	 * First sample input test
	 */
	@Test
	public void testBarrenLandAnalysisMultiThread1() {
		String input = "{\"0 292 399 307\"}";
		String expected = "116800 116800";

		// Set STDIN
		System.setIn(new ByteArrayInputStream(input.getBytes()));

		// Set STDOUT
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);

		BarrenLandAnalysisMultiThreadV3.main(null);

		Assert.assertEquals(expected, baos.toString());
	}

	/**
	 * Second sample input test
	 */
	@Test
	public void testBarrenLandAnalysisMultiThread2() {
		String input = "{\"48 192 351 207\", \"48 392 351 407\", \"120 52 135 547\", \"260 52 275 547\"}";
		String expected = "22816 192608";

		// Set STDIN
		System.setIn(new ByteArrayInputStream(input.getBytes()));

		// Set STDOUT
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);

		BarrenLandAnalysisMultiThreadV3.main(null);

		Assert.assertEquals(expected, baos.toString());
	}

	/**
	 * An extend version of the second sample input. This test have all barren lands
	 * extend to the edge of the land and cut the land into 9 fertile lands
	 */
	@Test
	public void testBarrenLandAnalysisMultiThread3() {
		String input = "{\"99 0 100 599\", \"299 0 300 599\", \"0 199 399 200\", \"0 399 399 400\"}";
		String expected = "19602 19602 19701 19701 19701 19701 39204 39402 39402";

		// Set STDIN
		System.setIn(new ByteArrayInputStream(input.getBytes()));

		// Set STDOUT
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);

		BarrenLandAnalysisMultiThreadV3.main(null);

		Assert.assertEquals(expected, baos.toString());
	}

	/**
	 * This test has a barren land in the middle of the land
	 */
	@Test
	public void testBarrenLandAnalysisMultiThread4() {
		String input = "{\"99 199 299 399\"}";
		String expected = "199599";

		// Set STDIN
		System.setIn(new ByteArrayInputStream(input.getBytes()));

		// Set STDOUT
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);

		BarrenLandAnalysisMultiThreadV3.main(null);

		Assert.assertEquals(expected, baos.toString());
	}

	/**
	 * This test has a barren land which is a single dot in the land grid
	 */
	@Test
	public void testBarrenLandAnalysisMultiThread5() {
		String input = "{\"199 299 199 299\"}";
		String expected = "239999";

		// Set STDIN
		System.setIn(new ByteArrayInputStream(input.getBytes()));

		// Set STDOUT
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);

		BarrenLandAnalysisMultiThreadV3.main(null);

		Assert.assertEquals(expected, baos.toString());
	}
}
