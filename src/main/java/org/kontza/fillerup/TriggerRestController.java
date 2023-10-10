package org.kontza.fillerup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TriggerRestController {
    private TriggerService triggerService;
    private TaskExecutor taskExecutor;
    private static final Logger logger = LoggerFactory.getLogger(TriggerRestController.class);

    public TriggerRestController(TriggerService triggerService, TaskExecutor taskExecutor) {
        this.triggerService = triggerService;
        this.taskExecutor = taskExecutor;
    }

    @GetMapping("/trigger")
    private void triggerIt(@RequestParam Boolean defaultClient) {
        var f = new Foo();
        try {
            var r = f.getParts().isEmpty();
            logger.error(">>> r = {}", r);
        } catch (NullPointerException e) {
            logger.error("NPE with plain isEmpty!");
        }
        try {
            var s = BooleanUtils.isFalse(f.getParts().isEmpty());
            logger.error(">>> s = {}", s);
        } catch (NullPointerException e) {
            logger.error("NPE with plain BooleanUtils!");
        }
        taskExecutor.execute(() -> {
            triggerService.triggerIt(defaultClient);
        });
    }

    @GetMapping("/events")
    private void getEvents(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate) {
        logger.error(">>> Got: {}", startDate);
    }

    @PostMapping("/with-glue")
    private ResponseEntity<String> glue(@RequestParam String filterString, @RequestBody Map<String, String> payload) {
        logger.error(">>> filterString = {}", filterString);
        logger.error(">>> payload = {}", payload);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/with-glue")
    private ResponseEntity<String> glue() {
        var ministry = "Ensimmäinen-";
        var lastDash = ministry.lastIndexOf('-');
        try {
            ministry = ministry.substring(lastDash + 1);
        } catch (IndexOutOfBoundsException e) {
            logger.info("Bounce!");
        }
        logger.error(">>> ministry = {}", ministry);
        return ResponseEntity.ok(ministry);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private class Foo {
        List<String> parts;
    }
}
