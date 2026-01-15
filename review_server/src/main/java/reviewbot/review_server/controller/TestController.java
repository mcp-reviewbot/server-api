package reviewbot.review_server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/test")
public class TestController {
    @GetMapping
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("테스트 성공!");
    }
}
