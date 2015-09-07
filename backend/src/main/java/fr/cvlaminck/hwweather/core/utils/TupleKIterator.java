package fr.cvlaminck.hwweather.core.utils;

import java.util.*;

/**
 * ex:
 * sourceCollection = {C, H, D};
 * k = 1 => {C} {H} {D}
 * k = 2 => {C, H} {C, D} {H, D}
 * k = 3 => {C, H, D}
 */
public class TupleKIterator<T>
        implements Iterator<List<T>> {

    private int k;
    private Collection<T> sourceCollection;

    private Iterator[] nextTupleElementIterators = null;
    private Object[] nextTuple = null;

    public TupleKIterator(int k, Collection<T> sourceCollection) {
        //TODO check collection size is greater or equals to k
        //TODO check k is strictly positive
        //TODO particular case k == sourceCollection.size()

        this.k = k;
        this.sourceCollection = sourceCollection;

        this.nextTupleElementIterators = new Iterator[k];
        this.nextTuple = new Object[k];
        for (int i = 0; i < k; i++) {
            this.nextTupleElementIterators[i] = sourceCollection.iterator();
            if (!this.nextTupleElementIterators[i].hasNext()) {
                //TODO throw new illegal state exception.
            }
        }

        initNextTuple();
    }

    private void initNextTuple () {
        int i = 0;
        while (i < k && nextTupleElementIterators[i].hasNext()) {
            nextTuple[i] = nextTupleElementIterators[i].next();
            if (!tupleFirstKElementsContains(nextTuple, i, nextTuple[i])) {
                i++;
            }
        }
        //TODO: Throw new illegal state exception if i < k since we havent fully built the next tuple
    }

    @Override
    public boolean hasNext() {
        return nextTuple != null;
    }

    @Override
    public List<T> next() {
        //TODO Thow exception if !hasNext
        //First we build the next tuple we will return
        List<T> nextTupleK = new ArrayList(k);
        for (int i = 0; i < k; i++) {
            nextTupleK.add((T) this.nextTuple[i]);
        }
        //Then, we compute the tuple that will be returned on the next iteration
        nextTuple = computeNextTuple(nextTuple);
        return nextTupleK;
    }

    private Object[] computeNextTuple(Object[] nextTuple) {
        int i = k - 1;
        while (i >= 0) {
            if (nextTupleElementIterators[i].hasNext()) {
                Object nextTupleElement = nextTupleElementIterators[i].next();
                if (!tupleFirstKElementsContains(nextTuple, i, nextTupleElement)) {
                    nextTuple[i] = nextTupleElement;
                    for (i = i + 1;i < k; i++) {
                        nextTupleElementIterators[i] = sourceCollection.iterator();
                        nextTuple[i] = nextTupleElementIterators[i].next();
                    }
                    return nextTuple;
                }
            } else {
                i --;
            }
        }
        return null;
    }

    private boolean tupleFirstKElementsContains(Object[] nextTuple, int k, Object elt) {
        if (k == 0) {
            return false;
        }
        boolean contains = false;
        int i = 0;
        while (!contains && i < k) {
            if (nextTuple[i].equals(elt)) {
                contains = true;
            }
            i++;
        }
        return contains;
    }
}
