package use_case.match;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class MatchOutputDataTest {
    private MatchOutputData outputData;

    @Before
    public void init() {
        HashMap<String, Double> map = new HashMap<>();
        map.put("1", 1.0);
        this.outputData = new MatchOutputData(map);
    }

    @Test
    public void testGetTopSimilarUsers() {
        HashMap<String, Double> map = new HashMap<>();
        map.put("1", 1.0);
        assertEquals(map, outputData.getTopSimilarUsers());
    }
}
