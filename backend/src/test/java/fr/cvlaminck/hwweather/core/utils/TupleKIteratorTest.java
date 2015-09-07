package fr.cvlaminck.hwweather.core.utils;

import fr.cvlaminck.hwweather.data.model.WeatherDataType;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

public class TupleKIteratorTest {

    enum SomeEnum {
        A, B, C
    }

    @Test
    public void testK1() throws Exception {
        Collection<SomeEnum> sourceCollection = Arrays.asList(SomeEnum.values());

        TupleKIterator<SomeEnum> it = new TupleKIterator<>(1, sourceCollection);

        assertEquals(Arrays.asList(SomeEnum.A), it.next());
        assertEquals(Arrays.asList(SomeEnum.B), it.next());
        assertEquals(Arrays.asList(SomeEnum.C), it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void testK2() throws Exception {
        Collection<SomeEnum> sourceCollection = Arrays.asList(SomeEnum.values());

        TupleKIterator<SomeEnum> it = new TupleKIterator<>(2, sourceCollection);

        assertEquals(Arrays.asList(SomeEnum.A, SomeEnum.B), it.next());
        assertEquals(Arrays.asList(SomeEnum.A, SomeEnum.C), it.next());
        assertEquals(Arrays.asList(SomeEnum.B, SomeEnum.A), it.next());
        assertEquals(Arrays.asList(SomeEnum.B, SomeEnum.C), it.next());
        assertEquals(Arrays.asList(SomeEnum.C, SomeEnum.A), it.next());
        assertEquals(Arrays.asList(SomeEnum.C, SomeEnum.B), it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void testK3() throws Exception {
        Collection<SomeEnum> sourceCollection = Arrays.asList(SomeEnum.values());

        TupleKIterator<SomeEnum> it = new TupleKIterator<>(3, sourceCollection);

        assertEquals(Arrays.asList(SomeEnum.A, SomeEnum.B, SomeEnum.C), it.next());
        assertFalse(it.hasNext());
    }
}