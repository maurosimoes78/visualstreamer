package org.nomanscode.visualstreamer.common;

import org.reactivestreams.Subscription;
import reactor.core.publisher.EmitterProcessor;
//import rx.Emitter;

public final class CybertronEmitterProcessor<T>  {

    private EmitterProcessor<T> emitter_;

    public CybertronEmitterProcessor() {
        this.emitter_ = EmitterProcessor.create(false);
    }

    public CybertronEmitterProcessor(boolean autoCancel) {
        this.emitter_ = EmitterProcessor.create(autoCancel);
    }

    public void onSubscribe(final Subscription s) {
        this.emitter_.onSubscribe(s);
    }

    public void onNext(T t) {

        try {
            if (this.emitter_.downstreamCount() == 0) {
                return;
            }

            this.emitter_.onNext(t);
        }
        catch(Exception e) {
            e.getStackTrace();
        }
    }

    public void onError(Throwable t) {
        this.emitter_.onError(t);
    }

    public void onComplete() {
        this.emitter_.onComplete();
    }

    public static <E> CybertronEmitterProcessor<E> create() {
        return new CybertronEmitterProcessor<E>();
    }

    public static <E> CybertronEmitterProcessor<E> create(boolean autoCancel) {
        return new CybertronEmitterProcessor<E>(autoCancel);
    }

    public EmitterProcessor<T> getProcessor() {
        return this.emitter_;
    }

    public void dispose() {
        try {
            this.emitter_.dispose();
        }
        catch(Exception e) {
            e.getStackTrace();
        }
    }
}