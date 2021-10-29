//********************************************************************
//
// Jaron Ritter
// Operating Systems
// Programming Project #5: Simulation of Page Replacement Strategies
// November 7, 2021
// Instructor: Dr. Siming Liu
//
//********************************************************************

//********************************************************************
//
// isIn Function
//
// This function takes in a String and checks to see if a given array
// contains the string
// 
// Return Value 
// ------------
// boolean
//
// Value Parameters
// ----------------
// target   String          The string to be searched for
// arr	    String Array	The array to be searched thru
//
//********************************************************************

//********************************************************************
//
// calcFIFO Function
//
// this function calculates the number of page faults that would occur
// using the First In First Out algorithm for paging
// 
// Return Value 
// ------------
// null
//
// Value Parameters
// ----------------
// numFrames        integer         the number of available frames
// referenceString	String Array	list of processes to be allocated
//
//********************************************************************

//********************************************************************
//
// calcLRU Function
//
// this function calculates the number of page faults that would occur
// using the Last Recently Used algorithm for paging
// 
// Return Value 
// ------------
// null
//
// Value Parameters
// ----------------
// numFrames        integer         the number of available frames
// referenceString	String Array	list of processes to be allocated
//
//********************************************************************

//********************************************************************
//
// calcOPT Function
//
// this function calculates the number of page faults that would occur
// using the Optimal algorithm for paging
// 
// Return Value 
// ------------
// null
//
// Value Parameters
// ----------------
// numFrames        integer         the number of available frames
// referenceString	String Array	list of processes to be allocated
//
//********************************************************************

//********************************************************************
//
// calcRAND Function
//
// this function calculates the number of page faults that would occur
// using random selection as the algorithm for paging
// 
// Return Value 
// ------------
// null
//
// Value Parameters
// ----------------
// numFrames        integer         the number of available frames
// referenceString	String Array	list of processes to be allocated
//
//********************************************************************

import java.util.*;
import java.time.*;

public class simpager {
    static boolean isIn(String target, String[] arr) {
        // this is a helper function to determine array containment
        boolean wasFound = false;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(target)) {
                wasFound = true;
            }
        }
        return wasFound;
    }

    static void calcFIFO(String[] referenceString, int numFrames) {
        int counter = 0, insertLocation = 0, faults = 0;

        String[] frameArr = new String[numFrames];

        while (counter < referenceString.length) {
            if (frameArr[insertLocation] == null || !isIn(referenceString[counter], frameArr)) {
                // This handles initalization of the page frames
                // This also keeps track of the insert location and increments it in case of fault
                frameArr[insertLocation] = referenceString[counter];
                insertLocation = (insertLocation + 1) % numFrames;
                faults++;
            }
            counter++;
        }

        System.out.printf("FIFO: %d\n", faults);

    }

    static void calcLRU(String[] referenceString, int numFrames) {
        // initalize a clock
        Clock clock = Clock.systemDefaultZone();
        // create an empty array of time instances
        Instant[] times = new Instant[numFrames];
        // create an arr for the frames
        String[] frameArr = new String[numFrames];
        // initalize the counters
        int insertLocation = 0, counter = 0, faults = 0;

        while (counter < referenceString.length) {
            if (frameArr[insertLocation] == null) {
                // fill the inital frame array
                frameArr[insertLocation] = referenceString[counter];
                // insert the time the frame was created into an array at the corresponding index
                times[insertLocation] = clock.instant();
                insertLocation = (insertLocation + 1) % numFrames;
                faults++;
            } else {
                // if the current frame is not in the loaded frames
                if (!isIn(referenceString[counter], frameArr)) {
                    Instant leastTime = Instant.MAX;
                    for (int i = 0; i < times.length; i++) {
                        if (times[i].compareTo(leastTime) < 0) {
                            insertLocation = i;
                        }
                    }
                    // insert the value into the frame array and set its time
                    frameArr[insertLocation] = referenceString[counter];
                    times[insertLocation] = clock.instant();
                    faults++;
                }
            }
            counter++;
        }

        System.out.printf("LRU: %d\n", faults);

    }

    static void calcOPT(String[] referenceString, int numFrames) {
        String[] frameArr = new String[numFrames];
        // initalize the counters
        int insertLocation = 0, counter = 0, faults = 0;

        while (counter < referenceString.length) {
            if (frameArr[insertLocation] == null) {
                // fill the inital frame array
                frameArr[insertLocation] = referenceString[counter];
                insertLocation = (insertLocation + 1) % numFrames;
                faults++;
            } else {
                // if the current frame is not in the loaded frames
                if (!isIn(referenceString[counter], frameArr)) {
                    int indexOflastToBeUsed = counter;
                    String valueOfLastToBeUsed = new String();
                    // loop through the frame arr
                    for (int i = 0; i < frameArr.length; i++) {
                        boolean found = false;
                        // for every value in the frame array, 
                        // find the furthest first instance of that value in 
                        // the remainder of the reference string
                        for (int j = counter; j < referenceString.length; j++) {
                            if (frameArr[i].equals(referenceString[j])) {
                                if (indexOflastToBeUsed < j) {
                                    indexOflastToBeUsed = j;
                                    valueOfLastToBeUsed = referenceString[j];
                                }
                                found = true;
                                break;
                            }
                        }

                        // if the value was not found in the remainder of the 
                        // reference string then it is the furthest from use
                        if (!found) {
                            valueOfLastToBeUsed = frameArr[i];
                            indexOflastToBeUsed = Integer.MAX_VALUE;
                        }
                    }

                    // find the frame who is the furthest and replace it
                    for (int k = 0; k < frameArr.length; k++) {
                        if (frameArr[k].equals(valueOfLastToBeUsed)) {
                            frameArr[k] = referenceString[counter];
                            faults++;
                        }
                    }

                }
            }
            counter++;
        }

        System.out.printf("OPT: %d\n", faults);
    }

    static void calcRAND(String[] referenceString, int numFrames) {
        Random randomInt = new Random();
        String[] frameArr = new String[numFrames];
        // initalize the counters
        int insertLocation = 0, counter = 0, faults = 0;

        while (counter < referenceString.length) {
            if (frameArr[insertLocation] == null) {
                // fill the inital frame array
                frameArr[insertLocation] = referenceString[counter];
                insertLocation = (insertLocation + 1) % numFrames;
                faults++;
            } else {
                // select a random location for insertion 
                insertLocation = randomInt.nextInt(numFrames - 1);
                if (!isIn(referenceString[counter], frameArr)) {
                    frameArr[insertLocation] = referenceString[counter];
                    faults++;
                }
            }
            counter++;
        }

        System.out.printf("RAND: %d", faults);

    }

    public static void main(String[] args) {
        // scanner to read from standard in
        Scanner sc = new Scanner(System.in);

        // read the reference string and turn it into an array
        String referenceString = sc.nextLine();
        String[] referenceStringArr = referenceString.split(" ");

        // read the number of frames then go to next line
        int numFrames = sc.nextInt();
        sc.nextLine();

        // create list for algorithm types to go. This while loop terminates if it reads
        // an empty line or recieves 4 inputs
        List<String> replacementAlgs = new ArrayList<>();
        String s = new String();
        int i = 0;
        while (true) {
            s = sc.nextLine();
            if (s.equals("") || i >= 4) {
                break;
            } else {
                replacementAlgs.add(s);
            }
            i++;
        }

        // close scanner
        sc.close();
        System.out.println("Page Reference String:");
        System.out.println(referenceString);
        System.out.printf("Number of Frames: %d\n", numFrames);

        for (String j : replacementAlgs) {
            switch (j) {
                case "FIFO": {
                    calcFIFO(referenceStringArr, numFrames);
                    break;
                }
                case "LRU": {
                    calcLRU(referenceStringArr, numFrames);
                    break;
                }
                case "OPT": {
                    calcOPT(referenceStringArr, numFrames);
                    break;
                }
                case "RAND": {
                    calcRAND(referenceStringArr, numFrames);
                    break;
                }
                default: {
                    System.out.printf("%s is not supported\n", j);
                }
            }
        }
    }
}