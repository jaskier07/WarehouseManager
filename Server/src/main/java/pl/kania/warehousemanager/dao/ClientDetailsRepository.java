package pl.kania.warehousemanager.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.kania.warehousemanager.model.OauthClientDetails;

@Repository
public interface ClientDetailsRepository extends JpaRepository<OauthClientDetails, Long> {
    OauthClientDetails findByClientId(String clientId);

    @Query("SELECT d.clientSecret FROM OauthClientDetails d WHERE d.clientId = :id")
    String findClientSecretByClientId(@Param("id") String clientId);
}
