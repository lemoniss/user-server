package com.maxlength.spec.vo;

import com.maxlength.spec.enums.Provider;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "회원 VO")
public class User {

    @Getter
    @Builder
    @ApiModel(description = "회원정보 VO")
    public static class UserInfo {

        @ApiModelProperty(notes = "계정식별자", example = "1")
        private Long id;

        @ApiModelProperty(notes = "로그인ID", example = "test")
        private String username;

        @ApiModelProperty(notes = "이름", example = "홍길동")
        private String name;

        @ApiModelProperty(notes = "성별", example = "M or F")
        private String sexdstn;

        @ApiModelProperty(notes = "생년월일", example = "19900101")
        private String birthday;

        @ApiModelProperty(notes = "이메일", example = "test@test.com")
        private String email;

        @ApiModelProperty(notes = "휴대폰번호", example = "01071178214")
        private String mbtlnum;

    }

    @Getter
    @Setter
    @ApiModel(description = "회원가입 요청VO")
    public static class RegUserRequest {

        @NotBlank(message = "로그인ID가 없습니다.")
        @ApiModelProperty(notes = "로그인ID", required = true, example = "test")
        private String username;

        @ApiModelProperty(notes = "비밀번호")
        private String password;

        @NotBlank(message = "서비스가 없습니다.")
        @ApiModelProperty(notes = "서비스", required = true, example = "I")
        private String service;

        @NotBlank(message = "이름이 없습니다.")
        @ApiModelProperty(notes = "이름", required = true, example = "홍길동")
        private String name;

        @ApiModelProperty(notes = "성별")
        private String sexdstn;

        @ApiModelProperty(notes = "생년월일")
        private String birthday;

        @ApiModelProperty(notes = "휴대폰번호", example = "01012341234")
        private String mbtlnum;

        @ApiModelProperty(notes = "이메일", example = "test@test.com")
        private String email;

        @ApiModelProperty(notes = "CI", example = "인증기관에서 받은 CI값")
        private String ci;

        @NotNull(message = "동의한 약관이 없습니다.")
        @ApiModelProperty(notes = "이용약관 식별자(배열)", required = true, example = "[1,2,3]")
        private List<Long> termsIds;

        @NotNull(message = "계정제공자가 없습니다.")
        @ApiModelProperty(notes = "계정제공자", example = "GOOGLE, NAVER, KAKAO, APPLE, PROJECT 이중 하나")
        @Enumerated(EnumType.STRING)
        private Provider provider;

    }

    @Getter
    @Builder
    @ApiModel(description = "회원가입 응답VO")
    public static class RegUserResponse {

        @ApiModelProperty(notes = "계정식별자", example = "1")
        private Long id;

        @ApiModelProperty(notes = "로그인ID", example = "loginid")
        private String username;

        @ApiModelProperty(notes = "서비스", example = "A or B or etc...")
        private String service;

        @ApiModelProperty(notes = "이름", example = "홍길동")
        private String name;

        @ApiModelProperty(notes = "성별", example = "M or F")
        private String sexdstn;

        @ApiModelProperty(notes = "생년월일", example = "19900101")
        private String birthday;

        @ApiModelProperty(notes = "휴대폰번호", example = "01012341234")
        private String mbtlnum;

        @ApiModelProperty(notes = "이메일", example = "test@test.com")
        private String email;

        @ApiModelProperty(notes = "가입일", example = "2021-02-02 10:20:30")
        private LocalDateTime regDt;
    }

    @Getter
    @Setter
    @ApiModel(description = "회원정보수정 요청VO")
    public static class ModUserRequest {

        @NotEmpty
        @ApiModelProperty(notes = "이름", example = "홍길동")
        private String name;

        @NotEmpty
        @ApiModelProperty(notes = "성별 (M / F)", example = "M")
        private String sexdstn;

        @NotEmpty
        @ApiModelProperty(notes = "생년월일", example = "19900101")
        private String birthday;

        @NotEmpty
        @ApiModelProperty(notes = "이메일", example = "test@test.com")
        private String email;

        @NotEmpty
        @ApiModelProperty(notes = "휴대폰번호", example = "01071178214")
        private String mbtlnum;
    }

    @Getter
    @Builder
    @ApiModel(description = "회원정보수정 응답VO")
    public static class ModUserResponse {

        @ApiModelProperty(notes = "계정식별자", example = "1")
        private Long id;

        @ApiModelProperty(notes = "이름", example = "홍길동")
        private String name;

        @ApiModelProperty(notes = "성별 (M / F)", example = "M")
        private String sexdstn;

        @ApiModelProperty(notes = "생년월일", example = "19900101")
        private String birthday;

        @ApiModelProperty(notes = "이메일", example = "test@test.com")
        private String email;

        @ApiModelProperty(notes = "휴대폰번호", example = "01071178214")
        private String mbtlnum;

        @ApiModelProperty(notes = "수정일", example = "2021-02-02 10:20:30")
        private LocalDateTime modDt;
    }

    @Getter
    @Setter
    @ApiModel(description = "회원삭제 요청VO")
    public static class DelUserRequest {

        @NotBlank(message = "계정식별자가 없습니다.")
        @ApiModelProperty(notes = "계정식별자", required = true, example = "[1, 2, 3]")
        private List<Long> ids = new ArrayList<>();
    }

    @Getter
    @Setter
    @ApiModel(description = "계정 약관 업데이트")
    public static class AccountTermRequest {

        @NotNull(message = "동의한 약관이 없습니다.")
        @ApiModelProperty(notes = "이용약관 식별자(배열)", required = true)
        private List<Long> termsIds;
    }

    @Getter
    @Builder
    @ApiModel(description = "회원정보수정 응답VO")
    public static class AccountTermResponse {

        @NotNull(message = "계정식별자가 없습니다.")
        @ApiModelProperty(notes = "계정식별자", example = "1")
        private Long accountId;

        //        @NotNull(message = "동의한 약관이 없습니다.")
        @ApiModelProperty(notes = "이용약관 식별자(배열)", required = true, example = "1,2,3")
        private List<Long> termsIds;
    }

    @Getter
    @Builder
    @ApiModel(description = "탈퇴요청 VO")
    public static class SecedeRequest {
        @NotNull(message = "계정식별자가 없습니다.")
        @ApiModelProperty(notes = "계정식별자", required = true, example = "1")
        private Long accountId;

        @NotNull(message = "탈퇴사유가 없습니다.")
        @ApiModelProperty(notes = "탈퇴사유", required = true, example = "탈퇴사유")
        private String reason;
    }
}


