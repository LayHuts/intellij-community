// "Add constructor parameters from A(vararg Int)" "true"
// IGNORE_K1
open class A(vararg i: Int)

class B(i: Int) : A<caret>
// FUS_K2_QUICKFIX_NAME: org.jetbrains.kotlin.idea.k2.codeinsight.fixes.SuperClassNotInitializedFactories$AddParametersFix