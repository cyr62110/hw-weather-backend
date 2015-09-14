package fr.cvlaminck.hwweather.core.utils.iterators;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class KSubsetOfNSetIteratorTest {

    private Set<Integer> set(Integer... values) {
        Set<Integer> set = new HashSet<>();
        for (Integer value : values) {
            set.add(value);
        }
        return set;
    }

    @Test
    public void testKEquals1() throws Exception {
        Set<Integer> sourceSet = Arrays.asList(1, 2, 3).stream().collect(Collectors.toSet());

        KSubsetOfNSetIterator<Integer> it = new KSubsetOfNSetIterator<>(sourceSet, 1);

        assertEquals(set(1), it.next());
        assertEquals(set(2), it.next());
        assertEquals(set(3), it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void testKEquals2() throws Exception {
        Set<Integer> sourceSet = Arrays.asList(1, 2, 3).stream().collect(Collectors.toSet());

        KSubsetOfNSetIterator<Integer> it = new KSubsetOfNSetIterator<>(sourceSet, 2);

        assertEquals(set(1, 2), it.next());
        assertEquals(set(1, 3), it.next());
        assertEquals(set(2, 3), it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void testKEquals3() throws Exception {
        Set<Integer> sourceSet = Arrays.asList(1, 2, 3).stream().collect(Collectors.toSet());

        KSubsetOfNSetIterator<Integer> it = new KSubsetOfNSetIterator<>(sourceSet, 3);

        assertEquals(set(1, 2, 3), it.next());
        assertFalse(it.hasNext());
    }
}