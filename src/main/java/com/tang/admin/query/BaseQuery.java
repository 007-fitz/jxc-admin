package com.tang.admin.query;


import lombok.Data;

@Data
public class BaseQuery {
    private Integer page=1;
    private Integer limit=10;

}
