package ma.enset.zarouali.khawla.digitalBankingBackEnd.entities;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
@Data @NoArgsConstructor @AllArgsConstructor @Entity @DiscriminatorValue("CA")
public class CurrentAccount extends BankAccount {
    private double overDraft;
}