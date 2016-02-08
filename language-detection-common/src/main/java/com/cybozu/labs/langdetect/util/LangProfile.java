package com.cybozu.labs.langdetect.util;

/*
 * Copyright (C) 2016 Konstantin Gusarov
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
 */

import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * {@link LangProfile} is a Language Profile Class.
 * Users don't use this class directly.
 *
 * @author Nakatani Shuyo
 * @author Konstantin Gusarov
 */
public abstract class LangProfile {
    private static final int MINIMUM_FREQ = 2;
    private static final int LESS_FREQ_RATIO = 100000;
    private static final Pattern ROMAN_CHECK_REGEX = Pattern.compile(".*[A-Za-z].*");
    private static final Pattern LATIN_CHECK_REGEX = Pattern.compile("^[A-Za-z]$");

    final Map<String, Integer> frequencies = Maps.newHashMap();
    final int[] nGramCount = new int[NGram.MAX_NGRAM_LENGTH];

    private final String name;

    /**
     * @param name              Language name
     * @param initialFreqs      Initial frequency information
     * @param nGramCount        N-Gram counts in initial fre information
     */
    protected LangProfile(final String name, final Map<String, Integer> initialFreqs, final int[] nGramCount) {
        this.name = name;

        final int l = nGramCount.length;
        if (l != NGram.MAX_NGRAM_LENGTH) {
            throw new IllegalArgumentException("Invalid n-gram count: " + l);
        }

        System.arraycopy(nGramCount, 0, this.nGramCount, 0, l);

        frequencies.putAll(initialFreqs);
    }

    /**
     * Add n-gram to profile
     *
     * @param gram      N-Gram to be added
     */
    public void add(final String gram) {
        if (name == null || gram == null) {
            // Illegal
            return;
        }

        final int len = gram.length();
        if (len < 1 || len > NGram.MAX_NGRAM_LENGTH) {
            // Illegal
            return;
        }

        nGramCount[len - 1]++;
        final Integer current = frequencies.get(gram);
        final Integer val = (current == null)? 1 : current + 1;

        frequencies.put(gram, val);
    }

    /**
     * Eliminate below less frequency n-grams and noise Latin alphabets
     */
    public void omitLessFreq() {
        if (name == null) {
            // Illegal
            return;
        }

        int threshold = nGramCount[0] / LESS_FREQ_RATIO;
        if (threshold < MINIMUM_FREQ) {
            threshold = MINIMUM_FREQ;
        }

        int roman = 0;
        final Set<String> keys = frequencies.keySet();

        for (final Iterator<String> i = keys.iterator(); i.hasNext(); ) {
            final String key = i.next();
            final int count = frequencies.get(key);

            if (count <= threshold) {
                nGramCount[key.length() - 1] -= count;
                i.remove();
            } else {
                if (LATIN_CHECK_REGEX.matcher(key).matches()) {
                    roman += count;
                }
            }
        }

        // roman check
        if (roman < nGramCount[0] / NGram.MAX_NGRAM_LENGTH) {
            final Set<String> keys2 = frequencies.keySet();

            for (final Iterator<String> i = keys2.iterator(); i.hasNext(); ) {
                final String key = i.next();
                if (ROMAN_CHECK_REGEX.matcher(key).matches()) {
                    nGramCount[key.length() - 1] -= frequencies.get(key);
                    i.remove();
                }
            }
        }
    }

    /**
     * Update the language profile with (fragmented) text.
     * Extract n-grams from text and add their frequency into the profile.
     *
     * @param text          (Fragmented) Text to extract n-grams
     */
    public void update(String text) {
        if (text == null) return;
        text = NGram.normalizeVietnamese(text);
        final NGram gram = new NGram();
        for (int i = 0; i < text.length(); ++i) {
            final char ch = text.charAt(i);
            gram.addChar(ch);

            for (int n = 1; n <= NGram.MAX_NGRAM_LENGTH; ++n) {
                final String s = gram.get(n);
                add(s);
            }
        }
    }

    /**
     * Get name of the language profile
     *
     * @return          Name of this language profile
     */
    public String getName() {
        return name;
    }

    /**
     * Get frequencies from the language profile
     *
     * @return          N-Gram frequency table
     */
    public Map<String, Integer> getFrequencies() {
        return frequencies;
    }

    /**
     * Get counts of all n-grams for the language profile
     *
     * @return          N-Gram count table
     */
    public int[] getNGramCount() {
        return nGramCount;
    }
}
