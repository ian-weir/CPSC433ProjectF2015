import java.util.List;
import java.util.Map;

public class Eval {
	
    public int minFilled(List<Slot> schedule, int penCourseMin, int penLabMin) {
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
                    if(schedule.get(i).getCourse() instanceof Lab){
                        penalty += penLabMin;
                    } else {
                        penalty += penCourseMin;
                    }
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
            course = getProperPreferenceKey(preferenceMap, course);
            if (course != null) {
                preferenceList = preferenceMap.get(course);
                currentPenalty = 0;
                for (Preference preference : preferenceList) {
                    if (course instanceof Lab) {
                        preferenceSlot = labSlots.get(preference.getSlotId());
                        if (!slot.sameSlot(preferenceSlot)) {
                            currentPenalty += preference.getWeight();
                        }
                    } else {
                        preferenceSlot = courseSlots.get(preference.getSlotId());
                        if (!slot.sameSlot(preferenceSlot)) {
                            currentPenalty += preference.getWeight();
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
        
        for(Slot slot : schedule){
        	course = getProperPairKey(slot.getCourse(), pairs);
        	List<Course> courseList = pairs.get(course);
			if(courseList != null){
				for(Slot slot2 : schedule){
					Course course2 = slot2.getCourse();
					if(!slot.sameSlot(slot2)){
						for(Course c : courseList){
							if(c.isSame(course2) && course2.isSame(c)){
								penalty++;
							}
						}
					}
				}
			}
        }

        return penalty/2;
    }

    public int secDiff(List<Slot> schedule) {
        int penalty = 0;
        int compareIndex;
        int slotIndex = 0;
        Course compareCourse;

        for (Slot slot : schedule) {
            Course slotCourse = slot.getCourse();
            for (Slot compareSlot : schedule) {
                if (slot.sameSlot(compareSlot) && slot.getCourse().getDepartment().equals("CPSC")) {
                    compareCourse = compareSlot.getCourse();
                    if (!(slotCourse instanceof Lab) && !(compareCourse instanceof Lab)) {
                        if (slotCourse.getClassNum() == compareCourse.getClassNum() && !slotCourse.getLecSection().equals(compareCourse.getLecSection())) {
                            penalty++;
                        }
                    }
                }
            }
        }
        return penalty / 2;
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

    private Course getProperPreferenceKey(Map<Course, List<Preference>> preferenceMap, Course sameCourse){
        Course properCourse = null;

        for(Course course : preferenceMap.keySet()){
            if((!(sameCourse instanceof Lab) && !(course instanceof Lab)) && sameCourse.isSame(course)){
                properCourse = course;
                break;
            } else if((sameCourse instanceof Lab) && (course instanceof Lab) && ((Lab) course).sameLab((Lab) sameCourse)){
                properCourse = course;
                break;
            }
        }
        return properCourse;
    }

    private Course getProperPairKey(Course key, Map<Course, List<Course>> pairs){
        Course properCourseKey = null;

        for(Course course : pairs.keySet()){
            if(key.isSame(course)){
                properCourseKey = course;
            }
        }

        return  properCourseKey;
    }

    private boolean isInPairList(Course key, List<Course> coursePairs){
        boolean isInList = false;

        for (Course course : coursePairs){
            if(course.isSame(key)){
                isInList = true;
            }
        }

        return isInList;
    }

}