package hr.algebra.hotel.model;

import java.util.Objects;

public class City extends NamedEntity {

    private String country;

    public City() {}

    public City(Integer id, String name, String country) {
        super(id, name);
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof City other)) return false;
        return super.equals(o) && Objects.equals(country, other.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), country);
    }

    @Override
    public String toString() {
        return getName() + ", " + country;
    }
}