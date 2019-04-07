package messaging.dynrouter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jeroen Roovers
 * @param <T>
 */
public class Aggregator<T> {

    private Map<Integer, LinkedList<T>> storage;
    private Map<Integer, Integer> aggregatorCounter;
    private List<Integer> deletedAggregators;
    private int aggregationCount;

    public Aggregator() {
        this.aggregationCount = 1;
        this.storage = new HashMap<>();
        this.aggregatorCounter = new HashMap<>();
        this.deletedAggregators = new LinkedList<>();
    }

    /**
     * Add 1 to the expected amount of objects for the given aggregationID
     *
     * @param aggregrationID
     */
    public void countObject(Integer aggregrationID) {
        if (!aggregatorCounter.containsKey(aggregrationID)) {
            aggregatorCounter.put(aggregrationID, 1);
        } else {
            Integer current = aggregatorCounter.get(aggregrationID);
            aggregatorCounter.put(aggregrationID, current + 1);
        }
    }

    /**
     * Get the total expected amount of objects for an aggregation ID
     *
     * @param aggregrationID
     * @return
     */
    public Integer getObjectCount(Integer aggregrationID) {
        return aggregatorCounter.get(aggregrationID);
    }

    /**
     * Put an object in the aggregator
     *
     * @param aggregationID
     * @param object
     * @return true if all expected objects are received
     */
    public boolean storeObject(Integer aggregationID, T object) {
        if (deletedAggregators.contains(aggregationID)) {
            System.out.println("Aggregator received object but objects for this aggregationID already processed.");
        } else {
            if (!storage.containsKey(aggregationID)) {
                storage.put(aggregationID, new LinkedList<>());
            }
            storage.get(aggregationID).add(object);
            if (aggregatorCounter.get(aggregationID).equals(this.storage.get(aggregationID).size())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets all the objects belonging to a certain aggregationID. The list is
     * removed from the aggregator.
     *
     * @param aggregationId
     * @return
     */
    public List<T> getObjectsAndClearByAggregationId(Integer aggregationId) {
        if (deletedAggregators.contains(aggregationId)) {
            return null;
        } else {
            if (this.storage.containsKey(aggregationId)) {
                LinkedList<T> output = new LinkedList<>(this.storage.get(aggregationId));
                this.storage.remove(aggregationId);
                this.aggregatorCounter.remove(aggregationId);
                this.deletedAggregators.add(aggregationId);
                return output;
            } // Never received any message.
            else {
                this.deletedAggregators.add(aggregationId);
                return new LinkedList<>();
            }
        }
    }

    /**
     * Get a new counter
     *
     * @return incremented aggregation id
     */
    public int getNewAggregationCounter() {
        aggregationCount = aggregationCount + 1;
        return aggregationCount;
    }

}
