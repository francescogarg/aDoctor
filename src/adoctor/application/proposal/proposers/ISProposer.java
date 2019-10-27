package adoctor.application.proposal.proposers;

import adoctor.application.smell.ClassSmell;
import adoctor.application.smell.ISSmell;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

@SuppressWarnings("Duplicates")
public class ISProposer extends ClassSmellProposer {
    @Override
    public ASTRewrite computeProposal(ClassSmell classSmell) {
        if (classSmell == null) {
            return null;
        }
        if (!(classSmell instanceof ISSmell)) {
            return null;
        }
        ISSmell isSmell = (ISSmell) classSmell;
        MethodInvocation smellyCall = isSmell.getSmellyCall();
        Pair<MethodDeclaration, String> smellySetter = isSmell.getSmellySetter();
        if (smellyCall == null || smellySetter == null) {
            return null;
        }
        AST targetAST = smellyCall.getAST();

        Assignment newAssignment = targetAST.newAssignment();
        FieldAccess fieldAccess = targetAST.newFieldAccess();
        fieldAccess.setExpression(targetAST.newThisExpression());
        fieldAccess.setName(targetAST.newSimpleName(smellySetter.getValue()));
        newAssignment.setLeftHandSide(fieldAccess);
        newAssignment.setOperator(Assignment.Operator.ASSIGN);
        newAssignment.setRightHandSide((Expression) ASTNode.copySubtree(targetAST, (ASTNode) smellyCall.arguments().get(0)));
        ASTRewrite astRewrite = ASTRewrite.create(targetAST);
        astRewrite.replace(smellyCall, newAssignment, null);
        return astRewrite;
    }
}
