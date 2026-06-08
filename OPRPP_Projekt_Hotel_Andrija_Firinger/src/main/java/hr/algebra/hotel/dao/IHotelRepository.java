package hr.algebra.hotel.dao;

import hr.algebra.hotel.model.Amenity;
import hr.algebra.hotel.model.Category;
import hr.algebra.hotel.model.Hotel;

import java.util.List;

public interface IHotelRepository extends IRepository<Hotel, Integer> {

    List<Hotel> findByCity(Integer cityId);
    List<Hotel> findByCategory(Category category);
    List<Hotel> findByAmenity(Integer amenityId);

    Integer insertAndReturnId(Hotel hotel);
    void linkAmenity(Integer hotelId, Integer amenityId);
    void unlinkAmenity(Integer hotelId, Integer amenityId);
    List<Amenity> findAmenities(Integer hotelId);
}