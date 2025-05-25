package dev.misei.icloudui.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileRegistry {

    private final File file;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, Object> memory = new ConcurrentHashMap<>();

    public FileRegistry(File file) throws IOException {
        this.file = file;
        load();
    }

    public synchronized void put(String key, Object value) {
        memory.put(key, value);
        save();
    }

    public synchronized Object get(String key) {
        return memory.get(key);
    }

    public synchronized void remove(String key) {
        memory.remove(key);
        save();
    }

    public synchronized Map<String, Object> getAll() {
        return Collections.unmodifiableMap(memory);
    }

    private void load() throws IOException {
        if (file.length() == 0) return;
        Map<String, Object> loaded = mapper.readValue(file, new TypeReference<>() {
        });
        memory.clear();
        memory.putAll(loaded);
    }

    private void save() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, memory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
