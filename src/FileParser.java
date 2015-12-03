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
    private Map<Course, List<Course>> pairs;
    private Map<Course, Slot> partialAssignments;

    private List<String> validCourseMWFTimes;
    private List<String> validCourseTuThTimes;
    private List<String> validLabMTuWThTimes;
    private List<String> validLabFTimes;


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
        generateCourseAndLabSlotTimes();
    }

    public boolean setupData(String filename) { //This will get changed to return all our data set up
        String currentData;
        InputStreamReader fileReader;
        int state = 0;
        Pair<String, String> slotID;
        Slot slot;
        Preference preference = null;
        Pair<Course, Course> twoCoursePair;
        Pair<Course, Pair<String, String>> coursePair;
        Course properKey;
        Course properValue;
        boolean noErrors = true;

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
                        if (!checkCourseSlot(slot)) {
                            System.out.println("A Course slot had an invalid Day or Time");
                            noErrors = false;
                            System.exit(1);
                        }
                        courseSlots.put(slotID, slot);
                    } else if (state == 2) {
                        slot = getNextSlot(currentData);
                        slot.setIsCourse(false);
                        slotID = new Pair<>(slot.getDay(), slot.getTime());
                        if (!checkLabSlot(slot)) {
                            System.out.println("A Lab slot had an invalid Day or Time");
                            noErrors = false;
                            System.exit(1);
                        }
                        labSlots.put(slotID, slot);
                    } else if (state == 3) {
                        Course course = getNextCourse(currentData);
                        if (course.getClassNum() == 313 && course.getDepartment().equals("CPSC")) {
                            Lab lab = new Lab("CPSC", 813, course.getLecSection(), course.getLecSection());
                            lab.setType("LEC");
                            allLabs.add(lab);
                        } else if (course.getClassNum() == 413 && course.getDepartment().equals("CPSC")) {
                            Lab lab = new Lab("CPSC", 913, course.getLecSection(), course.getLecSection());
                            lab.setType("LEC");
                            allLabs.add(lab);
                        }
                        allCourses.add(course);
                    } else if (state == 4) {
                        Lab lab = getNextLab(currentData); //add to set of all labs
                        allLabs.add(lab);
                    } else if (state == 5) {
                        twoCoursePair = getNotCompatible(currentData);
                        properKey = getProperCourseElement(twoCoursePair.getKey());
                        properValue = getProperCourseElement(twoCoursePair.getValue());
                        if(properKey == null || properValue == null){
                            System.out.println("WARNING: Not Compatible " + currentData + " Contains an invalid course, item was not added");
                        } else {
                            if (notCompatible.containsKey(properKey)) {
                                notCompatible.get(properKey).add(properValue);
                            } else {
                                List<Course> courseListOne = new ArrayList<>();
                                courseListOne.add(properValue);
                                notCompatible.put(properKey, courseListOne);
                            }
                            if (notCompatible.containsKey(properValue)) {
                                notCompatible.get(properValue).add(properKey);
                            } else {
                                List<Course> courseListTwo = new ArrayList<>();
                                courseListTwo.add(properKey);
                                notCompatible.put(properValue, courseListTwo);
                            }
                        }
                    } else if (state == 6) {
                        coursePair = getNextUnwanted(currentData);
                        slot = getCorrectSlotType(coursePair);
                        properKey = getProperCourseElement(coursePair.getKey());
                        if(slot == null || properKey == null){
                         System.out.println("WARNING: Unwanted " + currentData + " Contains and invalid slot or Course, item was not added");
                        } else {
                            if (unwanted.containsKey(properKey)) {
                                unwanted.get(properKey).add(slot);
                            } else {
                                List<Slot> slotList = new ArrayList<>();
                                slotList.add(slot);
                                unwanted.put(properKey, slotList);
                            }
                        }
                    } else if (state == 7) {
                        preference = getNextPreferences(currentData);
                        if(preference != null) {
                            properKey = getProperCourseElement(preference.getCourse());
                            if (preferences.containsKey(properKey)) {
                                preferences.get(properKey).add(preference);
                            } else {
                                List<Preference> preferenceList = new ArrayList<>();
                                preferenceList.add(preference);
                                preferences.put(properKey, preferenceList);
                            }
                        }
                    } else if (state == 8) {
                        twoCoursePair = getNotCompatible(currentData); // using getNotCompatible because it parses the same way
                        properKey = getProperCourseElement(twoCoursePair.getKey());
                        properValue = getProperCourseElement(twoCoursePair.getValue());
                        if(properKey == null || properValue == null){
                            System.out.println("WARNING: Pair " + currentData + " Contains and invalid slot or Course, item was not added");
                        } else {
                            if (pairs.containsKey(properKey)) {
                                pairs.get(properKey).add(properValue);
                            } else {
                                List<Course> courseListOne = new ArrayList<>();
                                courseListOne.add(properValue);
                                pairs.put(properKey, courseListOne);
                            }
                            if (pairs.containsKey(properValue)) {
                                pairs.get(properValue).add(properKey);
                            } else {
                                List<Course> courseListTwo = new ArrayList<>();
                                courseListTwo.add(properKey);
                                pairs.put(properValue, courseListTwo);
                            }
                        }
                    } else if (state == 9) { // partial assignment
                        coursePair = getNextUnwanted(currentData); //using getNextUnwanted because it parses the same way
                        slot = getCorrectSlotType(coursePair);
                        Course course = getProperCourseElement(coursePair.getKey());
                        if (slot == null || course == null) {
                            noErrors = false;
                            System.out.println("ERROR: Partial Assignment " + currentData + " is invalid, exiting program");
                            System.exit(1);
                        }
                        partialAssignments.put(course, slot);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return noErrors;
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

        return new Slot(day.toUpperCase(), time, Integer.parseInt(max), Integer.parseInt(min));
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

        return new Course(department.toUpperCase(), Integer.parseInt(classNum), lecSection);
    }

    private Lab getNextLab(String currentData) { //will return lab
        String department, classNum, lecSection, tutorialSection, type;
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
        type = getNextString(' ', currentData, false); // ignores TUT
        tutorialSection = getNextString(' ', currentData, true).trim();

        lecSection = (lecSection.isEmpty()) ? "404" : lecSection; //if lecSection isn't set make it 404
        Lab lab = new Lab(department.toUpperCase(), Integer.parseInt(classNum), lecSection, tutorialSection);
        lab.setType(type.toUpperCase());
        
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

        if (currentData.contains("TUT") || currentData.contains("LAB")) {
            course = getNextLab(firstHalf);
        } else {
            course = getNextCourse(firstHalf);
        }
        startIndex = splitPoint + 1;

        endIndex = currentData.indexOf(',', startIndex);
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
            if (!labSlots.containsKey(slot)) {
                System.out.println("WARNING: The Lab slot " + slot.getKey() + " " + slot.getValue() + " associated with a preference doesn't exist!");
                preference = null;
            } else {
                preference = new Preference(lab, slot, Integer.parseInt(weight));
            }
        } else {
            Course course = getNextCourse(courseSplit);
            weight = getNextString(',', currentData, true).trim();
            if (!courseSlots.containsKey(slot)) {
                System.out.println("WARNING: The Course slot " + slot.getKey() + " " + slot.getValue() + " associated with a preference doesn't exist!");
                preference = null;
            } else {
                preference = new Preference(course, slot, Integer.parseInt(weight));
            }
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
        Slot slot = null;

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

    private Course getProperCourseElement(Course course) {
        Course properCourse = null;

        if (course instanceof Lab) {
            for (Lab lab : allLabs) {
                if (course.getDepartment().equals(lab.getDepartment()) &&
                        course.getLecSection().equals(lab.getLecSection()) &&
                        course.getClassNum() == lab.getClassNum() &&
                        ((Lab) course).getLabSection().equals(lab.getLabSection())) {
                    properCourse = lab;
                    break;
                }
            }
        } else {
            for (Course course1 : allCourses) {
                if (course.getDepartment().equals(course1.getDepartment()) &&
                        course.getLecSection().equals(course1.getLecSection()) &&
                        course.getClassNum() == course1.getClassNum()) {
                    properCourse = course1;
                    break;
                }
            }
        }


        return properCourse;
    }

    boolean checkCourseSlot(Slot slot) {
        boolean valid = true;

        if (slot.getDay().equals("MO")) {
            if (!validCourseMWFTimes.contains(slot.getTime())) {
                valid = false;
            }
        } else if (slot.getDay().equals("TU")) {
            if (!validCourseTuThTimes.contains(slot.getTime())) {
                valid = false;
            }
        } else {
            valid = false;
        }
        return valid;
    }

    boolean checkLabSlot(Slot slot) {
        boolean valid = true;

        if (slot.getDay().equals("MO") || slot.getDay().equals("TU")) {
            if (!validLabMTuWThTimes.contains(slot.getTime())) {
                valid = false;
            }
        } else if (slot.getDay().equals("FR")) {
            if (!validLabFTimes.contains(slot.getTime())) {
                valid = false;
            }
        } else {
            valid = false;
        }
        return valid;
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

    private void generateCourseAndLabSlotTimes() {
        validCourseMWFTimes = new ArrayList<>();
        validCourseTuThTimes = new ArrayList<>();
        validLabMTuWThTimes = new ArrayList<>();
        validLabFTimes = new ArrayList<>();

        validCourseMWFTimes.add("8:00");
        validCourseMWFTimes.add("9:00");
        validCourseMWFTimes.add("10:00");
        validCourseMWFTimes.add("11:00");
        validCourseMWFTimes.add("12:00");
        validCourseMWFTimes.add("13:00");
        validCourseMWFTimes.add("14:00");
        validCourseMWFTimes.add("15:00");
        validCourseMWFTimes.add("16:00");
        validCourseMWFTimes.add("17:00");
        validCourseMWFTimes.add("18:00");
        validCourseMWFTimes.add("19:00");
        validCourseMWFTimes.add("20:00");
        validCourseMWFTimes.add("21:00");

        validCourseTuThTimes.add("8:00");
        validCourseTuThTimes.add("9:30");
        validCourseTuThTimes.add("11:00");
        validCourseTuThTimes.add("12:30");
        validCourseTuThTimes.add("14:00");
        validCourseTuThTimes.add("15:30");
        validCourseTuThTimes.add("17:00");
        validCourseTuThTimes.add("18:30");

        validLabMTuWThTimes.add("8:00");
        validLabMTuWThTimes.add("9:00");
        validLabMTuWThTimes.add("10:00");
        validLabMTuWThTimes.add("11:00");
        validLabMTuWThTimes.add("12:00");
        validLabMTuWThTimes.add("13:00");
        validLabMTuWThTimes.add("14:00");
        validLabMTuWThTimes.add("15:00");
        validLabMTuWThTimes.add("16:00");
        validLabMTuWThTimes.add("17:00");
        validLabMTuWThTimes.add("18:00");
        validLabMTuWThTimes.add("19:00");
        validLabMTuWThTimes.add("20:00");
        validLabMTuWThTimes.add("21:00");

        validLabFTimes.add("8:00");
        validLabFTimes.add("10:00");
        validLabFTimes.add("12:00");
        validLabFTimes.add("14:00");
        validLabFTimes.add("16:00");
        validLabFTimes.add("18:00");
    }
}