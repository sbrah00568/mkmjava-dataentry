package com.momworks.dataentry.dto;

import com.momworks.dataentry.util.SehatIndoImunisasiMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // adjusted details for imunisasi rutin that must be entered into the Sehat Indonesiaku app.
    private SehatIndoImunisasiMap imunisasiRutinMap;
}
