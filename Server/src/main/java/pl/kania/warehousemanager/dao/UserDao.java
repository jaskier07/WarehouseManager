package pl.kania.warehousemanager.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kania.warehousemanager.model.User;

@Repository
public interface UserDao extends JpaRepository<User, Long> {
}
