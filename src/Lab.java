public class Lab extends Course{
    String department;
    String lecSection;
    String labSection;
    int classNum;

    public Lab(String department, int classNum, String lecSection, String labSection){
        super(department, classNum, lecSection);
        this.labSection = labSection;
    }
    public String getLabSection() {
        return labSection;
    }
}
