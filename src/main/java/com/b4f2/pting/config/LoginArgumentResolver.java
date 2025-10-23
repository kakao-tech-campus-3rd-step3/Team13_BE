package com.b4f2.pting.config;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

import com.b4f2.pting.repository.MemberRepository;
import com.b4f2.pting.util.JwtUtil;

@Component
@RequiredArgsConstructor
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String AUTHORIZATION_PREFIX = "Bearer ";

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Login.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory)
            throws Exception {
        String authHeader = webRequest.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.isBlank(authHeader) || !authHeader.startsWith(AUTHORIZATION_PREFIX)) {
            throw new IllegalArgumentException("JWT 인증 헤더가 필요합니다.");
        }

        String token = authHeader.substring(AUTHORIZATION_PREFIX.length());
        Long memberId = jwtUtil.getMemberId(token);

        return memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("회원 정보가 존재하지 않습니다."));
    }
}
