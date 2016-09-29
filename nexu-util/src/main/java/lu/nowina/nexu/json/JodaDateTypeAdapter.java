package lu.nowina.nexu.json;

import com.google.gson.*;
import org.joda.time.LocalDate;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by david on 29/09/2016.
 */
public class JodaDateTypeAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = (JsonObject) jsonElement;
        int year = obj.getAsJsonPrimitive("year").getAsInt();
        int month = obj.getAsJsonPrimitive("month").getAsInt();
        int day = obj.getAsJsonPrimitive("day").getAsInt();
        Calendar cal = Calendar.getInstance();
        cal.set(year,month-1,day);
        return LocalDate.fromDateFields(cal.getTime());
    }

    @Override
    public JsonElement serialize(LocalDate localDate, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject el = new JsonObject();
        el.addProperty("year", localDate.getYear());
        el.addProperty("month", localDate.getMonthOfYear());
        el.addProperty("day", localDate.getDayOfMonth());
        return el;
    }

}
