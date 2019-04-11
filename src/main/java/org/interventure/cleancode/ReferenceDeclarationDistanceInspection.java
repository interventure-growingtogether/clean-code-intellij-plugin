package org.interventure.cleancode;

import static com.intellij.psi.controlFlow.DefUseUtil.getRefs;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiLocalVariable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class ReferenceDeclarationDistanceInspection extends AbstractBaseJavaLocalInspectionTool {

    public static final int MAX_DISTANCE = 40;

    private static PsiCodeBlock lookupCodeBlock(PsiElement element) {
        PsiElement context = element.getContext();
        return context instanceof PsiCodeBlock ? (PsiCodeBlock) context : lookupCodeBlock(context);
    }

    private static int getLineNumber(PsiElement element) {
        Document document = PsiDocumentManager
                .getInstance(element.getProject())
                .getDocument(element.getContainingFile());

        return document.getLineNumber(element.getTextOffset());
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {

            @Override
            public void visitLocalVariable(PsiLocalVariable variable) {
                PsiCodeBlock codeBlock = lookupCodeBlock(variable);
                if (codeBlock == null || variable.getInitializer() == null) {
                    return;
                }

                int variableLineNum = getLineNumber(variable);

                for (PsiElement ref : getRefs(codeBlock, variable, variable.getInitializer())) {
                    if (getLineNumber(ref) - variableLineNum > MAX_DISTANCE) {
                        holder.registerProblem(ref.getOriginalElement(), "Reference is too far away from declaration. The declaration and reference statements should be roughly in the same screen.", ProblemHighlightType.WEAK_WARNING);
                    }
                }
            }

        };
    }

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return Constants.INSPECTION_GROUP;
    }


    @NotNull
    @Override
    public String getShortName() {
        return this.getClass().getName();
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Scope Rule";
    }

}
