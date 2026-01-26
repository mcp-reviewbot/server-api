package reviewbot.review_server.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewType {
    REVIEW("REVIEW"),
    DESCRIBE("DESCRIBE");

    private final String type;

    @Override
    public String toString() {
        return type;
    }
}
