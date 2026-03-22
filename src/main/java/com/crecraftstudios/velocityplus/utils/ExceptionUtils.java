package com.crecraftstudios.velocityplus.utils;

import com.crecraftstudios.velocityplus.VelocityPlus;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {
    public static String createStacktrace(Exception err) {
        StringWriter sw = new StringWriter();
        err.printStackTrace(new PrintWriter(sw));
        return sw.toString();

    }

    public static void printException(Exception err) {
        VelocityPlus.get().logger.error(err.getMessage()+"\nStacktrace: "+ createStacktrace(err));
    }
}