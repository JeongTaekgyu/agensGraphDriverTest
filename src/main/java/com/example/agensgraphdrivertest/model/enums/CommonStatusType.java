package com.example.agensgraphdrivertest.model.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum CommonStatusType {
    SUCCESS(HttpStatus.OK, null, "OK")
    ,INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,null,"")
    ,BAD_REQUEST(HttpStatus.BAD_REQUEST, null, "")
    ,UNAUTHORIZED(HttpStatus.UNAUTHORIZED, null, "")

    ,LOGIN_FAIL(42000, BAD_REQUEST, "아이디, 패스워드를 확인하세요")
    ,CSV_TABLE_CHANGE_FAIL(42001, BAD_REQUEST, "CSV 파일 테이블 변환에 실패하였습니다.")
    ,CSV_FILE_DELETE_FAIL(42002, BAD_REQUEST, "CSV 파일 삭제에 실패하였습니다.")
    ,CSV_FILE_UPLOAD_FAIL(42003, BAD_REQUEST, "CSV 파일 업로드에 실패하였습니다.")
    ,CSV_DETAIL_FAIL(42004, BAD_REQUEST, "CSV 상세보기에 실패하였습니다.")

    ,DATABASE_ADD_FAIL(42005, BAD_REQUEST, "데이터 베이스 저장에 실패하였습니다.")
    ,DATABASE_DELETE_FAIL(42006, BAD_REQUEST, "데이터 베이스 삭제에 실패하였습니다.")
    ,DATABASE_DETAIL_FAIL(42007, BAD_REQUEST, "데이터 베이스 상세보기에 실패하였습니다.")
    ,DATABASE_TABLE_DETAIL_FAIL(42008, BAD_REQUEST, "데이터 베이스 테이블 상세보기에 실패하였습니다.")

    ,WORKSPACE_ADD_FAIL(42009, BAD_REQUEST, "워크스페이스 생성을 실패하였습니다.")
    ,WORKSPACE_CHECK_FAIL(42010, BAD_REQUEST, "워크스페이스 중복 체크를 실패하였습니다.")

    ,GRAPH_ADD_FAIL(42011, BAD_REQUEST, "그래프 생성을 실패하였습니다.")
    ,GRAPH_CHECK_FAIL(42012, BAD_REQUEST, "그래프 중복 체크를 실패하였습니다.")
    ,GRAPH_ETL_FAIL(HttpStatus.BAD_REQUEST, BAD_REQUEST, "ETL 생성에 실패하였습니다.")
    ;

    Object status;
    CommonStatusType parent;
    String msg;

    CommonStatusType(Object status, CommonStatusType parent, String msg) {
        this.status = status;
        this.parent = parent;
        this.msg = msg;
    }

    public int getDetailCd() {
        if(this.status instanceof HttpStatus) return ((HttpStatus) this.status).value();
        else return (Integer) status;
    }
}