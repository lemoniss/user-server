package com.maxlength.rest;

import com.github.pagehelper.PageInfo;
import com.maxlength.aggregate.service.UserService;
import com.maxlength.aop.annotation.IgnoreLogin;
import com.maxlength.spec.common.BaseResponse;
import com.maxlength.spec.common.Offset;
import com.maxlength.spec.vo.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/users")
@Api(tags = {"[회원] - 통합회원 API"}, protocols = "http", produces = "application/json", consumes = "application/json")
public class UserResource {

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    /**
     *
     * @param offset
     * @param limit
     * @param name
     * @return
     */
    @GetMapping
    @ApiOperation(value = "회원 목록", notes = "회원 목록")
    public ResponseEntity<PageInfo<User.UserInfo>> userList(@ApiParam(value = "페이지번호", required = true, defaultValue = "1") @RequestParam Integer offset,
        @ApiParam(value = "페이지당 리스트 수", required = true, defaultValue = "10") @RequestParam Integer limit, @ApiParam(value = "검색어-이름", defaultValue = "") @RequestParam(required = false) String name) {
        User.UserInfo searchKey = User.UserInfo.builder().name(name).build();
        return BaseResponse.ok(userService.userList(searchKey, new Offset(offset, limit)));
    }

    /**
     *
     * @param accountId
     * @return
     */
    @GetMapping("/{accountId}")
    @ApiOperation(value = "회원 조회", notes = "회원 단건 조회")
    public ResponseEntity<User.UserInfo> userDetail(@PathVariable("accountId") Long accountId) {
        return BaseResponse.ok(userService.userDetail(accountId));
    }


    /**
     *
     * @param request
     * @return
     */
    @IgnoreLogin
    @PostMapping
    @ApiOperation(value = "회원 등록", notes = "회원 등록")
    public ResponseEntity<Mono<User.RegUserResponse>> registerUser(@RequestBody @Valid User.RegUserRequest request) {
        return BaseResponse.ok(userService.registerUser(request));
    }

    /**
     *
     * @param accountId
     * @param request
     * @return
     */
    @PutMapping("/{accountId}")
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "회원 수정", notes = "회원 수정")
    public ResponseEntity<User.ModUserResponse> updateUser(@PathVariable("accountId") Long accountId, @RequestBody User.ModUserRequest request) {
        return BaseResponse.ok(userService.updateUser(accountId, request));
    }

    /**
     *
     * @param accountId
     * @return
     */
    @DeleteMapping("/{accountId}")
    @ApiOperation(value = "회원 삭제", notes = "회원 삭제")
    public boolean deleteUser(@PathVariable("accountId") Long accountId) {
        return userService.deleteUser(accountId);
    }


}
