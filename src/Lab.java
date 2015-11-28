public class Lab extends Course{
   private String labSection;
   private String type;

    public Lab(String department, int classNum, String lecSection, String labSection){
        super(department, classNum, lecSection);
        this.labSection = labSection;
    }

    public Lab(Lab lab){
        super(lab);
        labSection = lab.getLabSection();
        type = lab.getType();
    }

    public String getLabSection() {
        return labSection;
    }
    
    public void setType(String type){
    	this.type = type;
    }
    
    public String getType(){
    	return type;
    }
    
    
}
