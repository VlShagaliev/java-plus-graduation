package ru.practicum.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.practicum.model.User;

import java.util.List;

@Repository
public class CustomUserRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<User> findAllByIdsOrAll(Iterable<Long> ids, int from, int size) {
        String jpql;
        TypedQuery<User> query;

        if (ids == null) {
            jpql = "SELECT u FROM User u";
            query = entityManager.createQuery(jpql, User.class);
        } else {
            jpql = "SELECT u FROM User u WHERE u.id IN :ids";
            query = entityManager.createQuery(jpql, User.class);
            query.setParameter("ids", ids);
        }
        query.setFirstResult(from);
        query.setMaxResults(size);

        return query.getResultList();
    }
}