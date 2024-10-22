package com.momworks.dataentry.util;

import com.momworks.dataentry.dto.SehatIndoImunisasiDto;
import com.momworks.dataentry.enums.Imunisasi;

import java.util.EnumMap;

public class SehatIndoImunisasiMap extends EnumMap<Imunisasi, SehatIndoImunisasiDto> {
    public SehatIndoImunisasiMap(Class<Imunisasi> keyType) {
        super(keyType);
    }
}
