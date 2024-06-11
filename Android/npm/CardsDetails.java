package com.tom.npm;

public class CardsDetails {
    private String species;
    private String descriptions;
    private int drawable;

    public CardsDetails(String species, String descriptions, int drawable) {
        this.species = species;
        this.descriptions = descriptions;
        this.drawable = drawable;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }
}
