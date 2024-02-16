package com.meteor.setu.model;

import lombok.Data;
import java.util.List;

@Data
public class SetuResponse {
    private String error;
    private List<DataItem> data;
}
