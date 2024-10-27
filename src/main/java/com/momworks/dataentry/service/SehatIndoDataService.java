package com.momworks.dataentry.service;

import com.momworks.dataentry.dto.SehatIndoDto;
import com.momworks.dataentry.dto.SehatIndoImunisasiDto;
import com.momworks.dataentry.enums.Imunisasi;
import com.momworks.dataentry.enums.SehatIndoColumn;
import com.momworks.dataentry.util.IndonesianPublicHoliday;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

import static com.momworks.dataentry.constant.SehatIndoConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SehatIndoDataService {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN, new Locale(LANGUAGE, COUNTRY));

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
                EnumMap<Imunisasi, SehatIndoImunisasiDto> xlsxImunisasiMap = new EnumMap<>(Imunisasi.class);
                sheet.getRow(i).forEach(cell -> mapFromXlsxRows(cell, sehatIndoDto, headers, xlsxImunisasiMap));
                if (mapAndValidateImunisasi(sehatIndoDto, xlsxImunisasiMap)) {
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
            EnumMap<Imunisasi, SehatIndoImunisasiDto> xlsxImunisasiMap
    ) {
        Map<String, Consumer<String>> settersMap = getSehatIndoSettersMap(sehatIndoDto);
        String column = headers.get(cell.getColumnIndex());
        Consumer<String> setter = settersMap.get(column);
        if (setter != null) {
            setter.accept(cell.toString());
        } else {
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

    private boolean mapAndValidateImunisasi(SehatIndoDto sehatIndoDto, EnumMap<Imunisasi, SehatIndoImunisasiDto> xlsxImunisasiMap) {
        EnumMap<Imunisasi, SehatIndoImunisasiDto> imunisasiRutinMap = new EnumMap<>(Imunisasi.class);
        EnumMap<Imunisasi, SehatIndoImunisasiDto> riwayatImunisasiMap = new EnumMap<>(Imunisasi.class);

        int eligibleImunisasiRutin = ZERO;
        int eligibleRiwayatImunisasi = ZERO;

        SehatIndoImunisasiDto details = getImunisasiDetails(sehatIndoDto, xlsxImunisasiMap);
        for (Map.Entry<Imunisasi, SehatIndoImunisasiDto> xlsxImunisasi : xlsxImunisasiMap.entrySet()) {
            Imunisasi imunisasi = xlsxImunisasi.getKey();
            int status = xlsxImunisasi.getValue().getStatus();
            if (status == ONE) {
                String tanggal = tanggalAdjuster(details.getTanggal(), imunisasi);
                if (mustSetImunisasiRutin(imunisasi, sehatIndoDto.getUsiaAnak())) {
                    imunisasiRutinMap.put(imunisasi, new SehatIndoImunisasiDto(tanggal, details.getPos(), status));
                    eligibleImunisasiRutin++;
                } else {
                    riwayatImunisasiMap.put(imunisasi, new SehatIndoImunisasiDto(tanggal, details.getPos(), status));
                    eligibleRiwayatImunisasi++;
                }
            }
        }

        if (eligibleImunisasiRutin > ZERO) {
            sehatIndoDto.setImunisasiRutinMap(imunisasiRutinMap);
        }
        if (eligibleRiwayatImunisasi > ZERO) {
            sehatIndoDto.setRiwayatImunisasiMap(riwayatImunisasiMap);
        }

        return eligibleImunisasiRutin > ZERO || eligibleRiwayatImunisasi > ZERO;
    }

    private SehatIndoImunisasiDto getImunisasiDetails(SehatIndoDto sehatIndoDto, EnumMap<Imunisasi, SehatIndoImunisasiDto> xlsxImunisasiMap) {
        LocalDate tanggal = LocalDate.parse(sehatIndoDto.getTanggalLahirAnak(), formatter).withYear(LocalDate.now().getYear());
        String pos = EMPTY;

        for (SehatIndoImunisasiDto xlsxImunisasi : xlsxImunisasiMap.values()) {
            String tgl = xlsxImunisasi.getTanggal();
            if (!HYPHEN.equals(tgl) && !tanggal.isEqual(tanggal.withDayOfMonth(LocalDate.parse(tgl, formatter).getDayOfMonth()))) {
                int dayDifference = LocalDate.parse(tgl, formatter).getDayOfMonth() - tanggal.getDayOfMonth();
                tanggal = tanggal.plusDays(dayDifference);
            }

            if (!HYPHEN.equals(xlsxImunisasi.getPos())) {
                pos = xlsxImunisasi.getPos();
            }

            if (!pos.equals(EMPTY) && !tanggal.isEqual(tanggal.withDayOfMonth(LocalDate.now().getDayOfMonth()))) {
                break;
            }
        }

        pos = pos.equals(EMPTY) ? DALAM_GEDUNG : pos;

        // tanggal = (birthday of the month or previous imunisasi day of the month) + birth month
        return SehatIndoImunisasiDto.builder().tanggal(tanggal.format(formatter)).pos(pos).build();
    }


    private boolean mustSetImunisasiRutin(Imunisasi imunisasi, String usiaAnak) {
        String[] usiaAnakArr = usiaAnak.split(SPACE);
        if (imunisasi.equals(Imunisasi.HB_0)) {
            int day = Integer.parseInt(usiaAnakArr[TWO]);
            return day <= ONE;
        }
        int month = Integer.parseInt(usiaAnakArr[ZERO]);
        return month >= imunisasi.getScheduledMonth() && month < imunisasi.getEndScheduledMonth();
    }

    private String tanggalAdjuster(String strTanggal, Imunisasi imunisasi) {
        LocalDate tgl = LocalDate.parse(strTanggal, formatter);
        return holidayChecker(tgl.plusMonths((long) tgl.getMonthValue() - imunisasi.getScheduledMonth()));
    }

    private String holidayChecker(LocalDate tanggal) {
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
