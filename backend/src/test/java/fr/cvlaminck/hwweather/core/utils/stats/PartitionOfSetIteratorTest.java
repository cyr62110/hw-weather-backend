package fr.cvlaminck.hwweather.core.utils.stats;

import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class PartitionOfSetIteratorTest {

    @Test
    public void testNEquals3() throws Exception {
        Set<Integer> sourceSet = Arrays.asList(1, 2, 3).stream().collect(Collectors.toSet());

        PartitionOfSetIterator<Integer> it = new PartitionOfSetIterator<>(sourceSet);

        assertEquals(partition(1, 2, 3), it.next());
        assertEquals(partition(1, 2, null, 3), it.next());
        assertEquals(partition(1, null, 2, 3), it.next());
        assertEquals(partition(1, null, 2, null, 3), it.next());
        assertFalse(it.hasNext());
    }

    private List<Set<Integer>> partition(Integer... values) {
        List<Set<Integer>> partition = new ArrayList<>();
        Set<Integer> set = new HashSet<>();
        for (Integer value: values) {
            if (value == null) {
                partition.add(set);
                set = new HashSet<>();
            } else {
                set.add(value);
            }
        }
        if (!set.isEmpty()) {
            partition.add(set);
        }
        return partition;
    }
}