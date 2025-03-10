package com.jetbrains.python.psi.types

import com.jetbrains.python.psi.PyCallSiteExpression
import com.jetbrains.python.psi.PyClass
import com.jetbrains.python.psi.PyElement
import com.jetbrains.python.psi.PyReferenceExpression
import com.jetbrains.python.psi.impl.PyBuiltinCache
import org.jetbrains.annotations.ApiStatus

/**
 * Class is used for representing TypeGuard and TypeIs behavior
 */
@ApiStatus.Internal
class PyNarrowedType private constructor(
  pyClass: PyClass,
  val qname: String?,
  val original: PyCallSiteExpression?,
  val negated: Boolean,
  val typeIs: Boolean,
  val narrowedType: PyType?,
) : PyClassTypeImpl(pyClass, false) {

  fun negate(): PyNarrowedType {
    return PyNarrowedType(pyClass, qname, original, !negated, typeIs, narrowedType)
  }

  fun bind(callExpression: PyCallSiteExpression, name: String): PyNarrowedType {
    return PyNarrowedType(pyClass, name, callExpression, negated, typeIs, narrowedType)
  }

  fun substitute(narrowedTypeSubstitution: PyType?): PyNarrowedType {
    return PyNarrowedType(pyClass, qname, original, negated, typeIs, narrowedTypeSubstitution)
  }

  /**
   * A type is considered bound if it has an associated original call site expression,
   * indicating that it is valid only within the call-site scope.
   */
  fun isBound(): Boolean = original != null

  override fun toString(): String = "PyNarrowedType: ${narrowedType?.name ?: "Any"}"

  override fun equals(o: Any?): Boolean {
    if (this === o) return true
    if (javaClass != o?.javaClass) return false
    if (!super.equals(o)) return false

    o as PyNarrowedType

    if (negated != o.negated) return false
    if (typeIs != o.typeIs) return false
    if (qname != o.qname) return false
    if (original != o.original) return false
    if (narrowedType != o.narrowedType) return false

    return true
  }

  override fun hashCode(): Int {
    var result = super.hashCode()
    result = 31 * result + negated.hashCode()
    result = 31 * result + typeIs.hashCode()
    result = 31 * result + (qname?.hashCode() ?: 0)
    result = 31 * result + (original?.hashCode() ?: 0)
    result = 31 * result + (narrowedType?.hashCode() ?: 0)
    return result
  }

  companion object {
    fun create(anchor: PyElement, typeIs: Boolean, returnType: PyType?): PyNarrowedType? {
      val pyClass = PyBuiltinCache.getInstance(anchor).getClass("bool")
      if (pyClass == null) return null
      return PyNarrowedType(pyClass, null, null, false, typeIs, returnType)
    }

    fun bindIfNeeded(type: PyType?, callSiteExpression: PyCallSiteExpression?): PyType? {
      if (type is PyNarrowedType && callSiteExpression != null) {
        val arguments = callSiteExpression.getArguments(null)
        val pyReferenceExpression = arguments.firstOrNull()
        if (pyReferenceExpression is PyReferenceExpression) {
          val qname = pyReferenceExpression.asQualifiedName()
          if (qname != null) {
            return type.bind(callSiteExpression, qname.toString())
          }
        }
      }
      return type
    }
  }

  override fun <T : Any?> acceptTypeVisitor(visitor: PyTypeVisitor<T?>): T? {
    if (visitor is PyTypeVisitorExt) {
      return visitor.visitPyNarrowedType(this)
    }
    return visitor.visitPyClassType(this)
  }

}