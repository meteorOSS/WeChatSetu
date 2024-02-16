package com.meteor.setu.model;

import lombok.Data;

@Data
public class GitHubRelease {
    private String tagName;
    private String name;
    private String body;
}