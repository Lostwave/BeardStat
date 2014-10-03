/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tehbeard.beardstat.bukkit.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Constructs an argument pack to allow building commands eaiser Takes an
 * argument string as input and provides simple methods to access boolean flags
 * (-a , -b) option flags (-f "flag") Concatenating strings
 * "this will not be 6 args"
 * 
 * @author james
 * 
 */
public class ArgumentPack<T> {

    private Set<String>         boolFlags;
    private Map<String, String> flagOptions;
    private List<String>        strArgs;
    private T       sender;

    public T getSender() {
        return this.sender;
    }

    public ArgumentPack(T sender, String[] boolFlags, String[] flagOpts, String[] rawArguments) {
        this(boolFlags, flagOpts, rawArguments);
        this.sender = sender;
    }

    public ArgumentPack(String[] boolFlags, String[] flagOpts, String[] rawArguments) {
        initialise(boolFlags, flagOpts, rawArguments);
    }

    public ArgumentPack(String[] boolFlags, String[] flagOpts, String rawArguments) {
        initialise(boolFlags, flagOpts, rawArguments.split(" "));
    }

    private void initialise(String[] boolFlags, String[] flagOpts, String[] rawArguments) {
        // initialise
        this.strArgs = new ArrayList<String>();
        this.boolFlags = new HashSet<String>();
        this.flagOptions = new HashMap<String, String>();
        boolean inQuotes = false;
        StringBuilder token = new StringBuilder();
        List<String> tokens = new ArrayList<String>();
        for (String rawArg : rawArguments) {
            boolean hasQuote = rawArg.startsWith("\"") || rawArg.endsWith("\"");

            if ((token.length() > 0) && !inQuotes) {
                tokens.add(token.toString().trim());
                token = new StringBuilder();
            }

            if (rawArg.startsWith("\"")) {
                token.append(rawArg.substring(1));
            } else if (rawArg.endsWith("\"")) {
                if (inQuotes && (token.length() > 0)) {
                    token.append(" ");
                }
                token.append(rawArg.substring(0, rawArg.length() - 1));
            } else {
                if (inQuotes && (token.length() > 0)) {
                    token.append(" ");
                }
                token.append(rawArg);
            }
            if (hasQuote) {
                inQuotes = !inQuotes;
            }

        }

        if (token.length() > 0) {
            tokens.add(token.toString().trim());
        }
        Iterator<String> it = tokens.iterator();
        while (it.hasNext()) {
            String tok = it.next();
            // check if it's a potential option (optFlag off, starts with -
            if (tok.startsWith("-")) {
                String t = tok.substring(1);
                if (inArray(boolFlags, t)) {
                    this.boolFlags.add(t);
                    continue;
                }
                if (inArray(flagOpts, t)) {
                    if (it.hasNext()) {
                        this.flagOptions.put(t, it.next());
                    }
                    continue;
                }
            }
            this.strArgs.add(tok);
        }

    }

    private boolean inArray(String[] arr, String search) {
        for (String a : arr) {
            if (a.equalsIgnoreCase(search)) {
                return true;
            }
        }
        return false;

    }

    /**
     * Return whether a boolean flag was set
     * 
     * @param flag
     * @return
     */
    public boolean getFlag(String flag) {
        return this.boolFlags.contains(flag);
    }

    /**
     * Return value of option flag
     * 
     * @param flag
     *            value or null on not set
     * @return
     */
    public String getOption(String flag) {
        return this.flagOptions.get(flag);
    }

    /**
     * Number of string arguments
     * 
     * @return
     */
    public int size() {
        return this.strArgs.size();
    }

    /**
     * Get a string argument
     * 
     * @param index
     * @return
     */
    public String get(int index) {
        return this.strArgs.get(index);
    }

    /**
     * Return string argument as number.
     * 
     * @param index
     * @return
     */
    public Number getNumber(int index) {
        return Double.parseDouble(get(index));
    }

}