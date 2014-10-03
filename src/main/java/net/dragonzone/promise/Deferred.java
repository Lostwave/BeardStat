/*
 * (C)2010-2012 Bryan Harclerode, All Rights Reserved
 * Alias: Darth Android <darthandroid@gmail.com>
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/3.0/ or send a letter to
 * Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 *
 * All derivative works must include attribution to the author(s) of the original work.
 */
package net.dragonzone.promise;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * This class is thread-safe, but provides no guarantee on order of operations.
 * For example, it is possible to {@link #notify(Object)} and then
 * {@link #resolve(Object)} such that the <code>Deferred</code> and its
 * <code>Promise</code> resolve before the progress handlers are done executing.
 * </p>
 * 
 * @author Bryan Harclerode
 */
public class Deferred<T> implements Promise<T>, Runnable {
    /**
     * A <code>Pipe</code> is a special type of <code>Deferred</code> which
     * listens for events from another <code>Deferred</code> and filters the
     * values, changes the events, or both before applying them to itself.
     * 
     * @author Bryan Harclerode
     * @created Nov 30, 2012
     * @param <U>
     *            The type of object returned by this <code>Pipe</code>'s
     *            resolve filter.
     */
    private class Pipe<U> extends Deferred<U> implements Delegate<Void, Promise<T>> {
        /**
         * The delegate invoked to filter the resolve value when the parent
         * promise resolves
         */
        private final Delegate<Promise<U>, T>         resolveFilter;
        /**
         * The delegate invoked to filter the error value when the parent
         * promise rejects
         */
        private final Delegate<Promise<U>, Throwable> rejectFilter;
        /**
         * The delegate invoked to filter each progress value when the parent
         * promise publishes a progress update.
         */
        private final Delegate<Object, Object>        progressFilter;
        /**
         * A delegate which can be attached to another promise via
         * {@link Promise#onDone(Delegate)} and pass the values on to this
         * <code>Pipe</code>. This is used to hook the <code>Pipe</code> up to
         * any promises returned by the filters.
         */
        private final Delegate<Void, Promise<U>>      proxyPipe = new Delegate<Void, Promise<U>>() {
                                                                    @Override
                                                                    public <P extends Promise<U>> Void invoke(P promise) {
                                                                        if (promise.isRejected()) {
                                                                            reject(promise.getError());
                                                                        }
                                                                        if (promise.isResolved()) {
                                                                            resolve(promise.getValue());
                                                                        }
                                                                        return null;
                                                                    }
                                                                };

        /**
         * @param resolveFilter
         * @param rejectFilter
         * @param progressFilter
         */
        public Pipe(Delegate<Promise<U>, T> resolveFilter, Delegate<Promise<U>, Throwable> rejectFilter,
                Delegate<Object, Object> progressFilter) {
            super();
            this.resolveFilter = resolveFilter;
            this.rejectFilter = rejectFilter;
            this.progressFilter = progressFilter;
        }

        @Override
        public <P extends Promise<T>> Void invoke(P promise) {
            if(promise == null){throw new IllegalArgumentException("Promise may not be null");}
            // handle progress events
            if (!promise.isDone() && (this.progressFilter != null)) {
                Object progress = promise.getProgress();
                try {
                    this.progressFilter.invoke(progress);
                } catch (Exception e) {
                    // We shouldn't reject a pipe if the progress handler fails,
                    // but
                    // we need a better way to report this
                    log.log(Level.SEVERE, "Error filtering progress value: " + progress, e);
                }
                notify(progress);
                return null;
            }
            // Only reaches here if the promise is done, which means the promise
            // is resolved or rejected
            // If either filter throws an exception, reject the Pipe with that
            // exception
            // Otherwise, try to obtain a proxy (replacement) promise from the
            // filter.
            // If there is no proxy promise, resolve or reject the promise as
            // appropriate;
            // else, pipe the proxy promise's events to the pipe's events
            // unfiltered.
            try {
                Promise<U> proxy = null;
                if (promise.isRejected()) {
                    proxy = this.rejectFilter.invoke(promise.getError());
                    // default for no proxy is to just propogate the error
                    if (proxy == null) {
                        reject(promise.getError());
                        return null;
                    }
                } else if (promise.isResolved()) {
                    proxy = this.resolveFilter.invoke(promise.getValue());
                    // default for no proxy is to just resolve with null
                    if (proxy == null) {
                        resolve(null);
                        return null;
                    }
                }
                // if we have a proxy, pipe the events to this pipe
                if (proxy != null) {
                    proxy.onDone(this.proxyPipe);
                }
            } catch (Throwable e) {
                // If any of the handlers throw an exception, reject the pipe
                // with it.
                reject(e);
            }
            return null;
        }
    }

    private static Logger log = Logger.getLogger(Deferred.class.getName());

    /**
     * Returns a {@link Promise} which resolves when all of the arguments to
     * this function resolve, or rejects if any of the arguments to this method
     * reject.
     * 
     * @param promises
     *            The <code>Promise</code>s or objects to wait on.
     * @return A new {@link Promise} which resolves when all of the parameter
     *         promises are resolved.
     */
    @SuppressWarnings("unchecked")
    public static Promise<Promise<?>[]> when(Object... promises) {
        // Array of promises to wait on
        final Promise<Object>[] promiseArray = (Promise<Object>[]) new Promise<?>[promises.length];
        // Counter, for tracking how many promises have resolved
        final AtomicInteger numResolved = new AtomicInteger();
        // The multi-promise, which will only be resolved when all the other
        // promises have been resolved
        final Deferred<Promise<?>[]> promise = new Deferred<Promise<?>[]>();
        Delegate<Void, Promise<Object>> handler = new Delegate<Void, Promise<Object>>() {
            @Override
            public <P extends Promise<Object>> Void invoke(P params) {
                // if any promise rejects, reject the whole thing
                if (params.isRejected()) {
                    promise.reject(params.getError());
                } else {
                    // wait until every promise has resolved, and then resolve
                    // this promise
                    int total = numResolved.getAndIncrement();
                    if (total == (promiseArray.length - 1)) {
                        promise.resolve(promiseArray);
                    }
                }
                return null;
            }
        };
        // Convert the objects to promises and hook up the handler
        for (int i = 0; i < promises.length; i++) {
            if ((promises[i] != null) && (promises[i] instanceof Promise<?>)) {
                promiseArray[i] = (Promise<Object>) promises[i];
            } else {
                promiseArray[i] = new Deferred<Object>(promises[i]);
            }
            promiseArray[i].onDone(handler);
        }
        return promise;
    }

    private ConcurrentLinkedQueue<Delegate<Void, Promise<T>>> progressCallbacks = new ConcurrentLinkedQueue<Delegate<Void, Promise<T>>>();
    private ConcurrentLinkedQueue<Delegate<Void, Promise<T>>> resolveCallbacks  = new ConcurrentLinkedQueue<Delegate<Void, Promise<T>>>();
    private ConcurrentLinkedQueue<Delegate<Void, Promise<T>>> rejectCallbacks   = new ConcurrentLinkedQueue<Delegate<Void, Promise<T>>>();
    private ConcurrentLinkedQueue<Delegate<Void, Promise<T>>> alwaysCallbacks   = new ConcurrentLinkedQueue<Delegate<Void, Promise<T>>>();
    /**
     * The current state of this <code>Deferred</code>. It starts as
     * {@link State#DEFERRED DEFERRED} and will change only once into either
     * {@link State#REJECTED REJECTED} or {@link State#RESOVLED RESOVLED}.
     */
    private AtomicReference<Promise.State>                    state             = new AtomicReference<Promise.State>(
                                                                                        Promise.State.DEFERRED);
    /**
     * The value that this <code>Deferred</code> was resolved with, if the
     * promise was resolved. If the promise was rejected or has not completed
     * yet, then this value is undefined.
     */
    private T                                                 value;
    /**
     * The value that this <code>Deferred</code> was rejected with, if the
     * promise was rejected. If the promise was resolved or has not completed
     * yet, then this value is undefined.
     */
    private Throwable                                         error;
    /**
     * The last progress value that was set for this <code>Deferred</code> using
     * {@link #notify()}.
     */
    private Object                                            progress          = null;

    /**
     * Creates a new <code>Deferred</code> in the {@link State#DEFERRED
     * DEFERRED} state.
     */
    public Deferred() {
    }

    /**
     * Creates a new <code>Deferred</code> and immediately resolves it with
     * <code>object</code>.
     * 
     * @param object
     *            The {@link Object} to resolve this <code>Deferred</code> with.
     * @see #resolve(Object)
     */
    public Deferred(T object) {
        this();
        resolve(object);
    }

    protected void doCallbacks() {
        if (!isDone()) {
            return;
        }
        run();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Throwable getError() {
        while (!isDone()) {
            synchronized (this.state) {
                try {
                    this.state.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (!isRejected()) {
            throw new IllegalStateException("Promise was resolved");
        }
        return this.error;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getProgress() {
        return this.progress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public State getState() {
        return this.state.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getValue() {
        waitForResolution();
        if (isRejected()) {
            throw new IllegalStateException("Promise was rejected", getError());
        }
        return this.value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDone() {
        return getState() != State.DEFERRED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRejected() {
        return getState() == State.REJECTED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isResolved() {
        return getState() == State.RESOVLED;
    }

    /**
     * Updates the current progress value for this <code>Deferred</code> and
     * then triggers a progress notification to all progress pipes and callbacks
     * currently registered with this <code>Deferred</code>.
     * 
     * @param progress
     *            The new progress value
     * @return This <code>Deferred</code>, for chaining.
     * @threadsafe
     */
    public Promise<T> notify(Object progress) {
        if (isDone()) {
            throw new IllegalStateException("Promise is already finished");
        }
        this.progress = progress;
        for (Delegate<Void, Promise<T>> progressCallback : this.progressCallbacks) {
            progressCallback.invoke(this);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDone(Delegate<Void, Promise<T>> callback) {
        if (callback == null) {
            return;
        }
        this.alwaysCallbacks.add(callback);
        doCallbacks();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onProgress(Delegate<Void, Promise<T>> callback) {
        if (callback == null) {
            return;
        }
        callback.invoke(this);
        if (isDone()) {
            return;
        }
        this.progressCallbacks.add(callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReject(Delegate<Void, Promise<T>> callback) {
        if (callback == null) {
            return;
        }
        this.rejectCallbacks.add(callback);
        doCallbacks();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResolve(Delegate<Void, Promise<T>> callback) {
        if (callback == null) {
            return;
        }
        this.resolveCallbacks.add(callback);
        doCallbacks();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> Promise<U> pipe(Delegate<Promise<U>, T> filter) {
        return pipe(filter, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> Promise<U> pipe(Delegate<Promise<U>, T> filter, Delegate<Promise<U>, Throwable> errorFilter) {
        return pipe(filter, errorFilter, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> Promise<U> pipe(Delegate<Promise<U>, T> filter, Delegate<Promise<U>, Throwable> errorFilter,
            Delegate<Object, Object> progressFilter) {
        Pipe<U> pipe = new Pipe<U>(filter, errorFilter, progressFilter);
        onDone(pipe);
        onProgress(pipe);
        return pipe;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> pipeError(Delegate<Promise<T>, Throwable> errorFilter) {
        return pipe(null, errorFilter, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> pipeProgress(Delegate<Object, Object> progressFilter) {
        return pipe(null, null, progressFilter);
    }

    /**
     * Rejects this <code>Deferred</code> with the specified error, and then
     * executes any callbacks registered with this deferred's
     * <code>Promise</code>. Any threads waiting on the promise with
     * {@link #getError()} will also be woken and the rejection error returned,
     * while any threads waiting on {@link #getValue()} will also be woken, but
     * an exception thrown.
     * 
     * @param error
     *            The error this <code>Deferred</code> was rejected with.
     * @return This <code>Deferred</code> as a {@link Promise}, for chaining.
     * @throws IllegalStateException
     *             If this <code>Deferred</code> has already been resolved or
     *             rejected.
     * @see #getError()
     * @see #getValue()
     * @threadsafe
     */
    public Promise<T> reject(Throwable error) {
        // Throw an exception if we can't update the promise state (because
        // another thread is)
        if (!this.state.compareAndSet(State.DEFERRED, State.REJECTED)) {
            throw new IllegalStateException("Promise already finished");
        }
        this.error = error;
        doCallbacks();
        return this;
    }

    /**
     * Resovles this <code>Deferred</code> with the specified value, and then
     * executes any callbacks registered with this <code>Promise</code>. Any
     * threads waiting on the promise with {@link #getValue()} will also be
     * woken and the resolved value returned, while any threads waiting on
     * {@link #getError()} will also be woken, but an exception thrown.
     * 
     * @param object
     *            The object this <code>Deferred</code> resolves with.
     * @return This <code>Deferred</code> as a {@link Promise}, for chaining.
     * @throws IllegalStateException
     *             If this <code>Deferred</code> has already been resolved or
     *             rejected.
     * @see #getError()
     * @see #getValue()
     * @threadsafe
     */
    public Promise<T> resolve(T object) {
        // Throw an exception if we can't update the promise state (because
        // another thread is)
        if (!this.state.compareAndSet(State.DEFERRED, State.RESOVLED)) {
            throw new IllegalStateException("Promise already finished");
        }
        this.value = object;
        doCallbacks();
        return this;
    }

    /**
     * Checks if this <code>Deferred</code> has been resolved, and if so,
     * executes any pending callbacks as appropriate.
     * 
     * @threadsafe
     */
    @Override
    public void run() {
        // if the deferred has not been completed, don't run
        // No synchronization needed because state can't go from done --> not
        // done.
        if (!isDone()) {
            return;
        }
        // wake up all the threads waiting on this deferred synchronously
        synchronized (this.state) {
            this.state.notifyAll();
        }
        // deferred is done, progress callbacks can't occur anymore
        this.progressCallbacks.clear();
        // execute all the rest of the callbacks, emptying the queues in the
        // process.
        Delegate<Void, Promise<T>> alwaysCallback = null;
        while ((alwaysCallback = this.alwaysCallbacks.poll()) != null) {
            try {
                alwaysCallback.invoke(this);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Error executing callback with Deferred: " + this, e);
            }
        }
        Delegate<Void, Promise<T>> resolveCallback = null;
        while ((resolveCallback = this.resolveCallbacks.poll()) != null) {
            if (isResolved()) {
                try {
                    resolveCallback.invoke(this);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "Error executing resolve callback with Deferred: " + this, e);
                }
            }
        }
        Delegate<Void, Promise<T>> rejectCallback = null;
        while ((rejectCallback = this.rejectCallbacks.poll()) != null) {
            if (isRejected()) {
                try {
                    rejectCallback.invoke(this);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "Error executing reject callback with Deferred: " + this, e);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "{" + this.state.get() + (isResolved() ? ("," + getValue()) : isRejected() ? ("," + getError()) : "")
                + "}";
    }

    /**
     * Suspends the current thread until this <code>Deferred</code>'s
     * {@link Promise} resolves.
     * 
     * @threadsafe
     * @throws RuntimeException
     *             if the thread is interrupted while waiting. The
     *             <code>cause</code> contains the actual
     *             {@link InterruptedException} that was thrown.
     */
    private void waitForResolution() {
        try {
            synchronized (this.state) {
                while (!isDone()) {
                    this.state.wait();
                }
            }
        } catch (InterruptedException ie) {
            // Convert to un-checked exception
            throw new RuntimeException(ie);
        }
    }
}
