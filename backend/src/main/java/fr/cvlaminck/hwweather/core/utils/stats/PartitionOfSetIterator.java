package fr.cvlaminck.hwweather.core.utils.stats;

import java.util.*;
import java.util.stream.Collectors;

public class PartitionOfSetIterator<T>
    implements Iterator<List<Set<T>>> {

    private List<T> sourceSet;
    private PartitionOfNIterator it;

    private List<Integer> partition = null;

    public PartitionOfSetIterator(Set<T> sourceSet) {
        //TODO only work up to 3 element in the source set.
        this.sourceSet = sourceSet.stream().collect(Collectors.toList());
        this.it = new PartitionOfNIterator(sourceSet.size());
    }

    @Override
    public boolean hasNext() {
        return it.hasNext();
    }

    @Override
    public List<Set<T>> next() {
        List<Set<T>> nextPartition = null;
        if (partition == null) {
            partition = it.next();
            nextPartition = createNextPartition(partition);
            if (isSymetric(partition)) {
                partition = null;
            } else {
                Collections.reverse(partition);
            }
        } else {
            nextPartition = createNextPartition(partition);
            partition = null;
        }
        return nextPartition;
    }

    private boolean isSymetric(List<Integer> partition) {
        if (partition.size() == 1) {
            return true;
        }
        for (int i = 0; i < Math.floorDiv(partition.size(), 2); i++) {
            if (partition.get(i) != partition.get(partition.size() - i - 1)) {
                return false;
            }
        }
        return true;
    }

    private List<Set<T>> createNextPartition(List<Integer> partition) {
        List<Set<T>> nextPartition = new ArrayList<>();
        int i = 0;
        for (int p : partition) {
            Set set = new HashSet<>();
            for (int j = 0; j < p; j++) {
                set.add(sourceSet.get(i + j));
            }
            i += p;
            nextPartition.add(set);
        }
        return nextPartition;
    }

}
