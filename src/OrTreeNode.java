import java.util.ArrayList;
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
        children = new ArrayList<>();

        for(int index = 0; index <  schedule.size(); index++) {
          //  System.out.println("Sched Size: " + schedule.size());
            aSlot = schedule.get(index);
            if (aSlot.getCourse() == null)
            {
//                childSchedule = createChild(index, course);
//                child.schedule = childSchedule;
                child = new OrTreeNode(createChild(index, course));

              children.add(children.size(),child);
              if(isGenetic)
                  break;
            }
        }
    }
    private List<Slot> createChild(int index, Course course)
    {
        List<Slot> tempCopy = deepCopy();  // If 2 lists are chaning at same time this is the problem -> Ian's fault
        Slot slotToAddTo = tempCopy.get(index);

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

    private List<Slot> deepCopy(){
        List<Slot> newList = new ArrayList<>();

        for(Slot slot : schedule){
            newList.add(new Slot(slot));
        }

        return newList;
    }


}
