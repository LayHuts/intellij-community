// "Terminate preceding call with semicolon" "true"

fun foo() {}

fun test {
    foo()
    // comment and formatting
    { { { foo() }<caret> } }()()()
}

// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.AddSemicolonBeforeLambdaExpressionFix
// FUS_K2_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.AddSemicolonBeforeLambdaExpressionFix