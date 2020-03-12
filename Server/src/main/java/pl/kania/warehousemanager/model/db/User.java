package pl.kania.warehousemanager.model.db;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kania.warehousemanager.model.WarehouseRole;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@NoArgsConstructor
@Getter
@Entity
public class User {

    @Id
    @Setter
    @GeneratedValue
    private Long id;

    @Column(name = "LOGIN")
    private String login;

    @Column(name = "PASSWORD")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE")
    private WarehouseRole role;

    public User(String login, String password, WarehouseRole role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public boolean isManager() {
        return role.equals(WarehouseRole.MANAGER);
    }
}
