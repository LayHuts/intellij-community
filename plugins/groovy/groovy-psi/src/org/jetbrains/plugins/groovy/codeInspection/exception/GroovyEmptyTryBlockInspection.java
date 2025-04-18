/*
 * Copyright 2007-2008 Dave Griffith
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.plugins.groovy.codeInspection.exception;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.GroovyBundle;
import org.jetbrains.plugins.groovy.codeInspection.BaseInspection;
import org.jetbrains.plugins.groovy.codeInspection.BaseInspectionVisitor;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrStatement;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrTryCatchStatement;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrOpenBlock;

public final class GroovyEmptyTryBlockInspection extends BaseInspection {

  @Override
  protected @Nullable String buildErrorString(Object... args) {
    return GroovyBundle.message("inspection.message.empty.ref.block");

  }

  @Override
  public @NotNull BaseInspectionVisitor buildVisitor() {
    return new Visitor();
  }

  private static class Visitor extends BaseInspectionVisitor {
    @Override
    public void visitTryStatement(@NotNull GrTryCatchStatement tryCatchStatement) {
      super.visitTryStatement(tryCatchStatement);
      final GrOpenBlock body = tryCatchStatement.getTryBlock();
      if (body == null || !isEmpty(body)) {
        return;
      }
      registerError(tryCatchStatement.getFirstChild());
    }

    private static boolean isEmpty(GrOpenBlock body) {
      final GrStatement[] statements = body.getStatements();
      return statements.length == 0;
    }
  }
}