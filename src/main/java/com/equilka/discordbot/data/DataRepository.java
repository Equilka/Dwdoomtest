package com.equilka.discordbot.data;

import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataRepository {
    private File dataFile;
    private JsonArray data;
    private final Gson gson;

    public DataRepository(String path) {
        File dataFolder = new File(System.getProperty("user.dir"), "/data/");
        if (!dataFolder.exists()) dataFolder.mkdirs();

        this.dataFile = new File(dataFolder, path);
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .create();
    }

    public void load() {
        if(!dataFile.exists() || dataFile.length() == 0) {
            data = new JsonArray();
            try {
                String folder = dataFile.getParent();
                new File(folder).mkdirs();
                dataFile.createNewFile();
                save();
            } catch (IOException e) {
                throw new RuntimeException("Failed to load file", e);
            }
            return;
        }

        try (Reader reader = new FileReader(dataFile)) {
            data = JsonParser.parseReader(reader).getAsJsonArray();
        } catch (IOException e) {
            data = new JsonArray();
            throw new RuntimeException("Failed to load data from file", e);
        }
    }

    public void save() {
        try (Writer writer = new FileWriter(dataFile)){
            gson.toJson(data, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save data to file", e);
        }
    }

    public void add(BotData element) {
        JsonElement jsonElement = gson.toJsonTree(element);
        data.add(jsonElement);
    }

    public void add(JsonElement jsonElement) {
        data.add(jsonElement);
    }

    public <T extends BotData> void removeById(String id, Class<T> tClass) {
        Optional<T> object = findRecursive(id, tClass);
        data.remove(gson.toJsonTree(object.get()));
    }

    public <T extends BotData> List<T> getAll(Class<T> tClass) {
        List<T> list = new ArrayList<>();
        if (data == null || data.isEmpty())
            return new ArrayList<>();

        for (JsonElement element : data) {
            T obj = gson.fromJson(element, tClass);
            list.add(obj);
        }
        return list;
    }

    public <T extends BotData> T getObject(String id, Class<T> tClass) {
        Optional<T> object = findRecursive(id, tClass);
        return object.orElse(null);
    }

    public <T extends BotData> void rewriteAll(List<T> list, Class<T> tClass) {
        for (T object : list) {
            JsonElement newData = gson.toJsonTree(object);
            String id = object.getId();
            boolean rewrited = false;
            for (int i = 0; i < data.size(); i++) {
                JsonElement oldData = data.get(i);
                if (!oldData.isJsonObject()) continue;

                JsonObject oldObject = oldData.getAsJsonObject();
                if (oldObject.has("id") && id.equals(oldObject.get("id").getAsString())) {
                    data.set(i, newData);
                    rewrited = true;
                    break;
                }
            }

            if (!rewrited) {
                data.add(newData);
            }
        }
    }

    private <T extends BotData> Optional<T> findRecursive(String id, Class<T> tClss) {
        return recursive(data, id, tClss);
    }

    private <T extends BotData> Optional<T> recursive(JsonArray array, String id, Class<T> tClass) {
        for (JsonElement element : array) {
            T obj = gson.fromJson(element, tClass);
            if (obj.getId().equals(id))
                return Optional.of(obj);

            JsonObject jsonObject = element.getAsJsonObject();
            if (jsonObject.has("children") && jsonObject.get("children").isJsonArray()) {
                Optional<T> found = recursive(jsonObject.getAsJsonArray("children"), id, tClass);
                if (found.isPresent())
                    return found;
            }
        }
        return Optional.empty();
    }

    public void delete() throws IOException {
        if(dataFile.exists())
            Files.delete(dataFile.toPath());
        else
            throw new RuntimeException("Failed to delete file");
    }

    public String getPath() {
        return dataFile.getPath();
    }

    public File getFile() {
        return dataFile.getAbsoluteFile();
    }

    private static class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd - HH:mm");

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(FORMATTER));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), FORMATTER);
        }
    }
}
