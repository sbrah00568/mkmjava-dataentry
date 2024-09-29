package com.momworks.dataentry.sehatindonesiaku;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DataEntryController {

    private final DataEntryService dataEntryService;

    @PostMapping("/sehatindo")
    public void start() {
        dataEntryService.startEntryData();
    }

}
