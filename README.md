# Barren Land Analysis
This is a simple Java program to solve the following problem:

You have a farm of 400m by 600m where coordinates of the field are from (0, 0) to (399, 599). A portion of the farm is barren, and all the barren land is in the form of rectangles. Due to these rectangles of barren land, the remaining area of fertile land is in no particular shape. An area of fertile land is defined as the largest area of land that is not covered by any of the rectangles of barren land.  
Read input from STDIN. Print output to STDOUT

### Input  
You are given a set of rectangles that contain the barren land. These rectangles are defined in a string, which consists of four integers separated by single spaces, with no additional spaces in the string. The first two integers are the coordinates of the bottom left corner in the given rectangle, and the last two integers are the coordinates of the top right corner. 

### Output   
Output all the fertile land area in square meters, sorted from smallest area to greatest, separated by a space. 

Sample Input | Sample Output
------------ | -------------
{“0 292 399 307”} | 116800  116800
{“48 192 351 207”, “48 392 351 407”,<br> “120 52 135 547”, “260 52 275 547”} | 22816 192608

## Getting Started
Download and install the following software if you do not have them on your machine.

**JDK8**  
To install java, [download JDK8 installer](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html) and add/update the `JAVA_HOME` variable to JDK install folder.  
Include `path_to_JDK8/bin` directory in `PATH` variable.

**Maven**  
[Download Maven](https://maven.apache.org/download.cgi) and add `MAVEN_HOME` Environment Variables.  
Include `path_to_maven/bin` directory in `PATH` variable.  

Download or pull the code to your local machine.
## Running the tests
### Automated test
Find `test_barren_land_analysis.cmd`, double click it.  
**test_barren_land_analysis.cmd**  
This script runs maven build, then invokes the jar file for each test case specified in the `barren_land_analysis_test_cases.txt`

### Manual test
Import the project as maven project into your prefered IDE. Find `BarrenLandAnalysis.java` in package `com.target.interview.barren_land_analysis` in the `src/main/java` folder.  
Execute the main class, maually type or copy/paste the sample input into the console after program starts, press `Enter`.

### Junit test
Import the project as maven project into your prefered IDE. Find `BarrenLandAnalysis.java` in package `com.target.interview.barren_land_analysis` in the `src/test/java` folder.  
Run the Junit test class.

## Time Complexity
```java
	public static void main(String[] args) {

		String[] input = readInput();

		initBarrenLandList(input);

		createLandGrid();

		countFertileArea();

		displayFertileAreas();
	}
```
### initBarrenLandList(input);
This method converts the input coordinates into BarrenLandPosition object, make it easier to access.  
The time complexity for this method is **O(I)**, I is the number of barren land in the input data  
### createLandGrid();
This method creates a grid represents the whole land, 0 indicates barren tile, 1 indicate fertile tile, each tile is checked against all barren land I  
The time complexity for this method is **O(Imn)**  
m is the the width of the land  
n is the height of the land
### countFertileArea();
This method count the fertile area with depth first search. Each tile is visited twice, one by the DFS counter(exclude the barren tiles), one by the outter loop checks the tiles  
The time complexity for this method is approximately **O(2mn)**  

**Add all above together is O(I) + O(Imn) + O(2mn) = O(I+(I+2)mn)**  
**Ignore constant +I = O((I+2)mn)**  
**If I can not be ignored, then time complexity for this program is O(Imn)**  
**If I is always small, then it can be ignored with \*2 together, the final time complexity is O(mn)**  

## Tools
* [Eclipse](https://www.eclipse.org/)
* [Java](https://www.java.com)
* [Maven](https://maven.apache.org/)
* [Git](https://git-scm.com/)


