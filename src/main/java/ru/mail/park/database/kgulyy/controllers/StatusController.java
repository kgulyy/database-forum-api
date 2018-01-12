package ru.mail.park.database.kgulyy.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mail.park.database.kgulyy.domains.Status;
import ru.mail.park.database.kgulyy.services.StatusService;

/**
 * @author Konstantin Gulyy
 */
@RestController
@RequestMapping("/api/service")
public class StatusController {
    private final StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping("/status")
    ResponseEntity<Status> getStatus() {
        return statusService.getStatus();
    }

    @PostMapping("/clear")
    ResponseEntity clear() {
        return statusService.clear();
    }
}
