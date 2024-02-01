package org.example;

import java.util.Date;

public class Transaction {
    private int id;
    private Book book;
    private Member member;
    private Date issueDate;
    private Date returnDate;
    private double fineAmount;

    public Transaction(){
        // Default constructor
    }
    public Transaction(int id, Book book, Member member, Date issueDate, Date returnDate, double fineAmount){
        this.id = id;
        this.book = book;
        this.member = member;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }
}
