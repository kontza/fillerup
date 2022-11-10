package org.kontza.fillerup;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class TriggerRestController {
    private TriggerService triggerService;

    public TriggerRestController(TriggerService triggerService) {
        this.triggerService = triggerService;
    }

    @GetMapping("/trigger")
    private void triggerIt() throws IOException {
        triggerService.triggerIt();
    }
}
