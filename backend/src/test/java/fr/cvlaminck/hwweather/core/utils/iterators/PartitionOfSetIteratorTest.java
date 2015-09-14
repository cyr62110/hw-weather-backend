package fr.cvlaminck.hwweather.core.utils.iterators;

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
        assertEquals(partition(1, 3, null, 2), it.next());
        assertEquals(partition(1, null, 2, 3), it.next());
        assertEquals(partition(1, null, 2, null, 3), it.next());
        assertFalse(it.hasNext());
    }

    public static <T> List<Set<T>> partition(T... values) {
        List<Set<T>> partition = new ArrayList<>();
        Set set = new HashSet();
        for (Object value: values) {
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