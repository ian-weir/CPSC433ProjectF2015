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
}
