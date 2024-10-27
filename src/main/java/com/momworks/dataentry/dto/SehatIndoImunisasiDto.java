package com.momworks.dataentry.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SehatIndoImunisasiDto {
    private String tanggal;
    private String pos;
    private Integer status; // status = 0 means already received imunisasi, otherwise status = 1
}
