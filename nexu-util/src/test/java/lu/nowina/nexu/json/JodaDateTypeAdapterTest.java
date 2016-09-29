package lu.nowina.nexu.json;

import com.google.gson.JsonElement;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by david on 29/09/2016.
 */
public class JodaDateTypeAdapterTest {

    @Test
    public void test1() throws Exception {

        JodaDateTypeAdapter adapter = new JodaDateTypeAdapter();

        LocalDate now = LocalDate.now();

        JsonElement el = adapter.serialize(now, null, null);
        LocalDate date = adapter.deserialize(el,null,null);

        Assert.assertEquals(now, date);
    }

}
