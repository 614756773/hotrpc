package cn.hotpot.rpc.client.controller;

import cn.hotpot.rpc.client.entity.User;
import cn.hotpot.rpc.client.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author qinzhu
 * @since 2019/12/9
 */
@RestController
public class UserController {
//    @Autowired
    private UserService userService;

    public ResponseEntity<List<User>> list() {
        return ResponseEntity.ok(userService.list());
    }
}
