package com.popa.books.controller.book;

import com.popa.books.config.BooksApplicationProperties;
import com.popa.books.model.Book;
import com.popa.books.model.BookCover;
import com.popa.books.model.node.BookNode;
import com.popa.books.model.node.Node;
import com.popa.books.repository.*;
import com.popa.books.util.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.transaction.Transactional;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookRepository repository;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    @Autowired
    private EdituraRepository edituraRepository;

    @Autowired
    private BooksApplicationProperties props;

    @RequestMapping(method = RequestMethod.GET)
    public List<Book> getAllBooks(){
        return repository.findAll();
    }

    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    public List<Node> getBooksTree(){

        List<Node> books =  new ArrayList<>();

        // we count books with no name filled
        Long booksWithNoTitle = repository.countByTitleIsNull();

        if (booksWithNoTitle > 0) {
            BookNode bean = new BookNode();
            bean.setLeaf(true);
            bean.setLoaded(true);
            bean.setHowManyBooks(booksWithNoTitle);
            bean.setName(Node.NOT_AVAILABLE);
            bean.setId(Node.NOT_AVAILABLE);
            books.add(bean);
        }

        List<Object[]> nodeSQLs = repository.findBooksAndCount();

        for (Object[] objects : nodeSQLs) {
            BookNode bean = new BookNode();
            bean.setLeaf(true);
            bean.setLoaded(true);
            bean.setName(objects[0].toString());
            bean.setId(objects[0].toString());
            bean.setHowManyBooks(Long.valueOf(objects[1].toString()));
            books.add(bean);
        }

        return books;
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    public void createNewBook(@RequestBody BookDTO dto) throws ServletException {
        Book book = new Book();
        convertDtoToBook(book, dto);
        saveBookCovers(book);
        repository.save(book);
    }

    //if no complex objects (e.g. Autor, Editura, etc) were in the Book object,
    //a Book could have been used instead of BookDTO directly.
    @Transactional
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public void updateBook(@PathVariable Long id, @RequestBody BookDTO dto) throws ServletException {
        Book book = repository.findOne(id);
        convertDtoToBook(book, dto);
        saveBookCovers(book);
        repository.save(book);
    }

    // de genul /cover/2_1453663625274_front.jpg?time=Sun Jan 24 21:27:05 EET 2016
    private File getNewUpload(String relativeUploadPath) {
        if (StringUtils.isEmpty(relativeUploadPath)) {
            return null;
        }
        if (relativeUploadPath.indexOf('?') != -1) {
            relativeUploadPath = relativeUploadPath.substring(0, relativeUploadPath.indexOf('?'));
        }
        final String exportFileName = props.getRootUploadDir() + relativeUploadPath;
        File uploadedFile = new File(exportFileName);
        if (uploadedFile.exists() && uploadedFile.isFile()) {
            return uploadedFile;
        }
        return null;
    }

    //delete cover is not necessary, since is done by hibernate/jpa automatically
    @Transactional
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteBook(@PathVariable Long id){
        repository.delete(id);
    }

    private void convertDtoToBook(Book book, BookDTO dto) {
        if (dto.getIdAutor() != null) {
            book.setAuthor(autorRepository.findOne(dto.getIdAutor()));
        } else {
            book.setAuthor(null);
        }
        if (dto.getIdCategorie() != null) {
            book.setCategorie(categorieRepository.findOne(dto.getIdCategorie()));
        } else {
            book.setCategorie(null);
        }
        book.setCitita("on".equals(dto.getCitita())
                || "true".equals(dto.getCitita())
                || "1".equals(dto.getCitita())
                || "yes".equals(dto.getCitita()));
        book.setDataAparitie(RequestUtils.parseDate(dto.getDataAparitie()));
        if (dto.getIdEditura() != null) {
            book.setEditura(edituraRepository.findOne(dto.getIdEditura()));
        } else {
            book.setEditura(null);
        }
        book.setHeight(dto.getHeight());
        book.setWidth(dto.getWidth());
        book.setIsbn(dto.getIsbn());
        book.setNrPagini(dto.getNrPagini());
        book.setOriginalTitle(dto.getOriginalTitle());
        book.setSerie(dto.getSerie());
        book.setTitle(dto.getTitle());
        book.setFrontCoverPath(dto.getFrontCoverName());
        book.setBackCoverPath(dto.getBackCoverName());
    }

    private byte[] loadFile(final File file, boolean deleteAfterUpload) throws IOException {
        byte[] bFile = new byte[(int) file.length()];
        BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(file));
        //convert file into array of bytes
        fileInputStream.read(bFile);
        fileInputStream.close();
        if (deleteAfterUpload && !file.delete()) {
            throw new IOException("could not delete file: " + file.getAbsolutePath());
        }
        return bFile;
    }

    private void saveBookCovers(final Book book) throws ServletException {
        try {
            final File newFrontCoverUpload = getNewUpload(book.getFrontCoverPath());
            final File newBackCoverUpload = getNewUpload(book.getBackCoverPath());
            // if no covers exist yet and no new covers were uploaded, there's no need to attach
            // a new covers object to the existing (or new) book. However, if a cover is already attached,
            // we nullify the front/back if necessary and leave it like this.
            if (book.getBookCover() == null && (newBackCoverUpload == null && newFrontCoverUpload == null)) { //save cover is not needed
                return;
            }
            if (book.getBookCover() == null) {
                book.setBookCover(new BookCover());
            }
            BookCover bookCover = book.getBookCover();
            if (newFrontCoverUpload != null) {
                bookCover.setFront(loadFile(newFrontCoverUpload, book.getBookId() == null));
            } else {
                bookCover.setFront(null);
            }
            if (newBackCoverUpload != null) {
                bookCover.setBack(loadFile(newBackCoverUpload, book.getBookId() == null));
            } else {
                bookCover.setBack(null);
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            throw new ServletException(exc.getMessage(), exc);
        }
    }

}
