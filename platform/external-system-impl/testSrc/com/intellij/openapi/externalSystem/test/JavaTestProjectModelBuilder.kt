// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.openapi.externalSystem.test

import com.intellij.platform.externalSystem.testFramework.*
import com.intellij.externalSystem.JavaProjectData
import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.pom.java.LanguageLevel
import com.intellij.util.execution.ParametersListUtil

class JavaProject : AbstractNode<JavaProjectData>("javaProject") {
  var compileOutputPath: String
    get() = props["compileOutputPath"]!!
    set(value) {
      props["compileOutputPath"] = value
    }
  var languageLevel: LanguageLevel?
    get() = props["languageLevel"]?.run { LanguageLevel.valueOf(this) }
    set(value) {
      if (value == null) props.remove("languageLevel")
      else props["languageLevel"] = value.name
    }
  var targetBytecodeVersion: String?
    get() = props["targetBytecodeVersion"]
    set(value) {
      if (value == null) props.remove("targetBytecodeVersion")
      else props["targetBytecodeVersion"] = value
    }
  var compilerArguments: List<String>
    get() = ParametersListUtil.parse(props["compilerArguments"]!!)
    set(value) {
      props["compilerArguments"] =  ParametersListUtil.join(value)
    }

  override fun createDataNode(parentData: Any?): DataNode<JavaProjectData> {
    val javaProjectData = JavaProjectData(systemId, compileOutputPath, languageLevel, targetBytecodeVersion, compilerArguments)
    return DataNode(JavaProjectData.KEY, javaProjectData, null)
  }
}

fun Project.javaProject(
  compileOutputPath: String,
  languageLevel: LanguageLevel? = null,
  targetBytecodeVersion: String? = null,
  compilerArguments: List<String> = emptyList(),
): JavaProject {
  return initChild(JavaProject()) {
    this.compileOutputPath = compileOutputPath
    this.languageLevel = languageLevel
    this.targetBytecodeVersion = targetBytecodeVersion
    this.compilerArguments = compilerArguments
  }
}

