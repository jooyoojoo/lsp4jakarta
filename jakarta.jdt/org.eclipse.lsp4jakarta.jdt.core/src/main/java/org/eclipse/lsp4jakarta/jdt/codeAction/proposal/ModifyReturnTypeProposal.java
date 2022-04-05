package org.eclipse.lsp4jakarta.jdt.codeAction.proposal;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.core.manipulation.dom.ASTResolving;
import org.eclipse.lsp4j.CodeActionKind;

public class ModifyReturnTypeProposal extends ChangeCorrectionProposal {
    
    private final CompilationUnit invocationNode;
    private final IBinding binding;
    private final Type newReturnType;

    public ModifyReturnTypeProposal(String label, ICompilationUnit targetCU, CompilationUnit invocationNode, 
            IBinding binding, int relevance, Type newReturnType) {
        super(label, CodeActionKind.QuickFix, targetCU, null, relevance);
        this.invocationNode = invocationNode;
        this.binding = binding;
        this.newReturnType = newReturnType;
    }
    
    @SuppressWarnings("restriction")
    @Override
    protected ASTRewrite getRewrite() {
        ASTNode declNode = null;
        ASTNode boundNode = invocationNode.findDeclaringNode(binding);
        CompilationUnit newRoot = invocationNode;
        
        if (boundNode != null) {
            declNode = boundNode;
        } else {
            newRoot = ASTResolving.createQuickFixAST(getCompilationUnit(), null);
            declNode = newRoot.findDeclaringNode(binding.getKey());
        }
        
        if (declNode.getNodeType() == ASTNode.METHOD_DECLARATION) {
            AST ast = declNode.getAST();
            ASTRewrite rewrite = ASTRewrite.create(ast);
            rewrite.set(declNode, MethodDeclaration.RETURN_TYPE2_PROPERTY, newReturnType, null);
            return rewrite;
        }
        return null;
    }
}