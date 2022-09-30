package br.com.fucks.application.controllers.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/v1/me")
public class MeController {

    @GetMapping
    public ResponseEntity getMe(HttpSession session) {
        return ResponseEntity.ok(SecurityContextHolder.getContext().getAuthentication());
    }
}
