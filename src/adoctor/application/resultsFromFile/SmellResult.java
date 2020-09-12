package adoctor.application.resultsFromFile;

import java.io.File;

public class SmellResult {
    private File file;
    private String smellName;
    private boolean selected;

    public SmellResult(File file, String smellName, boolean selected){
        this.file=file;
        this.smellName=smellName;
        this.selected=selected;
    }

    public File getFile(){
        return this.file;
    }

    public String getSmellName(){
        return this.smellName;
    }

    public boolean getSelected (){
        return this.selected;
    }

    public void setSelected(boolean selected){
        this.selected=selected;
    }
}
