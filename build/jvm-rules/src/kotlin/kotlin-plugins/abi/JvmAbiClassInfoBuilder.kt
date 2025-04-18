// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.jetbrains.kotlin.jvm.abi

/**
 * Collects information about kept/stripped/deleted classes, and then adapts it so that it would be consistent if private interfaces
 * are present in the module. The latter is done only if [removePrivateClasses] is enabled.
 *
 * When using this class, call [recordInitialClassInfo]/[addInnerClass] repeatedly until all necessary data is collected, and then call
 * [buildClassInfo] to get the resulting class infos.
 *
 * Private interface is a special case from the ABI standpoint because it is allowed to expose private interface by inheriting a public
 * class from it, see KT-20088. This code keeps all private interfaces which are implemented (perhaps indirectly) by at least one public
 * (in terms of ABI) class in the module. Also, in case such private interface is nested, all its outer classes should be kept but pruned.
 */
internal class JvmAbiClassInfoBuilder(private val removePrivateClasses: Boolean) {
    private val abiClassInfo = mutableMapOf<String, AbiClassInfo>()
    private val innerClassToOuter = mutableMapOf<String, String>()
    private val allSuperInterfaces = mutableMapOf<String, List<String>>()

    fun recordInitialClassInfo(className: String, info: AbiClassInfo, superInterfaces: List<String>) {
        abiClassInfo[className] = info
        if (removePrivateClasses) {
            allSuperInterfaces[className] = superInterfaces
        }
    }

    fun addInnerClass(innerClass: String, outerClass: String?) {
        if (outerClass == null) return
        if (removePrivateClasses) {
            innerClassToOuter.getOrPut(innerClass) { outerClass }
        }
    }

    fun buildClassInfo(): Map<String, AbiClassInfo> {
        if (removePrivateClasses) {
            for ((className, info) in abiClassInfo.toMap()) {
                if (info != AbiClassInfo.Deleted) {
                    keepInterfacesOf(className)
                }
            }
        }

        return abiClassInfo
    }

    private fun keepInterfacesOf(className: String) {
        for (superInterface in allSuperInterfaces[className].orEmpty()) {
            if (abiClassInfo.put(superInterface, AbiClassInfo.Public) == AbiClassInfo.Public)
                continue

            for (outerClass in generateSequence(superInterface, innerClassToOuter::get).drop(1)) {
                if (abiClassInfo[outerClass] == AbiClassInfo.Deleted) {
                    abiClassInfo[outerClass] = AbiClassInfo.Stripped(emptyMap(), prune = true)
                }
            }
            keepInterfacesOf(superInterface)
        }
    }
}
