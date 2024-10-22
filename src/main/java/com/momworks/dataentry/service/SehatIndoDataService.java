package com.momworks.dataentry.service;

import com.momworks.dataentry.dto.SehatIndoDto;
import com.momworks.dataentry.dto.SehatIndoImunisasiDto;
import com.momworks.dataentry.enums.Imunisasi;
import com.momworks.dataentry.enums.SehatIndoColumn;
import com.momworks.dataentry.util.IndonesianPublicHoliday;
import com.momworks.dataentry.util.SehatIndoImunisasiMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

import static com.momworks.dataentry.constant.SehatIndoConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SehatIndoDataService {
    public List<SehatIndoDto> retrieveData(MultipartFile xlsxFile) throws IOException {
        List<SehatIndoDto> dataImunisasi = new ArrayList<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(xlsxFile.getInputStream())) {
            Sheet sheet = workbook.getSheet(SHEET_NAME);

            // Retrieve header
            HashMap<Integer, String> headers = new HashMap<>();
            sheet.getRow(TWO).forEach(cell -> headers.putIfAbsent(cell.getColumnIndex(), cell.toString()));

            // Set body data
            for (int i = THREE; i <= sheet.getLastRowNum(); i++) {
                SehatIndoDto sehatIndoDto = new SehatIndoDto();
                SehatIndoImunisasiMap xlsxImunisasiMap = new SehatIndoImunisasiMap(Imunisasi.class);
                sheet.getRow(i).forEach(cell -> mapFromXlsxRows(cell, sehatIndoDto, headers, xlsxImunisasiMap));
                if (isSehatIndoDtoValid(sehatIndoDto, xlsxImunisasiMap)) {
                    if (sehatIndoDto.isImunisasiRutin()) {
                        mapImunisasiRutin(sehatIndoDto, xlsxImunisasiMap);
                    }
                    dataImunisasi.add(sehatIndoDto);
                }
            }

        }

        log.info("Data retrieval successful. (File: '{}', Size: {}).", xlsxFile.getOriginalFilename(), dataImunisasi.size());
        return dataImunisasi;
    }

    private void mapFromXlsxRows(
            Cell cell,
            SehatIndoDto sehatIndoDto,
            HashMap<Integer, String> headers,
            SehatIndoImunisasiMap xlsxImunisasiMap
    ) {
        Map<String, Consumer<String>> settersMap = getSehatIndoSettersMap(sehatIndoDto);
        String column = headers.get(cell.getColumnIndex());
        Consumer<String> setter = settersMap.get(column);
        if (setter != null) {
            setter.accept(cell.toString());
        } else {
            mapXlsxImunisasiDetails(column, cell, xlsxImunisasiMap);
        }
    }

    private Map<String, Consumer<String>> getSehatIndoSettersMap(SehatIndoDto sehatIndoDto) {
        return new HashMap<>(Map.ofEntries(
                Map.entry(SehatIndoColumn.NAMA_ANAK.getName(), sehatIndoDto::setNamaAnak),
                Map.entry(SehatIndoColumn.USIA_ANAK.getName(), sehatIndoDto::setUsiaAnak),
                Map.entry(SehatIndoColumn.TANGGAL_LAHIR_ANAK.getName(), sehatIndoDto::setTanggalLahirAnak),
                Map.entry(SehatIndoColumn.JENIS_KELAMIN_ANAK.getName(), sehatIndoDto::setJenisKelaminAnak),
                Map.entry(SehatIndoColumn.NAMA_ORANG_TUA.getName(), sehatIndoDto::setNamaOrangTua),
                Map.entry(SehatIndoColumn.PUSKESMAS.getName(), sehatIndoDto::setPuskesmas)
        ));
    }

    private void mapXlsxImunisasiDetails(String column, Cell cell, SehatIndoImunisasiMap xlsxImunisasiMap) {
        Imunisasi code = Imunisasi.getImunisasi(column);
        xlsxImunisasiMap.computeIfAbsent(code, k -> new SehatIndoImunisasiDto());

        if (column.contains(SehatIndoColumn.TANGGAL.getName())) {
            xlsxImunisasiMap.get(code).setTanggal(cell.toString());
        } else if (column.contains(SehatIndoColumn.POS.getName())) {
            xlsxImunisasiMap.get(code).setPos(cell.toString());
        } else if (column.contains(SehatIndoColumn.STATUS.getName())) {
            String statusValue = cell.toString().replace(POINT_ZERO, EMPTY);
            xlsxImunisasiMap.get(code).setStatus(Integer.valueOf(statusValue));
        }
    }

    private boolean isSehatIndoDtoValid(SehatIndoDto sehatIndoDto, SehatIndoImunisasiMap xlsxImunisasiMap) {
        int eligibleImunisasi = ZERO;
        String usiaAnak = sehatIndoDto.getUsiaAnak();
        for (Map.Entry<Imunisasi, SehatIndoImunisasiDto> map : xlsxImunisasiMap.entrySet()) {
            // ONE means not yet received imunisasi rutin
            Imunisasi imunisasi = map.getKey();
            if (map.getValue().getStatus() == ONE && mustSetImunisasiRutin(imunisasi, usiaAnak)) {
                sehatIndoDto.setImunisasiRutin(Imunisasi.isImunisasiRutin(sehatIndoDto.getUsiaAnak(), imunisasi));
                if (sehatIndoDto.getImunisasiRutinMap() == null) {
                    sehatIndoDto.setImunisasiRutinMap(new SehatIndoImunisasiMap(Imunisasi.class));
                }
                sehatIndoDto.getImunisasiRutinMap().computeIfAbsent(imunisasi, k -> new SehatIndoImunisasiDto());
                eligibleImunisasi++;
            }
        }
        return eligibleImunisasi > ZERO;
    }

    private boolean mustSetImunisasiRutin(Imunisasi imunisasi, String usiaAnak) {
        int day = Integer.parseInt(usiaAnak.split(SPACE)[TWO]);
        int month = Integer.parseInt(usiaAnak.split(SPACE)[ZERO]);
        return switch (imunisasi) {
            case HB_0 -> day <= ONE;
            case BCG_1, POLIO_1 -> month >= ONE && month < TWO;
            case DPT_HB_HIB_1, POLIO_2, PCV_1, ROTA_1 -> month >= TWO && month < THREE;
            case DPT_HB_HIB_2, POLIO_3, PCV_2, ROTA_2 -> month >= THREE && month < FOUR;
            case DPT_HB_HIB_3, POLIO_4, IPV_1, ROTA_3 -> month >= FOUR && month < NINE;
            case MR_1, IPV_2 -> month >= NINE && month < TWELVE;
            case PCV_3 -> month >= TWELVE && month < EIGHTEEN;
            case MR_2, DPT_HB_HIB_4 -> month >= EIGHTEEN && month < TWENTY_FOUR;
            default -> false;
        };
    }

    private void mapImunisasiRutin(SehatIndoDto sehatIndoDto, SehatIndoImunisasiMap xlsxImunisasiMap) {
        String[] tglParts = sehatIndoDto.getTanggalLahirAnak().split(HYPHEN);
        String tanggal = LocalDateTime.now().getYear() + HYPHEN + tglParts[ONE] + HYPHEN + tglParts[TWO];// default value
        String pos = DALAM_GEDUNG;  // default value

        boolean isTanggalAdjusted = false;
        boolean isPosAdjusted = false;
        for (SehatIndoImunisasiDto xlsxImunisasi : xlsxImunisasiMap.values()) {
            if (!HYPHEN.equals(xlsxImunisasi.getTanggal())) {
                tanggal = xlsxImunisasi.getTanggal();
                isTanggalAdjusted = true;
            }
            if (!HYPHEN.equals(xlsxImunisasi.getPos())) {
                pos = xlsxImunisasi.getPos();
                isPosAdjusted = true;
            }
            if (isTanggalAdjusted && isPosAdjusted) {
                break;
            }
        }

        for (Map.Entry<Imunisasi, SehatIndoImunisasiDto> rutinMap : sehatIndoDto.getImunisasiRutinMap().entrySet()) {
            rutinMap.getValue().setTanggal(holidayChecker(tanggal));
            rutinMap.getValue().setPos(pos);
            rutinMap.getValue().setStatus(xlsxImunisasiMap.get(rutinMap.getKey()).getStatus());
        }
    }

    private String holidayChecker(String strTanggal) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN, new Locale(LANGUAGE, COUNTRY));
        LocalDate tanggal = LocalDate.parse(strTanggal, formatter);
        while (tanggal.getDayOfWeek() == DayOfWeek.SUNDAY || IndonesianPublicHoliday.isPublicHoliday(tanggal)) {
            tanggal = tanggal.plusDays(ONE);
        }
        return tanggal.format(formatter);
    }

    public String getPosName(SehatIndoDto sehatIndoDto) {
        StringBuilder posBuilder = new StringBuilder();
        sehatIndoDto.getImunisasiRutinMap().values().stream().map(SehatIndoImunisasiDto::getPos)
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
