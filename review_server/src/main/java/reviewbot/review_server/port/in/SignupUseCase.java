package reviewbot.review_server.port.in;

import reviewbot.review_server.dto.SignupDto;

public interface SignupUseCase {
    SignupDto.SignupResponse signup(SignupDto.SignupRequest request);
}