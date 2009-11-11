/*
 * Copyright (c) 2007-2008, Hazel Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.hazelcast.client;

import com.hazelcast.core.IQueue;
import com.hazelcast.core.ItemListener;
import com.hazelcast.core.Instance;
import com.hazelcast.impl.ClusterOperation;

import java.util.concurrent.TimeUnit;
import java.util.Collection;
import java.util.NoSuchElementException;

public class QueueClientProxy<E> extends CollectionClientProxy<E> implements IQueue<E>, ClientProxy {
    public QueueClientProxy(HazelcastClient hazelcastClient, String name) {
        super(hazelcastClient, name);
    }

    public String getName() {
        return name.substring(2);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public InstanceType getInstanceType() {
        return InstanceType.QUEUE;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean offer(E e) {
        return innerOffer(e, 0);
    }

    public E poll() {
        return innerPoll(0);
    }

    public E remove() {
        return (E)proxyHelper.doOp(ClusterOperation.BLOCKING_QUEUE_REMOVE, null, null);
    }

    public E peek() {
        return (E)proxyHelper.doOp(ClusterOperation.BLOCKING_QUEUE_PEEK, null, null);
    }

    public E element() {
        if(this.size()==0){
            throw new NoSuchElementException();
        }
        return peek();
    }

    public boolean offer(E e, long l, TimeUnit timeUnit) throws InterruptedException {
        l = (l<0)?0:l;
        if(e==null){
            throw new NullPointerException();
        }
        return innerOffer(e, timeUnit.toMillis(l));
    }

    private boolean innerOffer(E e, long millis){
        return (Boolean)proxyHelper.doOp(ClusterOperation.BLOCKING_QUEUE_OFFER, e, millis);
    }

    public E poll(long l, TimeUnit timeUnit) throws InterruptedException {
        l = (l<0)?0:l;
        return innerPoll(timeUnit.toMillis(l));
    }
    private E innerPoll(long millis){
        return (E)proxyHelper.doOp(ClusterOperation.BLOCKING_QUEUE_POLL, null, millis);
    }


    public E take() throws InterruptedException {
        return innerPoll(-1);
    }

    public void put(E e) throws InterruptedException {
        innerOffer(e, -1);
    }

    public int remainingCapacity() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int drainTo(Collection<? super E> objects) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int drainTo(Collection<? super E> objects, int i) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
