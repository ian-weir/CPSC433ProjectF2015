import java.util.List;

public class OrTreeNode {
    private List<Slot> schedule;
    private int solved;             //0 means no, 1 means yes and 2 means ?
    private List<OrTreeNode> children;

    OrTreeNode(){}

    OrTreeNode(List<Slot> schedule){
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

    public void setSolvedToFalse(){
        solved = 0;
    }

    public void setSolvedToTrue(){
        solved = 1;
    }

    public void altern(Course course, boolean isGenetic){

        Slot aSlot;
        List<Slot> childSchedule;
        OrTreeNode child = new OrTreeNode();

        for(int index = 0; index <  schedule.size(); index++) {
            aSlot = schedule.get(index);
            if (aSlot.getCourse() == null)
            {
                childSchedule = createChild(index, course);
                child.schedule = childSchedule;

              children.add(children.size()-1,child);
              if(isGenetic)
                  break;
            }
        }
    }
    private List<Slot> createChild(int index, Course course)
    {
        List<Slot> tempCopy = schedule;  // If 2 lists are chaning at same time this is the problem -> Ian's fault
        Slot slotToAddTo = schedule.get(index);

        if (course instanceof Lab)
        {
            slotToAddTo.setCourse(course);
            slotToAddTo.setIsCourse(false);
        }
        else
        {
            slotToAddTo.setCourse(course);
            slotToAddTo.setIsCourse(true);
        }

        tempCopy.remove(index);
        tempCopy.add(index, slotToAddTo);
        return tempCopy;
    }

}
