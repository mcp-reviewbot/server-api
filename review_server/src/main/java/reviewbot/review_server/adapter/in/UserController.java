package reviewbot.review_server.adapter.in;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reviewbot.review_server.dto.SignupDto;
import reviewbot.review_server.port.in.SignupUseCase;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final SignupUseCase signupUseCase;

    @PostMapping("/signup")
    public ResponseEntity<SignupDto.SignupResponse> signup(
            @Valid @RequestBody SignupDto.SignupRequest request) {
        SignupDto.SignupResponse response = signupUseCase.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}