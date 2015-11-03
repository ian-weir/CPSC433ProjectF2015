import java.io.*;
import java.nio.charset.Charset;

public class FileParser {
    private int startIndex = 0;
    private int endIndex = 0;

    public FileParser() {
    }

    public void setupData(String filename) { //This will get changed to return all our data set up
        String currentData;
        InputStreamReader fileReader;
        int state = 0;

        try {
            InputStream file = new FileInputStream(filename);
            fileReader = new InputStreamReader(file, Charset.defaultCharset());
            BufferedReader lineReader = new BufferedReader(fileReader);
            while ((currentData = lineReader.readLine()) != null) {
                if (!currentData.isEmpty()) {
                    currentData = currentData.trim();
                    if (currentData.equals("Course slots:")) {
                        state = 1;
                    } else if (currentData.equals("Lab slots:")) {
                        state = 2;
                    } else if (currentData.equals("Courses:")) {
                        state = 3;
                    } else if (currentData.equals("Labs:")) {
                        state = 4;
                    } else if (currentData.equals("Not compatible:")) {
                        state = 5;
                    } else if (currentData.equals("Unwanted:")) {
                        state = 6;
                    } else if (currentData.equals("Preferences:")) {
                        state = 7;
                    } else if (currentData.contains("Pair:")) {
                        state = 8;
                    } else if (currentData.contains("Partial assignments:")){
                        state = 9;
                    } else if (state == 1) { //Reading class slots
                        getNextSlot(currentData); //will assign to a slot superclass
                        //will get implemented to then create a classSlot
                    } else if (state == 2) {
                        getNextSlot(currentData); //will assign to a slot superclass
                        //will get implemented to then create a labSlot
                    } else if (state == 3) {
                        getNextCourse(currentData); //add to set of all courses
                    } else if (state == 4) {
                        getNextLab(currentData); //add to set of all labs
                    } else if (state == 5) {
                        getNotCompatible(currentData);
                    } else if (state == 6) {
                        getNextUnwanted(currentData);
                    } else if (state == 7) {
                        getNextPreferences(currentData);
                    } else if (state == 8) {
                        getNotCompatible(currentData);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getNextSlot(String currentData) { //this will get changed to return a slot
        String day, time, max, min;
        startIndex = 0;
        endIndex = 0;

        endIndex = currentData.indexOf(',');
        day = currentData.substring(startIndex, endIndex).trim();
        time = getNextString(',', currentData, false).trim();
        max = getNextString(',', currentData, false).trim();
        min = getNextString(',', currentData, true).trim();
        //set up slot super class
    }

    private void getNextCourse(String currentData) { //will return course
        String department, classNum, lecSection;
        startIndex = 0;
        endIndex = 0;

        endIndex = currentData.indexOf(' ');
        department = currentData.substring(startIndex, endIndex).trim();

        classNum = getNextString(' ', currentData, false).trim();
        getNextString(' ', currentData, false); // ignores LEC
        lecSection = getNextString(' ', currentData, true).trim();
        //setup Course
    }

    private void getNextLab(String currentData) { //will return lab
        String department, classNum, lecSection, tutorialSection;
        startIndex = 0;
        endIndex = 0;

        endIndex = currentData.indexOf(' ');
        department = currentData.substring(startIndex, endIndex).trim();

        classNum = getNextString(' ', currentData, false).trim();
        if(currentData.contains("LEC")) {
            getNextString(' ', currentData, false); // ignores LEC
            lecSection = getNextString(' ', currentData, false).trim();
        }
        getNextString(' ', currentData, false); // ignores TUT
        tutorialSection = getNextString(' ', currentData, true).trim();
        //setup Lab
    }

    private void getNotCompatible(String currentData) {
        String firstHalf, secondHalf;
        int splitPoint;

        splitPoint = currentData.indexOf(',');
        firstHalf = currentData.substring(0, splitPoint).trim();
        secondHalf = currentData.substring(splitPoint + 2, currentData.length()).trim();

        if (firstHalf.contains("TUT")) {
            getNextLab(firstHalf);
        } else {
            getNextCourse(firstHalf);
        }
        if (secondHalf.contains("TUT")){
            getNextLab(secondHalf);
        } else {
            getNextCourse(secondHalf);
        }
    }

    private void getNextUnwanted(String currentData) {
        String firstHalf, secondHalf, day, time;
        int splitPoint;

        splitPoint = currentData.indexOf(',');
        firstHalf = currentData.substring(0, splitPoint).trim();

        getNextCourse(firstHalf);

        startIndex = 0;
        endIndex = 0;

        endIndex = currentData.indexOf(',');
        day = currentData.substring(startIndex, endIndex).trim();
        time = currentData.substring(endIndex + 1, currentData.length()).trim();
        //used day/time combo to get instance of slot already created
        //add to multimap
    }

    private void getNextPreferences(String currentData) {
        String day, time, weight, courseSplit;
        int splitPoint;
        startIndex = 0;
        endIndex = 0;

        endIndex = currentData.indexOf(',');
        day = currentData.substring(startIndex, endIndex).trim();
        time = getNextString(',', currentData, false).trim();
        splitPoint = currentData.indexOf(',', endIndex + 1);
        courseSplit = currentData.substring(endIndex + 1, splitPoint).trim();

        if (courseSplit.contains("TUT")) {
            getNextLab(courseSplit);
        } else {
            getNextCourse(courseSplit);
        }
        weight = getNextString(',', currentData, true).trim();
        //add to multimap
    }

    private String getNextString(char seperator, String currentData, boolean isLastString) {
        startIndex = currentData.indexOf(seperator, endIndex);
        endIndex = currentData.indexOf(seperator, startIndex + 1);
        if (isLastString) {
            endIndex = currentData.length();
        }
        return currentData.substring(startIndex + 1, endIndex);
    }
}