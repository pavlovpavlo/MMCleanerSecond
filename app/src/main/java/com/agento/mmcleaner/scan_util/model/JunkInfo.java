package com.agento.mmcleaner.scan_util.model;


import java.util.ArrayList;
import java.util.Objects;

public class JunkInfo implements Comparable<JunkInfo> {
    public String name = "";
    public long mSize = -1;
    public String mPackageName = "";
    public String mPath = "";
    public ArrayList<JunkInfo> mChildren = new ArrayList<>();
    public boolean mIsVisible = false;
    public boolean isCheck = true;
    public boolean mIsChild = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JunkInfo junkInfo = (JunkInfo) o;
        return Objects.equals(mPackageName, junkInfo.mPackageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mPackageName);
    }

    @Override
    public int compareTo(JunkInfo another) {
        String top = "memo";

        if (this.name != null && this.name.equals(top)) {
            return 1;
        }

        if (another.name != null && another.name.equals(top)) {
            return -1;
        }

        if (this.mSize > another.mSize) {
            return 1;
        } else if (this.mSize < another.mSize) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone(); // You can also provide your own implementation here
    }
}
