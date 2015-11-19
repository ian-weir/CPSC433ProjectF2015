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
                head.altern(course_added);
            }
            //add to tree
        }
        if (labs.size() > 0) {
            lab_added = labs.get(0);
            labs.remove(0);
            for (int i = 0; i < head.getSchedule().size(); i++) {
                head.altern(lab_added);
            }
        }
        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(randomInt);
        randomInt = randomInt % head.getSchedule().size();

        solved = generateTree(head.getChildren().get(randomInt), course, labs, randomInt);
        return solved;

    }


}