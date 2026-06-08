package hr.algebra.hotel.model;

import java.util.Objects;

public abstract class NamedEntity extends Entity implements Comparable<NamedEntity> {

    private String name;

    protected NamedEntity() {}

    protected NamedEntity(Integer id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(NamedEntity other) {
        return this.name.compareToIgnoreCase(other.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamedEntity other)) return false;
        return super.equals(o) && Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
}