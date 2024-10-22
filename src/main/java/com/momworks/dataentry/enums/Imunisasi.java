package com.momworks.dataentry.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

import static com.momworks.dataentry.constant.SehatIndoConstants.*;

@Getter
@AllArgsConstructor
public enum Imunisasi {

    HB_0("HB0", BAYI),
    BCG_1("BCG 1", BAYI),
    POLIO_1("POLIO 1", BAYI),
    POLIO_2("POLIO 2", BAYI),
    POLIO_3("POLIO 3", BAYI),
    POLIO_4("POLIO 4", BAYI),
    DPT_HB_HIB_1("DPT-Hb-Hib 1", BAYI),
    DPT_HB_HIB_2("DPT-Hb-Hib 2", BAYI),
    DPT_HB_HIB_3("DPT-Hb-Hib 3", BAYI),
    IPV_1("IPV 1", BAYI),
    IPV_2("IPV 2", BAYI),
    ROTA_1("ROTA 1", BAYI),
    ROTA_2("ROTA 2", BAYI),
    ROTA_3("ROTA 3", BAYI),
    PCV_1("PCV 1", BAYI),
    PCV_2("PCV 2", BAYI),
    MR_1("MR 1", BAYI),
    IDL_1("IDL 1", BAYI),
    DPT_HB_HIB_4("DPT-Hb-Hib 4", BADUTA),
    MR_2("MR 2", BADUTA),
    IBL_1("IBL 1", BADUTA),
    PCV_3("PCV 3", BADUTA);

    private final String name;
    private final String target;

    public static Imunisasi getImunisasi(String columnName) {
        for (Imunisasi imunisasi : Imunisasi.values()) {
            if (columnName.contains(imunisasi.getName())) {
                return imunisasi;
            }
        }
        return null;
    }

    public static boolean isImunisasiRutin(String usiaAnak, Imunisasi imunisasi) {
        int month = Integer.parseInt(usiaAnak.split(SPACE)[ZERO]);
        return Objects.equals(imunisasi.getTarget(), BAYI) ?
                month >= ZERO && month < TWELVE :
                month >= TWELVE && month < TWENTY_FOUR;
    }


}