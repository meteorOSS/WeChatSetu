package com.meteor.setu.model;

import lombok.Data;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.List;

@Data
public class DataItem {
    private Long pid;
    private Integer p;
    private Long uid;
    private String title;
    private String author;
    private Boolean r18;
    private Integer width;
    private Integer height;
    private List<String> tags;
    private String ext;
    private Integer aiType;
    private Long uploadDate;
    private Urls urls;
}
