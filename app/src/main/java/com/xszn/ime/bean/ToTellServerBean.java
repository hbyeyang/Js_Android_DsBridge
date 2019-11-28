package com.xszn.ime.bean;

/**
 * @author yeyang
 * @name qskd_tip_demo
 * @class name：com.xszn.ime.bean
 * @class describe
 * @time 2019-11-22 18:21
 * @change
 * @chang time
 * @class describe
 */
public class ToTellServerBean {
    private String name;
    private boolean display;

    public ToTellServerBean(String name, boolean display) {
        this.name = name;
        this.display = display;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }
}
