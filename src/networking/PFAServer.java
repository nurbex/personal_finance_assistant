package networking;

import domain.Expense;
import domain.Income;
import domain.User;
import services.ExpenseService;
import services.IncomeService;
import services.UserService;
import ui.UI;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;

public class PFAServer {
    public static void main(String[] args) {

        UI ui = new UI();
        UserService userService= new UserService();
        IncomeService incomeService = new IncomeService();
        ExpenseService expenseService = new ExpenseService();
        try (ServerSocket serverSocket = new ServerSocket(8888)) {

            System.out.println("Server is up and running...");
            String userName;
            String password;

            Socket incoming = serverSocket.accept();
            //read data from connected socket
            InputStream inputStream = incoming.getInputStream();
            Scanner in = new Scanner(inputStream, "UTF-8");

            //write data to connected socket
            OutputStream outputStream = incoming.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
            BufferedWriter out = new BufferedWriter(outputStreamWriter);

            if (incoming.isConnected()) {
                System.out.println("Somebody is just connected! "+ incoming.getInetAddress().toString());
            }
            writeToWriterAndFlush(ui.loginMenu(), out);
            userName=in.nextLine();
            password=in.nextLine();

            Optional<User> optionalUser = userService.getUserByEmailAndPassword(userName, password);

            if (optionalUser.isPresent()) {
                System.out.println("user is present");
                User user = optionalUser.get();
                Integer input ;
                do {
                    writeToWriterAndFlush(ui.mainMenu(), out);
                    input = in.nextInt();

                    if (input == -1){
                        System.out.println("user is not present");
                        break;
                    }

                    switch (input) {
                        case 1:
                            writeToWriterAndFlush(ui.newIncomeMenu(), out);
                            String incomeName = in.next();
                            float incomeValue = in.nextFloat();

                            if (incomeValue == -1 || incomeName.equals("-1"))
                                break;

                            Income income = new Income(incomeName, incomeValue, LocalDateTime.now(),user);

                            if (incomeService.insert(income))
                                writeToWriterAndFlush("Your income recorded as "+incomeService.numberOfIncomes(), out);
                            break;
                        case 2:
                            writeToWriterAndFlush(ui.newExpenseMenu(), out);

                            String expenseName = in.next();
                            float expenseValue = in.nextFloat();

                            if (expenseValue == -1 || expenseName.equals("-1"))
                                break;

                            Expense expense =  new Expense(expenseName, expenseValue, LocalDateTime.now(), user);

                            if (expenseService.insert(expense))
                                writeToWriterAndFlush("Your expense recorded as "+expenseService.numberOfExpense(), out);

                            break;
                        case 3:
                            writeToWriterAndFlush("List income is selected", out);
                            writeToWriterAndFlush(incomeService.getData(user).toString(), out);
                            break;
                        case 4:
                            writeToWriterAndFlush("List expense is selected", out);
                            writeToWriterAndFlush(expenseService.getData(user).toString(), out);
                            break;
                        case 5:
                            Float incomeSum = incomeService.calculateSum(incomeService.getIncomesOfGivenDate(LocalDateTime.now(), user));
                            Float expenseSum = expenseService.calculateSum(expenseService.getExpensesOfGivenDate(LocalDateTime.now(), user));

                            writeToWriterAndFlush(incomeService.getIncomesOfGivenDate(LocalDateTime.now(), user).toString(), out);
                            writeToWriterAndFlush(expenseService.getExpensesOfGivenDate(LocalDateTime.now(), user).toString(), out);

                            writeToWriterAndFlush("sum of your incomes for month: "+LocalDateTime.now().getMonth()+" : "
                                    + incomeSum, out);

                            writeToWriterAndFlush("sum of expenses for month: "+LocalDateTime.now().getMonth()+ " : "
                                    + expenseSum, out);
                            writeToWriterAndFlush("Your balance is : "+ (incomeSum - expenseSum), out);
                            break;
                        default:
                            writeToWriterAndFlush("please use numbers between 1 and 4", out);
                            break;
                    }

                } while (input != -1);

            }
            else{
                writeToWriterAndFlush(ui.noSucchUser(),out);
                System.out.println(ui.noSucchUser());
                System.exit(0);
            }

            out.close();
            in.close();

        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void writeToWriterAndFlush(String text, BufferedWriter writer) throws IOException {
        writer.write(text+"\n");
        writer.flush();
    }
}
