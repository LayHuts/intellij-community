// WITH_STDLIB
// ERROR: Type mismatch: inferred type is Array<Int> but Iterable<TypeVariable(R)> was expected
fun test() {
    arrayOf(arrayOf(1, 2), arrayOf(3)).flatMap<caret> { it }
}