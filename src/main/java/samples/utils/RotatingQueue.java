package samples.utils;

/*
 *  Copyright 2009 Ancora Research Group.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

//package org.ancora.SharedLibrary.DataStructures;

import java.util.ArrayList;
import java.util.List;

/**
 * Rotating queue of fixed size.
 *
 * @author Joao Bispo
 */
public class RotatingQueue<T> {

    public RotatingQueue(int capacity) {
        size = capacity;
        queue = new ArrayList<T>(capacity);
        mostRecentItem = capacity - 1;
    }

    /**
     * Inserts an element to the head of the queue, pushing all other elements one
     * position forward.
     *
     * @param element
     */
    public void add(T element) {
        // Get index
        mostRecentItem = advancePointer(mostRecentItem);

        // Check if list already has an element
        if (queue.size() == mostRecentItem) {
            queue.add(element);
        } else {
            queue.set(mostRecentItem, element);
        }
    }

    public T getElement(int index) {
        // Normalize index to size of queue
        index = index % size;

        // Translate wanted index to queue index
        int queueIndex = mostRecentItem - index;
        // If negative, add size
        if (queueIndex < 0) {
            queueIndex += size;
        }

        // Check if element already exists in queue
        if (queueIndex < queue.size()) {
            return queue.get(queueIndex);
        } else {
            return null;
        }
    }

    public int size() {
        return size;
    }

    private int advancePointer(int oldPointer) {
        int pointer = oldPointer + 1;
        if (pointer < size) {
            return pointer;
        } else {
            return 0;
        }
    }

    ///
    // INSTANCE VARIABLES
    ///
    private List<T> queue;
    private int mostRecentItem;
    private int size;
}