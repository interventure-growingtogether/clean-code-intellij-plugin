package org.interventure.cleancode;

import com.intellij.psi.PsiMethod;
import com.siyeh.InspectionGadgetsBundle;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.methodmetrics.MethodMetricInspection;
import org.jetbrains.annotations.NotNull;

public class LongMethodRuleInspection
  extends MethodMetricInspection {

  private static final int DEFAULT_LIMIT = 30;

  @Override
  @NotNull
  public String getID() {
    return "CCOverlyLongMethod";
  }

  @Override
  @NotNull
  public String getDisplayName() {
    return "Long Method Rule";
  }

  @Override
  protected int getDefaultLimit() {
    return DEFAULT_LIMIT;
  }

  @Override
  protected String getConfigurationLabel() {
    return "Long Method";
  }

  @Override
  @NotNull
  public String buildErrorString(Object... infos) {
    final Integer statementCount = (Integer)infos[0];
    return InspectionGadgetsBundle.message(
      "non.comment.source.statements.problem.descriptor",
      statementCount);
  }

  @NotNull
  @Override
  public String getShortName() {
    return this.getClass().getName();
  }

  @Override
  public BaseInspectionVisitor buildVisitor() {
    return new NonCommentSourceStatementsMethodVisitor();
  }

  private class NonCommentSourceStatementsMethodVisitor
    extends BaseInspectionVisitor {

    @Override
    public void visitMethod(@NotNull PsiMethod method) {
      // note: no call to super
      if (method.getNameIdentifier() == null) {
        return;
      }
      final NCSSVisitor visitor = new NCSSVisitor();
      method.accept(visitor);
      final int count = visitor.getStatementCount();
      if (count <= getLimit()) {
        return;
      }
      registerMethodError(method, Integer.valueOf(count));
    }
  }
}
