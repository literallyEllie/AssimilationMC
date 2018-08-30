package com.assimilation.ellie.assibperms.permission;

/**
 * Created by Ellie on 22/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public enum GroupOption {

    DEFAULT(Boolean.class), PREFIX(String.class), SUFFIX(String.class);

    private Class clazz;

    GroupOption(Class clazz){
        this.clazz = clazz;
    }

    public Class getClazz() {
        return clazz;
    }

}
