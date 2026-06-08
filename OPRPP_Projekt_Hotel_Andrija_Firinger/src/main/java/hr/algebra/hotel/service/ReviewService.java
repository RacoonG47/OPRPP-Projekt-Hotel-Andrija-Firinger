package hr.algebra.hotel.service;

import hr.algebra.hotel.dao.IReviewRepository;
import hr.algebra.hotel.model.Review;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ReviewService {

    private final IReviewRepository reviewRepository;

    public ReviewService(IReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public void insert(Review review) {
        reviewRepository.insert(review);
    }

    public void update(Review review) {
        reviewRepository.update(review);
    }

    public void delete(Integer id) {
        reviewRepository.delete(id);
    }

    public Optional<Review> findById(Integer id) {
        return reviewRepository.findById(id);
    }

    public List<Review> findByHotel(Integer hotelId) {
        return reviewRepository.findByHotel(hotelId);
    }

    public List<Review> findByCondition(List<Review> reviews, Predicate<Review> predicate) {
        return reviews.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public void forEach(List<Review> reviews, Consumer<Review> consumer) {
        reviews.forEach(consumer);
    }

    public OptionalDouble averageRating(List<Review> reviews) {
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average();
    }

    public boolean anyMatch(List<Review> reviews, Predicate<Review> predicate) {
        return reviews.stream().anyMatch(predicate);
    }

    public boolean noneMatch(List<Review> reviews, Predicate<Review> predicate) {
        return reviews.stream().noneMatch(predicate);
    }

    public List<Review> sorted(List<Review> reviews) {
        return reviews.stream()
                .sorted()
                .collect(Collectors.toList());
    }
}