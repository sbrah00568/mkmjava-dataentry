package com.momworks.dataentry.dto;

import com.momworks.dataentry.enums.Imunisasi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.EnumMap;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SehatIndoDto {
    private String namaAnak;
    private String usiaAnak;
    private String tanggalLahirAnak;
    private String jenisKelaminAnak;
    private String namaOrangTua;
    private String puskesmas;
    private EnumMap<Imunisasi, SehatIndoImunisasiDto> imunisasiRutinMap;
    private EnumMap<Imunisasi, SehatIndoImunisasiDto> riwayatImunisasiMap;
}
