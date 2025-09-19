package com.b4f2.pting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "school")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "domain", unique = true)
    private String domain;

    public School(String name, String domain) {
        this.name = name;
        this.domain = domain;
    }
}
