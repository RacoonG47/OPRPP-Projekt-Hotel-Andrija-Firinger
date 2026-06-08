package hr.algebra.hotel.dao;

import hr.algebra.hotel.model.User;

import java.util.Optional;

public interface IUserRepository extends IRepository<User, Integer> {

    Optional<User> findByUsername(String username);
}