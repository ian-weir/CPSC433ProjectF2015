import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class OrTree {

    private List<Slot> solution;
    private FileParser fileParser;
    private Constr hardConstraints;

    public OrTree(FileParser fileParser) {
        this.fileParser = fileParser;
        hardConstraints = new Constr(fileParser);//new Constr(fileParser.getNotCompatible(), fileParser.getPartialAssignments(), fileParser.getUnwanted(), fileParser.getAllCourses(), fileParser.getAllLabs());
    }

    public List<Slot> initialize() {
        generateTree(new OrTreeNode(createBlankSchedule(fileParser)), deepCopyCourses(fileParser.getAllCourses()), deepCopyLabs(fileParser.getAllLabs()));
        stripEmptySlots();
        return solution;
    }

    public List<Slot> getSolution() {
        return solution;
    }


    public boolean generateTree(OrTreeNode head, List<Course> course, List<Lab> labs) { /// FIX randomINT
        int randomInt;
        Course course_added = null;
        Lab lab_added = null;
        boolean solved = false;
        Slot fakeSlot = new Slot("X", "X", 1, 1);

        if (head.getSolved() == 0)  // Base Case 1 Not handled
            return false;

        if (head.getSolved() == 1) {  // Base Case 2
            solution = head.getSchedule();
            return true;
        }

        if (course != null && !course.isEmpty()) {    // Add the classes to the schedule first
            course_added = course.get(0);                            // get the first class in the list
            course.remove(0);                                        // remove it as it "should not" be readded unless backtracking
            fakeSlot.setCourse(course_added);
            head.altern(fakeSlot, false, hardConstraints);    // generate all the children with that class now assigned to a slot
        } else if (labs != null && !labs.isEmpty()) {  // Add the labs to the schedule when all the classes are assigned
            lab_added = labs.get(0);                           // get the first lab in the list
            labs.remove(0);                                    //remove it as it "should not" be readded unless backtracking
            fakeSlot.setCourse(lab_added);
            head.altern(fakeSlot, false, hardConstraints);    //enerate all the children with that class now assigned to a slot
        } else {                              // If there are no more classes or labs to assign then we are done
            head.setSolvedToTrue();
            solution = head.getSchedule();
        }
        Random randomGenerator = new Random();   // Need to choose a random child node to expand
        randomInt = randomGenerator.nextInt();
        while (randomInt < 1) {
            randomInt = randomGenerator.nextInt();
        }
        int modNumber = (head.getChildren() == null || head.getChildren().isEmpty() ? head.getSchedule().size() : head.getChildren().size());
        int randomInt1 = randomInt % modNumber;

        if (head.getChildren() != null && !head.getChildren().isEmpty())  // If there are children go to the randomly selected one and check for solution
            solved = generateTree(head.getChildren().get(randomInt1), course, labs);

        if (solved) {
            return solved;
        } else {              // if no solution has been found need to check the other children of the current node
            if (head.getChildren() != null) {
                int index = randomInt1 + 1;  // Always go to the next child index from the randomly selected node
                if (index == head.getChildren().size()) {
                    index = 0;
                }
                for (int i = 0; i < head.getChildren().size(); i++) {  // For all the children
                    if (index == head.getChildren().size() - 1) {  // Check if in array bounds
                        index = 0;
                    }
                    solved = generateTree(head.getChildren().get(index), course, labs);  // Check if other child has a solution
                    if (solved == true) { // if a solution is found then stop
                        break;
                    }
                    index++;
                }
            }
            if (!solved) {    // If none of the children have a solution -> need to backtrack
                if (course_added != null) {  // If it was a course added to create the schedual at this point
                    course.add(course_added);    // Put the course back in the course list
                    head.getSchedule().remove(course_added); // Take the course out of the schedual.
                } else if (lab_added != null) {  // If it was a lab added to create the schedule at this point
                    labs.add(lab_added);      // put the lab back in the Lab list
                    head.getSchedule().remove(lab_added); // remove the lab from the schedule
                }
                return false;  // return no solution found
            }
        }
        return solved;
    }
/*
    public List<Slot> converge(OrTreeNode head, List<Slot> schedule_1, List<Slot> schedule_2, int randomInt) {
        if (schedule_1.size() == 0)
            return head.getSchedule();

        if (schedule_1.get(0).equals(schedule_2.get(0))) {
            head.altern(schedule_1.get(0).getCourse(), true);
            schedule_1.remove(0);
            schedule_2.remove(0);
        } else {
            Random randomGenerator = new Random();
            randomInt = randomGenerator.nextInt(randomInt);
            randomInt = randomInt % 2;

            if (randomInt == 1)
                head.altern(schedule_1.get(0).getCourse(), true);
            else
                head.altern(schedule_2.get(0).getCourse(), true);

            schedule_1.remove(0);
            schedule_2.remove(0);
        }
        return converge(head.getChildren().get(0), schedule_1, schedule_2, randomInt);
    }
*/

    private List<Slot> createBlankSchedule(FileParser fileParser) {
        List<Slot> blankSchedule = new ArrayList<>();

        for (Slot slot : fileParser.getCourseSlots().values()) {
            blankSchedule.add(slot);
        }
        for (Slot slot : fileParser.getLabSlots().values()) {
            blankSchedule.add(slot);
        }
        return blankSchedule;
    }

    private List<Slot> stripEmptySlots() {
        for (int i = 0; i < solution.size(); i++) {
            if (solution.get(i).getCourse() == null) {
                solution.remove(i);
                i--;
            }
        }
        return solution;
    }

    private List<Course> deepCopyCourses(List<Course> courses) {
        List<Course> deepCopyCourses = new ArrayList<>();
        for (Course course : courses) {
            deepCopyCourses.add(new Course(course));
        }
        return deepCopyCourses;
    }

    private List<Lab> deepCopyLabs(List<Lab> labs) {
        List<Lab> deepCopyLabs = new ArrayList<>();
        for (Lab lab : labs) {
            deepCopyLabs.add(new Lab(lab));
        }
        return deepCopyLabs;
    }


    public List<Slot> runCrossover(List<Slot> parentOne, List<Slot> parentTwo, FileParser fileParser) {
        //OrTreeNode head = new OrTreeNode();
        List<Slot> tempCopy = new ArrayList<>();
        for (Slot slot : parentOne)
            tempCopy.add(new Slot(slot));

        crossover(new OrTreeNode(createBlankSchedule(fileParser)), parentOne, parentTwo, tempCopy);
        if (solution != null) {
            stripEmptySlots();
        }
        return solution;
    }

    private boolean crossover(OrTreeNode head, List<Slot> parentOne, List<Slot> parentTwo, List<Slot> localSched) //TODO in the works
    {
        Random randomGenerator = new Random();   // Need to choose a random child node to expand
        int randomInt = randomGenerator.nextInt();
        boolean notAdded = false;
        while (randomInt < 1) {
            randomInt = randomGenerator.nextInt();
        }
        randomInt = randomInt % 2;
        Slot slotRemoved;//= new Slot();
        int index;
        boolean isSolved = false;

        if (localSched == null)
            return true;

        if (localSched.size() == 0) {
            solution = head.getSchedule();
            return true;
        }

        slotRemoved = localSched.get(0);
        localSched.remove(0);

//********************************************************************************
        //CASE 1: SAME
        index = findIndexCounterpart(slotRemoved, parentTwo);

        if (slotRemoved.sameSlot(parentTwo.get(index))) {
            head.altern(slotRemoved, true, hardConstraints);
            if (head.getChildren() == null || head.getChildren().size() == 0)
                notAdded = true;
            else
                isSolved = crossover(head.getChildren().get(0), parentOne, parentTwo, localSched);
        }
// **********************************************************************************
        // Case 2: DIFFERENT
        else {
            if (randomInt == 0) {
                head.altern(slotRemoved, true, hardConstraints);
                if (hasNoChildren(head))
                    head.altern(parentOne.get(index), true, hardConstraints);

            } else {
                head.altern(parentTwo.get(index), true, hardConstraints);
                if (hasNoChildren(head))
                    head.altern(slotRemoved, true, hardConstraints);
            }

            if (head.getChildren() == null || head.getChildren().size() == 0)
                notAdded = true;
            else
                isSolved = crossover(head.getChildren().get(0), parentOne, parentTwo, localSched);
        }
//**********************************************************************************
        // CASE 3:  RANDOM
        if (notAdded) {              // if no solution has been found need to create and check the other children of the current node
            localSched.add(slotRemoved);
            isSolved = genAllPossibleNodes(head, parentOne, parentTwo, localSched);
        }
        if(!notAdded && !isSolved)
            isSolved = genAllPossibleNodes(head, parentOne, parentTwo, localSched);

        return isSolved;
    }



    private boolean genAllPossibleNodes(OrTreeNode head, List<Slot> parentOne, List<Slot> parentTwo,List<Slot> slotToAdd) {
        boolean isSolved = false;
        Random randomGenerator = new Random();   // Need to choose a random child node to expand
        int randomInt = randomGenerator.nextInt();

        Slot classToAdd = slotToAdd.get(0);

        head.altern(slotToAdd.get(0), false, hardConstraints);
        if (head.getChildren() == null || head.getChildren().size() == 0)
            return false;
        slotToAdd.remove(0);

        while (randomInt < 1) {
            randomInt = randomGenerator.nextInt();
        }
        randomInt = randomInt % head.getChildren().size();

        for (int i = 0; i < head.getChildren().size(); i++) {
            if (randomInt == head.getChildren().size())
                randomInt = 0;

            isSolved = crossover(head.getChildren().get(randomInt), parentOne, parentTwo, slotToAdd);
            if (isSolved)
                break;

            randomInt++;
        }
        if(!isSolved)
            slotToAdd.add(classToAdd);
        return isSolved;
    }

     

    private boolean hasNoChildren(OrTreeNode head)
    {
        if(head.getChildren().size() == 0)
            return true;
        else
            return false;
    }
    private int findIndexCounterpart(Slot desiredSlot, List<Slot> schedTwo)
    {
        Course target = desiredSlot.getCourse();
        int index = -1;

        for(int i = 0; i < schedTwo.size(); i++)
        {
            if(desiredSlot.sameSlot(schedTwo.get(i)))
            {
                index = i;
                break;
            }
            else if(schedTwo.get(i).getCourse().isSame(target))
                if(schedTwo.get(i).isCourse() == desiredSlot.isCourse())
                {
                    index = i;
                    break;
                }
        }
        return index;
    }

}