package com.example.david.dabplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 2018-07-15.
 */

// Super List!!
public class SList<T> extends ArrayList<T> {

    // get Rear
    public T getR(int index) {
        return this.get(this.size() - 1 - index);
    }
}
