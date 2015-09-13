package fr.cvlaminck.hwweather.core.utils.stats;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class PartitionOfSetIterator<T>
    implements Iterator<List<Set<T>>> {

    private List<T> sourceSet;

    private Boolean mtc = null;
    private Integer p[] = null;
    private Integer q[] = null;


    public PartitionOfSetIterator(Set<T> sourceSet) {
        this.sourceSet = sourceSet.stream().collect(Collectors.toList());
    }

    @Override
    public boolean hasNext() {
        if (mtc == null) {
            return true;
        }
        return mtc;
    }

    @Override
    public List<Set<T>> next() {
        if (mtc == null) {
            mtc = false;
            p = new Integer[sourceSet.size()];
            q = new Integer[sourceSet.size()];
        }
        mtc = nexequ(sourceSet.size(), p, q, mtc);
        return convertToNextPartition(q);
    }

    private List<Set<T>> convertToNextPartition(Integer[] q) {
        Map<Integer, Set<T>> nextPartitionMap = new HashMap<>();
        for (int i = 0; i < q.length; i ++) {
            Set set = nextPartitionMap.get(q[i]);
            if (set == null) {
                set = new HashSet<>();
                nextPartitionMap.put(q[i], set);
            }
            set.add(sourceSet.get(i));
        }

        ArrayList<Set<T>> nextPartition = new ArrayList<>(nextPartitionMap.size());
        nextPartition.addAll(nextPartitionMap.values());
        return nextPartition;
    }

    private int nc;
    private boolean nexequ(int n, Integer[] p, Integer[] q, boolean mtc) {
        if (!mtc) {
            nc = 1;
            for (int i = 0; i < n; i++) {
                q[i] = 1;
            }
            p[0] = n;
        } else {
            int m = n;
            int l = q[m - 1];
            while (p[l - 1] == 1) {
                q[m - 1] = 1;
                m = m - 1;
                l = q[m - 1];
            }
            nc = nc + m - n;
            p[0] = p[0] + n - m;
            if (l == nc) {
                nc = nc + 1;
                p[nc - 1] = 0;
            }
            q[m - 1] = l + 1;
            p[l - 1] = p[l - 1] - 1;
            p[l] = p[l] + 1;
        }
        return nc != n;
    }
}
