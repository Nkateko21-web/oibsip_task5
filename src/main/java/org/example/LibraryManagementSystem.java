package org.example;

import org.example.DAO.BookDAO;
import org.example.DAO.MemberDAO;
import org.example.DAO.TransactionDAO;

import java.util.List;
import java.util.Scanner;

public class LibraryManagementSystem {


    public static void start() {
        TransactionDAO transactionDAO = new TransactionDAO();
        double FINE_RATE_PER_DAY = 0.5;
        Scanner scanner = new Scanner(System.in);
        BookDAO bookDAO = new BookDAO();
        MemberDAO memberDAO = new MemberDAO(transactionDAO);

        while (true) {
            System.out.println("1. View All Books");
            System.out.println("2. Add a Book");
            System.out.println("3. Update a Book");
            System.out.println("4. Delete a Book");
            System.out.println("5. View All Members");
            System.out.println("6. Add a Member");
            System.out.println("7. Update a Member");
            System.out.println("8. Delete a Member");
            System.out.println("9. Issue a Book");
            System.out.println("10. Return a Book");
            System.out.println("11. View All Transactions");
            System.out.println("12. Exit");
            System.out.println("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    List<Book> books = bookDAO.getAllBooks();
                    for (Book book: books) {
                        System.out.println(book.getId() + ". " + book.getTitle() + " by " + book.getAuthor()
                        +  "(Quantity: " + book.getQuantity() + ")");
                    }
                    break;
                case 2:
                    System.out.println("Enter book title: ");
                    String title = scanner.nextLine();
                    System.out.println("Enter book author: ");
                    String author = scanner.nextLine();
                    System.out.println("Enter quantity: ");
                    int quantity = scanner.nextInt();
                    Book newBook = new Book();
                    newBook.setTitle(title);
                    newBook.setAuthor(author);
                    newBook.setQuantity(quantity);
                    bookDAO.addBook(newBook);
                    System.out.println("Book added successfully!");
                    break;
                case 3:
                    // Update a Book logic
                    System.out.print("Enter the book ID to update: ");
                    int bookIdToUpdate = scanner.nextInt();
                    scanner.nextLine();
                    Book updatedBook = bookDAO.getBookById(bookIdToUpdate);
                    if (updatedBook != null) {
                        System.out.print("Enter new book title: ");
                        updatedBook.setTitle(scanner.nextLine());
                        System.out.print("Enter new book author: ");
                        updatedBook.setAuthor(scanner.nextLine());
                        System.out.print("Enter new quantity: ");
                        updatedBook.setQuantity(scanner.nextInt());
                        bookDAO.updateBook(updatedBook);
                        System.out.println("Book updated successfully!");
                    } else {
                        System.out.println("Book not found with ID: " + bookIdToUpdate);
                    }
                    break;
                case 4:
                    System.out.print("Enter the book ID to delete: ");
                    int bookIdToDelete = scanner.nextInt();
                    bookDAO.deleteBook(bookIdToDelete);
//                    System.out.println("Book deleted successfully!");
                    break;
                case 5:
                    List<Member> members = memberDAO.getAllMembers();
                    for (Member member: members) {
                        System.out.println(member.getId() + ". " + member.getName() + " (" + member.getEmail() + ")");
                    }
                    break;
                case 6:
                    System.out.println("Enter member name: ");
                    String memberName = scanner.nextLine();
                    System.out.println("Enter member email: ");
                    String memberEmail = scanner.nextLine();
                    Member newMember = new Member();
                    newMember.setName(memberName);
                    newMember.setEmail(memberEmail);
                    memberDAO.addMember(newMember);
                    System.out.println("Member added successfully!");
                    break;
                case 7:
                    System.out.print("Enter the member ID to update: ");
                    int memberIdToUpdate = scanner.nextInt();
                    scanner.nextLine();
                    Member updatedMember = memberDAO.getMemberById(memberIdToUpdate);
                    if (updatedMember != null) {
                        System.out.print("Enter new member name: ");
                        updatedMember.setName(scanner.nextLine());
                        System.out.print("Enter new member email: ");
                        updatedMember.setEmail(scanner.nextLine());
                        memberDAO.updateMember(updatedMember);
                        System.out.println("Member updated successfully!");
                    } else {
                        System.out.println("Member not found with ID: " + memberIdToUpdate);
                    }
                    break;
                case 8:
                    System.out.print("Enter the member ID to delete: ");
                    int memberIdToDelete = scanner.nextInt();
                    memberDAO.deleteMember(memberIdToDelete);
                    System.out.println("Member deleted successfully!");
                    break;
                case 9:
                    // Issue a Book
                    System.out.print("Enter the book ID to issue: ");
                    int bookIdToIssue = scanner.nextInt();
                    System.out.print("Enter the member ID for issuing: ");
                    int memberIdForIssue = scanner.nextInt();

                    Book bookToIssue = bookDAO.getBookById(bookIdToIssue);
                    Member memberForIssue = memberDAO.getMemberById(memberIdForIssue);

                    if (bookToIssue != null && memberForIssue != null) {
                        // Check if the book is available
                        if (bookToIssue.getQuantity() > 0) {
                            // Update book quantity
                            bookToIssue.setQuantity(bookToIssue.getQuantity() - 1);
                            bookDAO.updateBook(bookToIssue);

                            // Create a new transaction for issuing
                            Transaction issueTransaction = new Transaction();
                            issueTransaction.setBook(bookToIssue);
                            issueTransaction.setMember(memberForIssue);
                            issueTransaction.setIssueDate(new java.util.Date());
                            issueTransaction.setReturnDate(null); // Set return date to null initially
                            issueTransaction.setFineAmount(0.0); // Initial fine amount is 0.0
                            transactionDAO.addTransaction(issueTransaction, FINE_RATE_PER_DAY);

                            System.out.println("Book issued successfully!");
                        } else {
                            System.out.println("Book is not available for issuing.");
                        }
                    } else {
                        System.out.println("Invalid book or member ID for issuing.");
                    }
                    break;
                case 10:
                    // Return a Book
                    System.out.print("Enter the transaction ID to return: ");
                    int transactionIdToReturn = scanner.nextInt();

                    Transaction returnTransaction = transactionDAO.getTransactionById(transactionIdToReturn);

                    if (returnTransaction != null) {
                        // Update return date and calculate fine
                        returnTransaction.setReturnDate(new java.util.Date());
                        transactionDAO.updateTransactionReturnDateAndFine(returnTransaction, FINE_RATE_PER_DAY);

                        // Update book quantity
                        Book returnedBook = returnTransaction.getBook();
                        returnedBook.setQuantity(returnedBook.getQuantity() + 1);
                        bookDAO.updateBook(returnedBook);

                        System.out.println("Book returned successfully!");
                    } else {
                        System.out.println("Invalid transaction ID for returning.");
                    }
                    break;
                case 11:
                    // View All Transactions
                    List<Transaction> transactions = transactionDAO.getAllTransactions();
                    for (Transaction transaction : transactions) {
                        System.out.println("Transaction ID: " + transaction.getId());
                        System.out.println("Book: " + transaction.getBook().getTitle());
                        System.out.println("Member: " + transaction.getMember().getName());
                        System.out.println("Issue Date: " + transaction.getIssueDate());
                        System.out.println("Return Date: " + transaction.getReturnDate());
                        System.out.println("Fine Amount: " + transaction.getFineAmount());
                        System.out.println();
                    }
                    break;
                case 12:
                    System.out.println("Exiting...");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }
}
