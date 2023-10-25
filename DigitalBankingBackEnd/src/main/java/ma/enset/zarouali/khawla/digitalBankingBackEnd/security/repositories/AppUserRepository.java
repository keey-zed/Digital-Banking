package ma.enset.zarouali.khawla.digitalBankingBackEnd.security.repositories;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.security.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findAppUserByUsername(String username);
}