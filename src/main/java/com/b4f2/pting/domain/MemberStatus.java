package com.b4f2.pting.domain;

public enum MemberStatus {
    ACTIVE, // 정상
    SUSPENDED, // 자동 정지 (3회 이상 신고)
    BANNED // 영구 제재
}
