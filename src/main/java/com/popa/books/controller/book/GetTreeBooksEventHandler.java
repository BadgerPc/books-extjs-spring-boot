package com.popa.books.controller.book;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.popa.books.controller.EventHandler;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.popa.books.model.node.BookNode;
import com.popa.books.model.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetTreeBooksEventHandler extends EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(GetTreeBooksEventHandler.class);

    @Override
    public String handleEvent(final HttpServletRequest request) throws ServletException {
        try {
            List<Node> nodeList = new ArrayList<Node>();
            BookNode nonLetterBean = null;
            String sql = "SELECT @firstletter as SUBSTRING(b.title,1,1), (SELECT COUNT(1) FROM Book b1 WHERE SUBSTRING(b1.title,1,1) LIKE @firstletter) AS booksNumber"
                    + " FROM Book b GROUP BY @firstletter";
            List<Object[]> lettersList = new ArrayList<>();
            for (Object[] data : lettersList) {
                BookNode bean = new BookNode();
                String letter = String.valueOf(data[0]);
                final int howManyBooks = Integer.valueOf(String.valueOf(data[1]));
                bean.setHowManyBooks(howManyBooks);
                bean.setLeaf(true);
                bean.setLoaded(true);
                if (StringUtils.isEmpty(letter) || !Character.isLetter(letter.charAt(0))) {
                    letter = Node.ALL;
                    if (nonLetterBean == null) {
                        nonLetterBean = new BookNode();
                        nonLetterBean.setLeaf(true);
                        nonLetterBean.setLoaded(true);
                        nonLetterBean.setName(letter);
                        nonLetterBean.setId(letter);
                    }
                    nonLetterBean.setHowManyBooks(nonLetterBean.getHowManyBooks() + howManyBooks);
                    continue;
                }
                bean.setName(letter.toUpperCase());
                bean.setId(letter);
                nodeList.add(bean);
            }
            if (nonLetterBean != null) {
                nodeList.add(nonLetterBean);
            }
            System.err.println(new Gson().toJson(nodeList));
            return new Gson().toJson(nodeList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServletException(e);
        }
    }
}
