// "class org.jetbrains.kotlin.idea.quickfix.replaceWith.DeprecatedSymbolUsageFix" "false"
// ERROR: The integer literal does not conform to the expected type Array<out String>
// ERROR: Assigning single elements to varargs in named form is forbidden
// K2_AFTER_ERROR: Argument type mismatch: actual type is 'Int', but 'Array<out String>' was expected.
// K2_AFTER_ERROR: Assigning single elements to varargs in named form is prohibited.

@Deprecated("", ReplaceWith("newFun()", imports = 123))
fun oldFun() {
    newFun()
}

fun newFun(){}

fun foo() {
    <caret>oldFun()
}
