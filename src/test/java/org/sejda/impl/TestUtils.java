/*
 * Created on 13/gen/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of sejda-itext5.
 *
 * sejda-itext5 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sejda-itext5 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with sejda-itext5.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl;

import java.lang.reflect.Field;

import org.junit.Ignore;

/**
 * @author Andrea Vacondio
 *
 */
@Ignore
public final class TestUtils {
    private TestUtils() {
        // hide
    }

    /**
     * Sets the given property to the given instance at the given value.
     * 
     * @param instance
     * @param propertyName
     * @param propertyValue
     */
    public static void setProperty(Object instance, String propertyName, Object propertyValue) {
        Field field;
        try {
            field = instance.getClass().getDeclaredField(propertyName);
            field.setAccessible(true);
            field.set(instance, propertyValue);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(String.format("Unable to set field %s", propertyName), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(String.format("Unable to set field %s", propertyName), e);
        }
    }
}
