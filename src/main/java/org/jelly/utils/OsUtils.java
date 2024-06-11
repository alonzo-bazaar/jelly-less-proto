package org.jelly.utils;
/*
  helper class to check the operating system this Java VM runs in
  please keep the notes below as a pseudo-license
  http://stackoverflow.com/questions/228477/how-do-i-programmatically-determine-operating-system-in-java
  compare to http://svn.terracotta.org/svn/tc/dso/tags/2.6.4/code/base/common/src/com/tc/util/runtime/Os.java
  http://www.docjar.com/html/api/org/apache/commons/lang/SystemUtils.java.html

  (the class was modified the one found in the original question)
 */
import java.util.Locale;

public final class OsUtils {
    public enum OSType {
        Windows, MacOS, Linux, Other
    };

    // lazy init
    private static OSType detectedOS;
    public static OSType getOs() {
        if (detectedOS == null) {
            detectedOS = detect();
        }
        return detectedOS;
    }

    private static OSType detect() {
        String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if ((os.contains("mac")) || (os.contains("darwin"))) {
            return OSType.MacOS;
        } else if (os.contains("win")) {
            return OSType.Windows;
        } else if (os.contains("nux")) {
            return OSType.Linux;
        } else {
            return OSType.Other;
        }
    }

    public static boolean isWindows() {
        return getOs() == OSType.Windows;
    }
    public static boolean isUnix() {
        OSType os = getOs();
        return os == OSType.MacOS || os == OSType.Linux;
    }
}