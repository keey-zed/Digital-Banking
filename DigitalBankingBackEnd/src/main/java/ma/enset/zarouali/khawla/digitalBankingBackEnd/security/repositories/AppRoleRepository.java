package ma.enset.zarouali.khawla.digitalBankingBackEnd.security.repositories;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.security.entities.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AppRoleRepository extends JpaRepository<AppRole, Long> {
    AppRole findAppRoleByRoleName(String username);
}