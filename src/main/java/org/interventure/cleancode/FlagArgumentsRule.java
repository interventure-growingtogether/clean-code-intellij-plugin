package org.interventure.cleancode;

import static org.interventure.cleancode.Constants.INSPECTION_GROUP;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiField;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class FlagArgumentsRule extends AbstractBaseJavaLocalInspectionTool {

    public static final int MAX_FIELD_COUNT = 7;

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        Map<PsiClass, Set<PsiField>> fieldsOfClass = new HashMap<>();

        return new JavaElementVisitor() {
            @Override
            public void visitField(PsiField field) {
                if (isStatic(field)) {
                    return;
                }

                if (field.getContainingClass().isInterface()) {
                    return;
                }

                Set<PsiField> fields = fieldsOfClass.computeIfAbsent(
                        field.getContainingClass(),
                        x -> new HashSet<>());
                fields.add(field);

                if (fields.size() > MAX_FIELD_COUNT) {
                    holder.registerProblem(field.getOriginalElement(), "Too many instance fields in one class.");
                }
            }
        };
    }

    private boolean isStatic(PsiField field) {
        return field.getModifierList() != null && field.getModifierList().hasExplicitModifier("static");
    }

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return INSPECTION_GROUP;
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
        return "Flag Arguments Rule";
    }
    
}
