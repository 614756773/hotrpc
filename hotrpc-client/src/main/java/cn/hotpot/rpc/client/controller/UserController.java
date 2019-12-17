package cn.hotpot.rpc.client.controller;

import cn.hotpot.rpc.common.service.UserService;
import cn.hotpot.rpc.common.service.entity.User;
import org.springframework.beans.factory.InitializingBean;
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
public class UserController implements InitializingBean {
    @Autowired
    private ApplicationContext applicationContext;

    private UserService userService;

    @GetMapping("/list")
    public ResponseEntity<List<User>> list() {
        return ResponseEntity.ok(userService.list());
    }

    @GetMapping("/one")
    public ResponseEntity<String> getOne() {
        return ResponseEntity.ok(userService.getOne());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.userService = (UserService) applicationContext.getBean("userService");
    }
}
