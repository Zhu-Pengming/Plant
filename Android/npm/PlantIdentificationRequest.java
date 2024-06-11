package com.tom.npm;

import java.util.List;

public class PlantIdentificationRequest {
    private String project;
    private List<String> images;
    private List<String> organs;
    private boolean includeRelatedImages;
    private boolean noReject;
    private String language;

    public PlantIdentificationRequest(String project, List<String> images, List<String> organs, boolean includeRelatedImages, boolean noReject, String language) {
        this.project = project;
        this.images = images;
        this.organs = organs;
        this.includeRelatedImages = includeRelatedImages;
        this.noReject = noReject;
        this.language = language;
    }

    // Getters and Setters
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getOrgans() {
        return organs;
    }

    public void setOrgans(List<String> organs) {
        this.organs = organs;
    }

    public boolean isIncludeRelatedImages() {
        return includeRelatedImages;
    }

    public void setIncludeRelatedImages(boolean includeRelatedImages) {
        this.includeRelatedImages = includeRelatedImages;
    }

    public boolean isNoReject() {
        return noReject;
    }

    public void setNoReject(boolean noReject) {
        this.noReject = noReject;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}

