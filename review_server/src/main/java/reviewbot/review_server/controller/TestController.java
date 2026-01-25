package reviewbot.review_server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("테스트 성공!!");
    }

    @PostMapping("/for")
    public ResponseEntity<String> testFor() {
        System.out.println("이것은 웹훅이 제대로 날라가는지에 대한 테스트 코드입니다.");
        return ResponseEntity.ok("킬킬");
    }
}
