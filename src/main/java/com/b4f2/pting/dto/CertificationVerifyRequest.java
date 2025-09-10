package com.b4f2.pting.dto;

import jakarta.validation.constraints.NotNull;

public record CertificationVerifyRequest(
    @NotNull
    String localPart,

    @NotNull
    String code
) {

}
