package com.momworks.dataentry.sehatindonesiaku;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class DataEntryController {

    private final DataEntryService dataEntryService;

    @PostMapping("/sehatindo")
    public void execute(
            @RequestParam("xlsxFile") MultipartFile xlsxFile,
            @RequestParam("kejarType") String kejarType
    ) {
        dataEntryService.execute(xlsxFile, kejarType);
    }

}
