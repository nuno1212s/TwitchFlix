package com.twitchflix.databases.mongodb;

import org.reactivestreams.Subscription;

/**
 * A Subscriber that immediately requests Integer.MAX_VALUE onSubscribe
 *
 * @param <T> The publishers result type
 */
public class OperationSubscriber<T> extends ObservableSubscriber<T> {

    @Override
    public void onSubscribe(final Subscription s) {
        super.onSubscribe(s);
        s.request(Integer.MAX_VALUE);
    }

}