package com.tehbeard.beardstat.containers.documents;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tehbeard.utils.factory.ClassCatalogue;

/**
 *
 * @author James
 */
public class DocumentRegistry {

    private static GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
    private static Gson gson = null;
    private static final ClassCatalogue<IStatDocument> documentCatalogue = new ClassCatalogue<IStatDocument>() {
        @Override
        protected String getTag(Class<? extends IStatDocument> _class) {
            return _class.getAnnotation(StatDocument.class).value();
        }
    };

    public static void registerDocument(Class<? extends IStatDocument> _class) {
        documentCatalogue.addProduct(_class);
    }

    public static void registerDocument(Class<? extends IStatDocument> _class, Object adapater) {
        builder.registerTypeAdapter(_class, adapater);
    }

    public static Gson instance() {
        if (gson == null) {
            gson = builder.registerTypeHierarchyAdapter(IStatDocument.class, new ClassBasedParser<IStatDocument>(documentCatalogue)).create();
        }
        return gson;
    }
    public static void cleanup(){
        builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
        gson = null;
    }
}
