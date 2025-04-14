package org.platform.service;

import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<Object> getUser();
}
