// IGNORE_K1

/**
 * [ba<caret>]
 */
class Foo(bar: String)

// INVOCATION_COUNT: 0
// EXIST: { itemText: "bar", typeText: "String" }