package com.crecraftstudios.velocitycore.utils;

import com.crecraftstudios.velocitycore.VelocityCore;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {
    public static String createStacktrace(Exception err) {
        StringWriter sw = new StringWriter();
        err.printStackTrace(new PrintWriter(sw));
        return sw.toString();

    }

    public static void printException(Exception err) {
        VelocityCore.get().logger.error("{}\nStacktrace: {}", err.getMessage(), createStacktrace(err));
    }
}