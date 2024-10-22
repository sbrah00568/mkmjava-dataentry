package com.momworks.dataentry.controller;

import com.momworks.dataentry.service.SehatIndoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class SehatIndoController {

    private final SehatIndoService sehatIndoService;

    @PostMapping("/sehatindo")
    public void execute(@RequestParam("xlsxFile") MultipartFile xlsxFile, @RequestParam("type") String type) {
        sehatIndoService.performDataEntry(xlsxFile, type);
    }

}
