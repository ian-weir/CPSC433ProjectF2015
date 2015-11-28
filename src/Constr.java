import javafx.util.Pair;

import java.util.*;

public class Constr {
	private Map<Course, List<Course>> notCompatible;
	private Map<Course, Slot> partialAssignments;
	private Map<Course, List<Slot>> unwanted; // changed slot to list of slots
	private List<Course> allCourses;
	private List<Lab> allLabs;
	private List<Course> cpsc813notCompatibleList = new ArrayList<>();
	private List<Course> cpsc913notCompatibleList = new ArrayList<>();
	private boolean containsCPSC813 = false;
	private boolean containsCPSC913 = false;
	
	public Constr(FileParser fp){
		this.notCompatible = fp.getNotCompatible();
    	this.partialAssignments = fp.getPartialAssignments();
    	this.unwanted = fp.getUnwanted();
    	this.allCourses = new ArrayList<>();
    	this.allCourses.addAll(fp.getAllCourses());
    	this.allLabs = new ArrayList<>();
    	this.allLabs.addAll(fp.getAllLabs());
    	generateSpecialCourseList();
	}
	
    //returns true if assigning a slot(with class in it) to a schedule passes all the hard constraints
    public boolean constr(List<Slot> schedule, Slot slot){
    	return 	classMax(schedule,slot) && 
    			unequalAssignment(schedule,slot) && 
    			notCompatible(schedule,slot) &&
    			partialAssignment(slot) &&
    			unwanted(slot) &&
    			lec9Evening(slot) &&
    			level500(schedule, slot) &&
    			tuesday11(slot) &&
    			cpsc813(slot) &&
    			cpsc913(slot)
    			;
    }
    
	//checks if two courses or labs overlap
	private boolean overlap(Slot slot1, Slot slot2){
		int time1 = Integer.parseInt(slot1.getTime().substring(0,slot1.getTime().indexOf(":")));
		int time2 = Integer.parseInt(slot2.getTime().substring(0,slot2.getTime().indexOf(":")));
		if(slot1.getDay().equals("MO") && slot2.getDay().equals("MO")){
			return time1 == time2;
		}
		if(slot1.getDay().equals("TU") && slot2.getDay().equals("TU")){
			if(!(slot1.getCourse().getDepartment().equals("CPSC") && (slot1.getCourse().getClassNum() == 813 || slot1.getCourse().getClassNum() == 913))){
				if(!(slot2.getCourse().getDepartment().equals("CPSC") && (slot2.getCourse().getClassNum() == 813 || slot2.getCourse().getClassNum() == 913))){
					if(slot1.isCourse() == slot2.isCourse()){
						return time1 == time2;
					}
					if(slot1.isCourse() && !slot2.isCourse()){
						return time2 == time1 || time2 == time1+1;
					}
					if(!slot1.isCourse() && slot2.isCourse()){
						return time1 == time2 || time1 == time2+1;
					}
				}
			}
		}
		if(slot1.getDay().equals("FR") && slot2.getDay().equals("FR")){
			return time1 == time2;
		}
		if(slot1.getDay().equals("MO") && slot2.getDay().equals("FR")){
			if(slot1.isCourse()){
				return time1 == time2 || time1 == time2+1;
			}
		}
		if(slot1.getDay().equals("FR") && slot2.getDay().equals("MO")){
			if(slot2.isCourse()){
				return time2 == time1 || time2 == time1+1;
			}
		}
		return false;
	}
	
	//checks if two courses have identical information
	private boolean courseEquals(Course course1, Course course2){
		if(course1.getDepartment().equals(course2.getDepartment())){
			if(course1.getClassNum() == course2.getClassNum()){
				if(course1.getLecSection().equals(course2.getLecSection())){
					if(course1 instanceof Lab && course2 instanceof Lab){
						if(((Lab)course1).getLabSection().equals(((Lab)course2).getLabSection())){
							return true;
						}	
					}
					if(!(course1 instanceof Lab || course2 instanceof Lab)){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	//checks if two slots have identical information
	private boolean slotEquals(Slot slot1, Slot slot2){
		if(slot1.isCourse() == slot2.isCourse()){
			if(slot1.getDay().equals(slot2.getDay())){
				if(slot1.getTime().equals(slot2.getTime())){
					return true;
				}
			}
		}
		return false;
	}
	
	//checks to see if a course is inside a course list
	//if course is in course list return the version from the list else return null
	private Course containsCourse(List<Course> courseList, Course course1){
		for(Course course2 : courseList){
			if(courseEquals(course1, course2)){
				return course2;
			}
		}
		return null;
	}
	
	//no more than course max or lab max can be assigned to a slot
	private boolean classMax(List<Slot> schedule, Slot slot){
		int current = 1;
		for(Slot scheduleSlot : schedule){
			if(scheduleSlot.getCourse() != null){
				if(slotEquals(slot,scheduleSlot)){
					current++;
				}
			}
		}
		if(current > slot.getMaxCapcity())
			return false;
		else
			return true;
	}
	
	//a course can not be assigned to the same slot as its labs
	private boolean unequalAssignment(List<Slot> schedule, Slot slot){
		Course course = slot.getCourse();
		for(Slot scheduleSlot : schedule){
			if(scheduleSlot.getCourse() != null){
				Course scheduleCourse = scheduleSlot.getCourse();
				if(slot.isCourse() != scheduleSlot.isCourse()){ //check that one is a lab/tutorial and the other is a lecture
					if(course.getDepartment().equals(scheduleCourse.getDepartment())){
						if(course.getClassNum() == scheduleCourse.getClassNum()){
							if(overlap(slot, scheduleSlot)){
								if(course.getLecSection().equals(scheduleCourse.getLecSection())){
									return false;
								}
								if(course.getLecSection().equals("404")){ //check if lab has no lecture section, lab is connected to all lecture sections
									return false;
								}
								if(scheduleCourse.getLecSection().equals("404")){
									return false;
								}
							}
						}
					}
				}
			}
		}
		return true;
	}
	
	//not compatible courses and labs should not be in the same slot
	private boolean notCompatible(List<Slot> schedule, Slot slot){
		List<Course> allKeys = new ArrayList<>();
		allKeys.addAll(notCompatible.keySet());
		Course courseKey = containsCourse(allKeys, slot.getCourse()); //changes the object into a usable key object for notCompatible
		if(courseKey != null){
			List<Course> notCompatibleList = notCompatible.get(courseKey);
			for(Slot scheduleSlot : schedule){
				if(scheduleSlot.getCourse() != null){
					if(overlap(scheduleSlot,slot)){
						Course scheduleCourse = scheduleSlot.getCourse();
						if(containsCourse(notCompatibleList, scheduleCourse) != null){
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	
	//the partial assignment can not be changed
	private boolean partialAssignment(Slot slot){
		List<Course> allKeys = new ArrayList<>();
		allKeys.addAll(partialAssignments.keySet());
		Course courseKey = containsCourse(allKeys, slot.getCourse());
		if(courseKey != null){
			Slot partialSlot = partialAssignments.get(courseKey);
			if(!slotEquals(slot, partialSlot)){
				return false;
			}
		}
		return true;
	}
	
	//courses can not be in unwanted time slots
	private boolean unwanted(Slot slot){
		List<Course> allKeys = new ArrayList<>();
		allKeys.addAll(unwanted.keySet());
		Course courseKey = containsCourse(allKeys, slot.getCourse());
		if(courseKey != null){
			List<Slot> unwantedSlots = unwanted.get(courseKey);
			for(Slot unwantedSlot : unwantedSlots){
				if(slotEquals(slot,unwantedSlot)){
					return false;
				}
			}
		}
		return true;
	}

	//Department CPSC hard constraint
	//lecture 9x courses must be in the evening (18:00 or later)
	//assuming only courses and not labs/tutorials
	private boolean lec9Evening(Slot slot){
		if(slot.isCourse()){
			Course course = slot.getCourse();
			if(course.getDepartment().equals("CPSC")){ //is SENG classes apart of this department?
				String lec = course.getLecSection();
			//	lecNumber = Integer.parseInt(lec.substring(lec.indexOf(" ")+1)); //this one checked for "LEC 9x"
				int lecNumber = Integer.parseInt(lec);	// this one only checks for "9x"
				if(lecNumber >= 90){
					int time = Integer.parseInt(slot.getTime().substring(0,slot.getTime().indexOf(":")));
					if(time < 18){
						return false;
					}
				}
			}
		}
		return true;
	}
	
	//Department CPSC
	//assuming courses only and not labs/tuts
	//500-level courses must be scheduled into different time slots
	private boolean level500(List<Slot> schedule, Slot slot){
		Course course = slot.getCourse();
		if(slot.isCourse() && course.getDepartment().equals("CPSC") && course.getClassNum() >= 500 && course.getClassNum() < 600){
			for(Slot scheduleSlot : schedule){
				if(scheduleSlot.getCourse() != null){
					Course scheduleCourse = scheduleSlot.getCourse();
					if(scheduleSlot.isCourse() && scheduleCourse.getDepartment().equals("CPSC") && scheduleCourse.getClassNum() >= 500 && scheduleCourse.getClassNum() < 600){
						if(slotEquals(slot, scheduleSlot)){
							return false;	
						}
					}
				}
			}
		}
		return true;
	}
	
	//Department CPSC
	//assume courses only and not labs/tutorials
	//no courses can be scheduled at Tuesdays 11:00 - 12:30, so 11:00
	private boolean tuesday11(Slot slot){
		if(slot.isCourse() && slot.getDay().equals("TU") && slot.getTime().equals("11:00") && slot.getCourse().getDepartment().equals("CPSC")){
			return false;
		}
		return true;
	}
	
	//checks if 813 or 913 courses exists first
	//generates list of classes that can not overlap cpsc813 and cpsc913
	private void generateSpecialCourseList(){
		for(Course course: allLabs){
			if(course.getDepartment().equals("CPSC") && course.getClassNum() == 813){
				containsCPSC813 = true;
			}
			if(course.getDepartment().equals("CPSC") && course.getClassNum() == 913){
				containsCPSC913 = true;
			}
		}
		if(containsCPSC813 || containsCPSC913){
			List<Course> cpsc313list = new ArrayList<>();
			List<Course> cpsc413list = new ArrayList<>();
			for(Course course : allCourses){
				if(course.getClassNum() == 313){
					cpsc313list.add(course);
				}
				if(course.getClassNum() == 413){
					cpsc413list.add(course);
				}
			}
			for(Lab lab : allLabs){
				if(lab.getClassNum() == 313){
					cpsc313list.add(lab);
				}
				if(lab.getClassNum() == 413){
					cpsc413list.add(lab);
				}
			}
			cpsc813notCompatibleList.addAll(cpsc313list);
			cpsc913notCompatibleList.addAll(cpsc413list);
			List<Course> notCompatibleKeys = new ArrayList<>();
			notCompatibleKeys.addAll(notCompatible.keySet());
			for(Course course : cpsc313list){
				Course courseKey = containsCourse(notCompatibleKeys, course);
				if(courseKey != null){
					cpsc813notCompatibleList.addAll(notCompatible.get(courseKey));
				}
			}
			for(Course course : cpsc413list){
				Course courseKey = containsCourse(notCompatibleKeys, course);
				if(courseKey != null){
					cpsc913notCompatibleList.addAll(notCompatible.get(courseKey));
				}
			}
		}
	}
	
	//cpsc813 must be scheduled at tuesday/thursday 18:00-19:00
	//cpsc813 can not overlap with courses and labs of cpsc313 -
	//(and transitively with any other courses that are not allowed to overlap with cpsc313)
	private boolean cpsc813(Slot slot){	
		if(containsCPSC813){
			Course course = slot.getCourse();
			if(course.getDepartment().equals("CPSC") && course.getClassNum() == 813){
				if(!(slot.getDay().equals("TU") && slot.getTime().equals("18:00"))){
					return false;
				}
			}
			else if(containsCourse(cpsc813notCompatibleList,course) != null){
				if(slot.getDay().equals("TU")){
					if(slot.getTime().equals("18:00") || slot.getTime().equals("18:30")){
						return false;
					}
					if(slot.isCourse() && slot.getTime().equals("17:00")){ //lectures can overlap
						return false;
					}
				}
			}
		}
		return true;
	}
	
	//cpsc813 and cpsc913 must be scheduled at tuesday/thursday 18:00-19:00
	//cpsc913 can not overlap with courses and labs of cpsc413 -
	//(and transitively with any other courses that are not allowed to overlap with cpsc413)
	private boolean cpsc913(Slot slot){	
		if(containsCPSC913){
			Course course = slot.getCourse();
			if(course.getDepartment().equals("CPSC") && course.getClassNum() == 913){
				if(!(slot.getDay().equals("TU") && slot.getTime().equals("18:00"))){
					return false;
				}
			}
			else if(containsCourse(cpsc913notCompatibleList,course) != null){
				if(slot.getDay().equals("TU")){
					if(slot.getTime().equals("18:00") || slot.getTime().equals("18:30")){ //tutorials 18-19 and lectures 18:30-20:00
						return false;
					}
					if(slot.isCourse() && slot.getTime().equals("17:00")){ //check lectures for overlap 17:00-18:30
						return false;
					}
				}
			}
		}
		return true;
	}
}
