package com.agento.mmcleaner.scan_util.model;

import java.util.ArrayList;

/**
 * Created by mazhuang on 16/1/14.
 */
public class JunkGroup {
    public static final int GROUP_CACHE = 0;
    public static final int GROUP_ADVERTISING = 1;
    public static final int GROUP_TEMPORARY_FILES = 2;
    public static final int GROUP_APK = 3;

    public int mType;
    public long mSize;
    public boolean isOpen = false;
    public boolean isCheck = true;
    public ArrayList<JunkInfo> mChildren = new ArrayList<>();
}
