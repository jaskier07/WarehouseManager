package pl.kania.warehousemanager.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.kania.warehousemanager.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT p FROM User p JOIN FETCH p.roles WHERE p.login = (:login)")
    User findByLoginAndFetchRolesEagerly(@Param("login") String login);

    User findByLogin(String login);
}
