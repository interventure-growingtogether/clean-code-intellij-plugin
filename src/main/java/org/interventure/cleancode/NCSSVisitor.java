package org.interventure.cleancode;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

class NCSSVisitor extends JavaRecursiveElementWalkingVisitor {
  private int m_statementCount;

  @Override
  public void visitAnonymousClass(@NotNull PsiAnonymousClass aClass) {
    // no call to super, to keep this from drilling down
  }

  @Override
  public void visitStatement(@NotNull PsiStatement statement) {
    super.visitStatement(statement);
    if (statement instanceof PsiEmptyStatement ||
        statement instanceof PsiBlockStatement) {
      return;
    }
    m_statementCount++;
  }

  int getStatementCount() {
    return m_statementCount;
  }
}