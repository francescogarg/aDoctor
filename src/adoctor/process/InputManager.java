package adoctor.process;

import adoctor.application.analysis.analyzers.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class InputManager {

    private static final int CODESMELLSINPUTSIZE = 6;

    private File project;
    private List<File> projectFiles;
    private String[] pathEntries;
    private ArrayList<ClassSmellAnalyzer> classSmellAnalyzers;
    private String selectedPackage;
    private File outputLogFile;

    public InputManager(String directoryPath, String outputPath, String selectedSmells){
        this.project = new File(directoryPath);
        this.pathEntries = new String[1];
        this.pathEntries[0]=directoryPath;
        this.outputLogFile = new File(outputPath);
        this.projectFiles = listJavaFiles(this.project);
        this.classSmellAnalyzers = setClassSmellAnalyzers(selectedSmells);
        this.selectedPackage="";
    }


    public File getProject(){
        return this.project;
    }

    public ArrayList<ClassSmellAnalyzer> getClassSmellAnalyzers() {
        return classSmellAnalyzers;
    }

    public String[] getPathEntries() {
        return pathEntries;
    }

    public List<File> getProjectFiles() {
        return projectFiles;
    }

    public String getSelectedPackage() {
        return selectedPackage;
    }

    public File getOutputLogFile() {
        return outputLogFile;
    }

    public static boolean isAValidInput(String directoryPath, String outputLogFile, String selectedSmells){
        File project = new File(directoryPath);
        File output = new File(outputLogFile);

        if(project.isDirectory() && output.getParentFile().isDirectory() && selectedSmells.length()==CODESMELLSINPUTSIZE){
            return true;
        }
        else{
            return false;
        }

    }

    private ArrayList<File> listJavaFiles(File pDirectory) {
        ArrayList<File> javaFiles = new ArrayList<>();
        File[] fList = pDirectory.listFiles();

        if (fList != null) {
            for (File file : fList) {
                if (file.isFile()) {
                    if (file.getName().endsWith(".java")) {
                        javaFiles.add(file);
                    }
                } else if (file.isDirectory()) {
                    File directory = new File(file.getAbsolutePath());
                    javaFiles.addAll(listJavaFiles(directory));
                }
            }
        }
        return javaFiles;
    }


    private  ArrayList<ClassSmellAnalyzer> setClassSmellAnalyzers (String selectedSmells){
        ArrayList<ClassSmellAnalyzer> classSmellAnalyzers = new ArrayList<>();
        if (selectedSmells.charAt(0)=='1') {
            classSmellAnalyzers.add(new DWAnalyzer());
        }
        if (selectedSmells.charAt(1)=='1') {
            classSmellAnalyzers.add(new ERBAnalyzer());
        }
        if (selectedSmells.charAt(2)=='1') {
            classSmellAnalyzers.add(new IDSAnalyzer());
        }
        if (selectedSmells.charAt(3)=='1') {
            classSmellAnalyzers.add(new ISAnalyzer());
        }
        if (selectedSmells.charAt(4)=='1') {
            classSmellAnalyzers.add(new LTAnalyzer());
        }
        if (selectedSmells.charAt(5)=='1') {
            classSmellAnalyzers.add(new MIMAnalyzer());
        }

        return classSmellAnalyzers;
    }
}
