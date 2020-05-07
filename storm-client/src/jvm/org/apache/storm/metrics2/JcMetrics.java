/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package org.apache.storm.metrics2;

import com.codahale.metrics.Histogram;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.storm.utils.JCQueue;

public class JcMetrics {
    private final SimpleGauge<Long> capacity;
    private final SimpleGauge<Long> population;
    private final QueueArrival arrival;

    JcMetrics(SimpleGauge<Long> capacity,
              SimpleGauge<Long> population,
              Histogram arrivalTimes) {
        this.capacity = capacity;
        this.population = population;
        this.arrival = new QueueArrival(arrivalTimes);
    }

    public void setCapacity(Long capacity) {
        this.capacity.set(capacity);
    }

    public void setPopulation(Long population) {
        this.population.set(population);
    }

    public void set(JCQueue.QueueMetrics metrics) {
        this.capacity.set(metrics.capacity());
        this.population.set(metrics.population());
    }

    public void updateArrival(int count) {
        arrival.update(count);
    }

    public class QueueArrival {

        private AtomicLong previous;
        private Histogram arrivalTimes;


        public QueueArrival(Histogram arrivalTimes) {

            this.previous = new AtomicLong();
            this.arrivalTimes = arrivalTimes;
        }

        public void update(int count) {
            long time = System.nanoTime();
            long oldTime = previous.getAndSet(time);
            if (oldTime > 0) {
                arrivalTimes.update(time - oldTime);
            }
        }
    }
}
