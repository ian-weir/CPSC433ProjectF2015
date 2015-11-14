public class Slot {
    private String day, time;
    private int maxCapcity, minCapacity;
    private boolean isCourse;

    private Course course;

    public Slot(String day, String time, int maxCapcity, int minCapacity){
        this.day = day;
        this.time = time;
        this.maxCapcity = maxCapcity;
        this.minCapacity = minCapacity;
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
