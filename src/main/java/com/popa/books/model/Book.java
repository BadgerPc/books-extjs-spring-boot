package com.popa.books.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.*;

@Entity
@Table(name = "book")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "bookId", nullable = false, unique = true)
    private Long bookId;

    @Column(name = "title")
    private String title;

    @Column(name = "originalTitle", nullable = true)
    private String originalTitle;

    @Column(name = "dataAparitie", nullable = true)
    private Date dataAparitie = null;

    @Column(name = "nrPagini", nullable = true)
    private Integer nrPagini;

    @Column(name = "width", nullable = true)
    private Integer width;

    @Column(name = "height", nullable = true)
    private Integer height;

    @Column(name = "isbn", nullable = true)
    private String isbn;

    @Column(name = "citita", nullable = true)
    private Boolean citita;

    @Column(name = "serie", nullable = true)
    private String serie;

    @OneToOne
    @JoinColumn(name = "idAutor")
    private Autor author;

    @OneToOne
    @JoinColumn(name = "idEditura", nullable = true)
    private Editura editura;

    @OneToOne
    @JoinColumn(name = "idCategorie", nullable = true)
    private Categorie categorie;

    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "bookCoverId", nullable = true)
    private BookCover bookCover;

    @Transient
    private String frontCoverPath;
    @Transient
    private String backCoverPath;

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public Date getDataAparitie() {
        return dataAparitie;
    }

    public void setDataAparitie(Date dataAparitie) {
        this.dataAparitie = dataAparitie;
    }

    public Autor getAuthor() {
        return author;
    }

    public void setAuthor(Autor author) {
        this.author = author;
    }

    public Integer getNrPagini() {
        return nrPagini;
    }

    public void setNrPagini(Integer nrPagini) {
        this.nrPagini = nrPagini;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Boolean getCitita() {
        return citita;
    }

    public void setCitita(Boolean citita) {
        this.citita = citita;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Editura getEditura() {
        return editura;
    }

    public void setEditura(Editura editura) {
        this.editura = editura;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public BookCover getBookCover() {
        return bookCover;
    }

    public void setBookCover(BookCover bookCover) {
        this.bookCover = bookCover;
    }

    public String getFrontCoverPath() {
        return frontCoverPath;
    }

    public void setFrontCoverPath(String frontCoverPath) {
        this.frontCoverPath = frontCoverPath;
    }

    public String getBackCoverPath() {
        return backCoverPath;
    }

    public void setBackCoverPath(String backCoverPath) {
        this.backCoverPath = backCoverPath;
    }
}
