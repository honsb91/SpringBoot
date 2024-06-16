package com.example.guestbook.service;

import com.example.guestbook.dto.GuestBookDTO;
import com.example.guestbook.dto.PageRequestDTO;
import com.example.guestbook.dto.PageResultDTO;
import com.example.guestbook.entity.GuestBook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GuestBookServiceTests {

    @Autowired
    private GuestBookService service;

    @Test
    public void testRegister(){

        GuestBookDTO guestBookDTO = GuestBookDTO.builder()
                .title("Sample Title...")
                .content("Sample Content...")
                .writer("user0")
                .build();

        System.out.println(service.register(guestBookDTO));

    }

    @Test
    public void testList(){
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(1).size(10).build();
        PageResultDTO<GuestBookDTO, GuestBook> resultDTO = service.getList(pageRequestDTO);

        System.out.println("PREV: "+resultDTO.isPrev());
        System.out.println("NEXT: "+resultDTO.isNext());
        System.out.println("TOTAL: "+resultDTO.getTotalPage());
        System.out.println("==============================================");

        for (GuestBookDTO guestBookDTO : resultDTO.getDtoList()){
            System.out.println(guestBookDTO);
        }

        System.out.println("==================================================");
        resultDTO.getPageList().forEach(i-> System.out.println(i));
    }

    @Test
    public void testSearch(){
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .type("tc")
                .Keyword("Title")
                .build();

        PageResultDTO<GuestBookDTO, GuestBook> resultDTO = service.getList(pageRequestDTO);

        System.out.println("PREV: " + resultDTO.isPrev());
        System.out.println("NEXT: " + resultDTO.isNext());
        System.out.println("TOTAL: " + resultDTO.getTotalPage());

        System.out.println("---------------------------------------------------");
        for (GuestBookDTO guestBookDTO : resultDTO.getDtoList()){
            System.out.println(guestBookDTO);
        }

        System.out.println("=========================================================");
        resultDTO.getPageList().forEach(i -> System.out.println(i));
    }
}
