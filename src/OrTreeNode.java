import java.util.ArrayList;
import java.util.List;

public class OrTreeNode {
    private List<Slot> schedule;
    private int solved;             //0 means no, 1 means yes and 2 means ?
    private List<OrTreeNode> children;

    OrTreeNode() {
    }

    OrTreeNode(List<Slot> schedule) {
        this.schedule = schedule;
        solved = 2;
    }

    public List<Slot> getSchedule() {
        return schedule;
    }

    public int getSolved() {
        return solved;
    }

    public List<OrTreeNode> getChildren() {
        return children;
    }

    public void setSolvedToFalse() {
        solved = 0;
    }

    public void setSolvedToTrue() {
        solved = 1;
    }

    public void altern(Course course, boolean isGenetic, Constr constraintChecker) {

        Slot aSlot;
        List<Slot> childSchedule;
        OrTreeNode child = new OrTreeNode();
        children = new ArrayList<>();


        for (int index = 0; index < schedule.size(); index++) {
            //  System.out.println("Sched Size: " + schedule.size());

            if ((course instanceof Lab && !schedule.get(index).isCourse()) || (!(course instanceof Lab) && schedule.get(index).isCourse())) {

                aSlot = schedule.get(index);

                if (aSlot.getCourse() == null) {
                    aSlot.setCourse(course);
                    if(constraintChecker.constr(schedule, aSlot))
                    {

                    child = new OrTreeNode(createChild(index, course));
                    children.add(children.size(), child);
                    if (isGenetic)
                        break;
                }
                }
            }
        }
    }

    private List<Slot> createChild(int index, Course course) {
        List<Slot> tempCopy = deepCopy();  // If 2 lists are chaning at same time this is the problem -> Ian's fault

        if (course instanceof Lab) {
            tempCopy.get(index).setCourse(course);
            tempCopy.get(index).setIsCourse(false);
        }
        else
        {
            tempCopy.get(index).setCourse(course);
            tempCopy.get(index).setIsCourse(true);
        }
        return tempCopy;
    }

    private List<Slot> deepCopy() {
        List<Slot> newList = new ArrayList<>();

        for (Slot slot : this.schedule) {
            newList.add(new Slot(slot));
        }

        return newList;
    }


}
