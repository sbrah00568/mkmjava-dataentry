package com.momworks.dataentry.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImunisasiBayi {

    HB_0("HB0"),
    BCG_1("BCG 1"),
    POLIO_1("POLIO 1"),
    POLIO_2("POLIO 2"),
    POLIO_3("POLIO 3"),
    POLIO_4("POLIO 4"),
    DPT_HB_HIB_1("DPT-Hb-Hib 1"),
    DPT_HB_HIB_2("DPT-Hb-Hib 2"),
    DPT_HB_HIB_3("DPT-Hb-Hib 3"),
    IPV_1("IPV 1"),
    IPV_2("IPV 2"),
    ROTA_1("ROTA 1"),
    ROTA_2("ROTA 2"),
    ROTA_3("ROTA 3"),
    PCV_1("PCV 1"),
    PCV_2("PCV 2"),
    MR_1("MR 1"),
    IDL_1("IDL 1");

    private final String name;

    public static String getCode(String columnName) {
        for (ImunisasiBayi imunisasiBayi : ImunisasiBayi.values()) {
            if (columnName.contains(imunisasiBayi.getName())) {
                return imunisasiBayi.getName();
            }
        }
        return null;
    }

}
