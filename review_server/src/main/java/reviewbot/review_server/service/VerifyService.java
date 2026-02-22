package reviewbot.review_server.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import reviewbot.review_server.common.client.properties.GitHubProperties;

/**
 * 추후 Filter 혹은 Interceptor 레이어로 로직 이동 예정
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VerifyService {
    private final GitHubProperties props;

    public void verifyHMAC(String sig256, byte[] body) {
        if (!StringUtils.hasText(sig256)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-Hub-Signature-256이 누락되었습니다.");
        }

        String expectedSig = "";
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(
                    props.getWebHookSecret().getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"));

            byte[] digest = mac.doFinal(body);
            StringBuilder sb = new StringBuilder(digest.length * 2);

            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }

            expectedSig = "sha256=" + sb;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "HMAC 검증 중 오류가 발생하였습니다.");
        }

        if (!MessageDigest.isEqual(
                expectedSig.getBytes(StandardCharsets.UTF_8),
                sig256.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "올바르지 않은 요청입니다.");
        }
    }
}
