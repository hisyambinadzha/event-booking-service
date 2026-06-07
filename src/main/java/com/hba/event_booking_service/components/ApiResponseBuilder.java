package com.hba.event_booking_service.components;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.hba.event_booking_service.dtos.responses.Generic;
import com.hba.event_booking_service.dtos.responses.Response;

import tools.jackson.databind.ObjectMapper;

@Component
public class ApiResponseBuilder {
    private final ErrorCatalog errorCatalog;

    public ApiResponseBuilder(ErrorCatalog errorCatalog) {
        this.errorCatalog = errorCatalog;
    }

    private Response.Header buildHeader() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kuala_Lumpur"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String timestamp = now.format(formatter);

        Response.Header header = new Response.Header();
        header.setResponseId(UUID.randomUUID().toString());
        header.setTimestamp(timestamp);
        return header;
    }

    private Response.ResponseInfo buildResponseInfo(String code) {
        Response.ResponseInfo responseInfo = new Response.ResponseInfo();
        responseInfo.setCode(code);
        responseInfo.setMessage(errorCatalog.get(code));
        return responseInfo;
    }

    private Response.ResponseInfo buildResponseInfo(String code, String message) {
        Response.ResponseInfo responseInfo = new Response.ResponseInfo();
        responseInfo.setCode(code);
        responseInfo.setMessage(errorCatalog.get(code, message));
        return responseInfo;
    }

    private Generic wrapResponse(Response.Body body) {
        Response response = new Response();
        response.setHeader(buildHeader());
        response.setBody(body);

        Generic generic = new Generic();
        generic.setResponse(response);
        return generic;
    }

    public Generic result(String code) {
        Response.Body body = new Response.Body();
        body.setResponseInfo(buildResponseInfo(code));

        return wrapResponse(body);
    }

    public Generic result(String code, String message) {
        Response.Body body = new Response.Body();
        body.setResponseInfo(buildResponseInfo(code, message));

        if (message != null) {
            body.addField("error", message);
        }

        return wrapResponse(body);
    }

    public Generic result(String code, Map<String, Object> params) {
        Response.Body body = new Response.Body();
        body.setResponseInfo(buildResponseInfo(code));

        if (params != null) {
            params.forEach(body::addField);
        }

        return wrapResponse(body);
    }

    public <T> Generic result(String code, List<T> data) {
        Response.Body body = new Response.Body();
        body.setResponseInfo(buildResponseInfo(code));

        if (data != null) {
            body.setData(data); // safe cast for serialization
        }

        return wrapResponse(body);
    }

    @SuppressWarnings("unchecked")
    public <T> Generic result(String code, T data) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.convertValue(data, Map.class);
        return result(code, map);
    }
}
