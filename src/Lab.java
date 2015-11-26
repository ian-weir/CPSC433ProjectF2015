public class Lab extends Course{
    String labSection;

    public Lab(String department, int classNum, String lecSection, String labSection){
        super(department, classNum, lecSection);
        this.labSection = labSection;
    }

    public Lab(Lab lab){
        super(lab);
        labSection = lab.getLabSection();
    }

    public String getLabSection() {
        return labSection;
    }
}
