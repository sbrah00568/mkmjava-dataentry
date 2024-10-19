package com.momworks.dataentry.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImunisasiBaduta {

    DPT_HB_HIB_4("DPT-Hb-Hib 4"),
    MR_2("MR 2"),
    IBL_1("IBL 1"),
    PCV_3("PCV 3");

    private final String name;

    public static String getCode(String columnName) {
        for (ImunisasiBaduta imunisasiBaduta : ImunisasiBaduta.values()) {
            if (columnName.contains(imunisasiBaduta.getName())) {
                return imunisasiBaduta.getName();
            }
        }
        return null;
    }

}
