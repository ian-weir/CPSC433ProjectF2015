import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class OrTree {

    private List<Slot> solution;

    public List<Slot> initialize(){
        return crossover(null, null);
    }

    public List<Slot> crossover(List<Slot> parentOne, List<Slot> parentTwo){
        List<Slot> newFact = new ArrayList<>();


        return newFact;
    }

    public List<Slot> getSolution(){
        return solution;
    }

    public void run()
    {
        List<Course> courses = new ArrayList<Course>();;
        Course class1 = new Course("CPSC",413, "L01");
        Course class2 = new Course("CPSC",511, "L01");
        Course class3 = new Course("CPSC",201, "L01");
        Course class4 = new Course("CPSC",109, "L01");
        Course class5 = new Course("CPSC",625, "L01");

        courses.add(class1);
        courses.add(class2);
        courses.add(class3);
        courses.add(class4);
        courses.add(class5);

        List<Lab> labs = null;
        List<Slot> sched = new ArrayList<Slot>();;
        Slot element = new Slot("mon","8am",1,1);
        Slot element1 = new Slot("mon","9am",1,1);
        Slot element2 = new Slot("mon","10am",1,1);
        Slot element3 = new Slot("mon","1am",1,1);
        Slot element4 = new Slot("mon","2am",1,1);
        sched.add(element);
        sched.add(element1);
        sched.add(element2);
        sched.add(element3);
        sched.add(element4);


        OrTreeNode head = new OrTreeNode(sched);
        generateTree(head,courses,labs,5677);

        Output output = new Output();

        output.output(solution, 10);

//        System.out.print(solution.get(0).getCourse() + "\n");
//        System.out.print(solution.get(1).getCourse() + "\n");
//        System.out.print(solution.get(2).getCourse() + "\n");
//        System.out.print(solution.get(3).getCourse() + "\n");
//        System.out.print(solution.get(4).getCourse() + "\n");

    }



    public boolean generateTree(OrTreeNode head, List<Course> course, List<Lab> labs, int randomInt) { /// FIX randomINT
        Course course_added;
        Lab lab_added;
        boolean solved = false;

        if (head.getSolved() == 0)
            return false;
        if (head.getSolved() == 1) {
            solution = head.getSchedule();
            return true;
        }

        if (course != null && !course.isEmpty()) {
            course_added = course.get(0);
            course.remove(0);
            //for (int i = 0; i < head.getSchedule().size(); i++) {
                head.altern(course_added, false);
            //}
            //add to tree
        }
        else if (labs != null  && !labs.isEmpty()) {
            lab_added = labs.get(0);
            labs.remove(0);
            for (int i = 0; i < head.getSchedule().size(); i++) {
                head.altern(lab_added, false);
            }
        }
        else {
            head.setSolvedToTrue();
            solution = head.getSchedule();
        }
        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt();
        while(randomInt <  1){
            randomInt = randomGenerator.nextInt();
        }
       int randomInt1 = randomInt % head.getSchedule().size() - 1;
        if(head.getChildren() != null)
        solved = generateTree(head.getChildren().get(0), course, labs, randomInt1);
        return solved;

    }

    public List<Slot> converge(OrTreeNode head, List<Slot> schedule_1, List<Slot> schedule_2, int randomInt)
    {
        if(schedule_1.size() == 0)
            return head.getSchedule();

        if(schedule_1.get(0).equals(schedule_2.get(0)))
        {
            head.altern(schedule_1.get(0).getCourse(), true);
            schedule_1.remove(0);
            schedule_2.remove(0);
        }
        else{
            Random randomGenerator = new Random();
            randomInt = randomGenerator.nextInt(randomInt);
            randomInt = randomInt % 2;

            if(randomInt == 1)
                head.altern(schedule_1.get(0).getCourse(), true);
            else
                head.altern(schedule_2.get(0).getCourse(),true);

            schedule_1.remove(0);
            schedule_2.remove(0);
        }
        return converge(head.getChildren().get(0), schedule_1, schedule_2, randomInt);
    }


}