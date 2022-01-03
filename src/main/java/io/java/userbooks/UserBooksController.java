package io.java.userbooks;

import io.java.book.Book;
import io.java.book.BookRepository;
import io.java.user.BooksByUser;
import io.java.user.BooksByUserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class UserBooksController {

    private UserBooksRepository userBooksRepository;
    private BooksByUserRepository booksByUserRepository;
    private BookRepository bookRepository;

    public UserBooksController(UserBooksRepository userBooksRepository, BooksByUserRepository booksByUserRepository, BookRepository bookRepository) {
        this.userBooksRepository = userBooksRepository;
        this.booksByUserRepository = booksByUserRepository;
        this.bookRepository = bookRepository;
    }

    @PostMapping("/addUserBook")
    public ModelAndView addBookForUser(@RequestBody MultiValueMap<String,String> formData,
                                       @AuthenticationPrincipal OAuth2User principal){

        if (principal == null || principal.getAttribute("login") == null){
            return null;
        }

        String bookId = formData.getFirst("bookId");
        Optional<Book> optionalBook = bookRepository.findById(bookId);

        if (optionalBook.isEmpty()){
            return new ModelAndView("redirect:/");
        }
        Book book = optionalBook.get();

        UserBooks userBooks = new UserBooks();
        UserBooksPrimaryKey key = new UserBooksPrimaryKey();

        String userId = principal.getAttribute("login");
        key.setUserId(userId);
        key.setBookId(bookId);

        userBooks.setKey(key);

        int rating = Integer.parseInt(formData.getFirst("rating"));

        userBooks.setStartedDate(LocalDate.parse(formData.getFirst("startDate")));
        userBooks.setCompletedDate(LocalDate.parse(formData.getFirst("completedDate")));
        userBooks.setRating(rating);
        userBooks.setReadingStatus(formData.getFirst("readingStatus"));

        userBooksRepository.save(userBooks);

/*  Multiple Record on Home Page When update book's reading status and stars.
    TODO

        Optional<BooksByUser> optionalBooksByUser = booksByUserRepository.findById(userId);
        if (optionalBooksByUser.isPresent()){
            BooksByUser booksByUser = optionalBooksByUser.get();
            booksByUser.setReadingStatus(formData.getFirst("readingStatus"));
            booksByUser.setRating(rating);
            booksByUserRepository.save(booksByUser);
        }
  */
        BooksByUser booksByUser = new BooksByUser();
        booksByUser.setId(userId);
        booksByUser.setBookId(bookId);
        booksByUser.setBookName(book.getName());
        booksByUser.setCoverIds(book.getCoverIds());
        booksByUser.setAuthorNames(book.getAuthorNames());
        booksByUser.setReadingStatus(formData.getFirst("readingStatus"));
        booksByUser.setRating(rating);
        booksByUserRepository.save(booksByUser);

        return new ModelAndView("redirect:/books/" + bookId);
    }












}
