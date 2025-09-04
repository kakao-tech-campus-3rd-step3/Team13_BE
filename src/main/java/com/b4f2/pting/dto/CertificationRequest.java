package com.b4f2.pting.dto;

import jakarta.validation.constraints.Email;

public record CertificationRequest(
    @Email String schoolEmail,
    String token
) {

}
