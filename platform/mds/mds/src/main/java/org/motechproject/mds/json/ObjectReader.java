package org.motechproject.mds.json;

import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.util.Constants;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The <code>ObjectReader</code> class is a wrapper for JsonReader that provides methods that performs common
 * reader tasks such as array deserialization or parsing objects.
 *
 * @see com.google.gson.stream.JsonReader
 */
public class ObjectReader {

    private JsonReader jsonReader;

    public ObjectReader(JsonReader jsonReader) {
        this.jsonReader = jsonReader;
    }

    public <T> T readObject(Class<T> type) throws IOException { // NO CHECKSTYLE Cyclomatic Complexity is 13 (max allowed is 10).
        if (type.isAssignableFrom(String.class)) {
            return (T) readString();
        } else if (type.isAssignableFrom(Boolean.class)) {
            return (T) readBoolean();
        } else if (type.isAssignableFrom(Integer.class)) {
            return (T) readInteger();
        } else if (type.isAssignableFrom(Long.class)) {
            return (T) readLong();
        } else if (type.isAssignableFrom(Double.class)) {
            return (T) readDouble();
        } else if (type.isAssignableFrom(Period.class)) {
            return (T) readPeriod();
        } else if (type.isAssignableFrom(Date.class)) {
            return (T) readDate();
        } else if (type.isAssignableFrom(Time.class)) {
            return (T) readTime();
        } else if (type.isAssignableFrom(DateTime.class)) {
            return (T) readDateTime();
        } else if (type.isAssignableFrom(LocalDate.class)) {
            return (T) readLocalDate();
        } else if (type.isAssignableFrom(Locale.class)) {
            return (T) readLocale();
        } else if (type.isEnum()) {
            return (T) readEnum((Class<? extends Enum>) type);
        } else {
            throw new IllegalArgumentException(type.getName() + " is not supported");
        }
    }

    public boolean isNextNull() throws IOException {
        return JsonToken.NULL == jsonReader.peek();
    }

    public <T> T readNull() throws IOException {
        jsonReader.nextNull();
        return null;
    }

    public String readString() throws IOException {
        return isNextNull() ? (String) readNull() : jsonReader.nextString();
    }

    public Boolean readBoolean() throws IOException {
        return isNextNull() ? (Boolean) readNull() : (Boolean) jsonReader.nextBoolean();
    }

    public Integer readInteger() throws IOException {
        return isNextNull() ? (Integer) readNull() : (Integer) jsonReader.nextInt();
    }

    public Long readLong() throws IOException {
        return isNextNull() ? (Long) readNull() : (Long) jsonReader.nextLong();
    }

    public Double readDouble() throws IOException {
        return isNextNull() ? (Double) readNull() : (Double) jsonReader.nextDouble();
    }

    public Period readPeriod() throws IOException {
        return isNextNull() ? (Period) readNull() : new Period(jsonReader.nextString());
    }

    public Object readDate() throws IOException {
        try {
            return isNextNull() ? readNull() : Constants.Util.DEFAULT_DATE_FORMAT.parse(jsonReader.nextString());
        } catch (ParseException e) {
            throw new UnsupportedOperationException("Cannot parse date", e);
        }
    }

    public Time readTime() throws IOException {
        return isNextNull() ? (Time) readNull() : new Time(jsonReader.nextString());
    }

    public DateTime readDateTime() throws IOException {
        return isNextNull() ? (DateTime) readNull() : new DateTime(jsonReader.nextString());
    }

    public LocalDate readLocalDate() throws IOException {
        return isNextNull() ? (LocalDate) readNull() : new LocalDate(jsonReader.nextString());
    }

    public Locale readLocale() throws IOException {
        return isNextNull() ? (Locale) readNull() : LocaleUtils.toLocale(jsonReader.nextString());
    }

    public <T extends Enum<T>> T readEnum(Class<T> type) throws IOException {
        return isNextNull() ? (T) readNull() : Enum.valueOf(type, jsonReader.nextString());
    }

    public String readName() throws IOException {
        return isNextNull() ? (String) readNull() : jsonReader.nextName();
    }

    public void expect(String name) throws IOException {
        if (!StringUtils.equals(name, readName())) {
            throw new JsonParseException("Invalid json format!");
        }
    }

    public void expectAndSkip(String name) throws IOException {
        expect(name);
        jsonReader.skipValue();
    }

    public void expectAndSkipIfExists(String name) throws IOException {
        if (jsonReader.hasNext()) {
            expectAndSkip(name);
        }
    }

    public boolean readBoolean(String name) throws IOException {
        expect(name);
        return readBoolean();
    }

    public String readString(String name) throws IOException {
        expect(name);
        return readString();
    }

    public <T extends Enum<T>> T readEnum(String name, Class<T> type) throws IOException {
        expect(name);
        return readEnum(type);
    }

    public List<String> readStringArray(String name) throws IOException {
        expect(name);
        if (!isNextNull()) {
            List<String> list = new ArrayList<>();
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                list.add(readString());
            }
            jsonReader.endArray();
            return list;
        } else {
            jsonReader.nextNull();
            return null;
        }
    }

    public List<Long> readLongArray() throws IOException {
        if (!isNextNull()) {
            List<Long> list = new ArrayList<>();
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                list.add(readLong());
            }
            jsonReader.endArray();
            return list;
        } else {
            jsonReader.nextNull();
            return null;
        }
    }

    public <T> List<T> readList(Class<T> type) throws IOException {
        if (!isNextNull()) {
            List<T> list = new ArrayList<>();
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                list.add(readObject(type));
            }
            jsonReader.endArray();
            return list;
        } else {
            return readNull();
        }
    }



    public Map<String, String> readStringMap() throws IOException {
        if (!isNextNull()) {
            Map<String, String> map = new HashMap<>();
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String key = readName();
                String value = readString();
                map.put(key, value);
            }
            jsonReader.endObject();
            return map;
        } else {
            jsonReader.nextNull();
            return null;
        }
    }

    public Map<String, String> readStringMap(String name) throws IOException {
        expect(name);
        return readStringMap();
    }

    public Map<String, Boolean> readBooleanMap(String name) throws IOException {
        expect(name);
        if (!isNextNull()) {
            Map<String, Boolean> map = new HashMap<>();
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String key = readName();
                Boolean value = readBoolean();
                map.put(key, value);
            }
            jsonReader.endObject();
            return map;
        } else {
            jsonReader.nextNull();
            return null;
        }
    }
}
