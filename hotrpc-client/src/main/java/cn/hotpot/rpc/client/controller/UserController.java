package cn.hotpot.rpc.client.controller;

import cn.hotpot.rpc.common.service.UserService;
import cn.hotpot.rpc.common.service.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author qinzhu
 * @since 2019/12/9
 */
@RestController
public class UserController {
    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping("/list")
    public ResponseEntity<List<User>> list() {
        UserService userService = (UserService) applicationContext.getBean("userService");
        return ResponseEntity.ok(userService.list());
    }
}
