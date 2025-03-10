// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.xdebugger.attach;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.BaseProcessHandler;
import com.intellij.execution.process.CapturingProcessRunner;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.xdebugger.attach.osHandlers.AttachOSHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

/**
 * This abstract class represent {@link XAttachHost} with extended functional, such as executing {@link GeneralCommandLine},
 * downloading files and getting OS of a host
 */
public abstract class EnvironmentAwareHost implements XAttachHost {

  private AttachOSHandler myOsHandler = null;

  /**
   * @param commandLine commandLine to execute on this host
   * @return {@link BaseProcessHandler}, with which the command is executed (for example with a timeout)
   */
  public abstract @NotNull BaseProcessHandler getProcessHandler(@NotNull GeneralCommandLine commandLine) throws ExecutionException;

  /**
   * @param commandLine commandLine to execute on this host
   * @return output of the corresponding process
   */
  public @NotNull ProcessOutput getProcessOutput(@NotNull GeneralCommandLine commandLine) throws ExecutionException {
    BaseProcessHandler handler = getProcessHandler(commandLine);
    CapturingProcessRunner runner = new CapturingProcessRunner(handler);
    return runner.runProcess();
  }

  public @NotNull AttachOSHandler getOsHandler() {
    if(myOsHandler == null) {
      myOsHandler = AttachOSHandler.getAttachOsHandler(this);
    }

    return myOsHandler;
  }

  /**
   * Retrieves file contents stream. May be used to sync parts of the debugged project.
   *
   * @param filePath path of the file on host machine
   * @return stream with file contents or <code>null</code> if the specified file does not exist
   * @throws IOException on stream retrieval error
   */
  public abstract @Nullable InputStream getFileContent(@NotNull String filePath) throws IOException;

  /**
   * Check if it is possible to read the file on host machine
   *
   * @param filePath path of the file on host machine
   * @throws ExecutionException on stream retrieval error
   */
  public abstract boolean canReadFile(@NotNull String filePath) throws ExecutionException;

  /**
   * File system prefix for files from this host. It should be noted that the prefixes must be different for different hosts.
   * Path to the host file is obtained by concatenation of hostId and it's on-host path
   */
  public abstract @NotNull String getFileSystemHostId();

  /**
   * @param credentialsObject is a parametrization of a host
   * @return whether the given credentials corresponds the same host
   */
  public boolean isSameHost(final @NotNull Object credentialsObject) {
    return false;
  }
}
