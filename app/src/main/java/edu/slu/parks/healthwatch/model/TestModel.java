package edu.slu.parks.healthwatch.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by okori on 25-Mar-17.
 */

public class TestModel {

    private ArrayList<Integer> list;

    public TestModel() {
        list = new ArrayList<>();

        String s = "some string";
        s.toUpperCase();

        Map<Integer, String> a = new HashMap<>();
        a.put(1, "test");


        TreeMap<Integer, Object> m = new TreeMap<>();

        m.put(1, 4);
        Iterator b = m.entrySet().iterator();

        Map.Entry<Integer, Object> me = (Map.Entry<Integer, Object>) b.next();
        me.getValue();
    }
}
