package adoctor.application.resultsFromFile;


import org.apache.velocity.runtime.directive.Foreach;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class SmellResultList {

    private ArrayList<String> smellTypes;
    private ArrayList<SmellResult> smells;


    public SmellResultList(){
        this.smellTypes=new ArrayList<String>();
        this.smells=new ArrayList<SmellResult>();
    }

    public ArrayList<String> getSmellTypes(){
        return this.smellTypes;
    }

    public ArrayList<SmellResult> getSmellResultList(){
        return this.smells;
    }






    public void addSmell(SmellResult smell){
        if (!smellsContains(smell)){
            this.smells.add(smell);
        }

        if(!typesContains(smell.getSmellName())){
            this.smellTypes.add(smell.getSmellName());
        }
    }

    public int instancesNumberOfType(String smellType){
        int number=0;

        for (int i=0; i< smells.size();i++){
            if(smells.get(i).getSmellName().equalsIgnoreCase(smellType)){
                number++;
            }
        }

        return number;
    }


    public ArrayList<SmellResult> getSmellResultsOfType(String smellType){
        ArrayList<SmellResult> smellResultsOfType = new ArrayList<>();

        for (int i=0; i<smells.size(); i++){
            if (smells.get(i).getSmellName().equalsIgnoreCase(smellType)){
                smellResultsOfType.add(smells.get(i));
            }
        }

        return smellResultsOfType;
    }



    public void setSelected(SmellResult smellResult, boolean selected){
        for (int i=0;i<smells.size();i++){

            if (smells.get(i).getFile().getAbsolutePath().equalsIgnoreCase(smellResult.getFile().getAbsolutePath())
                    && smells.get(i).getSmellName().equalsIgnoreCase(smellResult.getSmellName()))
            {
                smells.get(i).setSelected(selected);
                return;
            }
        }
    }




    private boolean smellsContains(SmellResult result){
        File f=result.getFile();
        String smellName=result.getSmellName();

        for (int i=0; i<smells.size(); i++){
            if (smells.get(i).getFile().getAbsolutePath().equalsIgnoreCase(f.getAbsolutePath())
                    && smells.get(i).getSmellName().equalsIgnoreCase(smellName))
            {
                return true;
            }
        }

        return false;
    }

    private boolean typesContains(String smellName){
        for(int i=0; i<smellTypes.size(); i++){
            if(smellTypes.get(i).equalsIgnoreCase(smellName)){
                return true;
            }
        }

        return false;
    }
}
