package com.maxlength.aggregate.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@Table(name = "terms_mst")
public class TermsEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name= "name", length = 50)   // 약관명
    private String name;

    @Column(name= "contents", columnDefinition = "text")   // 약관내용
    private String contents;

    @Column(name= "ver", length = 10)   // 약관버전
    private String ver;

    @Column(name= "require_yn", length = 1, columnDefinition = "char")  // 필수여부
    @Enumerated(EnumType.STRING)
    private Yesno requireYn;

    @Column(name= "del_yn", length = 1, columnDefinition = "char")
    @Enumerated(EnumType.STRING)
    private Yesno delYn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "service_id")
    private ServiceEntity serviceEntity;

    @Builder.Default
    @OneToMany(mappedBy = "termsEntity")
    List<AccountTermsEntity> accountTermsEntityList = new ArrayList<>();

}