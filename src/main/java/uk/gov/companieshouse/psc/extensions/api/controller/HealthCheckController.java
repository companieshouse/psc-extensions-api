package uk.gov.companieshouse.psc.extensions.api.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

  @GetMapping("/psc-extensions-api/healthcheck")
  public Map<String, String> healthCheck() {
    Map<String, String> response = new HashMap<>();
    response.put("status", "UP");
    return response;
  }
}
