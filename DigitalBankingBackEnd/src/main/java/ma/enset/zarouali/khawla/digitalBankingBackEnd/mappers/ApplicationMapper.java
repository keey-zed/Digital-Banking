package ma.enset.zarouali.khawla.digitalBankingBackEnd.mappers;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.dtos.AccountOperationDTO;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.dtos.CurrentBankAccountDTO;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.dtos.CustomerDTO;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.dtos.SavingBankAccountDTO;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.entities.AccountOperation;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.entities.CurrentAccount;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.entities.Customer;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.entities.SavingAccount;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class ApplicationMapper {
    @Autowired
    ApplicationMapper applicationMapper;
    public CustomerDTO fromCustomer(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer, customerDTO);
        return customerDTO;
    }
    public Customer fromCustomerDTO(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        return customer;
    }
    public SavingBankAccountDTO fromSavingBankAccount(SavingAccount savingAccount) {
        SavingBankAccountDTO savingBankAccountDTO = new SavingBankAccountDTO();
        BeanUtils.copyProperties(savingAccount, savingBankAccountDTO);
        savingBankAccountDTO.setCustomerDTO(applicationMapper.fromCustomer(savingAccount.getCustomer()));
        savingBankAccountDTO.setType("saving account");
        return savingBankAccountDTO;
    }
    public SavingAccount fromSavingBankAccountDTO(SavingBankAccountDTO savingAccountDTO) {
        SavingAccount savingAccount = new SavingAccount();
        BeanUtils.copyProperties(savingAccountDTO, savingAccount);
        savingAccount.setCustomer(applicationMapper.fromCustomerDTO(savingAccountDTO.getCustomerDTO()));
        return savingAccount;
    }
    public CurrentAccount fromCurrentBankAccountDTO(CurrentBankAccountDTO currentAccountDTO) {
        CurrentAccount currentAccount = new CurrentAccount();
        BeanUtils.copyProperties(currentAccountDTO, currentAccount);
        currentAccount.setCustomer(applicationMapper.fromCustomerDTO(currentAccountDTO.getCustomerDTO()));
        return  currentAccount;
    }
    public CurrentBankAccountDTO fromCurrentBankAccount(CurrentAccount currentAccount) {
        CurrentBankAccountDTO currentBankAccountDTO = new CurrentBankAccountDTO();
        BeanUtils.copyProperties(currentAccount, currentBankAccountDTO);
        currentBankAccountDTO.setType("Current account");
        currentBankAccountDTO.setCustomerDTO(applicationMapper.fromCustomer(currentAccount.getCustomer()));
        return currentBankAccountDTO;
    }
    public AccountOperationDTO fromAccountOperation(AccountOperation accountOperation) {
        AccountOperationDTO accountOperationDTO = new AccountOperationDTO();
        BeanUtils.copyProperties(accountOperation, accountOperationDTO);
        accountOperationDTO.setAccountId(accountOperation.getBankAccount().getId());
        return  accountOperationDTO;
    }
}