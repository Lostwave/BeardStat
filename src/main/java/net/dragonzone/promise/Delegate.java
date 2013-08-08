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

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * A generic representation of a simplified java {@link Method}, which takes a
 * single argument and returns a single value. This is useful for creating
 * handlers (like with {@link Callable}), but that can accept an object which
 * contains various parameters to be passed to the called code. This allows this
 * interface to function more like a closure, instead of requiring that all
 * parameters be captures provided by the code creating the closure.
 * 
 * @author Bryan Harclerode
 * @param <Return>
 *            The return type of the <code>Delegate</code>
 * @param <Params>
 *            The parameter type of the <code>Delegate</code>
 */
public interface Delegate<Return, Params> {
    /**
     * Invokes this delegate, causing it to execute and possibly return a value.
     * 
     * @param params
     *            The parameter object being passed to the delegate
     * @return The result of executing the delegate call, possibly {@link Void}
     */
    public <P extends Params> Return invoke(P params);
}
