package hr.algebra.hotel.dao;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;


public interface IRepository<T, ID> {

    void insert(T entity);
    void update(T entity);
    void delete(ID id);
    List<T> findAll();
    Optional<T> findById(ID id);
}

