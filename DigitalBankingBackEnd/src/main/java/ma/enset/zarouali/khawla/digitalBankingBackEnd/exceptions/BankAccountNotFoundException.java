package ma.enset.zarouali.khawla.digitalBankingBackEnd.exceptions;
public class BankAccountNotFoundException extends Exception {
    public BankAccountNotFoundException(String bank_account_not_found) {
        super(bank_account_not_found);
    }
}