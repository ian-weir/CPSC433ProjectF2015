public class Slot {
    private String day, time;
    private int maxCapcity, minCapacity;
    private boolean isCourse;

    private Course course;

    public Slot(String day, String time, int maxCapcity, int minCapacity) {
        this.day = day;
        this.time = time;
        this.maxCapcity = maxCapcity;
        this.minCapacity = minCapacity;
    }

    public Slot(Slot slot) {
        day = slot.getDay();
        time = slot.getTime();
        maxCapcity = slot.getMaxCapcity();
        minCapacity = slot.getMinCapacity();
        setCourse(slot.getCourse());
        isCourse = slot.isCourse();
    }

    public boolean sameSlot(Slot slot){
        return this.day.equals(slot.getDay()) && this.getTime().equals(slot.getTime()) && this.isCourse == slot.isCourse();
    }

    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    public int getMaxCapcity() {
        return maxCapcity;
    }

    public int getMinCapacity() {
        return minCapacity;
    }

    public boolean isCourse(){
        return isCourse;
    }

    public void setIsCourse(boolean isCourse) {
        this.isCourse = isCourse;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
