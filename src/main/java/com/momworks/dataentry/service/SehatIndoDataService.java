package com.momworks.dataentry.service;

import com.momworks.dataentry.dto.SehatIndoDto;
import com.momworks.dataentry.dto.SehatIndoImunisasiDto;
import com.momworks.dataentry.enums.Imunisasi;
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

    public List<SehatIndoDto> retrieveData(MultipartFile xlsxFile) throws IOException {
        List<SehatIndoDto> dataImunisasi = new ArrayList<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(xlsxFile.getInputStream())) {
            Sheet sheet = workbook.getSheet(SHEET_NAME);

            // Retrieve header
            Map<Integer, String> headerMap = new HashMap<>();
            sheet.getRow(TWO).forEach(cell -> headerMap.putIfAbsent(cell.getColumnIndex(), cell.toString()));

            // Set body data
            for (int i = THREE; i <= sheet.getLastRowNum(); i++) {
                SehatIndoDto sehatIndoDto = new SehatIndoDto();
                processRowData((sheet.getRow(i)), headerMap, sehatIndoDto);
                if (isSehatIndoDtoValid(sehatIndoDto)) {
                    dataImunisasi.add(sehatIndoDto);
                }
            }

        }

        log.info("Data retrieval successful. File: '{}', Size: {}", xlsxFile.getOriginalFilename(), dataImunisasi.size());
        return dataImunisasi;
    }

    private void processRowData(Row row, Map<Integer, String> headerMap, SehatIndoDto sehatIndoDto) {
        // Map of column names to corresponding setter methods
        Map<String, Consumer<String>> settersMap = new HashMap<>();
        settersMap.put(SehatIndoColumn.NAMA_ANAK.getName(), sehatIndoDto::setNamaAnak);
        settersMap.put(SehatIndoColumn.USIA_ANAK.getName(), sehatIndoDto::setUsiaAnak);
        settersMap.put(SehatIndoColumn.TANGGAL_LAHIR_ANAK.getName(), sehatIndoDto::setTanggalLahirAnak);
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
                processImunisasiDetails(columnName, cell, sehatIndoDto);
            }
        });
    }

    private void processImunisasiDetails(String columnName, Cell cell, SehatIndoDto sehatIndoDto) {
        if (sehatIndoDto.getDetailImunisasiMap() == null) {
            sehatIndoDto.setDetailImunisasiMap(new HashMap<>());
        }

        Imunisasi code = Imunisasi.getImunisasi(columnName);
        sehatIndoDto.getDetailImunisasiMap().computeIfAbsent(code, k -> new SehatIndoImunisasiDto());

        if (columnName.contains(SehatIndoColumn.TANGGAL.getName())) {
            sehatIndoDto.getDetailImunisasiMap().get(code).setTanggal(cell.toString());
        } else if (columnName.contains(SehatIndoColumn.POS.getName())) {
            sehatIndoDto.getDetailImunisasiMap().get(code).setPos(cell.toString());
        } else if (columnName.contains(SehatIndoColumn.STATUS.getName())) {
            String statusValue = cell.toString().replace(POINT_ZERO, EMPTY);
            sehatIndoDto.getDetailImunisasiMap().get(code).setStatus(Integer.valueOf(statusValue));
        }
    }

    private boolean isSehatIndoDtoValid(SehatIndoDto sehatIndoDto) {
        if (!sehatIndoDto.isImunisasiRutin()) {
            return true; // Riwayat Imunisasi is Valid
        }
        int eligibleImunisasi = ZERO;
        String usiaAnak = sehatIndoDto.getUsiaAnak();
        for (Map.Entry<Imunisasi, SehatIndoImunisasiDto> map : sehatIndoDto.getDetailImunisasiMap().entrySet()) {
            // ONE means not yet received imunisasi rutin
            if (map.getValue().getStatus() == ONE && mustSetImunisasiRutin(map.getKey(), usiaAnak)) {
                sehatIndoDto.setImunisasiRutin(Imunisasi.isImunisasiRutin(usiaAnak, map.getKey()));
                // TODO: sehatIndoDto.setMustSetImunisasiRutinDetails(); ---> create void method accept Imunisasi & SehatIndoDto as args
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
