package com.momworks.dataentry.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SehatIndoDto {
    private String type;
    private boolean isImunisasiRutin;
    private String namaAnak;
    private String usiaAnak;
    private String tanggalLahirAnak;
    private String jenisKelaminAnak;
    private String namaOrangTua;
    private String puskesmas;
    private Map<String, SehatIndoImunisasiDto> detailImunisasiMap;
}
