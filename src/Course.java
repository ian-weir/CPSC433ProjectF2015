public class Course {
    String department, lecSection;

    int classNum;
    public Course(String department, int classNum, String lecSection){
        this.department = department;
        this.classNum = classNum;
        this.lecSection = lecSection;
    }

    public String getDepartment() {
        return department;
    }

    public String getLecSection() {
        return lecSection;
    }

    public int getClassNum() {
        return classNum;
    }

}