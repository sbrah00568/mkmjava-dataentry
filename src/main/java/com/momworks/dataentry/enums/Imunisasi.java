package com.momworks.dataentry.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.momworks.dataentry.constant.SehatIndoConstants.*;

@Getter
@AllArgsConstructor
public enum Imunisasi {

    HB_0("HB0", BAYI, ZERO, ZERO),
    BCG_1("BCG 1", BAYI, ONE, TWO),
    POLIO_1("POLIO 1", BAYI, ONE, TWO),
    POLIO_2("POLIO 2", BAYI, TWO, THREE),
    POLIO_3("POLIO 3", BAYI, THREE, FOUR),
    POLIO_4("POLIO 4", BAYI, FOUR, NINE),
    DPT_HB_HIB_1("DPT-Hb-Hib 1", BAYI, TWO, THREE),
    DPT_HB_HIB_2("DPT-Hb-Hib 2", BAYI, THREE, FOUR),
    DPT_HB_HIB_3("DPT-Hb-Hib 3", BAYI, FOUR, NINE),
    IPV_1("IPV 1", BAYI, FOUR, NINE),
    IPV_2("IPV 2", BAYI, NINE, TWELVE),
    ROTA_1("ROTA 1", BAYI, TWO, THREE),
    ROTA_2("ROTA 2", BAYI, THREE, FOUR),
    ROTA_3("ROTA 3", BAYI, FOUR, NINE),
    PCV_1("PCV 1", BAYI, TWO, THREE),
    PCV_2("PCV 2", BAYI, THREE, FOUR),
    MR_1("MR 1", BAYI, NINE, TWELVE),
    IDL_1("IDL 1", BAYI, ZERO, ONE),
    PCV_3("PCV 3", BADUTA, TWELVE, EIGHTEEN),
    DPT_HB_HIB_4("DPT-Hb-Hib 4", BADUTA, EIGHTEEN, TWENTY_FOUR),
    MR_2("MR 2", BADUTA, EIGHTEEN, TWENTY_FOUR),
    IBL_1("IBL 1", BADUTA, TWELVE, EIGHTEEN);

    private final String name;
    private final String target;
    private final int scheduledMonth;
    private final int endScheduledMonth;

    public static Imunisasi getImunisasi(String columnName) {
        for (Imunisasi imunisasi : Imunisasi.values()) {
            if (columnName.contains(imunisasi.getName())) {
                return imunisasi;
            }
        }
        return null;
    }

}
