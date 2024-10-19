package com.momworks.dataentry.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SehatIndoColumn {

    NAMA_ANAK("Nama Anak"),
    USIA_ANAK("Usia Anak"),
    TANGGAL_LAHIR_ANAK("Tanggal Lahir Anak"),
    JENIS_KELAMIN_ANAK("Jenis Kelamin Anak"),
    NAMA_ORANG_TUA("Nama Orang Tua"),
    PUSKESMAS("Puskesmas"),
    TANGGAL("Tanggal"),
    POS("Pos"),
    STATUS("Status");

    private final String name;
}
