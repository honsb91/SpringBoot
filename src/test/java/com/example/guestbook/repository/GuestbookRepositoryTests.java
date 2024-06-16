package com.example.guestbook.repository;

import com.example.guestbook.entity.GuestBook;
import com.example.guestbook.entity.QGuestBook;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
public class GuestbookRepositoryTests {

    @Autowired
    private GuestbookRepository guestbookRepository;

    @Test
    public void insert(){

        IntStream.rangeClosed(1,300).forEach(i ->{

            GuestBook guestBook = GuestBook.builder()
                    .title("Title..." + i)
                    .content("Content..." + i)
                    .writer("uesr" + (i % 10))
                    .build();
            System.out.println(guestbookRepository.save(guestBook));
        });

    }

    @Test
    public void updateTest(){

        Optional<GuestBook> result = guestbookRepository.findById(300L);

        // 존재하는 번호로 테스트
        if(result.isPresent()){
            GuestBook guestBook = result.get();

            guestBook.changeTitle("Changed Title.....");
            guestBook.changeContent("Changed Content.....");

            guestbookRepository.save(guestBook);
        }
    }

    @Test
    public void testQuery1(){

        // PageRequest.of(0, 10, Sort.by("gno").descending())는 페이지 요청 객체를 생성합니다.
        // 첫 번째 페이지(인덱스 0)를 요청하며, 페이지당 10개의 엔티티를 포함하고, gno 필드를 기준으로 내림차순으로 정렬합니다.
        Pageable pageable = PageRequest.of(0,10,Sort.by("gno").descending());

        //QGuestBook는 QueryDSL을 사용하여 GuestBook 엔티티에 대한 메타데이터를 제공합니다.
        // 이 메타데이터는 타입 안전한 쿼리를 작성하는 데 사용됩니다.
        QGuestBook qGuestBook = QGuestBook.guestBook; //1

        //검색 키워드로 사용할 문자열 keyword를 정의합니다.
        // 이 경우 키워드는 "1"입니다.
        String keyword = "1";

        //BooleanBuilder 객체를 생성합니다.
        // 이 객체는 여러 BooleanExpression을 결합하여 복잡한 조건을 생성하는 데 사용됩니다.
        BooleanBuilder builder = new BooleanBuilder(); //2

        //qGuestBook.title.contains(keyword)는 title 필드에 keyword가 포함되어 있는지를 검사하는 BooleanExpression 객체를 생성합니다.
        BooleanExpression expression = qGuestBook.title.contains(keyword); //3

        builder.and(expression); //4

        Page<GuestBook> result = guestbookRepository.findAll(builder, pageable);//5

        result.stream().forEach(guestBook -> {
            System.out.println(guestBook);
        });


    }
}
