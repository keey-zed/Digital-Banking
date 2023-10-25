package ma.enset.zarouali.khawla.digitalBankingBackEnd.exceptions;
public class BalanceNotSufficientException extends  Exception {
    public BalanceNotSufficientException(String message) {
        super(message);
    }
}