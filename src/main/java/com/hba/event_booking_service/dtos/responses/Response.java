package com.hba.event_booking_service.dtos.responses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({ "header", "body" })
public class Response {
    private Header header;
    private Body body;

    @Data
    public static class Header {
        private String responseId;
        private String timestamp;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Body {
        private ResponseInfo responseInfo;
        private Map<String, Object> fields = new HashMap<>();
        private List<?> data;

        @JsonAnyGetter
        public Map<String, Object> getFields() {
            return fields;
        }

        @JsonAnySetter
        public void addField(String key, Object value) {
            this.fields.put(key, value);
        }
    }

    @Data
    public static class ResponseInfo {
        private String code;
        private String message;
    }
}