package hr.algebra.hotel.dao;

import hr.algebra.hotel.model.Review;

import java.util.List;

public interface IReviewRepository extends IRepository<Review, Integer> {

    List<Review> findByHotel(Integer hotelId);
}
