package org.platform.service;

import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<Object> getUser();
    boolean sendPasswordResetCode(String email);
    boolean resetPassword(String email, String resetCode, String newPassword);
}
