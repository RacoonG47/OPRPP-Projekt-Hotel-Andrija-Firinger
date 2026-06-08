package hr.algebra.hotel.service;

import hr.algebra.hotel.dao.IAmenityRepository;
import hr.algebra.hotel.model.Amenity;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class AmenityService {

    private final IAmenityRepository amenityRepository;

    public AmenityService(IAmenityRepository amenityRepository) {
        this.amenityRepository = amenityRepository;
    }

    public void insert(Amenity amenity) {
        amenityRepository.insert(amenity);
    }

    public void update(Amenity amenity) {
        amenityRepository.update(amenity);
    }

    public void delete(Integer id) {
        amenityRepository.delete(id);
    }

    public Optional<Amenity> findById(Integer id) {
        return amenityRepository.findById(id);
    }

    public List<Amenity> findAll() {
        return amenityRepository.findAll();
    }

     public List<Amenity> findByCondition(Predicate<Amenity> predicate) {
        return amenityRepository.findAll().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public void forEach(List<Amenity> amenities, Consumer<Amenity> consumer) {
        amenities.forEach(consumer);
    }
}