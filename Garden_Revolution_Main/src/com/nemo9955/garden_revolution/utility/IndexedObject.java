package com.nemo9955.garden_revolution.utility;


public class IndexedObject<Obj> implements Comparable<Integer> {

    public Obj object;
    public int index;

    public IndexedObject(Obj point, int index) {
        this.object = point;
        this.index = index;
    }

    @Override
    public int compareTo(Integer to) {
        if ( index >to )
            return 1;
        else if ( index ==to )
            return 0;
        else
            return -1;

    }

}
