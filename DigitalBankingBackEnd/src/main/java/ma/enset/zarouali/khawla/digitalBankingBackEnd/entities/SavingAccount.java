package ma.enset.zarouali.khawla.digitalBankingBackEnd.entities;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
@Data @NoArgsConstructor @AllArgsConstructor @DiscriminatorValue("SA") @Entity
public class SavingAccount extends BankAccount {
    private double interestRate;
}