package com.company.testgraphics.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum LaserTypeEnum implements EnumClass<String> {

    PROFILE_IN("profile_in"),
    PROFILE_OUT("profile_out");

    private String id;

    LaserTypeEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static LaserTypeEnum fromId(String id) {
        for (LaserTypeEnum at : LaserTypeEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}