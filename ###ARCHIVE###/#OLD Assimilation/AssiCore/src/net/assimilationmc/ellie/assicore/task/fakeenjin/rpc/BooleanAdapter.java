package net.assimilationmc.ellie.assicore.task.fakeenjin.rpc;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created by Ellie on 22/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class BooleanAdapter  extends TypeAdapter<Boolean> {

    public void write(JsonWriter out, Boolean value)
            throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value);
        }
    }

    public Boolean read(JsonReader in) throws IOException {
        JsonToken peek = in.peek();
        switch (peek) {
            case BOOLEAN:
                return in.nextBoolean();
            case NULL:
                in.nextNull();
                return null;
            case NUMBER:
                return in.nextInt() != 0;
            case STRING:
                return in.nextString().equalsIgnoreCase("1");
        }
        throw new IllegalStateException("Expected BOOLEAN or NUMBER but was " + peek);
    }

}

