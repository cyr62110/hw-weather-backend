package fr.cvlaminck.hwweather.core.utils.iterators;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation as a Java iterator of the NEXKSB described in
 * Combinatorial Algorithms for Computers and Calculators
 * by Albert Nijenhuis and Herbert S. Wilf.
 */
public class KSubsetOfNSetIterator<T>
        implements Iterator<Set<T>> {

    private List<T> sourceSet;

    private int k;

    private Integer[] a = null;
    private Boolean mtc = null;

    public KSubsetOfNSetIterator(Set<T> sourceSet, int k) {
        //TODO Throw exception if k is negative or zero
        //TODO Throw exception if k is greater than n
        //TODO Throw exception sourceSet is null.
        this.sourceSet = sourceSet.stream().collect(Collectors.toList());
        this.k = k;
    }

    @Override
    public boolean hasNext() {
        if (mtc == null) {
            return true;
        }
        return mtc;
    }

    @Override
    public Set<T> next() {
        if (mtc == null) {
            mtc = false;
            a = new Integer[k];
        }
        mtc = nexksb(sourceSet.size(), k, a, mtc);
        return convertToNextKSubset(a);
    }

    private Set<T> convertToNextKSubset(Integer[] a) {
        Set nextKSubset = new HashSet();
        for (int i = 0; i < a.length; i++) {
            nextKSubset.add(sourceSet.get(a[i] - 1));
        }
        return nextKSubset;
    }

    private int m2;
    private int h;

    private boolean nexksb(int n, int k, Integer[] a, boolean mtc) {
        if (!mtc) {
            m2 = 0;
            h = k;
        } else {
            if (m2 < n - h) {
                h = 0;
            }
            h = h + 1;
            m2 = a[k - h];
        }
        for (int j = 0; j < h; j++) {
            a[k + j - h] = m2 + (j + 1);
        }
        return a[0] != n - k + 1;
    }
}
