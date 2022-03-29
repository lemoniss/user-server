package com.maxlength.aggregate.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.maxlength.spec.enums.Provider;
import com.maxlength.spec.enums.Yesno;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.*;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@Table(name = "account_mst", indexes = @Index(name = "idx_account_mst_username", columnList = "username"))
public class AccountEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name= "username", length = 50)   // 로그인ID
    private String username;

    @Column(name= "password", length = 200)   // 비밀번호
    private String password;

    @Column(name= "provider", length = 10)  // 계정공급자
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(name= "del_yn", length = 1, columnDefinition = "char")
    @Enumerated(EnumType.STRING)
    private Yesno delYn;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name= "user_id")
    private UserEntity userEntity;

    @Builder.Default
    @OneToMany(mappedBy = "accountEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountRoleEntity> accountRoleEntityList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "accountEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountServiceEntity> accountServiceEntityList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "accountEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountTermsEntity> accountTermsEntityList = new ArrayList<>();


}