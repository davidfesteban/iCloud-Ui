package dev.misei.icloudui.model;

import java.io.*;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileBackedCookieStore implements CookieStore {

    private final File file;
    private final Map<URI, List<HttpCookie>> cookieJar = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public FileBackedCookieStore(File file) throws IOException {
        this.file = file;
        if (file.exists() && file.length() > 0) {
            loadFromFile();
        }
    }

    @Override
    public synchronized void add(URI uri, HttpCookie cookie) {
        cookieJar.computeIfAbsent(uri, k -> new ArrayList<>()).add(cookie);
        saveToFile();
    }

    @Override
    public synchronized List<HttpCookie> get(URI uri) {
        return cookieJar.getOrDefault(uri, Collections.emptyList());
    }

    @Override
    public synchronized List<HttpCookie> getCookies() {
        List<HttpCookie> all = new ArrayList<>();
        for (List<HttpCookie> list : cookieJar.values()) {
            all.addAll(list);
        }
        return all;
    }

    @Override
    public synchronized List<URI> getURIs() {
        return new ArrayList<>(cookieJar.keySet());
    }

    @Override
    public synchronized boolean remove(URI uri, HttpCookie cookie) {
        List<HttpCookie> cookies = cookieJar.get(uri);
        if (cookies != null && cookies.remove(cookie)) {
            saveToFile();
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean removeAll() {
        cookieJar.clear();
        saveToFile();
        return true;
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(cookieJar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                cookieJar.clear();
                cookieJar.putAll((Map<URI, List<HttpCookie>>) obj);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
