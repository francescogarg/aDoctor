package adoctor.process;

import adoctor.application.analysis.AnalysisDriver;
import adoctor.application.analysis.StopAnalysisException;
import adoctor.application.smell.ClassSmell;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;



public class RunADoctor {

    private static final int JSONINDENTATION = 2;
    private static final String OUTPUTFILENAME = "aDoctorResults.json";

    public static void main (String[] args){

        args=new String[3];
        args[0] = "C:\\Users\\Francesco\\Desktop\\testCI\\src\\testclasses"; //project path
        args[1] = "C:\\Users\\Francesco\\Desktop"; //output path
        args[2] = "111111"; //selected code smells

        //validating the input
        if(!InputManager.isAValidInput(args[0], args[1], args[2])){
            System.out.println("ERROR: INVALID INPUT");
            return;
        }

        InputManager inputCL =new InputManager(args[0], args[1], args[2]);
        AnalysisDriver analysisDriver = new AnalysisDriver(inputCL.getProjectFiles(),inputCL.getPathEntries(), inputCL.getClassSmellAnalyzers(),inputCL.getSelectedPackage());


        try {
            //starting the code analysis
            List<ClassSmell> classSmells =  analysisDriver.startAnalysis();

            JSONArray smellsJSONArray = new JSONArray();
            JSONObject smellJSON;

            for (ClassSmell cs: classSmells) {

                //creating the JSON array of code smells
                smellJSON=new JSONObject();
                smellJSON.put("Class Name", cs.getClassBean().getSourceFile().getName());
                smellJSON.put("Smell Name", cs.getName());
                smellJSON.put("Smell Description", cs.getDescription());
                smellJSON.put("Location", cs.getClassBean().getSourceFile().getAbsolutePath());

                smellsJSONArray.put(smellJSON);
            }


            //printing the results as a JSON file
            File outputJSONFile = new File(inputCL.getOutputPath()+"\\"+OUTPUTFILENAME);
            outputJSONFile.createNewFile();

            FileWriter writer = new FileWriter(outputJSONFile);
            writer.write(smellsJSONArray.toString(JSONINDENTATION));
            writer.close();



        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (StopAnalysisException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
