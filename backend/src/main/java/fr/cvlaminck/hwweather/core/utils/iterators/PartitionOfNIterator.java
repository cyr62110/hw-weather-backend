package fr.cvlaminck.hwweather.core.utils.iterators;

import java.util.*;

// http://www.geeksforgeeks.org/generate-unique-partitions-of-an-integer/
public class PartitionOfNIterator
    implements Iterator<List<Integer>> {

    private int n;
    private int[] partition = null;
    private int numberOfPartition = 0;

    public PartitionOfNIterator(int n) {
        this.n = n;
    }

    @Override
    public boolean hasNext() {
        return numberOfPartition != n;
    }

    @Override
    public List<Integer> next() {
        if (numberOfPartition == 0) {
            generateInitialPartition();
        } else {
            generateNextPartition();
        }

        return createNextPartitionSets(partition, numberOfPartition);
    }

    private void generateInitialPartition() {
        partition = new int[n];
        partition[0] = n;
        numberOfPartition = 1;
    }

    private void generateNextPartition() {
        int k = numberOfPartition - 1;

        // Find the rightmost non-one value in p[]. Also, update the
        // rem_val so that we know how much value can be accommodated
        int rem_val = 0;
        while (k >= 0 && partition[k] == 1)
        {
            rem_val += partition[k];
            k--;
        }

        // Decrease the p[k] found above and adjust the rem_val
        partition[k]--;
        rem_val++;

        // If rem_val is more, then the sorted order is violeted.  Divide
        // rem_val in differnt values of size p[k] and copy these values at
        // different positions after p[k]
        while (rem_val > partition[k])
        {
            partition[k+1] = partition[k];
            rem_val = rem_val - partition[k];
            k++;
        }

        // Copy rem_val to next position and increment position
        partition[k+1] = rem_val;
        numberOfPartition = k + 2;
    }

    private List<Integer> createNextPartitionSets(int[] partition, int numberOfPartition) {
        List<Integer> nextPartition = new ArrayList<>();
        for (int i = 0; i < numberOfPartition; i++) {
            nextPartition.add(partition[i]);
        }
        return nextPartition;
    }

}
