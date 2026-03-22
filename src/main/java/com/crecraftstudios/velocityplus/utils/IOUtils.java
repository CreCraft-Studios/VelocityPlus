package com.crecraftstudios.velocityplus.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class IOUtils {
    public static JsonObject loadJsonObject(String path) throws IOException {
        File file = new File(path);

        if (file.isFile()) {
            Scanner reader = new Scanner(file);
            String txt = "";

            while (reader.hasNextLine()) {
                txt += reader.nextLine();
            }

            reader.close();
            return JsonParser.parseString(txt).getAsJsonObject();
        } else return new JsonObject();
    }

    public static boolean createDirIfNeeded(String path) throws IOException {
        File file = new File(path);
        if (!Files.exists(file.toPath())) {
            Files.createDirectories(file.toPath().getParent());
            return true;
        }

        return false;
    }

    public static void saveJsonObject(String path, JsonObject json) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(json);

        Path _path = Paths.get(path);
        Files.writeString(_path, jsonString);
    }
}