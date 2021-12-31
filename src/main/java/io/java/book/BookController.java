package io.java.book;

import jdk.jfr.ContentType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
public class BookController {
    private final String COVER_IMAGE_ROOT = "http://covers.openlibrary.org/b/id/";

    private BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @RequestMapping("/books/{bookId}")
    public String getBook(@PathVariable String bookId, Model model,
                          @AuthenticationPrincipal OAuth2User principle){

        Optional<Book> optionalBook = bookRepository.findById(bookId);
        //System.out.println(optionalBook.isPresent());
        if(optionalBook.isPresent()){

            // Get book if not null
            Book book = optionalBook.get();

            //Setting up Image URL
            String coverImageUrl = "/images/no-image.png";
            if (book.getCoverIds() != null && book.getCoverIds().size() > 0){
                coverImageUrl = COVER_IMAGE_ROOT + book.getCoverIds().get(0) + "-L.jpg";
            }

            model.addAttribute("coverImage",coverImageUrl);
            model.addAttribute("book",book);

            //Checking User Authentication status TODO
            /*if (principle != null && principle.getAttribute("login") != null){
                System.out.println(principle.toString());

            }*/

            return "book";
        }

        return "book-not-found";
    }
}



















