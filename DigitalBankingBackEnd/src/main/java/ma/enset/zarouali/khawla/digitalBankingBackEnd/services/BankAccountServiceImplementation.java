package ma.enset.zarouali.khawla.digitalBankingBackEnd.services;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.dtos.*;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.entities.*;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.enums.OperationType;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.exceptions.BalanceNotSufficientException;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.exceptions.BankAccountNotFoundException;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.exceptions.CustomerNotFoundException;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.mappers.ApplicationMapper;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.repositories.AccountOperationRepository;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.repositories.BankAccountRepository;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.repositories.CustomerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Transactional @Service @AllArgsConstructor @NoArgsConstructor @Slf4j
public class BankAccountServiceImplementation implements BankAccountService {
    @Autowired
    @Order(1)
    BankAccountRepository bankAccountRepository;
    @Autowired
    @Order(1)
    CustomerRepository customerRepository;
    @Autowired
    @Order(1)
    AccountOperationRepository accountOperationRepository;
    @Autowired
    ApplicationMapper dtoMapper;
    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        customer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(customer);
    }
    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null)
            throw new CustomerNotFoundException("The customer was not found");
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setInterestRate(interestRate);
        savingAccount.setCustomer(customer);
        savingAccount = bankAccountRepository.save(savingAccount);
        return dtoMapper.fromSavingBankAccount(savingAccount);
    }
    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overdraft, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null)
            throw new CustomerNotFoundException("The customer was not found");
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setOverDraft(overdraft);
        currentAccount.setCustomer(customer);
        currentAccount = bankAccountRepository.save(currentAccount);
        return dtoMapper.fromCurrentBankAccount(currentAccount);
    }
    public List<BankAccountDTO> bankAccountListOfCustomer(Long id){
        Customer customer = new Customer();
        customer.setId(id);
        List<BankAccount> bankAccounts = bankAccountRepository.findByCustomer(customer);
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingBankAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentBankAccount(currentAccount);
            }
        }).collect(Collectors.toList());
        return bankAccountDTOS;
    }
    @Override
    public List<CustomerDTO> listCustomers(int page) {
        Page<Customer> customers = customerRepository.findAll(PageRequest.of(page,6));
        List<CustomerDTO> collect = customers.stream().map(customer -> dtoMapper.fromCustomer(customer)).collect(Collectors.toList());
        return collect;
    }
    @Override
    public List<Customer> listCustomer() {
        return customerRepository.findAll();
    }
    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(() -> new BankAccountNotFoundException("the bank account was not found"));
        if(bankAccount instanceof SavingAccount) {
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            SavingBankAccountDTO savingBankAccountDTO = dtoMapper.fromSavingBankAccount(savingAccount);
            return savingBankAccountDTO ;
        } else {
            CurrentAccount currentAccount =(CurrentAccount) bankAccount;
            CurrentBankAccountDTO currentBankAccountDTO = dtoMapper.fromCurrentBankAccount(currentAccount);
            return currentBankAccountDTO ;
        }
    }
    @Override
    public void debit(String accountId, double amount , String description) throws BalanceNotSufficientException, BankAccountNotFoundException {
        System.out.println(accountId);
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(() -> new BankAccountNotFoundException("The bank account was not found") );
        if (bankAccount.getBalance() < amount) {
            throw new BalanceNotSufficientException("The balance is not sufficient");
        }
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setOperationDate(new Date());
        accountOperation.setAmount(amount);
        bankAccountRepository.save(bankAccount);
        accountOperationRepository.save(accountOperation);
    }
    @Override
    public void credit(String accountId, double amount , String description) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(()->new BankAccountNotFoundException("The bank account was not found") );
        if (bankAccount == null)
            throw new BankAccountNotFoundException("The bank account was not found");
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setOperationDate(new Date());
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setAmount(amount);
        accountOperation.setOperationDate(new Date());
        accountOperation.setDescription(accountOperation.getDescription());
        bankAccountRepository.save(bankAccount);
        accountOperationRepository.save(accountOperation);
    }
    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
        AccountOperationDTO accountSourceOperationDTO = new AccountOperationDTO();
        accountSourceOperationDTO.setAccountId(accountIdSource);
        accountSourceOperationDTO.setAmount(amount);
        debit(accountIdSource, amount, "Just a test description :)");
        AccountOperationDTO accountDestOperationDTO = new AccountOperationDTO();
        accountDestOperationDTO.setAccountId(accountIdDestination);
        accountDestOperationDTO.setAmount(amount);
        credit(accountIdDestination, amount, "Just a description test :)");
    }
    @Override
    public BankAccountsDTO getBankAccountList(int page) {
        Page<BankAccount> bankAccounts = bankAccountRepository.findAll(PageRequest.of(page,5));
        List<BankAccountDTO> bankAccountDTOList = bankAccounts.stream().map(bankAccount -> {
            if(bankAccount instanceof  SavingAccount){
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingBankAccount(savingAccount);
            }else{
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentBankAccount(currentAccount);
            }
                }
        ).collect(Collectors.toList());
        BankAccountsDTO  bankAccountsDTO= new BankAccountsDTO();
        bankAccountsDTO.setBankAccountDTOS(bankAccountDTOList);
        bankAccountsDTO.setTotalPage(bankAccounts.getTotalPages());
        return bankAccountsDTO;
    }
    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("The customer was not found"));
        return dtoMapper.fromCustomer(customer);
    }
    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        customer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(customer);
    }
    @Override
    public BankAccountDTO updateBankAccount(BankAccountDTO bankAccountDTO) {
        BankAccount bankAccount;
        if(bankAccountDTO.getType().equals("saving account")) {
            SavingBankAccountDTO saving = new SavingBankAccountDTO();
            BeanUtils.copyProperties(bankAccountDTO, saving);
            bankAccount = dtoMapper.fromSavingBankAccountDTO(saving);
            bankAccount = bankAccountRepository.save(bankAccount);
            return dtoMapper.fromSavingBankAccount((SavingAccount) bankAccount);

        } else {
            CurrentBankAccountDTO current = new CurrentBankAccountDTO();
            BeanUtils.copyProperties(bankAccountDTO, current);
            bankAccount = dtoMapper.fromCurrentBankAccountDTO(current);
            bankAccount = bankAccountRepository.save(bankAccount);
            return dtoMapper.fromCurrentBankAccount((CurrentAccount) bankAccount);
        }
    }
    @Override
    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
    }
    @Override
    public List<AccountOperationDTO> accountOperationHistory(String accountId){
        List<AccountOperation>  accountOperations= accountOperationRepository.findByBankAccountId(accountId);
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.stream().map(accountOperation -> {
           return   dtoMapper.fromAccountOperation(accountOperation);
        }).collect(Collectors.toList());
        return accountOperationDTOS;
    }
    @Override
   public AccountHistoryDTO getAccoutHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount= bankAccountRepository.findById(accountId).orElse(null);
        if (bankAccount == null)
            throw  new BankAccountNotFoundException("the bank account was not found");
        Page<AccountOperation> accountOperationPage = accountOperationRepository.findByBankAccountIdOrderByOperationDateDesc(accountId, PageRequest.of(page,size));
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationDTOList=accountOperationPage.getContent().stream().map(op->dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTOList(accountOperationDTOList);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setTotalPages(accountOperationPage.getTotalPages());
        return accountHistoryDTO;
   }
    @Override
    public CustomersDTO getCustomerByName(String keyword, int page) throws CustomerNotFoundException {
        Page<Customer> customers ;
        customers = customerRepository.searchByName(keyword, PageRequest.of(page,5));
        List<CustomerDTO> customerDTOS=customers.getContent().stream().map(c->dtoMapper.fromCustomer(c)).collect(Collectors.toList());
        System.out.println(customerDTOS);
        if (customers == null)
            throw new CustomerNotFoundException("The customer was not found");
        CustomersDTO customersDTO= new CustomersDTO();
        customersDTO.setCustomerDTO(customerDTOS);
        customersDTO.setTotalpage(customers.getTotalPages());
        return customersDTO;
    }
}