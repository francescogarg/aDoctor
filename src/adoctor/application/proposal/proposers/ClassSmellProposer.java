package adoctor.application.proposal.proposers;

import adoctor.application.smell.ClassSmell;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

public abstract class ClassSmellProposer {
    public abstract ASTRewrite computeProposal(ClassSmell classSmell);
}
