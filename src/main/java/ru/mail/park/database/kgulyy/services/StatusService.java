package ru.mail.park.database.kgulyy.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.mail.park.database.kgulyy.domains.Status;
import ru.mail.park.database.kgulyy.repositories.StatusRepository;

/**
 * @author Konstantin Gulyy
 */
@Service
public class StatusService {
    private StatusRepository statusRepository;

    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    public ResponseEntity<Status> getStatus() {
        return ResponseEntity.ok(statusRepository.getStatus());
    }

    public ResponseEntity clear() {
        statusRepository.clear();

        return ResponseEntity.ok().build();
    }
}
