package com.momworks.dataentry.service;

import com.momworks.dataentry.dto.SehatIndoDto;
import com.momworks.dataentry.dto.SehatIndoImunisasiDto;
import com.momworks.dataentry.enums.ImunisasiBaduta;
import com.momworks.dataentry.enums.ImunisasiBayi;
import com.momworks.dataentry.enums.SehatIndoColumn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import static com.momworks.dataentry.constant.SehatIndoConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SehatIndoDataService {

    public List<SehatIndoDto> retrieveData(MultipartFile xlsxFile, String type) throws IOException {
        List<SehatIndoDto> dataImunisasi = new ArrayList<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(xlsxFile.getInputStream())) {
            Sheet sheet = workbook.getSheet(SHEET_NAME);

            Map<Integer, String> headerMap = new HashMap<>();
            boolean isHeader = true;

            for (Row row : sheet) {
                int currentRowIndex = row.getRowNum();
                if (currentRowIndex < SECOND) {
                    continue; // Skip title rows
                }

                if (isHeader) {
                    extractHeaders(row, headerMap);
                    isHeader = false;
                } else {
                    SehatIndoDto sehatIndoDto = new SehatIndoDto();
                    sehatIndoDto.setType(type);
                    processRowData(row, headerMap, sehatIndoDto, type);
                    dataImunisasi.add(sehatIndoDto);
                }
            }
        }

        log.info("Data retrieval successful. File: '{}', Size: {}", xlsxFile.getOriginalFilename(), dataImunisasi.size());
        return dataImunisasi;
    }

    private void extractHeaders(Row row, Map<Integer, String> headerMap) {
        row.forEach(cell -> headerMap.put(cell.getColumnIndex(), cell.toString()));
    }

    private void processRowData(Row row, Map<Integer, String> headerMap, SehatIndoDto sehatIndoDto, String type) {
        // Map of column names to corresponding setter methods
        Map<String, Consumer<String>> settersMap = new HashMap<>();
        settersMap.put(SehatIndoColumn.NAMA_ANAK.getName(), sehatIndoDto::setNamaAnak);
        settersMap.put(SehatIndoColumn.USIA_ANAK.getName(), sehatIndoDto::setUsiaAnak);
        settersMap.put(SehatIndoColumn.TANGGAL_LAHIR_ANAK.getName(), value -> {
            sehatIndoDto.setTanggalLahirAnak(value);
            sehatIndoDto.setImunisasiRutin(isImunisasiRutin(sehatIndoDto, type));
        });
        settersMap.put(SehatIndoColumn.JENIS_KELAMIN_ANAK.getName(), sehatIndoDto::setJenisKelaminAnak);
        settersMap.put(SehatIndoColumn.NAMA_ORANG_TUA.getName(), sehatIndoDto::setNamaOrangTua);
        settersMap.put(SehatIndoColumn.PUSKESMAS.getName(), sehatIndoDto::setPuskesmas);

        row.forEach(cell -> {
            String columnName = headerMap.get(cell.getColumnIndex());
            String cellValue = cell.toString();

            // Call the appropriate setter method
            Consumer<String> setter = settersMap.get(columnName);
            if (setter != null) {
                setter.accept(cellValue);
            } else {
                processImunisasiDetails(columnName, cell, sehatIndoDto, type);
            }
        });
    }

    private boolean isImunisasiRutin(SehatIndoDto sehatIndoDto, String type) {
        int month = Integer.parseInt(sehatIndoDto.getUsiaAnak().split(SPACE)[ZERO]);
        return month >= (type.equals(BAYI) ? 0 : 12) && month < (type.equals(BAYI) ? 12 : 24);
    }

    private void processImunisasiDetails(String columnName, Cell cell, SehatIndoDto sehatIndoDto, String type) {
        String code = BAYI.equals(type) ? ImunisasiBayi.getCode(columnName) : ImunisasiBaduta.getCode(columnName);

        if (sehatIndoDto.getDetailImunisasiMap() == null) {
            sehatIndoDto.setDetailImunisasiMap(new HashMap<>());
        }

        sehatIndoDto.getDetailImunisasiMap().computeIfAbsent(code, k -> new SehatIndoImunisasiDto());

        SehatIndoImunisasiDto detailImunisasi = sehatIndoDto.getDetailImunisasiMap().get(code);
        String cellValue = cell.toString();
        if (columnName.contains(SehatIndoColumn.TANGGAL.getName())) {
            detailImunisasi.setTanggal(cellValue);
        } else if (columnName.contains(SehatIndoColumn.POS.getName())) {
            detailImunisasi.setPos(cellValue);
        } else if (columnName.contains(SehatIndoColumn.STATUS.getName())) {
            String statusValue = cellValue.replace(POINT_ZERO, EMPTY);
            detailImunisasi.setStatus(Integer.valueOf(statusValue));
        }

    }

    public String getPosName(SehatIndoDto sehatIndoDto) {
        StringBuilder posBuilder = new StringBuilder();
        sehatIndoDto.getDetailImunisasiMap().values().stream().map(SehatIndoImunisasiDto::getPos)
                .filter(this::isNotHyphen).map(this::posTrimmer).findFirst().ifPresent(posBuilder::append);
        String pos = posBuilder.toString();
        return pos.isEmpty() ? DALAM_GEDUNG : pos;
    }

    private boolean isNotHyphen(String pos) {
        return !Objects.equals(pos, HYPHEN);
    }

    private String posTrimmer(String pos) {
        return pos.replace(PYD, EMPTY).replace(POSYANDU, EMPTY).replace(HYPHEN_WANASARI, EMPTY);
    }

    public String getHasilPencarian(Elements hasilPencarianElements) {
        for (Element hasilPencarianElement : hasilPencarianElements) {
            String contentDesc = hasilPencarianElement.attr(ATTR_KEY_CONTENT_DESC);
            if (isHasilPencarianValid(contentDesc)) {
                return contentDesc;
            }
        }
        return EMPTY;
    }

    private boolean isHasilPencarianValid(String contentDesc) {
        return !Objects.equals(contentDesc, EMPTY) && !Objects.equals(contentDesc, PILIH)
                && !Objects.equals(contentDesc, HASIL_PENCARIAN) && contentDesc.contains(WANASARI);
    }


}
