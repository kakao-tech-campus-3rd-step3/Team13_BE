package com.b4f2.pting.dto;

import org.hibernate.validator.constraints.Length;

public record ChangeProfileDescriptionRequest(
    @Length(max = 255)
    String description
) {

}
