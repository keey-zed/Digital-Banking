package ma.enset.zarouali.khawla.digitalBankingBackEnd;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.entities.AccountOperation;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.entities.CurrentAccount;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.entities.SavingAccount;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.enums.AccountStatus;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.enums.OperationType;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.exceptions.CustomerNotFoundException;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.repositories.AccountOperationRepository;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.repositories.BankAccountRepository;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.repositories.CustomerRepository;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.security.entities.AppRole;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.security.entities.AppUser;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.security.services.AccountService;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.services.BankAccountService;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.dtos.BankAccountDTO;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.dtos.CustomerDTO;
import ma.enset.zarouali.khawla.digitalBankingBackEnd.dtos.SavingBankAccountDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
@SpringBootApplication
public class DigitalBankingBackEnd {
    public static void main(String[] args) {
        SpringApplication.run(DigitalBankingBackEnd.class, args);
    }
    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService, AccountService accountService) {
        return args -> {
            accountService.addNewRole(new AppRole(null, "ADMIN"));
            accountService.addNewRole(new AppRole(null, "CUSTOMER"));
            accountService.addNewUser(new AppUser(null, "admin", "admin", new ArrayList<>()));
            accountService.addNewUser(new AppUser(null, "Jimin", "1310", new ArrayList<>()));
            accountService.addNewUser(new AppUser(null, "Jungkook", "7777", new ArrayList<>()));
            accountService.addNewUser(new AppUser(null, "Namjoon", "1306", new ArrayList<>()));
            accountService.addRoleToUser("admin", "ADMIN");
            accountService.addRoleToUser("Jimin", "CUSTOMER");
            accountService.addRoleToUser("Jungkook", "CUSTOMER");
            accountService.addRoleToUser("Namjoon", "CUSTOMER");
            Stream.of("Jimin", "Jungkook", "Namjoon").forEach(name -> {
                CustomerDTO customer = new CustomerDTO();
                customer.setName(name);
                customer.setEmail(name + "@gmail.com");
                bankAccountService.saveCustomer(customer);
            });
            bankAccountService.listCustomers(1).forEach(customer -> {
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random() * 1310, 7777, customer.getId());
                    bankAccountService.saveSavingBankAccount(Math.random() * 1306, 3.7, customer.getId());
                } catch (CustomerNotFoundException e) {
                    e.printStackTrace();
                }
            });
            List<BankAccountDTO> bankAccounts = bankAccountService.getBankAccountList(1).getBankAccountDTOS();
            for (BankAccountDTO bankAccount : bankAccounts) {
                for (int i = 0; i < 10; i++) {
                    String accountId;
                    if (bankAccount instanceof SavingBankAccountDTO) {
                        accountId = bankAccount.getId();
                    } else {
                        accountId = bankAccount.getId();
                    }
                    bankAccountService.credit(accountId, 1310 + Math.random() * 777706, "Credit");
                    bankAccountService.debit(accountId, 1310 + Math.random() * 31127, "Debit");
                }
            }
        };
    }
    @Bean
    CommandLineRunner start(CustomerRepository customerRepository,
                            BankAccountRepository bankAccountRepository,
                            AccountOperationRepository accountOperationRepository) {
        return args -> {
            customerRepository.findAll().forEach(cust -> {
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random() * 1310);
                currentAccount.setCreatedAt(new Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCustomer(cust);
                currentAccount.setOverDraft(3112);
                bankAccountRepository.save(currentAccount);
                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random() * 7777);
                savingAccount.setCreatedAt(new Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCustomer(cust);
                savingAccount.setInterestRate(1.3);
                bankAccountRepository.save(savingAccount);

            });
            bankAccountRepository.findAll().forEach(acc -> {
                for (int i = 0; i < 10; i++) {
                    AccountOperation accountOperation = new AccountOperation();
                    accountOperation.setOperationDate(new Date());
                    accountOperation.setAmount(Math.random() * 1310);
                    accountOperation.setType(Math.random() > 0.5 ? OperationType.CREDIT : OperationType.DEBIT);
                    accountOperation.setBankAccount(acc);
                    accountOperationRepository.save(accountOperation);
                }

            });
        };

    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
