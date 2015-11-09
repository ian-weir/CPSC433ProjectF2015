public class Slot {
    String day, time;
    int maxCapcity, minCapacity;
    boolean isCourse;

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

}