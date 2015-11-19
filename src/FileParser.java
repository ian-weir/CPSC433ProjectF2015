import javafx.util.Pair;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class FileParser {
    private int startIndex = 0;
    private int endIndex = 0;
    private List<Course> allCourses;
    private List<Lab> allLabs;
    private Map<Pair<String, String>, Slot> courseSlots; //use Day/Time Pair to get the slot
    private Map<Pair<String, String>, Slot> labSlots; //use Day/Time Pair to get the slot
    private Map<Course, List<Course>> notCompatible;
    private Map<Course, List<Slot>> unwanted;
    private Map<Course, List<Preference>> preferences;
    private Map<Course, List<Course>> pairs; //change to list
    private Map<Course, Slot> partialAssignments;


    public FileParser() {
        allCourses = new ArrayList<>();
        allLabs = new ArrayList<>();
        courseSlots = new HashMap<>();
        labSlots = new HashMap<>();
        notCompatible = new HashMap<>();
        unwanted = new HashMap<>();
        preferences = new HashMap<>();
        pairs = new HashMap<>();
        partialAssignments = new HashMap<>();
    }

    public void setupData(String filename) { //This will get changed to return all our data set up
        String currentData;
        InputStreamReader fileReader;
        int state = 0;
        Pair<String, String> slotID;
        Slot slot;
        Preference preference;
        Pair<Course, Course> twoCoursePair;
        Pair<Course, Pair<String, String>> coursePair;

        try {
            InputStream file = new FileInputStream(filename);
            fileReader = new InputStreamReader(file, Charset.defaultCharset());
            BufferedReader lineReader = new BufferedReader(fileReader);
            while ((currentData = lineReader.readLine()) != null) {
                if (!currentData.isEmpty()) {
                    currentData = currentData.trim();
                    currentData = removeExtraSpaces(currentData);
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
                        slot = getNextSlot(currentData);
                        slot.setIsCourse(true);
                        slotID = new Pair<>(slot.getDay(), slot.getTime());
                        courseSlots.put(slotID, slot);
                    } else if (state == 2) {
                        slot = getNextSlot(currentData);
                        slot.setIsCourse(false);
                        slotID = new Pair<>(slot.getDay(), slot.getTime());
                        labSlots.put(slotID, slot);
                    } else if (state == 3) {
                        Course course = getNextCourse(currentData);
                        allCourses.add(course);
                    } else if (state == 4) {
                        Lab lab = getNextLab(currentData); //add to set of all labs
                        allLabs.add(lab);
                    } else if (state == 5) {
                        List<Course> courseList = new ArrayList<>();
                        twoCoursePair = getNotCompatible(currentData);
                        if (notCompatible.containsKey(twoCoursePair.getKey())) {
                            notCompatible.get(twoCoursePair.getKey()).add(twoCoursePair.getValue());
                        } else {
                            courseList.add(twoCoursePair.getValue());
                            notCompatible.put(twoCoursePair.getKey(), courseList);
                        }
                        if (notCompatible.containsKey(twoCoursePair.getValue())) {
                            notCompatible.get(twoCoursePair.getValue()).add(twoCoursePair.getKey());
                        } else {
                            courseList.add(twoCoursePair.getKey());
                            notCompatible.put(twoCoursePair.getValue(), courseList);
                        }
                    } else if (state == 6) {
                        coursePair = getNextUnwanted(currentData);
                        slot = getCorrectSlotType(coursePair);
                        if (unwanted.containsKey(slot)) {
                            unwanted.get(coursePair.getKey()).add(slot);
                        } else {
                            List<Slot> slotList = new ArrayList<>();
                            slotList.add(slot);
                            unwanted.put(coursePair.getKey(), slotList);
                        }
                    } else if (state == 7) {
                        preference = getNextPreferences(currentData);

                        if (preferences.containsKey(preference.getCourse())) {
                            preferences.get(preference.getCourse()).add(preference);
                        } else {
                            List<Preference> preferenceList = new ArrayList<>();
                            preferenceList.add(preference);
                            preferences.put(preference.getCourse(), preferenceList);
                        }
                    } else if (state == 8) {
                        twoCoursePair = getNotCompatible(currentData); // using getNotCompatible because it parses the same way
                        List<Course> courseList = new ArrayList<>();
                        if (pairs.containsKey(twoCoursePair.getKey())) {
                            pairs.get(twoCoursePair.getKey()).add(twoCoursePair.getValue());
                        } else {
                            courseList.add(twoCoursePair.getValue());
                            pairs.put(twoCoursePair.getKey(), courseList);
                        }
                        if (pairs.containsKey(twoCoursePair.getValue())) {
                            pairs.get(twoCoursePair.getValue()).add(twoCoursePair.getKey());
                        } else {
                            courseList.add(twoCoursePair.getKey());
                            pairs.put(twoCoursePair.getValue(), courseList);
                        }
                    } else if (state == 9) {
                        coursePair = getNextUnwanted(currentData); //using getNextUnwanted because it parses the same way
                        slot = getCorrectSlotType(coursePair);
                        partialAssignments.put(coursePair.getKey(), slot);
                    }
                }
            }
//            //Printing data is just for testing, this can be removed
//            for (Pair<String, String> slotIDPair : courseSlots.keySet()) {
//                Slot slot1 = courseSlots.get(slotIDPair);
//                System.out.println(slot1.getDay() + " " + slot1.getTime() + " " + slot1.getMaxCapcity() + " " + slot1.getMinCapacity());
//            }
//            System.out.println();
//            for (Pair<String, String> slotIDPair : labSlots.keySet()) {
//                Slot slot1 = labSlots.get(slotIDPair);
//                System.out.println(slot1.getDay() + " " + slot1.getTime() + " " + slot1.getMaxCapcity() + " " + slot1.getMinCapacity());
//            }
//            System.out.println();
//            for (Course course : allCourses) {
//                System.out.println(course.getDepartment() + " " + course.getClassNum() + " " + course.getLecSection());
//            }
//            System.out.println();
//            for (Course course : notCompatible.keySet()) {
//                List<Course> courses = notCompatible.get(course);
//                for (Course course2 : courses) {
//                    System.out.print(course.getDepartment() + " " + course.getClassNum() + " " + course.getLecSection());
//                    if(course instanceof Lab){
//                        System.out.print(" " + ((Lab) course).getLabSection());
//                    }
//                    System.out.print(" " + course2.getDepartment() + " " + course2.getClassNum() + " " + course2.getLecSection());
//                    if(course2 instanceof Lab){
//                        System.out.print(" " + ((Lab) course2).getLabSection());
//                    }
//                    System.out.println();
//                }
//            }
//            System.out.println();
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

        return new Slot(day, time, Integer.parseInt(max), Integer.parseInt(min));
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

        return new Course(department, Integer.parseInt(classNum), lecSection);
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

        lecSection = (lecSection.isEmpty()) ? "404" : lecSection; //if lecSection isn't set make it 404
        return new Lab(department, Integer.parseInt(classNum), lecSection, tutorialSection);
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

        if (firstHalf.contains("TUT") || firstHalf.contains("LAB")) {
            firstLab = getNextLab(firstHalf);
        } else {
            firstCourse = getNextCourse(firstHalf);
        }
        if (secondHalf.contains("TUT") || secondHalf.contains("LAB")) {
            secondLab = getNextLab(secondHalf);
        } else {
            secondCourse = getNextCourse(secondHalf);
        }
        if (firstCourse == null) {
            if (secondCourse == null) {
                pair = new Pair<>(firstLab, secondLab);
            } else {
                pair = new Pair<>(firstLab, secondCourse);
            }
        } else {
            if (secondCourse == null) {
                pair = new Pair<>(firstCourse, secondLab);
            } else {
                pair = new Pair<>(firstCourse, secondCourse);
            }
        }
        return pair;
    }

    private Pair<Course, Pair<String, String>> getNextUnwanted(String currentData) {
        String firstHalf, day, time;
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

    private Preference getNextPreferences(String currentData) {
        String day, time, weight, courseSplit;
        int splitPoint;
        startIndex = 0;
        endIndex = 0;
        Pair<String, String> slot;
        Preference preference;

        endIndex = currentData.indexOf(',');
        day = currentData.substring(startIndex, endIndex).trim();
        time = getNextString(',', currentData, false).trim();
        splitPoint = currentData.indexOf(',', endIndex + 1);
        courseSplit = currentData.substring(endIndex + 1, splitPoint).trim();

        slot = new Pair<>(day, time);
        if (courseSplit.contains("TUT") || courseSplit.contains("LAB")) {
            Lab lab = getNextLab(courseSplit);
            weight = getNextString(',', currentData, true).trim();
            preference = new Preference(lab, slot, Integer.parseInt(weight));
        } else {
            Course course = getNextCourse(courseSplit);
            weight = getNextString(',', currentData, true).trim();
            preference = new Preference(course, slot, Integer.parseInt(weight));
        }

        return preference;
    }

    private String getNextString(char seperator, String currentData, boolean isLastString) {
        startIndex = currentData.indexOf(seperator, endIndex);
        endIndex = currentData.indexOf(seperator, startIndex + 1);
        if (isLastString) {
            endIndex = currentData.length();
        }
        return currentData.substring(startIndex + 1, endIndex);
    }

    private Slot getCorrectSlotType(Pair<Course, Pair<String, String>> courseTimePair) {
        Slot slot;

        if (courseTimePair.getKey() instanceof Lab) {
            slot = labSlots.get(courseTimePair.getValue());
        } else {
            slot = courseSlots.get(courseTimePair.getValue());
        }
        return slot;
    }

    private String removeExtraSpaces(String currentData) {
        StringBuilder stringBuilder = new StringBuilder(currentData);

        for (int i = currentData.length() - 1; i >= 0; i--) {
            if (currentData.charAt(i) == ' ' && currentData.charAt(i - 1) == ' ') {
                stringBuilder.deleteCharAt(i);
            }
        }

        return stringBuilder.toString();
    }

    public List<Course> getAllCourses() {
        return allCourses;
    }

    public List<Lab> getAllLabs() {
        return allLabs;
    }

    public Map<Pair<String, String>, Slot> getCourseSlots() {
        return courseSlots;
    }

    public Map<Pair<String, String>, Slot> getLabSlots() {
        return labSlots;
    }

    public Map<Course, List<Course>> getNotCompatible() {
        return notCompatible;
    }

    public Map<Course, List<Slot>> getUnwanted() {
        return unwanted;
    }

    public Map<Course, List<Preference>> getPreferences() {
        return preferences;
    }

    public Map<Course, List<Course>> getPairs() {
        return pairs;
    }

    public Map<Course, Slot> getPartialAssignments() {
        return partialAssignments;
    }

}