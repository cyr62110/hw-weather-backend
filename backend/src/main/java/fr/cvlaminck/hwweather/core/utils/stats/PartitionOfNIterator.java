package fr.cvlaminck.hwweather.core.utils.stats;

import java.util.Iterator;
import java.util.Set;

// http://www.geeksforgeeks.org/generate-unique-partitions-of-an-integer/
public class PartitionOfNIterator<T>
    implements Iterator<Set<T>> {

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Set<T> next() {
        return null;
    }

    int[] r = null;
    int[] m = null;
    int d;
    int nlast = 0;

    boolean nexpar(int n, boolean mtc) {
        int s, f;
        if (!mtc) {
            if (n == nlast) {
                int sum = 1;
                if (r[d - 1] <= 1) {
                    sum = m[d - 1] + 1;
                    d = d - 1;
                }
                f = r[d - 1] - 1;
                if (m[d - 1] <= 1) {

                }
                r[d - 1] = f;
                m[d - 1] = 1 + sum / f;
                s = sum % f;

            }
            nlast = n;
        }
        s = n;
        d = 1;
        r[d - 1] = s;
        m[d - 1] = 1;
        return m[d - 1] != n;
    }

}
