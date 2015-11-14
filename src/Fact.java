import java.util.List;

public class Fact {
    private List<Slot> schedule;
    private Integer value;
    private int SelectionChance;

    Fact(){}

    Fact(List<Slot> schedule, Integer value){
        this.schedule = schedule;
        this.value = value;
    }

    public List<Slot> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<Slot> schedule) {
        this.schedule = schedule;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public int getSelectionChance() {
        return SelectionChance;
    }

    public void setSelectionChance(int selectionChance) {
        SelectionChance = selectionChance;
    }

}
