package hr.algebra.hotel.model;

public class Amenity extends NamedEntity {

    public Amenity() {}

    public Amenity(Integer id, String name) {
        super(id, name);
    }

    @Override
    public String toString() {
        return getName();
    }
}