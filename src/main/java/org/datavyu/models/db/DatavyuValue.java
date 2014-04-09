/*
 * Copyright (c) 2011 Datavyu Foundation, http://datavyu.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.datavyu.models.db;

import org.datavyu.Datavyu;

import java.io.Serializable;
import java.util.UUID;
import org.datavyu.Datavyu;
import org.datavyu.util.StringUtils;


public abstract class DatavyuValue implements Value, Serializable, Comparable<DatavyuValue> {

    String value;
    int index;
    UUID parent_id;
    UUID id = UUID.randomUUID();
    String name = "";
    Argument arg;
    Datastore owningDatastore;

    @Override
    public boolean isValid(final String value) {
        return true;
    } 

    @Override
    public void clear() {
        this.value = null;
    }

    @Override
    public boolean isEmpty() {
        if (value == null || value.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
    
    public int getIndex() {
        return index;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int compareTo(DatavyuValue v) {
        if (this.getIndex() < v.getIndex()) {
            return -1;
        } else if (this.getIndex() > v.getIndex()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public void set(final String newValue)
    {
        if(!newValue.equals(toString()) && !newValue.equals(this.value))
        {
            this.value = newValue;
            if (owningDatastore != null) owningDatastore.markDBAsChanged();
        }
        else
        {
            //System.out.println("STOPPED set to " + newValue);
        }
    };

    public Argument getArgument() {
        return arg;
    }

    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "<" + arg.name + ">";
        } else {
            return value;
        }
    }
    
    public String serialize() {
        if(value == null) return "";
        return StringUtils.escapeCSVArgument(value);
    }
}
