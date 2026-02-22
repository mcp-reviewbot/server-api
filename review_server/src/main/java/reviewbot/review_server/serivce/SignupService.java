package reviewbot.review_server.serivce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reviewbot.review_server.domain.User;
import reviewbot.review_server.dto.SignupDto;
import reviewbot.review_server.port.in.SignupUseCase;
import reviewbot.review_server.port.out.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignupService implements SignupUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public SignupDto.SignupResponse signup(SignupDto.SignupRequest request) {
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다.");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .loginId(request.loginId())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .build();

        User savedUser = userRepository.save(user);
        log.info("회원가입 완료: loginId={}", savedUser.getLoginId());

        return new SignupDto.SignupResponse(
                savedUser.getId(),
                savedUser.getLoginId(),
                savedUser.getEmail()
        );
    }
}