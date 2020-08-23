package adoctor.process;

import adoctor.application.analysis.AnalysisDriver;
import adoctor.application.analysis.StopAnalysisException;
import adoctor.application.analysis.analyzers.*;
import adoctor.application.proposal.ProposalDriver;
import adoctor.application.proposal.proposers.*;
import adoctor.application.proposal.undo.Undo;
import adoctor.application.refactoring.RefactoringDriver;
import adoctor.application.smell.ClassSmell;
import org.eclipse.jface.text.BadLocationException;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
* To run aDoctor through command line:
* java - jar aDoctor.jar [path-to-the-project] [path-of-the-log-file] [smell-ID]
*
* where:
* [path-to-the-project] is the path the folder containing the Android project to analyze
* [path-of-the-log-file] is the path to the file where the code smells log will be printed
* [smell-ID] is the ID of the code smell to identify
*   100000: Durable Wakelock
*   010000: Early Resource Binding
*   001000: Inefficient Data Structure
*   000100: Internal Setter
*   000010: Leaking Thread
*   000001: Member Ignoring Method
* */

public class RunADoctor {

    private static final int JSONINDENTATION = 2;

    public static void main (String[] args){


        args=new String[3];
        args[0] = "C:\\Users\\Francesco\\Desktop\\qui\\testclasses"; //project path
        args[1] = "C:\\Users\\Francesco\\Desktop\\qui\\log.json"; //output path
        args[2] = "111111"; //selected code smells


        //validating the input
        if( args.length!=3 || (!InputManager.isAValidInput(args[0], args[1], args[2])) ){
            System.out.println("ERROR: INVALID INPUT");
            return;
        }

        InputManager inputCL =new InputManager(args[0], args[1], args[2]);
        if (inputCL.getClassSmellAnalyzers().size()==0){
            System.out.println("ERROR: SELECT AT LEAST ONE CODE SMELL");
            return;
        }
        AnalysisDriver analysisDriver = new AnalysisDriver(inputCL.getProjectFiles(),inputCL.getPathEntries(), inputCL.getClassSmellAnalyzers(),inputCL.getSelectedPackage());




        //starting the code analysis
        try {
            List<ClassSmell> classSmells =  analysisDriver.startAnalysis();
            if(classSmells.size()==0){
                System.out.println("NO CODE SMELL FOUND");
                return;
            }
/*
            //refactoring
            ArrayList<ClassSmellProposer> classSmellProposers = new ArrayList<>();
            if (args[2].charAt(0)=='1') {
                classSmellProposers.add(new DWProposer());
            }
            if (args[2].charAt(1)=='1') {
                classSmellProposers.add(new ERBProposer());
            }
            if (args[2].charAt(2)=='1') {
                classSmellProposers.add(new IDSProposer());
            }
            if (args[2].charAt(3)=='1') {
                classSmellProposers.add(new ISProposer());
            }
            if (args[2].charAt(4)=='1') {
                classSmellProposers.add(new LTProposer());
            }
            if (args[2].charAt(5)=='1') {
                classSmellProposers.add(new MIMProposer());
            }

            ProposalDriver proposalDriver = new ProposalDriver(classSmellProposers);
            Undo undo;
            RefactoringDriver refactoringDriver;

            for (ClassSmell cs: classSmells) {
                undo = proposalDriver.computeProposal(cs);
                refactoringDriver = new RefactoringDriver(cs.getClassBean().getSourceFile(), undo.getDocument());
                refactoringDriver.startRefactoring();
            }
            //refactoring finished
*/



            JSONArray smellsJSONArray = new JSONArray();
            JSONObject smellJSON;

            //creating the JSON array of code smells
            for (ClassSmell cs: classSmells) {
                smellJSON=new JSONObject();
                smellJSON.put("Class Name", cs.getClassBean().getSourceFile().getName());
                smellJSON.put("Smell Name", cs.getName());
                smellJSON.put("Smell Description", cs.getDescription());
                smellJSON.put("Location", cs.getClassBean().getSourceFile().getAbsolutePath());

                smellsJSONArray.put(smellJSON);
            }

            //printing the results as a JSON file
            File outputJSONFile = inputCL.getOutputLogFile();
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
        /* catch (BadLocationException e) {e.printStackTrace();}*/


    }


}
