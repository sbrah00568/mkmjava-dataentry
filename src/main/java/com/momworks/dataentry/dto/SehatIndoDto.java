package com.momworks.dataentry.dto;

import com.momworks.dataentry.enums.Imunisasi;
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
    private boolean isImunisasiRutin; // is received imunsasi on time
    private String namaAnak;
    private String usiaAnak;
    private String tanggalLahirAnak;
    private String jenisKelaminAnak;
    private String namaOrangTua;
    private String puskesmas;

    // details as is from source might contain ideal or non-ideal imunisasi
    private Map<Imunisasi, SehatIndoImunisasiDto> detailImunisasiMap;

    // adjusted details for imunisasi rutin that must be entered into the Sehat Indonesiaku app.
    private Map<Imunisasi, SehatIndoImunisasiDto> mustSetImunisasiRutinDetails;
}
