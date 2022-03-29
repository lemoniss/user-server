package com.maxlength.aop.aspect;

import com.maxlength.aggregate.repository.AccountRepository;
import com.maxlength.spec.common.BaseException;
import com.maxlength.spec.common.JwtUtil;
import com.maxlength.spec.common.UserUtil;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class IsAuthenticateAspect {

    private final AccountRepository repository;
    private final UserUtil userUtil;

    public IsAuthenticateAspect(AccountRepository repository, UserUtil userUtil) {
        this.repository = repository;
        this.userUtil = userUtil;
    }

    @Value("${security.oauth2.resource.jwt.key-value}")
    private String publicKey;

    @Pointcut("execution(* com.maxlength.rest..*Resource.*(..))")
    private void pointCut() {};

    @Before("pointCut() && !@annotation(com.maxlength.aop.annotation.IgnoreLogin)")
    public void authCheck() {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String token = request.getHeader("Authorization");

        if (StringUtils.isBlank(token))
            throw new BaseException("UNAUTHORIZED");

        JwtUtil jwtUtil = new JwtUtil();
        Map<String, Object> jwtMap;
        try {
            jwtMap = jwtUtil.decode(token.replaceAll("Bearer ", ""), publicKey);
        } catch (Exception e) {
            throw new BaseException("UNAUTHORIZED");
        }

        // 후처리 할게 있다면 @Around 로 교체
    }

}


