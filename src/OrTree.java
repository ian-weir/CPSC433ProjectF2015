import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class OrTree {

    private List<Slot> solution;
    private FileParser fileParser;
    private Constr hardConstraints;
    private int backtrackCount = 0;
    private long startTime;
    private long oneMinute = 60000;
    private long tenSeconds = 10000;

    public OrTree(FileParser fileParser) {
        this.fileParser = fileParser;
        hardConstraints = new Constr(fileParser);//new Constr(fileParser.getNotCompatible(), fileParser.getPartialAssignments(), fileParser.getUnwanted(), fileParser.getAllCourses(), fileParser.getAllLabs());
    }

    public List<Slot> initialize() {
        startTime = System.currentTimeMillis();
        generateTree(new OrTreeNode(createBlankSchedule(fileParser)), deepCopyCourses(fileParser.getAllCourses()), deepCopyLabs(fileParser.getAllLabs()));

        if(solution == null)
        {
            System.out.println("*** No valid solution found ***");
            System.exit(0);
        }
        stripEmptySlots();
        if(solution.isEmpty()){
            solution = null;
        }
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
        randomInt = randomGenerator.nextInt(Integer.MAX_VALUE - 1);
        while (randomInt < 1) {
            randomInt = randomGenerator.nextInt(Integer.MAX_VALUE - 1);
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
                    if (index == head.getChildren().size()) {  // Check if in array bounds
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
                long currentTime = System.currentTimeMillis() - startTime;
                if(currentTime > tenSeconds){
                    solution = new ArrayList<>();
                    head.setSolvedToFalse();
                    return true;
                }
                return false;  // return no solution found
            }
        }
        return solved;
    }
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

        startTime = System.currentTimeMillis();
        crossover(new OrTreeNode(createBlankSchedule(fileParser)), parentOne, parentTwo, tempCopy);
        if (solution != null) {
            stripEmptySlots();
        }
        return solution;
    }

    private boolean crossover(OrTreeNode head, List<Slot> parentOne, List<Slot> parentTwo, List<Slot> localSched) //TODO in the works
    {
        Random randomGenerator = new Random();   // Need to choose a random child node to expand
        int randomInt = randomGenerator.nextInt(Integer.MAX_VALUE - 1);
        boolean notAdded = false;
        if (randomInt < 1) {
            randomInt = randomGenerator.nextInt(Integer.MAX_VALUE - 1);
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

        head.getSchedule().remove(slotRemoved);

//********************************************************************************
        //CASE 1: SAME
        index = findIndexCounterpart(slotRemoved, parentTwo);

        if (slotRemoved.sameSlot(parentTwo.get(index))) {
      //      System.out.println("Case 1       "  + localSched.size() + "Class removed: " + slotRemoved.getCourse().getDepartment() + " " + slotRemoved.getCourse().getClassNum() +" " + slotRemoved.getCourse().getLecSection() );
            head.altern(slotRemoved, true, hardConstraints);
            if (head.getChildren() == null || head.getChildren().size() == 0)
                notAdded = true;
            else
                isSolved = crossover(head.getChildren().get(0), parentOne, parentTwo, localSched);
        }
// **********************************************************************************
        // Case 2: DIFFERENT
        else {
        //    System.out.println("Case 2       "  + localSched.size() + "Class removed: " + slotRemoved.getCourse().getDepartment() + " " + slotRemoved.getCourse().getClassNum()+ " " + slotRemoved.getCourse().getLecSection() );
            if (randomInt == 0) {
                head.altern(slotRemoved, true, hardConstraints);
                if (hasNoChildren(head)) {
                    notAdded= true;
                    //    System.out.println("Not added Case 1");
                //    head.getSchedule().remove(parentOne.get(index));
                 //   head.altern(parentOne.get(index), true, hardConstraints);
                }

            } else {
               // head.getSchedule().remove(parentOne.get(index));
                head.altern(parentTwo.get(index), true, hardConstraints);
                if (hasNoChildren(head)) {
                    notAdded = true;
                    //   System.out.println("Not added Case 2");
                  //  head.altern(slotRemoved, true, hardConstraints);
                }
            }

            if (head.getChildren() == null || head.getChildren().size() == 0)
                notAdded = true;
            else
                isSolved = crossover(head.getChildren().get(0), parentOne, parentTwo, localSched);
        }
//**********************************************************************************
        // CASE 3:  RANDOM
        if (notAdded) {// if no solution has been found need to create and check the other children of the current node
          //  System.out.println("Case 3    " + localSched.size() + "Class removed: " + slotRemoved.getCourse().getDepartment() + " " + slotRemoved.getCourse().getClassNum() +" " + slotRemoved.getCourse().getLecSection() );
            localSched.add(slotRemoved);
            head.getSchedule().remove(slotRemoved);
            isSolved = genAllPossibleNodes(head, parentOne, parentTwo, localSched);
        }
        if(!notAdded && !isSolved) {
           // System.out.println("Case 4     "  + localSched.size() + "Class Added: " + slotRemoved.getCourse().getDepartment() + " " + slotRemoved.getCourse().getClassNum() + " " + slotRemoved.getCourse().getLecSection());
            localSched.add(slotRemoved);
            head.getSchedule().remove(slotRemoved);
            head.getChildren().clear();
            isSolved = genAllPossibleNodes(head, parentOne, parentTwo, localSched);
        }
        //if(isSolved)
        //System.out.println("Return True");
        //else
         //   System.out.println("Return False");
        if(!isSolved){
            head.setSolvedToFalse();
            if(localSched.size() == 1){
                backtrackCount++;
            }
        }
        long currentTime = System.currentTimeMillis();
        if(currentTime - startTime >  oneMinute || backtrackCount > 10000){ //if the last element has failed to be inserted 10000 times or the tree is taking longer than 2 minutes, start over
            localSched = new ArrayList<>();
            isSolved = true;
        }

        return isSolved;
    }



    private boolean genAllPossibleNodes(OrTreeNode head, List<Slot> parentOne, List<Slot> parentTwo,List<Slot> slotToAdd) {
        boolean isSolved = false;
        Random randomGenerator = new Random();   // Need to choose a random child node to expand
        int randomInt = randomGenerator.nextInt(Integer.MAX_VALUE - 1);

        Slot classToAdd = slotToAdd.get(0);

        head.altern(slotToAdd.get(0), false, hardConstraints);
        if (head.getChildren() == null || head.getChildren().size() == 0)
            return false;
        slotToAdd.remove(0);

        while (randomInt < 1) {
            randomInt = randomGenerator.nextInt(Integer.MAX_VALUE - 1);
        }
        randomInt = randomInt % head.getChildren().size();
        int startingPoint = randomInt;

        for (int i = 0; i < head.getChildren().size(); i++) {
            if (randomInt == head.getChildren().size())
                randomInt = 0;

            while(head.getChildren().get(randomInt).getSolved() == 0){
                randomInt++;
                if(randomInt == head.getChildren().size()){
                    randomInt = 0;
                }
                isSolved = crossover(head.getChildren().get(randomInt), parentOne, parentTwo, slotToAdd);
                if (isSolved) {
                    break;
                }

                randomInt++;
                if(randomInt == startingPoint){
                    break;
                }
            }
        /*    isSolved = crossover(head.getChildren().get(randomInt), parentOne, parentTwo, slotToAdd);
            if (isSolved) {
                break;
            }

            randomInt++;
            if(randomInt == startingPoint){
                break;
            }*/
        }
        if(!isSolved) {
            slotToAdd.add(classToAdd);
            head.getChildren().remove(slotToAdd);// = null;
            head.setSolvedToFalse();
        }
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
            if(desiredSlot.sameSlot(schedTwo.get(i)) && desiredSlot.getCourse().isSame(schedTwo.get(i).getCourse()))
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