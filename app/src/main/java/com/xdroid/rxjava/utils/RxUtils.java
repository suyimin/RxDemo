package com.xdroid.rxjava.utils;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class RxUtils {

    private RxUtils() {
    }

    public static void unsubscribeIfNotNull(Subscription subscription) {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public static CompositeSubscription getNewCompositeSubIfUnsubscribed(CompositeSubscription subscription) {
        if (subscription == null || subscription.isUnsubscribed()) {
            return new CompositeSubscription();
        }

        return subscription;
    }
}
