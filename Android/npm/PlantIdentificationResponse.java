package com.tom.npm;

import java.util.List;


public class PlantIdentificationResponse {
    private Query query;
    private String language;
    private String preferredReferential;
    private String switchToProject;
    private String bestMatch;
    private List<Result> results;
    private int remainingIdentificationRequests;
    private String version;



    // Getters and setters for top-level response
    public Query getQuery() { return query; }


    public void setQuery(Query query) { this.query = query; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getPreferredReferential() { return preferredReferential; }
    public void setPreferredReferential(String preferredReferential) { this.preferredReferential = preferredReferential; }

    public String getSwitchToProject() { return switchToProject; }
    public void setSwitchToProject(String switchToProject) { this.switchToProject = switchToProject; }

    public String getBestMatch() { return bestMatch; }
    public void setBestMatch(String bestMatch) { this.bestMatch = bestMatch; }

    public List<Result> getResults() { return results; }
    public void setResults(List<Result> results) { this.results = results; }

    public int getRemainingIdentificationRequests() { return remainingIdentificationRequests; }
    public void setRemainingIdentificationRequests(int remainingIdentificationRequests) { this.remainingIdentificationRequests = remainingIdentificationRequests; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    // Nested classes to handle structured parts of the response
    public static class Result {
        private double score;
        private Species species;
        private List<ImageData> images;
        private Gbif gbif;
        private Powo powo;
        private Iucn iucn;

        // Getters and setters for Result
        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }

        public Species getSpecies() { return species; }
        public void setSpecies(Species species) { this.species = species; }

        // Add getters and setters for other fields...
    }

    public static class Species {
        private String scientificNameWithoutAuthor;
        private String scientificNameAuthorship;
        private String scientificName;
        private Genus genus;
        private Family family;
        private List<String> commonNames;

        // Getters and setters for Species
        public String getScientificNameWithoutAuthor() { return scientificNameWithoutAuthor; }
        public void setScientificNameWithoutAuthor(String scientificNameWithoutAuthor) { this.scientificNameWithoutAuthor = scientificNameWithoutAuthor; }

        public String getScientificNameAuthorship() { return scientificNameAuthorship; }
        public void setScientificNameAuthorship(String scientificNameAuthorship) { this.scientificNameAuthorship = scientificNameAuthorship; }

        public String getScientificName() { return scientificName; }
        public void setScientificName(String scientificName) { this.scientificName = scientificName; }

        public Genus getGenus() { return genus; }
        public void setGenus(Genus genus) { this.genus = genus; }

        public Family getFamily() { return family; }
        public void setFamily(Family family) { this.family = family; }

        public List<String> getCommonNames() { return commonNames; }
        public void setCommonNames(List<String> commonNames) { this.commonNames = commonNames; }
    }

    public static class Genus {
        private String scientificNameWithoutAuthor;
        private String scientificNameAuthorship;
        private String scientificName;

        // Getters and setters for Genus
    }

    public static class Query {
        private String name;
        private String value;

        // Getters and setters for Query
    }

    public static class Family {
        private String scientificNameWithoutAuthor;
        private String scientificNameAuthorship;
        private String scientificName;

        // Getters and setters for Family
    }

    public static class ImageData {
        private String organ;
        private String author;
        private String license;
        private ImageDate date;
        private String citation;
        private ImageUrl url;

        // Getters and setters for ImageData
    }

    public static class ImageDate {
        private long timestamp;
        private String string;

        // Getters and setters for ImageDate
    }

    public static class ImageUrl {
        private String o; // original size
        private String m; // medium size
        private String s; // small size

        // Getters and setters for ImageUrl
    }

    public static class Gbif {
        private int id;

        // Getters and setters for Gbif
    }

    public static class Powo {
        private String id;

        // Getters and setters for Powo
    }

    public static class Iucn {
        private String id;
        private String category;

        // Getters and setters for Iucn
    }
}
