
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;


public class Output{
	
	public void output(List<Slot> sched, int eval){
/*		List<Slot> sched = new ArrayList<Slot>();
		Course c;
		Slot s;
		Lab l;
		
		l = new Lab("SENG", 311,  "01" , "01");
		s = new Slot("MO","8:00",0,0);
		s.setCourse(l);
		sched.add(s);
		
		c = new Course("SENG", 311, "01");
		s = new Slot("MO","8:00",0,0);
		s.setCourse(c);
		sched.add(s);
		
		l = new Lab("CPSC", 567,  "404" , "01");
		s = new Slot("MO","8:00",0,0);
		s.setCourse(l);
		sched.add(s);
		
		c = new Course("CPSC", 567, "01");
		s = new Slot("MO","8:00",0,0);
		s.setCourse(c);
		sched.add(s);
		
		l = new Lab("CPSC", 433,  "02" , "02");
		s = new Slot("MO","8:00",0,0);
		s.setCourse(l);
		sched.add(s);
		
		c = new Course("CPSC", 433, "02");
		s = new Slot("MO","8:00",0,0);
		s.setCourse(c);
		sched.add(s);
		
		l = new Lab("CPSC", 433,  "01" , "01");
		s = new Slot("MO","8:00",0,0);
		s.setCourse(l);
		sched.add(s);
		
		c = new Course("CPSC", 433, "01");
		s = new Slot("MO","8:00",0,0);
		s.setCourse(c);
		sched.add(s);
*/
		
		Collections.sort(sched, slotCompare);
		
		System.out.println("Eval-value: " + eval);
		for(Slot slot : sched){
			Course course = slot.getCourse();
			System.out.print(course.getDepartment() + " "+ course.getClassNum());
			if(!course.getLecSection().equals("404")){
				System.out.print(" LEC " + course.getLecSection());
			}
			if(slot.getCourse() instanceof Lab){
				if(!(course.getDepartment().equals("CPSC") && (course.getClassNum() == 813 || course.getClassNum() == 913))){
					String type = ((Lab)course).getType();
					System.out.print(" " + type + " "  + ((Lab)course).getLabSection() + "      ");
					if(course.getLecSection().equals("404")){
						System.out.print("       ");
					}
				}
				else{
					System.out.print("             ");
				}
			}
			else{
				System.out.print("             ");
			}
			System.out.println(": " + slot.getDay() + " " + slot.getTime());
		}

		
	}
	
	  private Comparator<Slot> slotCompare = new Comparator<Slot>() {
		  @Override
		  public int compare(Slot s1, Slot s2) {
			  Course c1 = s1.getCourse();
			  Course c2 = s2.getCourse();
    	   
			  int dept = c1.getDepartment().compareTo(c2.getDepartment());

			  if(dept != 0){
				  return dept;
			  } 
			  else{
				  Integer classNum = ((Integer)c1.getClassNum()).compareTo((Integer)c2.getClassNum());
				  if(classNum != 0){
					  return classNum;
				  }
				  else{
					  Integer lec1 = Integer.parseInt(c1.getLecSection());
					  Integer lec2 = Integer.parseInt(c2.getLecSection());
					  Integer lec = lec1.compareTo(lec2);
					  if(lec != 0){
						  return lec;
					  }
					  else{
						  Integer lab1 = 0;
						  Integer lab2 = 0;
						  if(c1 instanceof Lab){
							  lab1 = Integer.parseInt(((Lab)c1).getLabSection());
						  }
						  if(c2 instanceof Lab){
							  lab2 = Integer.parseInt(((Lab)c2).getLabSection());
						  }
						  return lab1.compareTo(lab2);
					  }
				  }
			  }
		  }
	  };

}
