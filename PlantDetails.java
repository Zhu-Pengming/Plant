package com.example.npm;

public class PlantDetails {
    private String species;
    private String isPoisonous;
    private String isFlower;
    private String isInvasive;
    private String plantType;
    private String growthCycle;
    private String plantingTime;
    private String function;

    private String englishName;

    // 构造函数
    public PlantDetails(String species, String isPoisonous, String isFlower, String isInvasive, String plantType, String growthCycle, String plantingTime, String function,String englishName) {
        this.species = species;
        this.isPoisonous = isPoisonous;
        this.isFlower = isFlower;
        this.isInvasive = isInvasive;
        this.plantType = plantType;
        this.growthCycle = growthCycle;
        this.plantingTime = plantingTime;
        this.function = function;
        this.englishName = englishName;
    }

    // Getter and Setter methods
    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getIsFlower() {
        return isFlower;
    }

    public String getIsInvasive() {
        return isInvasive;
    }

    public String getIsPoisonous() {
        return isPoisonous;
    }

    public String getPlantType() {
        return plantType;
    }

    public void setPlantType(String plantType) {
        this.plantType = plantType;
    }

    public String getGrowthCycle() {
        return growthCycle;
    }

    public void setGrowthCycle(String growthCycle) {
        this.growthCycle = growthCycle;
    }

    public String getPlantingTime() {
        return plantingTime;
    }

    public void setPlantingTime(String plantingTime) {
        this.plantingTime = plantingTime;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }



}