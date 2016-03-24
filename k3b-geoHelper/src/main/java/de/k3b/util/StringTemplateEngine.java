/*
 * Copyright (c) 2015-2016 by k3b.
 *
 * This file is part of k3b-geoHelper library.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.k3b.util;

import java.util.ListIterator;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Engine that replaces all occurences of ${name} with corresponding values from callback.<br/>
 * new StringTemplate(valueResolver).format("hello ${first.name}!") will produce
 * "hello world!" if valueResolver.get("first","name") returns "world"
 * becomes "hello world!"
 *
 * Created by k3b on 01.04.2015.
 */
public class StringTemplateEngine {
    protected IValueResolver valueResolver = null;
    protected Stack<String> debugStack = null; // new Stack<String>();
    private StringBuilder errors = null;


    // ${a.b}
    private static final Pattern pattern = Pattern.compile("\\$\\{([^ \\.\\}]+)\\.([^ }]+)\\}");

    /** return strue, if value contains tempate parameters */
    protected boolean hasParameters(String value) {
        return ((value != null) && (value.contains("${")));
    }

    public StringTemplateEngine sedDebugEnabled(boolean enabled) {
        if (enabled) {
            this.debugStack = new Stack<String>();
            errors = new StringBuilder();
        } else {
            this.debugStack = null;
            errors = null;
        }
        return this;
    }

    public interface IValueResolver {
        String get(String className, String propertyName, String templateParameter);
    }

    public StringTemplateEngine(IValueResolver valueResolver) {
        this.valueResolver = valueResolver;
    }

    public String format(String template) {
        if (template == null) {
            return null;
        }

        final StringBuffer buffer = new StringBuffer();
        final Matcher match = pattern.matcher(template);
        while (match.find()) {
            final String templateParameter = match.group(0);
            final String className = match.group(1);
            final String propertyName = match.group(2);
            debugPush(templateParameter);
            String resolverResult = valueResolver.get(className, propertyName, templateParameter);
            resolverResult = onResolverResult(templateParameter, resolverResult);
            if (resolverResult != null) {
                match.appendReplacement(buffer, resolverResult);
            } else {
                match.appendReplacement(buffer, "");
            }
            debugPop();
        }
        match.appendTail(buffer);
        return buffer.toString();
    }

    protected String onResolverResult(String templateParameter, String resolverResult) {
        // log error if resolve failed
        if ((errors != null) && (resolverResult == null)) {
            this.errors
                    .append(templateParameter)
                    .append(" not found in ")
                    .append(getDebugStackTrace())
                    .append("\n");
        }

        return resolverResult;
    }

    protected void debugPush(String parameter) {
        if (debugStack != null) {
            if (debugStack.contains(parameter)) {
                final String errorMessage = this.getClass().toString() +
                        ".format() : " + parameter +
                        " - endless recursion detected " + getDebugStackTrace();
                if (errors != null) {
                    this.errors
                            .append(errorMessage)
                            .append("\n");
                }
                throw new StackOverflowError(errorMessage);
            }
            debugStack.push(parameter);
        }
    }

    protected void debugPop() {
        if (debugStack != null) {
            debugStack.pop();
        }
    }

    protected String getDebugStackTrace() {
        StringBuilder result = new StringBuilder();
        if (debugStack != null) {
            ListIterator<String> iter = debugStack.listIterator();
            if (iter.hasNext()) {
                result.append(" > ").append(iter.next());
            }
        }
        return result.toString();
    }

    public String getErrors() {
        if (errors == null) return null;

        final String result = errors.toString();
        errors.setLength(0);
        return result;
    }
}
