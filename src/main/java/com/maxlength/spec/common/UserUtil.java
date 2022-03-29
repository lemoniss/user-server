package com.maxlength.spec.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxlength.aggregate.entity.AccountEntity;
import com.maxlength.aggregate.repository.AccountRepository;
import com.maxlength.spec.enums.Yesno;
import com.maxlength.spec.vo.User;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
public class UserUtil {

    private final AccountRepository repository;

    public UserUtil(AccountRepository repository) {
        this.repository = repository;
    }

    @Value("${security.oauth2.resource.jwt.key-value}")
    private String publicKey;

    @Resource(name = "redisTemplate")
    RedisTemplate<String, Object> redisTemplate;

    public User.UserInfo me() {
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

        String username = (String) jwtMap.get("user_name");

        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        String me = (String) vo.get(username);

        if(me == null) return getUserMe(username, vo);

        try {
            return asObject(me);
        } catch (Exception e) {
            return getUserMe(username, vo);
        }

    }

    public User.UserInfo updateMe() {

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

        return updateMeByUsername((String) jwtMap.get("user_name"));
    }

    public void removeMe(String username) {
        try {
            redisTemplate.delete(username);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("removeMe error");
        }
    }

    public User.UserInfo updateMeByUsername(String username) {
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        return getUserMe(username, vo);
    }



    public User.UserInfo getUserMe(String username, ValueOperations<String, Object> vo) {
        Optional<AccountEntity> user = Optional.ofNullable(repository.findByUsernameAndDelYn(username, Yesno.N));

        if (user.isPresent()) {
            User.UserInfo userMe = User.UserInfo.builder()
                .id(user.get().getId())
                .username(user.get().getUsername())
                .name(user.get().getUserEntity().getName())
                .birthday(user.get().getUserEntity().getBirthday())
                .sexdstn(user.get().getUserEntity().getSexdstn())
                .mbtlnum(user.get().getUserEntity().getMbtlnum())
                .email(user.get().getUserEntity().getEmail())
                .build();
            try {

                vo.set(username, asJsonString(userMe), 1800, TimeUnit.SECONDS); // redis cache는 1800초(30분)

            } catch (Exception e) {
                e.printStackTrace();
                log.error("getUserMe asJsonString error");
            }
            return userMe;
        } else {
            throw new BaseException("UNAUTHORIZED");
        }
    }

    private String asJsonString(final Object obj) throws Exception {
        return new ObjectMapper().writeValueAsString(obj);
    }

    private User.UserInfo asObject(final String json) throws Exception {
        return new ObjectMapper().readValue(json, User.UserInfo.class);
    }
}
