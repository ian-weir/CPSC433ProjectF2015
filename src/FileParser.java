import javafx.util.Pair;

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
                    } else if (currentData.contains("Partial assignments:")) {
                        state = 9;
                    } else if (state == 1) { //Reading class slots
                        Slot slot = getNextSlot(currentData);
                        slot.setIsCourse(true);
                        //add to list of all slots
                    } else if (state == 2) {
                        Slot slot = getNextSlot(currentData); //will assign to a slot superclass
                        slot.setIsCourse(false);
                        //add to list of all slots
                    } else if (state == 3) {
                        Course course = getNextCourse(currentData); //add to set of all courses
                        //add to set of all courses
                    } else if (state == 4) {
                        Lab lab = getNextLab(currentData); //add to set of all labs
                    } else if (state == 5) {
                        Pair pair = getNotCompatible(currentData);
                        //add to set of not compatibles
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

    private Slot getNextSlot(String currentData) { //this will get changed to return a slot
        String day, time, max, min;
        startIndex = 0;
        endIndex = 0;

        endIndex = currentData.indexOf(',');
        day = currentData.substring(startIndex, endIndex).trim();
        time = getNextString(',', currentData, false).trim();
        max = getNextString(',', currentData, false).trim();
        min = getNextString(',', currentData, true).trim();

        Slot slot = new Slot(day, time, Integer.parseInt(max), Integer.parseInt(min));

        return slot;
    }

    private Course getNextCourse(String currentData) { //will return course
        String department, classNum, lecSection;
        startIndex = 0;
        endIndex = 0;

        endIndex = currentData.indexOf(' ');
        department = currentData.substring(startIndex, endIndex).trim();

        classNum = getNextString(' ', currentData, false).trim();
        getNextString(' ', currentData, false); // ignores LEC
        lecSection = getNextString(' ', currentData, true).trim();

        Course course = new Course(department, Integer.parseInt(classNum), lecSection);
        return course;
    }

    private Lab getNextLab(String currentData) { //will return lab
        String department, classNum, lecSection, tutorialSection;
        startIndex = 0;
        endIndex = 0;
        lecSection = "";

        endIndex = currentData.indexOf(' ');
        department = currentData.substring(startIndex, endIndex).trim();

        classNum = getNextString(' ', currentData, false).trim();
        if (currentData.contains("LEC")) {
            getNextString(' ', currentData, false); // ignores LEC
            lecSection = getNextString(' ', currentData, false).trim();
        }
        getNextString(' ', currentData, false); // ignores TUT
        tutorialSection = getNextString(' ', currentData, true).trim();

        lecSection = (lecSection.isEmpty()) ? "01" : lecSection; //if lecSection isn't set make it 01
        Lab lab = new Lab(department, Integer.parseInt(classNum), lecSection, tutorialSection);
        return lab;
    }

    private Pair<Course, Course> getNotCompatible(String currentData) {
        String firstHalf, secondHalf;
        int splitPoint;
        Lab firstLab = null;
        Lab secondLab = null;
        Course firstCourse = null;
        Course secondCourse = null;
        Pair<Course, Course> pair;

        splitPoint = currentData.indexOf(',');
        firstHalf = currentData.substring(0, splitPoint).trim();
        secondHalf = currentData.substring(splitPoint + 2, currentData.length()).trim();

        if (firstHalf.contains("TUT")) {
            firstLab = getNextLab(firstHalf);
        } else {
            firstCourse = getNextCourse(firstHalf);
        }
        if (secondHalf.contains("TUT")) {
            secondLab = getNextLab(secondHalf);
        } else {
            secondCourse = getNextCourse(secondHalf);
        }
        if (firstCourse == null){
            if(secondCourse == null){
                pair = new Pair<>(firstLab, secondLab);
            } else {
                pair = new Pair<>(firstLab, secondCourse);
            }
        } else {
            if (secondCourse == null){
                pair = new Pair<>(firstCourse, secondLab);
            } else {
                pair = new Pair<>(firstCourse, secondCourse);
            }
        }
        return pair;
    }

    private Pair<Course, Pair<String, String>> getNextUnwanted(String currentData) {
        String firstHalf, secondHalf, day, time;
        int splitPoint;
        Course course;
        Pair<Course, Pair<String, String>> courseDayTimePair;
        Pair<String, String> dayTimePair;

        splitPoint = currentData.indexOf(',');
        firstHalf = currentData.substring(0, splitPoint).trim();

        course = getNextCourse(firstHalf);

        startIndex = 0;
        endIndex = 0;

        endIndex = currentData.indexOf(',');
        day = currentData.substring(startIndex, endIndex).trim();
        time = currentData.substring(endIndex + 1, currentData.length()).trim();

        dayTimePair = new Pair<>(day, time);
        courseDayTimePair = new Pair<>(course, dayTimePair);

        return courseDayTimePair;
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