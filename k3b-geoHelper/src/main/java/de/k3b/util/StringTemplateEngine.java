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
 * Engine that replaces all occurences of ${name} with corresponding values from callback defined in {@link IValueResolver}.<br/>
 *
 * <code>
 * new StringTemplate(valueResolver).format("hello ${first.name}!")
 * </code>
 *
 * will produce "hello world!" if <code>valueResolver.get("first","name") returns "world"</code>
 *
 * Created by k3b on 01.04.2015.
 */
public class StringTemplateEngine {
    /** used to convert ${name} to corresponding value */
    protected IValueResolver valueResolver = null;

    /** if {@link StringTemplateEngine#sedDebugEnabled(boolean)} is set to true.
     * this stack will contain debugbessages as callstack */
    protected Stack<String> debugStack = null; // new Stack<String>();

    /** contain last processing error or null if there is no (new) error */
    private StringBuilder errors = null;


    // matches ${a.b}
    private static final Pattern pattern = Pattern.compile("\\$\\{([^ \\.\\}]+)\\.([^ }]+)\\}");

    /**
     * callback for {@link StringTemplateEngine} to resolve the values for names.
     *
     * <code>
     * new StringTemplate(valueResolver).format("hello ${first.name}!")
     * </code>
     *
     * will produce "hello world!" if <code>valueResolver.get("first","name") returns "world"</code>
     *
     * Created by k3b on 01.04.2015.
     */
    public interface IValueResolver {
        String get(String className, String propertyName, String templateParameter);
    }

    /** creates the engine with {@link IValueResolver}. */
    public StringTemplateEngine(IValueResolver valueResolver) {
        this.valueResolver = valueResolver;
    }

    /** return strue, if value contains tempate parameters */
    protected boolean hasParameters(String value) {
        return ((value != null) && (value.contains("${")));
    }

    /** translates tmeplate to string. */
    public String format(String template) {
        if (template == null) {
            return null;
        }

        final StringBuffer stringBuffer = new StringBuffer();
        final Matcher match = pattern.matcher(template);
        while (match.find()) {
            final String templateParameter = match.group(0);
            final String className = match.group(1);
            final String propertyName = match.group(2);
            debugPush(templateParameter);
            String resolverResult = valueResolver.get(className, propertyName, templateParameter);
            resolverResult = onResolverResult(templateParameter, resolverResult);
            if (resolverResult != null) {
                match.appendReplacement(stringBuffer, resolverResult);
            } else {
                match.appendReplacement(stringBuffer, "");
            }
            debugPop();
        }
        match.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    /** called for every parameter match. Is used to collect debug infos. */
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

    /** en- or disables debugging mode. */
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

    /** debug implementation: push to stack.  */
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

    /** debug implementation: pop from stack: forget last {@link #debugPush(String)}.  */
    protected void debugPop() {
        if (debugStack != null) {
            debugStack.pop();
        }
    }

    /** show debug stacktrace, if {@link StringTemplateEngine#sedDebugEnabled(boolean)} is set to true.  */
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

    /** get last error message. Error is cleared after reading message. */
    public String getErrors() {
        if (errors == null) return null;

        final String result = errors.toString();
        errors.setLength(0);
        return result;
    }
}
