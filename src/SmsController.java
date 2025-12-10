package gr.hua.dit.StreetFoodGo.web.ui;

import gr.hua.dit.StreetFoodGo.core.port.SmsNotificationPort;
import gr.hua.dit.StreetFoodGo.core.port.impl.dto.SendSmsRequest;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


    @RestController
    @RequestMapping("/api/v1/sms")
    public class SmsController {

        private final SmsNotificationPort smsNotificationPort;

        public SmsController(SmsNotificationPort smsNotificationPort) {
            this.smsNotificationPort = smsNotificationPort;
        }

        @PostMapping
        public ResponseEntity<?> sendSms(@RequestBody SendSmsRequest request) {
            boolean sent = smsNotificationPort.sendSms(request.e164(), request.content());
            return ResponseEntity.ok(Map.of("sent", sent));
        }
    }


