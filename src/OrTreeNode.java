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

    public void altern(Slot courseSlot, boolean isGenetic, Constr hardConstraints) {

        Slot aSlot = new Slot(courseSlot);
        Course course = courseSlot.getCourse();
       // List<Slot> childSchedule;
        OrTreeNode child = new OrTreeNode();
        children = new ArrayList<>();

        if(isGenetic)
        {
            if(hardConstraints.constr(schedule, courseSlot))
            {
                List<Slot> childSched = deepCopy();
                childSched.add(courseSlot);
                child = new OrTreeNode(childSched);
                children.add(child);
            }
        }
        else {
            for (int index = 0; index < schedule.size(); index++) {
                //  System.out.println("Sched Size: " + schedule.size());

                if ((course instanceof Lab && !schedule.get(index).isCourse()) || (!(course instanceof Lab) && schedule.get(index).isCourse())) {

                    aSlot = new Slot(schedule.get(index));

                    if (aSlot.getCourse() == null) {
                        aSlot.setCourse(course);
                       // if(!slotDoubled(schedule, aSlot))
                        if (hardConstraints.constr(schedule, aSlot)) {

                            child = new OrTreeNode(createChild(index, course));
                            children.add(children.size(), child);
                         //   if (isGenetic)
                           //     break;
                        }
                    }
                }
            }
        }
    }
    private boolean slotDoubled(List<Slot> schedule, Slot wantToAdd)
    {
        boolean alreadyExists = false;
        for(int i = 0; i < schedule.size(); i++)
        {
            if(schedule.get(i).sameSlot(wantToAdd))
                if(schedule.get(i).getCourse() != null)
                if(schedule.get(i).getCourse().isSame(wantToAdd.getCourse()))
                    alreadyExists = true;
        }
            return alreadyExists;
    }

    private List<Slot> createChild(int index, Course course) {
        List<Slot> tempCopy = deepCopy();  // If 2 lists are chaning at same time this is the problem -> Ian's fault
        Slot item;

        if(needToAddSlot(index, schedule))
        {
            item = new Slot(tempCopy.get(index));
            item.setCourse(null);
            tempCopy.add(index,item);
        }

        if (course instanceof Lab) {
            tempCopy.get(index).setCourse(course);
           // tempCopy.get(index).setIsCourse(false);
        }
        else
        {
            tempCopy.get(index).setCourse(course);
           // tempCopy.get(index).setIsCourse(true);
        }

        return tempCopy;
    }

    private boolean needToAddSlot(int index, List<Slot> tempCopy)
    {
        int max = tempCopy.get(index).getMaxCapcity();
        int counter = 0;
        boolean slotNeeded = false;

        while (counter < max && counter < schedule.size() && (schedule.get(index).sameSlot(schedule.get(counter))))
            counter++;

        if(counter < max)
            slotNeeded = true;

        return slotNeeded;
    }

    private List<Slot> deepCopy() {
        List<Slot> newList = new ArrayList<>();

        for (Slot slot : this.schedule) {
            newList.add(new Slot(slot));
        }

        return newList;
    }


}
