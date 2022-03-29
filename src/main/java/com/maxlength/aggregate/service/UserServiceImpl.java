package com.maxlength.aggregate.service;

import com.github.pagehelper.PageInfo;
import com.maxlength.aggregate.entity.AccountEntity;
import com.maxlength.aggregate.entity.AccountRoleEntity;
import com.maxlength.aggregate.entity.AccountServiceEntity;
import com.maxlength.aggregate.entity.AccountTermsEntity;
import com.maxlength.aggregate.entity.RoleEntity;
import com.maxlength.aggregate.entity.ServiceEntity;
import com.maxlength.aggregate.entity.TermsEntity;
import com.maxlength.aggregate.entity.UserEntity;
import com.maxlength.aggregate.repository.AccountRepository;
import com.maxlength.aggregate.repository.RoleRepository;
import com.maxlength.aggregate.repository.SecedeRepository;
import com.maxlength.aggregate.repository.ServiceRepository;
import com.maxlength.aggregate.repository.UserRepository;
import com.maxlength.spec.common.BaseException;
import com.maxlength.spec.common.Offset;
import com.maxlength.spec.enums.Provider;
import com.maxlength.spec.enums.Yesno;
import com.maxlength.spec.vo.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final ServiceRepository serviceRepository;
    private final RoleRepository roleRepository;
    private final SecedeRepository secedeRepository;

    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UserServiceImpl(UserRepository userRepository, AccountRepository accountRepository, ServiceRepository serviceRepository, RoleRepository roleRepository, SecedeRepository secedeRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.serviceRepository = serviceRepository;
        this.roleRepository = roleRepository;
        this.secedeRepository = secedeRepository;
    }

    @Value("${security.oauth2.resource.jwt.key-value}")
    private String publicKey;

    @Override
    public PageInfo<User.UserInfo> userList(User.UserInfo searchKey, Offset offset) {
        Pageable pageable = PageRequest.of(offset.getPage(), offset.getLimit(), Sort.Direction.ASC, "id");
        Page<AccountEntity> AccountEntityList = accountRepository.findAll(userListSpecification(searchKey.getName()), pageable);
        List<User.UserInfo> userList = new ArrayList<>();

        for(AccountEntity accountEntity: AccountEntityList) {
            User.UserInfo vo = User.UserInfo.builder()
                    .id(accountEntity.getId())
                    .username(accountEntity.getUsername())
                    .name(accountEntity.getUserEntity().getName())
                    .sexdstn(accountEntity.getUserEntity().getSexdstn())
                    .birthday(accountEntity.getUserEntity().getBirthday())
                    .email(accountEntity.getUserEntity().getEmail())
                    .build();
            userList.add(vo);
        }
        return new PageInfo<>(userList.stream().collect(Collectors.toList()), (int) AccountEntityList.getTotalElements());
    }

    @Override
    public User.UserInfo userDetail(Long accountId) {

        User.UserInfo response;

        AccountEntity accountEntity = accountRepository.findByIdAndDelYn(accountId, Yesno.N);
        if(accountEntity == null)
            throw new BaseException("사용자정보가 없습니다.");

        response = User.UserInfo.builder()
                .id(accountEntity.getId())
                .username(accountEntity.getUsername())
                .name(accountEntity.getUserEntity().getName())
                .mbtlnum(accountEntity.getUserEntity().getMbtlnum())
                .sexdstn(accountEntity.getUserEntity().getSexdstn())
                .birthday(accountEntity.getUserEntity().getBirthday())
                .email(accountEntity.getUserEntity().getEmail())
                .build();

        return response;
    }

    @Override
    public Mono<User.RegUserResponse> registerUser(@RequestBody User.RegUserRequest request) {

        User.RegUserResponse response;
        UserEntity userEntity;
        if(!StringUtils.isBlank(request.getCi())) {
            // validate user(ci)
            userEntity = userRepository.findByCiAndDelYn(request.getCi(), Yesno.N);
            if(userEntity != null)
                throw new BaseException("이미 가입된 사용자 입니다.");
        }

        // validate service
        ServiceEntity serviceEntity = serviceRepository.findByNameAndDelYn(request.getService(), Yesno.N);
        if(serviceEntity == null)
            throw new BaseException("서비스이름이 존재하지 않습니다.");

        // validate role
        RoleEntity roleEntity = roleRepository.findByNameAndDelYn("ROLE_USER", Yesno.N);
        if(roleEntity == null)
            throw new BaseException("역할이 존재하지 않습니다.");

        // validate account
        AccountEntity accountEntity = accountRepository.findByUsernameAndDelYn(request.getUsername(), Yesno.N);

        if(accountEntity != null)
            throw new BaseException("이미 존재하는 아이디 입니다.");

        // 계정정보 Bind
        accountEntity = new AccountEntity()
                .builder()
                .username(request.getUsername())
                .password(StringUtils.isBlank(request.getPassword()) ? "" : passwordEncoder.encode(request.getPassword()))
                .provider(Provider.valueOf(request.getProvider().name()))
                .delYn(Yesno.N)
                .build();

        List<AccountEntity> accountEntityList = new ArrayList<>();
        accountEntityList.add(accountEntity);

        userEntity = new UserEntity().builder()
            .name(request.getName())
            .sexdstn(request.getSexdstn())
            .birthday(request.getBirthday())
            .email(request.getEmail())
            .mbtlnum(request.getMbtlnum())
            .ci(request.getCi())
            .delYn(Yesno.N)
            .accountEntityList(accountEntityList)
            .build();
        accountEntity.setUserEntity(userEntity);

        // AccountService 관계Entity Bind
        AccountServiceEntity accountServiceEntity = new AccountServiceEntity()
                .builder()
                .accountEntity(accountEntity)
                .serviceEntity(serviceEntity)
                .build();

        List<AccountServiceEntity> accountServiceEntityList = new ArrayList<>();
        accountServiceEntityList.add(accountServiceEntity);
        accountEntity.setAccountServiceEntityList(accountServiceEntityList);

        // AccountTerms 관계Entity Bind (이용약관 선택한 IDs)
        List<AccountTermsEntity> accountTermsEntityList = new ArrayList<>();
        for(Long termsId: request.getTermsIds()) {

            TermsEntity termsEntity = new TermsEntity()
                    .builder()
                    .id(termsId)
                    .build();
            AccountTermsEntity accountTermsEntity = new AccountTermsEntity()
                    .builder()
                    .accountEntity(accountEntity)
                    .termsEntity(termsEntity)
                    .build();

            accountTermsEntityList.add(accountTermsEntity);
        }
        accountEntity.setAccountTermsEntityList(accountTermsEntityList);

        // AccountAuth 관계Entity Bind
        AccountRoleEntity accountRoleEntity = new AccountRoleEntity()
                .builder()
                .accountEntity(accountEntity)
                .roleEntity(roleEntity)
                .build();
        List<AccountRoleEntity> accountRoleEntityList = new ArrayList<>();
        accountRoleEntityList.add(accountRoleEntity);

        accountEntity.setAccountRoleEntityList(accountRoleEntityList);

        // save
        accountEntity = accountRepository.save(accountEntity);

        response = User.RegUserResponse.builder()
                .id(accountEntity.getId())
                .username(accountEntity.getUsername())
                .service(serviceEntity.getName())
                .name(userEntity.getName())
                .sexdstn(userEntity.getSexdstn())
                .birthday(userEntity.getBirthday())
                .mbtlnum(userEntity.getMbtlnum())
                .email(userEntity.getEmail())
                .regDt(accountEntity.getRegDt())
                .build();

        return Mono.just(response);
    }

    @Override
    public User.ModUserResponse updateUser(Long accountId, User.ModUserRequest request) {

        User.ModUserResponse response;

        // valid user
        UserEntity userEntity = userRepository.findByIdAndDelYn(accountId, Yesno.N);

        if(userEntity == null)
            throw new BaseException("사용자가 존재하지 않습니다.");

        if(!StringUtils.isBlank(request.getName())) userEntity.setName(request.getName());
        if(!StringUtils.isBlank(request.getSexdstn())) userEntity.setSexdstn(request.getSexdstn());
        if(!StringUtils.isBlank(request.getBirthday())) userEntity.setBirthday(request.getBirthday());
        if(!StringUtils.isBlank(request.getMbtlnum())) userEntity.setMbtlnum(request.getMbtlnum());
        if(!StringUtils.isBlank(request.getEmail())) userEntity.setEmail(request.getEmail());
        userEntity.setModId(accountId);
        userEntity.setModDt(LocalDateTime.now());
        userEntity = userRepository.save(userEntity);

        response = User.ModUserResponse.builder()
                .id(accountId)
                .name(userEntity.getName())
                .sexdstn(userEntity.getSexdstn())
                .birthday(userEntity.getBirthday())
                .mbtlnum(userEntity.getMbtlnum())
                .email(userEntity.getEmail())
                .modDt(userEntity.getModDt())
                .build();
        return response;
    }

    @Override
    public boolean deleteUser(Long accountId) {

        AccountEntity accountEntity = accountRepository.findByIdAndDelYn(accountId, Yesno.N);

        if(accountEntity == null) {
            throw new BaseException("등록이 안된 사용자입니다.");
        }

        // accountMst
        accountEntity.setDelYn(Yesno.Y);
        accountEntity.setModId(accountId);
        accountEntity.setModDt(LocalDateTime.now());

        // userMst
        accountEntity.getUserEntity().setDelYn(Yesno.Y);
        accountEntity.getUserEntity().setModId(accountId);
        accountEntity.getUserEntity().setModDt(LocalDateTime.now());

        /*
        // accountTerms
        accountEntity.getAccountTermsEntityList().forEach(
                accountTermsEntity -> {
                    accountTermsEntity.setDelYn(Yesno.Y);
                    accountTermsEntity.setModId(accountId);
                    accountTermsEntity.setModDt(LocalDateTime.now());
                });

        // accountService
        accountEntity.getAccountServiceEntityList().forEach(
                accountServiceEntity -> {
                    accountServiceEntity.setDelYn(Yesno.Y);
                    accountServiceEntity.setModId(accountId);
                    accountServiceEntity.setModDt(LocalDateTime.now());
                });

        // accountAuth
        accountEntity.getAccountRoleEntityList().forEach(
                accountRoleEntity -> {
                    accountRoleEntity.setDelYn(Yesno.Y);
                    accountRoleEntity.setModId(accountId);
                    accountRoleEntity.setModDt(LocalDateTime.now());
                });
        */
        accountRepository.save(accountEntity);
        return true;
    }

    private Specification<AccountEntity> userListSpecification(String searchKey) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            //            predicateList.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("useYn"), Yesno.Y)));
            predicateList.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("delYn"), Yesno.N)));
            predicateList.add(criteriaBuilder.and(criteriaBuilder.equal(root.<AccountEntity>get("userEntity").get("delYn"), Yesno.N)));

            if(!StringUtils.isEmpty(searchKey))
                predicateList.add(criteriaBuilder.and(criteriaBuilder.like(root.<AccountEntity>get("userEntity").get("name"), searchKey +'%')));

            return criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));
        };
    }
}

