package vmlens;

import com.vmlens.api.AllInterleavings;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUpdateWrong {
    public void update(ConcurrentHashMap<Integer, Integer> map) {
        Integer result = map.get(1);
        if (result == null) {
            map.put(1, 1);
        } else {
            map.put(1, result + 1);
        }
    }

    @Test
    public void testUpdate() throws InterruptedException {
        try (AllInterleavings allInterleavings =
                     new AllInterleavings("TestUpdateWrong");) {
            // surround the test with a while loop, iterationg over
            // the class AllInterleavings
            while (allInterleavings.hasNext()) {
                final ConcurrentHashMap<Integer, Integer> map =
                        new ConcurrentHashMap<Integer, Integer>();
                Thread first = new Thread(() -> {
                    update(map);
                });
                Thread second = new Thread(() -> {
                    update(map);
                });
                first.start();
                second.start();
                first.join();
                second.join();
                assertEquals(2, map.get(1).intValue());
            }
        }
    }
}