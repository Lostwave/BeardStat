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

/**
 * @author Bryan Harclerode
 */
public interface Promise<T> {
    /**
     * The state of a <code>Promise</code>.
     * 
     * @author Bryan Harclerode
     * @created Nov 3, 2012
     * @see Promise#getState()
     */
    public static enum State {
        /**
         * A <code>Promise</code> in the <code>DEFERRED</code> state has neither
         * been resolved nor rejected, and it sill pending completion. This is
         * the initial state of a promise, and it will change to either
         * {@link #RESOVLED} or {@link #REJECTED} upon completion.
         */
        DEFERRED,
        /**
         * A <code>Promise</code> in the <code>RESOLVED</code> state has
         * completed successfully, possibly with a value. The value can be
         * retreived with {@link Promise#getValue()}. This is a terminal state
         * of a promise; once resolved, a promise may not be rejected or
         * deferred and resolved with a new value.
         */
        RESOVLED,
        /**
         * A <code>Promise</code> in the <code>REJECTED</code> state has
         * completed erroneously, possibly with an error. The error can be
         * retreived with {@link Promise#getError()}. This is a terminal state
         * of a promise; once rejected, a promise may not be resolved or
         * deferred and rejected with a new error.
         */
        REJECTED;
    }

    /**
     * Returns the error value for this <code>Promise</code>. This method blocks
     * until the <code>Promise</code> is resovled or rejected. If the
     * <code>Promise</code> rejects, all callback handlers registered with
     * {@link #onReject(Delegate)} are invoked, and the error is passed along to
     * all pipes, through any filters registered with the pipes.
     * 
     * @return the current progress of this <code>Promise</code>.
     * @see #onReject(Delegate)
     * @see #pipe(Delegate, Delegate, Delegate)
     * @see #pipe(Delegate, Delegate)
     * @see #pipeError(Delegate)
     * @threadsafe
     */
    public Throwable getError();

    /**
     * Returns the current progress value for this <code>Promise</code>. This
     * value is initialized to <code>null</code>, but may be changed repeatedly
     * before the <code>Promise</code> is resovled or rejected. Each time the
     * progress value is changed, all callback handlers registered with
     * {@link #onProgress(Delegate)} are invoked, and the value is passed along
     * to all pipes, through any filters registered with the pipes.
     * 
     * @return the current progress of this <code>Promise</code>.
     * @see #onProgress(Delegate)
     * @see #pipe(Delegate, Delegate, Delegate)
     * @see #pipeProgress(Delegate)
     * @threadsafe
     */
    public Object getProgress();

    /**
     * Returns the current state of this <code>Promise</code>. A promise always
     * starts in the {@link State#DEFERRED DEFERRED} state, and then can
     * transition to either {@link State#RESOVLED RESOLVED} or
     * {@link State#REJECTED REJECTED}.
     * 
     * @return The <code>Promise</code>'s current state.
     * @see State
     * @see #isDone()
     * @see #isRejected()
     * @see #isResolved()
     * @see #onDone(Delegate)
     * @see #onReject(Delegate)
     * @see #onResolve(Delegate)
     * @threadsafe
     */
    public State getState();

    /**
     * Returns the value of this <code>Promise</code>. If this
     * <code>Promise</code> has not resolved or rejected, this call will wait
     * indefinately until it is completed before returning the value. If the
     * <code>Promise</code> is {@link State#REJECTED REJECTED}, then this method
     * will throw the error tha the <code>Promise</code> was rejected wtih.
     * 
     * @return The value of this <code>Promise</code>.
     * @threadsafe
     */
    public T getValue();

    /**
     * Returns whether this promise is still pending, or has been
     * resolved/rejected.
     * 
     * @return <code>true</code> if the promise is @link {@link State#RESOVLED
     *         RESOLVED} or {@link State#REJECTED REJECTED}, or <br />
     *         <code>false</code> if the promise is still {@link State#DEFERRED
     *         DEFERRED}.
     * @see #isResolved()
     * @see #isRejected()
     * @see State#REJECTED
     * @see State#RESOVLED
     * @threadsafe
     */
    public boolean isDone();

    /**
     * Returns whether this <code>Promise</code> has been rejected.
     * 
     * @return <code>true</code> if this <code>Promise</code> has been
     *         {@link State#REJECTED REJECTED}, or<br />
     *         <code>false</code> if this <code>Promise</code> is either
     *         {@link State#RESOVLED RESOLVED} or still {@link State#DEFERRED
     *         DEFERRED}.
     * @see #getState()
     * @see State#REJECTED
     * @threadsafe
     */
    public boolean isRejected();

    /**
     * Returns whether this <code>Promise</code> has been resolved.
     * 
     * @return <code>true</code> if this <code>Promise</code> has been
     *         {@link State#RESOVLED RESOLVED}, or <br />
     *         <code>false</code> if this <code>Promise</code> is either
     *         {@link State#REJECTED REJECTED} or still {@link State#DEFERRED
     *         DEFERRED}.
     * @see #getState()
     * @see State#RESOVLED
     * @threadsafe
     */
    public boolean isResolved();

    /**
     * Registers a callback to be executed when this promise is either resolved
     * or rejected. The callback is passed the entire promise and its return
     * value is discarded. If the <code>Promise</code> has already been resolved
     * or rejected, this callback is executed immediately.
     * 
     * @param callback
     *            The callback handler that should be registered.
     * @threadsafe
     */
    public void onDone(Delegate<Void, Promise<T>> callback);

    /**
     * Registers a callback to be executed when this <code>Promise</code> posts
     * a progress update, and then fires the callback to let it process the
     * current progress of the <code>Promise</code>. The callback is passed the
     * entire promise and its return value is discarded.
     * 
     * @param callback
     *            The callback handler that should be registered.
     * @threadsafe
     */
    public void onProgress(Delegate<Void, Promise<T>> callback);

    /**
     * Registers a callback to be executed when this <code>Promise</code>
     * rejects. The callback is passed the entire promise and its return value
     * is discarded.
     * 
     * @param callback
     *            The callback handler that should be registered.
     * @threadsafe
     */
    public void onReject(Delegate<Void, Promise<T>> callback);

    /**
     * Registers a callback to be executed when this <code>Promise</code>
     * resolves. The callback is passed the entire promise and its return value
     * is discarded.
     * 
     * @param callback
     *            The callback handler that should be registered.
     * @threadsafe
     */
    public void onResolve(Delegate<Void, Promise<T>> callback);

    /**
     * Pipes all events from this <code>Promise</code> to a new
     * <code>Promise</code>, and filters the resolve value with a callback
     * handler when the original <code>Promise</code> resolves.
     * 
     * @param filter
     *            The filter to use with the <code>Promise</code>'s resolve
     *            value.
     * @return A new <code>Promise</code> whose state and value have been
     *         filtered based upon this <code>Promise</code> 's events.
     * @see #pipe(Delegate, Delegate, Delegate)
     * @threadsafe
     */
    public <U> Promise<U> pipe(Delegate<Promise<U>, T> filter);

    /**
     * @param filter
     *            The filter to use with the <code>Promise</code>'s resolve
     *            value.
     * @param errorFilter
     *            The filter callback for handling errors.
     * @return A new <code>Promise</code> whose state and value have been
     *         filtered based upon this <code>Promise</code> 's events.
     * @see #pipe(Delegate, Delegate, Delegate)
     * @threadsafe
     */
    public <U> Promise<U> pipe(Delegate<Promise<U>, T> filter, Delegate<Promise<U>, Throwable> errorFilter);

    public <U> Promise<U> pipe(Delegate<Promise<U>, T> filter, Delegate<Promise<U>, Throwable> errorFilter,
            Delegate<Object, Object> progressFilter);

    /**
     * Pipes all events from this <code>Promise</code> to a new
     * <code>Promise</code>, and filters the reject error with a callback
     * handler if the original <code>Promise</code> rejects.
     * 
     * @param errorFilter
     *            The filter callback for handling errors.
     * @return A new <code>Promise</code> whose state and value have been
     *         filtered based upon this <code>Promise</code> 's events.
     * @see #pipe(Delegate, Delegate, Delegate)
     * @threadsafe
     */
    public Promise<T> pipeError(Delegate<Promise<T>, Throwable> errorFilter);

    /**
     * Pipes all events from this <code>Promise</code> to a new
     * <code>Promise</code>, and filters the progress value with a callback
     * handler if the original <code>Promise</code> publishes a progress value.
     * 
     * @param progressFilter
     *            The filter callback for handling progress values.
     * @return A new <code>Promise</code> whose state and value have been
     *         filtered based upon this <code>Promise</code> 's events.
     * @see #pipe(Delegate, Delegate, Delegate)
     * @threadsafe
     */
    public Promise<T> pipeProgress(Delegate<Object, Object> progressFilter);
}
