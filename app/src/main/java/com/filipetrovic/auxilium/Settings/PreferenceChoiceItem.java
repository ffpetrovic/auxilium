package com.filipetrovic.auxilium.Settings;

public class PreferenceChoiceItem {
    private String name;
    private String value;
    private int index;

    public PreferenceChoiceItem(String n, String v, int i) {
        name = n;
        value = v;
        index = i;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
