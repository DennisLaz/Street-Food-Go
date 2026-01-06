package gr.hua.dit.StreetFoodGo.web.ui;

import gr.hua.dit.StreetFoodGo.core.port.SmsNotificationPort;
import gr.hua.dit.StreetFoodGo.core.port.impl.dto.SendSmsRequest;
import gr.hua.dit.StreetFoodGo.core.port.impl.dto.SendSmsResult;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/api/v1/sms")
public class SmsController {

    @PostMapping
    public SendSmsResult sendSms(@RequestBody SendSmsRequest request) {

        // Αν θες απλά να επιστρέφει sent=true
        return new SendSmsResult(true);
    }
}



