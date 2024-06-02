package com.example.npm;

public class PlantDetails {
    private String species;
    private boolean isPoisonous;
    private boolean isFlower;
    private boolean isInvasive;
    private String plantType;
    private String growthCycle;
    private String plantingTime;
    private String function;

    // 构造函数
    public PlantDetails(String species, boolean isPoisonous, boolean isFlower, boolean isInvasive, String plantType, String growthCycle, String plantingTime, String function) {
        this.species = species;
        this.isPoisonous = isPoisonous;
        this.isFlower = isFlower;
        this.isInvasive = isInvasive;
        this.plantType = plantType;
        this.growthCycle = growthCycle;
        this.plantingTime = plantingTime;
        this.function = function;
    }

    // Getter and Setter methods
    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public boolean isPoisonous() {
        return isPoisonous;
    }

    public void setPoisonous(boolean poisonous) {
        isPoisonous = poisonous;
    }

    public boolean isFlower() {
        return isFlower;
    }

    public void setFlower(boolean flower) {
        isFlower = flower;
    }

    public boolean isInvasive() {
        return isInvasive;
    }

    public void setInvasive(boolean invasive) {
        isInvasive = invasive;
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
}