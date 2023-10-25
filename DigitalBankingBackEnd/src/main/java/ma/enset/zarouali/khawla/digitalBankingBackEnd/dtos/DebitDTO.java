package ma.enset.zarouali.khawla.digitalBankingBackEnd.dtos;
import lombok.Data;
@Data
public class DebitDTO {
    private String accountId ;
    private double amount ;
    private String description ;
}