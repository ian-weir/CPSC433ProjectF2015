import javafx.util.Pair;

import java.util.List;
import java.util.Map;

public class Eval {

	
	public int evalTot(List<Slot> schedule, FileParser fileParser)
	{
		 Map<Course, List<Preference>> preferenceMap = fileParser.getPreferences();
		 Map<Pair<String, String>,Slot> courseSlots = fileParser.getCourseSlots();
		 Map<Pair<String, String>, Slot> labSlots = fileParser.getLabSlots();
		 Map<Course, List<Course>> pairs = fileParser.getPairs();
		
		return minFilled(schedule) + pref(schedule, preferenceMap, courseSlots, labSlots) + pair(schedule, pairs); //+ secDiff(schedule);
	}
	
    public int minFilled(List<Slot> schedule) {
        int penalty = 0;
        int classCount = 1;
        int currentMin = schedule.get(0).getMinCapacity();

        for (int i = 0; i < schedule.size(); i++) {
            if (i != schedule.size() - 1 && schedule.get(i).getTime().equals(schedule.get(i + 1).getTime()) && schedule.get(i).getDay().equals(schedule.get(i + 1).getDay())) {
                if (schedule.get(i).getCourse() != null) {
                    classCount++;
                }
            } else {
                if (classCount < currentMin) {
                    penalty++;
                }
                if (i != schedule.size() - 1) {
                    currentMin = schedule.get(i + 1).getMinCapacity();
                }
                classCount = 1;
            }
        }
        return penalty;
    }

    public int pref(List<Slot> schedule, Map<Course, List<Preference>> preferenceMap, Map<Pair<String, String>, Slot> courseSlots, Map<Pair<String, String>, Slot> labSlots) {
        int penalty = 0;
        int currentPenalty;
        List<Preference> preferenceList;
        Course course;
        Slot preferenceSlot;

        for (Slot slot : schedule) {
            course = slot.getCourse();
            if (preferenceMap.containsKey(course)) {
                preferenceList = preferenceMap.get(course);
                currentPenalty = 0;
                for (Preference preference : preferenceList) {
                    if (course instanceof Lab) {
                        preferenceSlot = labSlots.get(preference.getSlotId());
                        if (!slot.equals(preferenceSlot)) {
                            currentPenalty += preference.getWeight();
                        } else {
                            currentPenalty = 0;
                            break;
                        }
                    } else {
                        preferenceSlot = courseSlots.get(preference.getSlotId());
                        if (!slot.equals(preferenceSlot)) {
                            currentPenalty += preference.getWeight();
                        } else {
                            currentPenalty = 0;
                            break;
                        }
                    }
                }
                penalty += currentPenalty;
            }
        }
        return penalty;
    }

    public int pair(List<Slot> schedule, Map<Course, List<Course>> pairs) {
        int penalty = 0;
        Course course;
        int pairCount = 0;
        boolean firstSlot = true;

        for (Slot slot : schedule) {
            course = slot.getCourse();
            if (pairs.containsKey(course)) {
                List<Course> pairedCourses = pairs.get(course);
                int index = getFirstSlot(schedule, slot);
                if (index == 0) {
                    if (pairedCourses.contains(schedule.get(index).getCourse())) {
                        pairCount++;
                    }
                    index++;
                    firstSlot = false;
                }
                while ((schedule.get(index).getDay().equals(schedule.get(index - 1).getDay()) && schedule.get(index).getTime().equals(schedule.get(index - 1).getTime())) || firstSlot) {
                    firstSlot = false;
                    if (pairedCourses.contains(schedule.get(index).getCourse())) {
                        pairCount++;
                    }
                    index++;
                }
                if (pairCount < pairedCourses.size()) {
                    penalty += pairedCourses.size() - pairCount;
                }
            }
        }
        return penalty;
    }

    public int secDiff(List<Slot> schedule) {
        int penalty = 0;
        int compareIndex;
        int slotIndex = 0;
        Course compareCourse;

        for (Slot slot : schedule) {
            Course slotCourse = slot.getCourse();
            compareIndex = slotIndex + 1;
            if(slot.getTime().equals(schedule.get(compareIndex).getTime()) && slot.getCourse().equals(schedule.get(compareIndex).getCourse())) {
                compareCourse = schedule.get(compareIndex).getCourse();
                if (!(slotCourse instanceof Lab) && !(compareCourse instanceof Lab)) {
                    if (slotCourse.getClassNum() == compareCourse.getClassNum()) {
                        penalty++;
                    }
                }
            }
        }
        return penalty;
    }

    //gets the index of first slot for specific day/time
    private int getFirstSlot(List<Slot> schedule, Slot slot) {
        int index = 0;

        for (Slot iterator : schedule) {
            if (iterator.getDay().equals(slot.getDay()) && iterator.getTime().equals(slot.getTime())) {
                break;
            }
            index++;
        }

        return index;
    }
}