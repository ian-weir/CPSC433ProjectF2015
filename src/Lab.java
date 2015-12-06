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
    
    @Override
    public boolean isSame(Course course)
    {
    	if(course instanceof Lab){
    		if(this.getDepartment().equals(course.getDepartment()))
    			if(this.getClassNum() == course.getClassNum())
    				if(this.getLecSection().equals(course.getLecSection()))
    					if(this.getLabSection().equals(((Lab) course).getLabSection()))
    						return true;
    	}
        return false;
    }

    public boolean sameLab (Lab lab){
        return this.isSame(lab) && labSection.equals(lab.getLabSection());
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
