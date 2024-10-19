package com.mikhailkarpov.user.api;

import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  @GetMapping("/api/v1/user")
  public Map<String, Object> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
    return jwt.getClaims();
  }

}
