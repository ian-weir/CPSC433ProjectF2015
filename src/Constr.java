import javafx.util.Pair;

import java.util.*;

public class Constr {
	private Map<Course, List<Course>> notCompatible;
	private Map<Course, Slot> partialAssignments;
	private Map<Course, List<Slot>> unwanted; // changed slot to list of slots
	private List<Course> allCourses;
	private List<Lab> allLabs;
	
    public Constr(Map<Course, List<Course>> notCompatible, Map<Course, Slot> partialAssignments, Map<Course,List<Slot>> unwanted,List<Course> allCourses,List<Lab> allLabs){
    	this.notCompatible = notCompatible;
    	this.partialAssignments = partialAssignments;
    	this.unwanted = unwanted;
    	this.allCourses = allCourses;
    	this.allLabs = allLabs;
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
    			cpsc813(schedule, slot) &&
    			cpsc913(schedule, slot)
    			;
    }
    
    //hopefully this covers all the cases ....
	//checks if two courses or labs overlap
	private boolean overlap(Slot slot1, Slot slot2){
		int time1 = Integer.parseInt(slot1.getTime().substring(0,slot1.getTime().indexOf(":")));
		int time2 = Integer.parseInt(slot2.getTime().substring(0,slot2.getTime().indexOf(":")));
		
		if(slot1.getDay().equals("MO") && slot2.getDay().equals("MO")){
			return time1 == time2;
		}
		if(slot1.getDay().equals("TU") && slot2.getDay().equals("TU")){
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
	
	//no more than course max or lab max can be assigned to a slot
	private boolean classMax(List<Slot> schedule, Slot slot){
		int max = slot.getMaxCapcity();
		int current = 1;
		Slot scheduleSlot;
		for(int i = 0; i < schedule.size(); i++){
			scheduleSlot = schedule.get(i);
			if(slot.isCourse() == scheduleSlot.isCourse()){ //check if both courses are labs or tutorial
				if(slot.getDay().equals(scheduleSlot.getDay())){	
					if(slot.getTime().equals(scheduleSlot.getTime())){ 
						current++;
					}
				}
			}
		}
		if(current > max)
			return false;
		else
			return true;
	}
	
	//a course can not be assigned to the same slot as its labs
	private boolean unequalAssignment(List<Slot> schedule, Slot slot){
		Slot scheduleSlot;
		Course scheduleCourse;
		Course course = slot.getCourse();
		for(int i = 0; i < schedule.size(); i++){
			scheduleSlot = schedule.get(i);
			if(slot.isCourse() != scheduleSlot.isCourse()){ //check that one is a lab/tut and the other is a lecture
				scheduleCourse = scheduleSlot.getCourse();
				if(course.getDepartment().equals(scheduleCourse.getDepartment())){ 
					if(course.getClassNum() == scheduleCourse.getClassNum()){ 
						if(course.getLecSection().equals(scheduleCourse.getLecSection())){ //check same lecture section
							if(overlap(slot,scheduleSlot)){
								return false;
							}
						}
						if(slot.isCourse() && scheduleCourse.getLecSection().equals("404")){ //tutorial removed lecture section
							if(overlap(slot,scheduleSlot)){
								return false;
							}
						}
						if(scheduleSlot.isCourse() && course.getLecSection().equals("404")){ //tutorial removed lecture section
							if(overlap(slot,scheduleSlot)){
								return false;
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
		Course course = slot.getCourse();
		Course scheduleCourse;
		if(notCompatible.containsKey(course)){ //this is bugged, only works some of the times?
			List<Course> list = notCompatible.get(course);
			for(int i = 0; i < schedule.size(); i++){
				if(overlap(slot, schedule.get(i))){
					scheduleCourse = schedule.get(i).getCourse();
					if(list.contains(scheduleCourse)){ //compare objects
						return false;
					}
				}
			}
		}
		return true;
	}
	
	//the partial assignment can not be changed
	private boolean partialAssignment(Slot slot){
		Slot partialSlot;
		Course course = slot.getCourse();
		if(partialAssignments.containsKey(course)){ //might be bugged
			partialSlot = partialAssignments.get(course);
			if(!slot.getDay().equals(partialSlot.getDay())){
				return false;
			}
			if(!slot.getTime().equals(partialSlot.getTime())){
				return false;
			}
		}
		return true;
	}
	
	//courses can not be in unwanted time slots
	private boolean unwanted(Slot slot){
		if(unwanted.containsKey(slot.getCourse())){ //bugged
			List<Slot> unwantedSlots = unwanted.get(slot.getCourse());
			for(int i = 0; i < unwantedSlots.size(); i++){
				if(slot.getDay().equals(unwantedSlots.get(i).getDay())){
					if(slot.getTime().equals(unwantedSlots.get(i).getTime())){
						return false;
					}
				}
			}
		}
		return true;
	}

	//Department CPSC hard constraint
	//lecture 9x courses must be in the evening (18:00 or later)
	//not sure if this includes TU 17:00 - 18:30 which goes into the evening (did not include this)
	//assuming only courses and not labs/tuts
	private boolean lec9Evening(Slot slot){
		Course course;
		String lec;
		int lecNumber;
		int time;
		if(slot.isCourse()){
			course = slot.getCourse();
			if(course.getDepartment().equals("CPSC")){ //is SENG classes apart of this department?
				lec = course.getLecSection();
			//	lecNumber = Integer.parseInt(lec.substring(lec.indexOf(" ")+1));
				lecNumber = Integer.parseInt(lec);
				if(lecNumber >= 90){
					time = Integer.parseInt(slot.getTime().substring(0,slot.getTime().indexOf(":")));
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
		Course scheduleCourse;
		if(slot.isCourse() && course.getDepartment().equals("CPSC") && course.getClassNum() >= 500 && course.getClassNum() < 600){
			for(int i = 0; i<schedule.size(); i++){
				scheduleCourse = schedule.get(i).getCourse();
				if(scheduleCourse.getClassNum() >= 500 && scheduleCourse.getClassNum() < 600){
					if(slot.getDay().equals(schedule.get(i).getDay())){
						if(slot.getTime().equals(schedule.get(i).getTime())){
							if(schedule.get(i).isCourse()){
								if(scheduleCourse.getDepartment().equals("CPSC")){
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
	
	//Department CPSC
	//assume courses only and not labs/tuts
	//no courses can be scheduled at Tuesdays 11:00 - 12:30, so 11:00
	private boolean tuesday11(Slot slot){
		if(slot.isCourse()){
			if(slot.getDay().equals("TU") && slot.getTime().equals("11:00")){
				if(slot.getCourse().getDepartment().equals("CPSC")){
					return false;
				}
			}
		}
		return true;
	}
	
	//cpsc813 and cpsc913 must be scheduled at tuesday/thursday 18:00-19:00
	//cpsc813 can not overlap with courses and labs of cpsc313 -
	//(and transitively with any other courses that are not allowed to overlap with cpsc313)
	private boolean cpsc813(List<Slot> schedule, Slot slot){	
		List<Course> cpsc813list;
		List<Course> cpsc313list;
		if(slot.getCourse().getClassNum() == 813 && slot.getCourse().getDepartment().equals("CPSC") && slot.isCourse()){
			if(slot.getDay().equals("TU") && slot.getTime().equals("18:00")){
				cpsc813list = new ArrayList<>();
				cpsc313list = new ArrayList<>();
				for(int i = 0; i< allCourses.size(); i++){
					if(allCourses.get(i).getClassNum() == 313 && allCourses.get(i).getDepartment().equals("CPSC")){
						cpsc313list.add(allCourses.get(i));
						cpsc813list.add(allCourses.get(i));
					}
				}
				for(int i = 0; i< allLabs.size(); i++){
					if(allCourses.get(i).getClassNum() == 313 && allCourses.get(i).getDepartment().equals("CPSC")){
						cpsc313list.add(allCourses.get(i));
						cpsc813list.add(allCourses.get(i));
					}
				}
				if(!cpsc313list.isEmpty()){
					for(int i = 0; i < cpsc313list.size();i++){
						if(notCompatible.containsKey(cpsc313list.get(i))){
							cpsc813list.addAll(notCompatible.get(cpsc313list.get(i)));
						}
					}
					for(int i = 0; i < schedule.size(); i++){
						if(cpsc813list.contains(schedule.get(i))){
							if(overlap(slot,schedule.get(i))){
								if(schedule.get(i).getCourse() != slot.getCourse()){
									return false;
								}
							}
						}
					}
				}
			}
			else{
				return false;
			}
		}
		return true;
	}
	
	//cpsc813 and cpsc913 must be scheduled at tuesday/thursday 18:00-19:00
	//cpsc913 can not overlap with courses and labs of cpsc413 -
	//(and transitively with any other courses that are not allowed to overlap with cpsc413)
	private boolean cpsc913(List<Slot> schedule, Slot slot){	
		List<Course> cpsc913list;
		List<Course> cpsc413list;
		if(slot.getCourse().getClassNum() == 913 && slot.getCourse().getDepartment().equals("CPSC") && slot.isCourse()){
			if(slot.getDay().equals("TU") && slot.getTime().equals("18:00")){
				cpsc913list = new ArrayList<>();
				cpsc413list = new ArrayList<>();
				for(int i = 0; i< allCourses.size(); i++){
					if(allCourses.get(i).getClassNum() == 313 && allCourses.get(i).getDepartment().equals("CPSC")){
						cpsc413list.add(allCourses.get(i));
						cpsc913list.add(allCourses.get(i));
					}
				}
				for(int i = 0; i< allLabs.size(); i++){
					if(allCourses.get(i).getClassNum() == 313 && allCourses.get(i).getDepartment().equals("CPSC")){
						cpsc413list.add(allCourses.get(i));
						cpsc913list.add(allCourses.get(i));
					}
				}
				if(!cpsc413list.isEmpty()){
					for(int i = 0; i < cpsc413list.size();i++){
						if(notCompatible.containsKey(cpsc413list.get(i))){
							cpsc913list.addAll(notCompatible.get(cpsc413list.get(i)));
						}
					}
					for(int i = 0; i < schedule.size(); i++){
						if(cpsc913list.contains(schedule.get(i))){
							if(overlap(slot,schedule.get(i))){
								if(schedule.get(i).getCourse() != slot.getCourse()){
									return false;
								}
							}
						}
					}
				}
			}
			else{
				return false;
			}
		}
		return true;
	}
}
