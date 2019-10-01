package com.township.manager;

class Wing {

    String name;
    int numberOfFloors, numberOfApartmentsPerFloor;
    int namingConvention;

    public void setName(String name) {
        this.name = name;
    }

    public void setNamingConvention(int namingConvention) {
        this.namingConvention = namingConvention;
    }

    public void setNumberOfApartmentsPerFloor(int numberOfApartmentsPerFloor) {
        this.numberOfApartmentsPerFloor = numberOfApartmentsPerFloor;
    }

    public void setNumberOfFloors(int numberOfFloors) {
        this.numberOfFloors = numberOfFloors;
    }

    public int getNamingConvention() {
        return namingConvention;
    }

    public int getNumberOfApartmentsPerFloor() {
        return numberOfApartmentsPerFloor;
    }

    public int getNumberOfFloors() {
        return numberOfFloors;
    }

    public String getName() {
        return name;
    }
}
