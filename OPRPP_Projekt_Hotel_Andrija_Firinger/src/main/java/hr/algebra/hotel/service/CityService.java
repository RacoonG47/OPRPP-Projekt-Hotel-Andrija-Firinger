package hr.algebra.hotel.service;



import hr.algebra.hotel.dao.ICityRepository;
import hr.algebra.hotel.model.City;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CityService {

    private final ICityRepository cityRepository;

    public CityService(ICityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public void insert(City city) {
        cityRepository.insert(city);
    }

    public void update(City city) {
        cityRepository.update(city);
    }

    public void delete(Integer id) {
        cityRepository.delete(id);
    }

    public Optional<City> findById(Integer id) {
        return cityRepository.findById(id);
    }

    public List<City> findAll() {
        return cityRepository.findAll();
    }

    public List<City> findByCondition(Predicate<City> predicate) {
        return cityRepository.findAll().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

     public void forEach(List<City> cities, Consumer<City> consumer) {
        cities.forEach(consumer);
    }
}