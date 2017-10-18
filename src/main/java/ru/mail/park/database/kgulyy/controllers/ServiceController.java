package ru.mail.park.database.kgulyy.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mail.park.database.kgulyy.domains.Status;
import ru.mail.park.database.kgulyy.services.MyService;

/**
 * @author Konstantin Gulyy
 */
@RestController
@RequestMapping("/api/service")
public class ServiceController {
    private final MyService myService;

    public ServiceController(MyService myService) {
        this.myService = myService;
    }

    @GetMapping("/status")
    ResponseEntity<Status> getStatus() {
        return ResponseEntity.ok(myService.getStatus());
    }
}
