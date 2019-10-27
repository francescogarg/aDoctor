package adoctor.application.ast;

import adoctor.application.ast.visitor.*;
import org.eclipse.jdt.core.dom.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// TODO High remove every useless methods. This must be made simpler!

public class ASTUtilities {

    public static CompilationUnit getCompilationUnit(File sourceFile, String[] pathEntries) throws IOException {
        /*
        CodeParser codeParser = new CodeParser();
        String javaFileContent = FileUtilities.readFile(sourceFile.getAbsolutePath());
        return codeParser.createParser(javaFileContent);
         */
        //IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        //String javaFileContent = FileUtilities.readFile(sourceFile.getAbsolutePath());

        ASTParser parser = ASTParser.newParser(AST.JLS11);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        String javaFileContent = new String(Files.readAllBytes(Paths.get(sourceFile.getAbsolutePath())), StandardCharsets.UTF_8);
        parser.setSource(javaFileContent.toCharArray());
        parser.setEnvironment(pathEntries, pathEntries, null, true);
        parser.setUnitName(sourceFile.getAbsolutePath());
        parser.setResolveBindings(true);
        return (CompilationUnit) parser.createAST(null);
    }

    public static MethodDeclaration getMethodDeclarationFromName(CompilationUnit compilationUnit, String methodName) {
        TypeDeclaration typeDeclaration = (TypeDeclaration) compilationUnit.types().get(0);
        List<MethodDeclaration> methodDeclarationList;
        // Fetch all MethodDeclarations of the class with an AST visitor of aDoctor
        // typeDeclaration.accept(new MethodVisitor(methodDeclarationList));
        methodDeclarationList = getMethodDeclarations(typeDeclaration);

        // Fetch the correct MethodDeclaration through a comparison with the content of the MethodBean parameter
        int i = 0;
        int methodDeclarationListSize = methodDeclarationList.size();
        boolean found = false;
        while (i < methodDeclarationListSize && !found) {
            if (methodDeclarationList.get(i).getName().toString().equals(methodName)) {
                found = true;
            } else {
                i++;
            }
        }
        if (found) {
            return methodDeclarationList.get(i);
        } else {
            return null;
        }
    }

    public static Block getBlockFromContent(MethodDeclaration methodDeclaration, String blockContent) {
        // Fetch all Blocks
        ArrayList<Block> blockList = new ArrayList<>();
        methodDeclaration.accept(new BlockVisitor(blockList));
        int i = 0;
        int blockListSize = blockList.size();
        while (i < blockListSize && !blockList.get(i).toString().equals(blockContent)) {
            i++;
        }
        if (i < blockListSize) {
            return blockList.get(i);
        } else {
            return null;
        }
    }

    public static FieldDeclaration getFieldDeclarationFromName(CompilationUnit compilationUnit, String variableName) {
        // Fetch all FieldDeclaration of the class
        TypeDeclaration typeDeclaration = (TypeDeclaration) compilationUnit.types().get(0);
        ArrayList<FieldDeclaration> fieldDeclarationList = new ArrayList<>();
        typeDeclaration.accept(new FieldDeclarationVisitor(fieldDeclarationList));
        for (FieldDeclaration fieldDeclaration : fieldDeclarationList) {
            List fragments = fieldDeclaration.fragments();
            if (fragments != null) {
                for (Object fragment : fragments) {
                    VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragment;
                    if (variableDeclarationFragment.getName().toString().equals(variableName)) {
                        return fieldDeclaration;
                    }
                }
            }
        }
        return null;
    }

    public static ExpressionStatement getExpressionStatementFromContent(MethodDeclaration methodDeclaration, String statementContent) {
        // Fetch all ExpressionStatments
        ArrayList<ExpressionStatement> statementList = new ArrayList<>();
        methodDeclaration.accept(new ExpressionStatementVisitor(statementList));
        int i = 0;
        int statementListSize = statementList.size();
        while (i < statementListSize && !statementList.get(i).toString().equals(statementContent)) {
            i++;
        }
        if (i < statementListSize) {
            return statementList.get(i);
        } else {
            return null;
        }
    }

    public static List<MethodDeclaration> getMethodDeclarations(ASTNode node) {
        if (node == null) {
            return null;
        } else {
            ArrayList<MethodDeclaration> methodDeclarations = new ArrayList<>();
            node.accept(new MethodDeclarationVisitor(methodDeclarations));
            return methodDeclarations;
        }
    }

    public static String getCallerName(Statement statement, String methodName) {
        if (statement == null || methodName == null) {
            return null;
        }
        if (statement instanceof ExpressionStatement) {
            ExpressionStatement expressionStatement = (ExpressionStatement) statement;
            Expression expression = expressionStatement.getExpression();
            if (expression instanceof MethodInvocation) {
                MethodInvocation methodInvocation = (MethodInvocation) expression;
                // If there is an explicit caller
                if (methodInvocation.getExpression() != null) {
                    if (methodInvocation.getName().toString().equals(methodName)) {
                        return methodInvocation.getExpression().toString();
                    }
                }
            }
        }
        return null;
    }

    public static ArrayList<Block> getBlocks(ASTNode node) {
        if (node == null) {
            return null;
        } else {
            ArrayList<Block> blockList = new ArrayList<>();
            node.accept(new BlockVisitor(blockList));
            return blockList;
        }
    }

    public static List<VariableDeclarationStatement> getVariableDeclarationStatements(ASTNode node) {
        if (node == null) {
            return null;
        } else {
            ArrayList<VariableDeclarationStatement> variableDeclarationStatements = new ArrayList<>();
            node.accept(new VariableDeclarationStatementVisitor(variableDeclarationStatements));
            return variableDeclarationStatements;
        }
    }

    public static List<MethodInvocation> getMethodInvocations(ASTNode node) {
        if (node == null) {
            return null;
        } else {
            ArrayList<MethodInvocation> methodInvocations = new ArrayList<>();
            node.accept(new MethodInvocationVisitor(methodInvocations));
            return methodInvocations;
        }
    }

    public static List<FieldDeclaration> getFieldDeclarations(ASTNode node) {
        if (node == null) {
            return null;
        } else {
            ArrayList<FieldDeclaration> fieldDeclarations = new ArrayList<>();
            node.accept(new FieldDeclarationVisitor(fieldDeclarations));
            return fieldDeclarations;
        }
    }

    public static List<ClassInstanceCreation> getClassInstanceCreations(ASTNode node) {
        if (node == null) {
            return null;
        } else {
            ArrayList<ClassInstanceCreation> classInstanceCreations = new ArrayList<>();
            node.accept(new ClassInstanceCreationVisitor(classInstanceCreations));
            return classInstanceCreations;
        }
    }

    public static List<SimpleName> getSimpleNames(ASTNode node) {
        if (node == null) {
            return null;
        } else {
            ArrayList<SimpleName> simpleNames = new ArrayList<>();
            node.accept(new SimpleNameVisitor(simpleNames));
            return simpleNames;
        }
    }

    public static List<ThisExpression> getThisExpressions(ASTNode node) {
        if (node == null) {
            return null;
        } else {
            ArrayList<ThisExpression> thisExpressions = new ArrayList<>();
            node.accept(new ThisExpressionVisitor(thisExpressions));
            return thisExpressions;
        }
    }

    public static List<SuperMethodInvocation> getSuperMethodInvocations(ASTNode node) {
        if (node == null) {
            return null;
        } else {
            ArrayList<SuperMethodInvocation> superMethodInvocations = new ArrayList<>();
            node.accept(new SuperMethodInvocationVisitor(superMethodInvocations));
            return superMethodInvocations;
        }
    }

    public static List<SuperFieldAccess> getSuperFieldAccess(ASTNode node) {
        if (node == null) {
            return null;
        } else {
            ArrayList<SuperFieldAccess> superFieldAccesses = new ArrayList<>();
            node.accept(new SuperFieldAccessVisitor(superFieldAccesses));
            return superFieldAccesses;
        }
    }

    public static Block getParentBlock(ASTNode node) {
        ASTNode parent = node;
        while (!(parent instanceof Block)) {
            parent = parent.getParent();
        }
        return (Block) parent;
    }

    public static TypeDeclaration getParentTypeDeclaration(ASTNode node) {
        ASTNode parent = node;
        while (!(parent instanceof TypeDeclaration)) {
            parent = parent.getParent();
        }
        return (TypeDeclaration) parent;
    }
}

