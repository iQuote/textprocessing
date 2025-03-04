package io.github.iquote.text_processing.language_detection.util;

/*
 * Copyright (C) 2010-2014 Cybozu Labs, 2016 Konstantin Gusarov
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This is {@link io.github.iquote.text_processing.language_detection.util.Messages} class generated by Eclipse automatically.
 * Users don't use this class directly.
 *
 * @author Nakatani Shuyo
 * @author Konstantin Gusarov
 */
final class Messages {
    private static final Logger LOGGER = LoggerFactory.getLogger(Messages.class);

    private static final String BUNDLE_NAME = Messages.class.getName();
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Messages() {
    }

    static String getString(final String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (final MissingResourceException e) {
            LOGGER.warn("Unable to find message for key " + key, e);
            return '!' + key + '!';
        }
    }
}
