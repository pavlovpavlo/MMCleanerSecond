package com.agento.mmcleaner.util.shared;

import java.io.Serializable;

public class SharedData implements Serializable {
    String date = "" + Long.MAX_VALUE;

    public SharedData(String date) {
        this.date = date;
    }
}
