package sosal_network.utility;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReCaptchaResponse {
    private boolean success;
    private String hostname;
    private String challenge_ts;

    @JsonProperty("error-codes")
    private String[] errorCodes;
}
