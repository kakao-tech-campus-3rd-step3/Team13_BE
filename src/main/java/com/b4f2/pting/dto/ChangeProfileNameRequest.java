package com.b4f2.pting.dto;

import org.hibernate.validator.constraints.Length;

public record ChangeProfileNameRequest(@Length(max = 31) String name) {}
