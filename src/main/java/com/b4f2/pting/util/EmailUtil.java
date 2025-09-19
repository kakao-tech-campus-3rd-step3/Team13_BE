package com.b4f2.pting.util;

import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

    public String getEmailAddress(String localPart, String postfix) {
        return String.format("%s@%s", localPart, postfix);
    }

    public String getEmailCertificationKey(Long memberId, String schoolEmail) {
        return String.format("cert:%d:%s", memberId, schoolEmail);
    }

    public boolean isSchoolEmail(String schoolEmail) {
        return schoolEmail.endsWith(".ac.kr");
    }
}
