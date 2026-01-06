package gr.hua.dit.StreetFoodGo.core.port.impl.dto;


/**
 * SendSmsRequest DTO.
 */
public record SendSmsRequest(String e164, String content) {}