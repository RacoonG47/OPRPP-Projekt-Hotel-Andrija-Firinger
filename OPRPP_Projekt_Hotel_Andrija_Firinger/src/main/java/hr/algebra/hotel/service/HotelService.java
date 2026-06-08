package hr.algebra.hotel.service;

import hr.algebra.hotel.dao.IHotelRepository;
import hr.algebra.hotel.model.Amenity;
import hr.algebra.hotel.model.Category;
import hr.algebra.hotel.model.Hotel;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HotelService {

    private final IHotelRepository hotelRepository;

    public HotelService(IHotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public void insert(Hotel hotel) {
        hotelRepository.insert(hotel);
    }

    public Integer insertAndReturnId(Hotel hotel) {return hotelRepository.insertAndReturnId(hotel);}

    public void update(Hotel hotel) {
        hotelRepository.update(hotel);
    }

    public void delete(Integer id) {
        hotelRepository.delete(id);
    }

    public List<Hotel> findAll() {
        return hotelRepository.findAll();
    }

    public Optional<Hotel> findById(Integer id) {
        return hotelRepository.findById(id);
    }

    public List<Hotel> findByCity(Integer cityId) {
        return hotelRepository.findByCity(cityId);
    }

    public List<Hotel> findByCategory(Category category) {
        return hotelRepository.findByCategory(category);
    }

    public List<Hotel> findByAmenity(Integer amenityId) {
        return hotelRepository.findByAmenity(amenityId);
    }

    public void linkAmenity(Integer hotelId, Integer amenityId) {
        hotelRepository.linkAmenity(hotelId, amenityId);
    }

    public void unlinkAmenity(Integer hotelId, Integer amenityId) {
        hotelRepository.unlinkAmenity(hotelId, amenityId);
    }

    public List<Amenity> findAmenities(Integer hotelId) {
        return hotelRepository.findAmenities(hotelId);
    }

     public List<Hotel> findByCondition(List<Hotel> hotels, Predicate<Hotel> predicate) {
        return hotels.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public <R> List<R> map(List<Hotel> hotels, Function<Hotel, R> mapper) {
        return hotels.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    public void forEach(List<Hotel> hotels, Consumer<Hotel> consumer) {
        hotels.forEach(consumer);
    }

    public List<Hotel> sorted(List<Hotel> hotels) {
        return hotels.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public Optional<Hotel> findCheapest(List<Hotel> hotels) {
        return hotels.stream()
                .min(Comparator.comparing(Hotel::getPricePerNight));
    }

    public Optional<Hotel> findMostExpensive(List<Hotel> hotels) {
        return hotels.stream()
                .max(Comparator.comparing(Hotel::getPricePerNight));
    }

    public List<String> distinctCityNames(List<Hotel> hotels) {
        return hotels.stream()
                .map(h -> h.getCity().getName())
                .distinct()
                .collect(Collectors.toList());
    }

    public boolean anyMatch(List<Hotel> hotels, Predicate<Hotel> predicate) {
        return hotels.stream().anyMatch(predicate);
    }

    public boolean noneMatch(List<Hotel> hotels, Predicate<Hotel> predicate) {
        return hotels.stream().noneMatch(predicate);
    }

    public Optional<Hotel> findFirst(List<Hotel> hotels, Predicate<Hotel> predicate) {
        return hotels.stream()
                .filter(predicate)
                .findFirst();
    }

    public List<Hotel> paged(List<Hotel> hotels, int skip, int limit) {
        return hotels.stream()
                .skip(skip)
                .limit(limit)
                .collect(Collectors.toList());
    }
}