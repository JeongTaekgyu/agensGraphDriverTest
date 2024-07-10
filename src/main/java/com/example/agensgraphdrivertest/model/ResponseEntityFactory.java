package com.example.agensgraphdrivertest.model;

import com.example.agensgraphdrivertest.model.enums.CommonStatusType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResponseEntityFactory {

    public static <T> ResponseEntity ok(@Nullable T data) {
        return ok(getDefaultHttpHeaders(), data);
    }

    public static <T> ResponseEntity fail(CommonStatusType commonStatusType, @Nullable T data) {
        return fail(getDefaultHttpHeaders(), commonStatusType, data);
    }

    private static HttpHeaders getDefaultHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
        return httpHeaders;
    }

    public static <T> ResponseEntity ok(HttpHeaders httpHeaders, @Nullable T data) {
        return ResponseEntity.ok().headers(httpHeaders).body(BodyEntity.get(CommonStatusType.SUCCESS, data));
    }

    public static <T> ResponseEntity fail(HttpHeaders httpHeaders, CommonStatusType commonStatusType, @Nullable T data) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(httpHeaders).body(BodyEntity.get(commonStatusType, data));
    }

/*    public static <T> ResponseEntity status(CommonStatusType commonStatusType, T data) {
        BodyEntity bodyEntity= BodyEntity.get(commonStatusType, data);
        return ResponseEntity.status(commonStatusType.getHttpStatus()).body(bodyEntity);
    }*/

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @Getter
    public static class BodyEntity<T> {
        Integer detailCd;
        String msg;
        T data;

        public BodyEntity(Integer detailCd, String msg, T data) {
            this.detailCd =detailCd;
            this.msg = msg;
            this.data = data;
        }

        public static <T> BodyEntity<T> data(@Nullable T data) {
            return new BodyEntity<>(null, null, data);
        }

        public static <T> BodyEntity<T> get(CommonStatusType commonStatusType, @Nullable T data) {
            return new BodyEntity<>(commonStatusType.getDetailCd(), commonStatusType.getMsg(), data);
        }

    }
}
