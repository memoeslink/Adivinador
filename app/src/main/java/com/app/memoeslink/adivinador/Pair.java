package com.app.memoeslink.adivinador;

/**
 * Created by Memoeslink on 27/03/2018.
 */

public class Pair<A, B> {
    private A subKey = null;
    private B subValue = null;

    public Pair(A subKey, B subValue) {
        this.subKey = subKey;
        this.subValue = subValue;
    }

    public A getSubKey() {
        return subKey;
    }

    public void setSubKey(A subKey) {
        this.subKey = subKey;
    }

    public B getSubValue() {
        return subValue;
    }

    public void setSubValue(B subValue) {
        this.subValue = subValue;
    }
}
