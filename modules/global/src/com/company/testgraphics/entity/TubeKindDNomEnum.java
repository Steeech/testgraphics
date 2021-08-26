package com.company.testgraphics.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum TubeKindDNomEnum implements EnumClass<Integer> {

    FiveHundredThirty(530),
    SixHundredThirty(630),
    SevenHundredTwenty(720),
    EightHundredTwenty(820),
    OneThousandTwenty(1020),
    OneThousandTwoHundredTwenty(1220),
    OneThousandFourHundredTwenty(1420);

    private Integer id;

    TubeKindDNomEnum(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static TubeKindDNomEnum fromId(Integer id) {
        for (TubeKindDNomEnum at : TubeKindDNomEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}