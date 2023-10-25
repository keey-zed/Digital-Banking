package ma.enset.zarouali.khawla.digitalBankingBackEnd.entities;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.enums.OperationType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import javax.persistence.*;
import java.util.Date;
@Data @NoArgsConstructor @AllArgsConstructor @Entity
public class AccountOperation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date operationDate;
    private double amount;
    private OperationType type;
    private String description;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private BankAccount bankAccount;
}