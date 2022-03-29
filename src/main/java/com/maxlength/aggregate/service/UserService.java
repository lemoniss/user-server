package com.maxlength.aggregate.service;

import com.github.pagehelper.PageInfo;
import com.maxlength.spec.common.Offset;
import com.maxlength.spec.vo.User;
import reactor.core.publisher.Mono;

public interface UserService {

    PageInfo<User.UserInfo> userList(User.UserInfo searchKey, Offset offset);

    User.UserInfo userDetail(Long accountId);

    Mono<User.RegUserResponse> registerUser(User.RegUserRequest request);

    User.ModUserResponse updateUser(Long userId, User.ModUserRequest request);

    boolean deleteUser(Long accountId);

}
