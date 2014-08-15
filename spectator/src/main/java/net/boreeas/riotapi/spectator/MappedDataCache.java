/*
 * Copyright 2014 The LolDevs team (https://github.com/loldevs)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.boreeas.riotapi.spectator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * A mapped data cache is a dictionary-like class where all calls to get() block until a value has been
 * assigned to the given key.
 * Created on 8/10/2014.
 */
public class MappedDataCache<K, V> {
    private Map<K, V> cache = new HashMap<>();
    private Map<K, CountDownLatch> locks = new HashMap<>();

    private CountDownLatch acquireLock(K id) {
        CountDownLatch result = locks.get(id);

        if (result == null) {
            // Synchronize here to prevent double-put
            synchronized (locks) {
                if (locks.get(id) == null) {
                    locks.put(id, new CountDownLatch(1));
                }
            }

            // Result is guaranteed to not be null here
            result = locks.get(id);
        }

        return result;
    }


    /**
     * Caches the given mapping and releases all waiting locks.
     * @param k The key to store.
     * @param v The value to store.
     */
    public void put(K k, V v) {
        cache.put(k, v);
        acquireLock(k).countDown();
    }

    /**
     * Retrieve the value associated with the given key, blocking as long as necessary.
     * @param k The key.
     * @return The value associated with the key.
     * @throws InterruptedException
     */
    public V get(K k) throws InterruptedException {
        await(k);
        return cache.get(k);
    }

    /**
     * Retrieve the value associated with the given key, blocking as long as necessary up to the specified maximum.
     * @param k The key.
     * @param timeout The length of the timeout.
     * @param unit The time unit of the timeout.
     * @return The value associated with the key.
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public V get(K k, long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        await(k, timeout, unit);

        return cache.get(k);
    }

    /**
     * Waits until the key has been assigned a value.
     * @param k The key to wait for.
     * @throws InterruptedException
     */
    public void await(K k) throws InterruptedException {
        acquireLock(k).await();
    }

    /**
     * Waits until the key has been assigned a value, up to the specified maximum.
     * @param k THe key to wait for.
     * @param timeout The maximum time to wait.
     * @param unit The time unit of the timeout.
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public void await(K k, long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        if (!acquireLock(k).await(timeout, unit)) {
            throw new TimeoutException("Wait time for retrieving value for key " + k + " exceeded " + timeout + " " + unit);
        }
    }

    /**
     * Removes the mapping from the specified key. Any future calls to get() will block again until a new value
     * has been assigned.
     * @param k The key to remove.
     */
    public void remove(K k) {
        if (cache.containsKey(k)) {
            cache.remove(k);
            locks.remove(k);
        }
    }

    /**
     * Checks if future calls to get(k) will block.
     * @param k The key to check for.
     * @return <code>true</code> if future requests to get() will return immediately, <code>false</code> otherwise.
     */
    public boolean isReleased(K k) {
        return acquireLock(k).getCount() == 0;
    }

    /**
     * Creates a future object waiting for the given key.
     * @param k The key to wait for.
     * @return A future representing the future value associated with the given key.
     */
    public Future<V> getFuture(K k) {
        return new CacheFuture(k);
    }


    /**
     * Returns the number of elements in the cache.
     * @return The number of already set elements.
     */
    public int size() {
        return cache.size();
    }



    public class CacheFuture implements Future<V> {
        private K key;

        public CacheFuture(K k) {
            this.key = k;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return isReleased(key);
        }

        @Override
        public V get() throws InterruptedException, ExecutionException {
            return MappedDataCache.this.get(key);
        }

        @Override
        public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return MappedDataCache.this.get(key, timeout, unit);
        }
    }
}
