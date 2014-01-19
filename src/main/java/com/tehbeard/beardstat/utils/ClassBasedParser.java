/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tehbeard.beardstat.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.tehbeard.beardstat.containers.documents.StatDocument;
import com.tehbeard.utils.factory.ClassCatalogue;
import java.lang.reflect.Type;

/**
 *
 * @author James
 */
public class ClassBasedParser<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    private final ClassCatalogue<T> catalogue;

    public ClassBasedParser(ClassCatalogue<T> catalogue) {
        this.catalogue = catalogue;
    }

    @Override
    public JsonElement serialize(T t, Type type, JsonSerializationContext context) {
        JsonElement element = context.serialize(t, t.getClass());
        element.getAsJsonObject().addProperty("_type", t.getClass().getAnnotation(StatDocument.class).value());
        return element;
    }

    @Override
    public T deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        try {
            String id = element.getAsJsonObject().get("_type").getAsString();
            Class<?> c = catalogue.get(id);
            if (c != null) {
                return context.deserialize(element, c);
            }
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }
        return null;
    }
}
