package reviewbot.review_server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SignupDto {

    public record SignupRequest(
            @NotBlank(message = "아이디는 필수입니다.")
            @Size(min = 4, max = 50, message = "아이디는 4자 이상 50자 이하여야 합니다.")
            String loginId,

            @NotBlank(message = "비밀번호는 필수입니다.")
            @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하여야 합니다.")
            @Pattern(
                    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/~`|\\\\]).*$",
                    message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다."
            )
            String password,

            @NotBlank(message = "이메일은 필수입니다.")
            @Email(message = "올바른 이메일 형식이 아닙니다.")
            String email
    ) {}

    public record SignupResponse(
            Long id,
            String loginId,
            String email
    ) {}
}