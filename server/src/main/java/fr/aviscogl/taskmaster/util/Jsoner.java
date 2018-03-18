package fr.aviscogl.taskmaster.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

public class Jsoner {
    static Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static <T> Optional<T> getJsonFromFile(File f, Class<T> cl) {
        try {
            JsonReader reader = new JsonReader(new FileReader(f));
            return Optional.ofNullable(gson.fromJson(reader, cl));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

}