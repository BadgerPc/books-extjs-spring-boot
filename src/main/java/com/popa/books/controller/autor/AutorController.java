package com.popa.books.controller.autor;

import com.popa.books.model.Autor;
import com.popa.books.model.node.AutorNode;
import com.popa.books.model.node.Node;
import com.popa.books.model.node.NodeSQL;
import com.popa.books.repository.AutorRepository;
import com.popa.books.repository.BookRepository;
import com.popa.books.util.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/autor")
public class AutorController {

    @Autowired
    private AutorRepository repository;

    @Autowired
    private BookRepository bookRepository;

    @RequestMapping(method = RequestMethod.GET)
    public List<Autor> getAllAutori(){
        List<Autor> autori =  new ArrayList<>();
        Iterable<Autor> it = repository.findAll();
        for (Autor Autor: it) {
            autori.add(Autor);
        }
        return autori;
    }

    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    public List<Node> getAutoriTree(){
        List<Node> autori =  new ArrayList<>();

        // we count books without authors first
        Long booksWithNoAuthor = bookRepository.countByAuthorIsNull();
        if (booksWithNoAuthor > 0) {
            AutorNode bean = new AutorNode();
            bean.setLeaf(true);
            bean.setLoaded(true);
            bean.setHowManyBooks(booksWithNoAuthor);
            bean.setName(Node.NOT_AVAILABLE);
            bean.setId(Node.NOT_AVAILABLE);
            autori.add(bean);
        }

        List<NodeSQL> nodeSQLs = repository.findAutorsAndBookCountUsingHQLAndNodeSQL();

        for (NodeSQL nodeSQL : nodeSQLs) {
            AutorNode bean = new AutorNode();
            bean.setLeaf(true);
            bean.setLoaded(true);
            bean.setHowManyBooks(nodeSQL.getCount());
            bean.setName(nodeSQL.getName());
            bean.setId(nodeSQL.getName());
            autori.add(bean);
        }
        return autori;
    }

    @RequestMapping(method = RequestMethod.POST)
    public void createNewAutor(@RequestParam String nume,
                               @RequestParam String dataNasterii){
        Autor autor = new Autor();
        autor.setDataNasterii(RequestUtils.parseDate(dataNasterii));
        autor.setNume(nume);
        repository.save(autor);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public void updateAutor(@PathVariable Long id,
                            @RequestParam String nume,
                            @RequestParam String dataNasterii){
        Autor autor = repository.findOne(id);
        autor.setNume(nume);
        autor.setDataNasterii(RequestUtils.parseDate(dataNasterii));
        repository.save(autor);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteAutor(@PathVariable Long id){
        repository.delete(id);
    }
}
