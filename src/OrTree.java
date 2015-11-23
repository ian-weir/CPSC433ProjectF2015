import java.util.ArrayList;
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

        if (course.size() > 0) {
            course_added = course.get(0);
            course.remove(0);
            for (int i = 0; i < head.getSchedule().size(); i++) {
                head.altern(course_added, false);
            }
            //add to tree
        }
        if (labs.size() > 0) {
            lab_added = labs.get(0);
            labs.remove(0);
            for (int i = 0; i < head.getSchedule().size(); i++) {
                head.altern(lab_added, false);
            }
        }
        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(randomInt);
        randomInt = randomInt % head.getSchedule().size();

        solved = generateTree(head.getChildren().get(randomInt), course, labs, randomInt);
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