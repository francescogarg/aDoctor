package adoctor.application.analysis;

import adoctor.application.analysis.analyzers.*;
import adoctor.application.ast.ASTUtilities;
import adoctor.application.bean.ClassBean;
import adoctor.application.resultsFromFile.SmellResult;
import adoctor.application.smell.ClassSmell;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AnalysisDriver {

    private List<File> projectFiles;
    private String[] pathEntries;
    private ArrayList<ClassSmellAnalyzer> classSmellAnalyzers;
    private String targetPackage;
    private final AnalysisThread analysisThread;

    public AnalysisDriver(List<File> projectFiles, String[] pathEntries, ArrayList<ClassSmellAnalyzer> classSmellAnalyzers, String targetPackage) {
        this.projectFiles = projectFiles;
        this.pathEntries = pathEntries;
        this.classSmellAnalyzers = classSmellAnalyzers;
        this.targetPackage = targetPackage;
        this.analysisThread = new AnalysisThread();
    }

    public List<ClassSmell> startAnalysis() throws InterruptedException, StopAnalysisException {
        analysisThread.start();
        System.out.println("Analysis started");
        synchronized (analysisThread) {
            while (analysisThread.result == null && analysisThread.stopAnalysisException == null) {
                analysisThread.wait();
            }
        }
        if (analysisThread.stopAnalysisException != null) {
            System.out.println("Analysis aborted");
            throw analysisThread.stopAnalysisException;
        } else {
            System.out.println("Analysis terminated successfully");
            return analysisThread.result;
        }
    }

    public void abortAnalysis() {
        analysisThread.stop = true;
    }

    private class AnalysisThread extends Thread {
        private volatile List<ClassSmell> result;
        private volatile StopAnalysisException stopAnalysisException;
        private volatile boolean stop;
        private final static String STOP_MESSAGE = "Analysis has been stopped";

        private AnalysisThread() {
            this.result = null;
            this.stopAnalysisException = null;
            this.stop = false;
        }

        public void run() {
            synchronized (this) {
                try {
                    result = runAnalysis();
                } catch (StopAnalysisException e) {
                    stopAnalysisException = e;
                }
                notify();
            }
        }

        private List<ClassSmell> runAnalysis() throws StopAnalysisException {
            checkStop();
            List<ClassSmell> classSmells = new ArrayList<>();
            if (classSmellAnalyzers == null || classSmellAnalyzers.size() <= 0
                    || projectFiles == null || projectFiles.size() <= 0) {
                return classSmells;
            }

            // Analysis phase
            Pattern patternPackage = targetPackage == null || targetPackage.equals("")
                    ? Pattern.compile(".*")
                    : Pattern.compile("^" + targetPackage + "(\\..*)?$");

            for (File projectFile : projectFiles) {
                // TODO Size estmate to implement a progress bar
                System.out.println("Analyzing file: " + projectFile.getName());
                checkStop();
                try {
                    // Package filtering
                    CompilationUnit compilationUnit = ASTUtilities.getCompilationUnit(projectFile, pathEntries);
                    if (compilationUnit.getPackage() != null &&
                            compilationUnit.getPackage().getName().toString().matches(patternPackage.pattern())) {
                        List<TypeDeclaration> topTypes = (List<TypeDeclaration>) compilationUnit.types();
                        // Get up to level 1 inner classes
                        List<TypeDeclaration> innerTypes = topTypes.stream()
                                .flatMap(type -> Arrays.stream(type.getTypes()))
                                .collect(Collectors.toList());
                        List<TypeDeclaration> types = new ArrayList<>(topTypes);
                        types.addAll(innerTypes);
                        for (TypeDeclaration type : types) {
                            System.out.println("\tClass: " + type.getName().getIdentifier());
                            ClassBean classBean = new ClassBean();
                            classBean.setTypeDeclaration(type);
                            classBean.setSourceFile(projectFile);
                            // Analysis launch
                            for (ClassSmellAnalyzer analyzer : classSmellAnalyzers) {
                                ClassSmell classSmell = analyzer.analyze(classBean);
                                if (classSmell != null) {
                                    classSmells.add(classSmell);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return classSmells;
        }

        private void checkStop() throws StopAnalysisException {
            if (stop) {
                throw new StopAnalysisException(STOP_MESSAGE);
            }
        }
    }

}