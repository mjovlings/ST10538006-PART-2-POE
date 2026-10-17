package com.mycompany.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Stores pending messages in a JSON array, preserving previous messages. */
public class MessageStore {
    private final Path file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public MessageStore(Path file) { this.file = file.toAbsolutePath(); }

    public JsonArray read() throws IOException {
        if (!Files.exists(file)) return new JsonArray();
        try {
            JsonElement root = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8));
            if (!root.isJsonArray()) throw new IOException("Stored messages must be a JSON array.");
            return root.getAsJsonArray();
        } catch (JsonParseException ex) {
            throw new IOException("Stored message file contains invalid JSON; it has not been overwritten.", ex);
        }
    }

    public void append(MessageData message) throws IOException {
        // Gson object conversion and pretty printing adapted from Google (n.d.),
        // Gson User Guide: https://google.github.io/gson/UserGuide.html
        // File replacement uses Java Files API; see docs/REFERENCES.md.
        JsonArray messages = read();
        JsonObject stored = gson.toJsonTree(message).getAsJsonObject();
        stored.addProperty("status", "Stored");
        messages.add(stored);
        Files.createDirectories(file.getParent());
        Path temporary = Files.createTempFile(file.getParent(), "quickchat-", ".tmp");
        try {
            Files.writeString(temporary, gson.toJson(messages), StandardCharsets.UTF_8);
            try {
                Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }
}
