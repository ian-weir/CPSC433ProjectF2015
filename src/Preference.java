import javafx.util.Pair;

public class Preference {
    Course course;
    Pair<String, String> slotId;
    Integer weight;

    Preference(Course course, Pair<String, String> slotId, Integer weight){
        this.course = course;
        this.slotId = slotId;
        this.weight = weight;
    }


    public Course getCourse() {
        return course;
    }

    public Pair<String, String> getSlotId() {
        return slotId;
    }

    public Integer getWeight() {
        return weight;
    }



}
