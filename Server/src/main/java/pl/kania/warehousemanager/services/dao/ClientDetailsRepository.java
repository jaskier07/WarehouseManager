package pl.kania.warehousemanager.services.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.kania.warehousemanager.model.db.ClientDetails;

@Repository
public interface ClientDetailsRepository extends JpaRepository<ClientDetails, Long> {
    ClientDetails findByClientId(String clientId);

    @Query("SELECT d.clientSecret FROM ClientDetails d WHERE d.clientId = :id")
    String findClientSecretByClientId(@Param("id") String clientId);
}
