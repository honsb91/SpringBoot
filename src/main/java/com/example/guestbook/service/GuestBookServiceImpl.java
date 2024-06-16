package com.example.guestbook.service;

import com.example.guestbook.dto.GuestBookDTO;
import com.example.guestbook.dto.PageRequestDTO;
import com.example.guestbook.dto.PageResultDTO;
import com.example.guestbook.entity.GuestBook;
import com.example.guestbook.entity.QGuestBook;
import com.example.guestbook.repository.GuestbookRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor // 의존성 자동주입
public class GuestBookServiceImpl implements GuestBookService{

    private final GuestbookRepository repository;

    @Override
    public Long register(GuestBookDTO dto) {

        log.info("DTO======================");
        log.info(dto);

        GuestBook entity = dtoToEntity(dto);
        log.info(entity);
        repository.save(entity);
        return entity.getGno();
    }

    @Override
    public PageResultDTO<GuestBookDTO, GuestBook> getList(PageRequestDTO requestDTO) {

        Pageable pageable = requestDTO.getPageable(Sort.by("gno").descending());

        BooleanBuilder booleanBuilder = getSearch(requestDTO);

        Page<GuestBook> result = repository.findAll(booleanBuilder, pageable);

        Function<GuestBook, GuestBookDTO> fn = (entity ->
            entityToDto(entity));

            return new PageResultDTO<>(result, fn);
    }

    // 방명록 조회
    @Override
    public GuestBookDTO read(Long gno) {

        Optional<GuestBook> result = repository.findById(gno);

        return result.isPresent()? entityToDto(result.get()): null;
    }

    // 방명록 삭제
    @Override
    public void remove(Long gno){
        repository.deleteById(gno);
    }

    // 방명록 수정
    @Override
    public void modify(GuestBookDTO dto){
        // 업데이트 하는 항목은 '제목', '내용'
        Optional<GuestBook> result = repository.findById(dto.getGno());
        if(result.isPresent()){
            GuestBook entity = result.get();
            entity.changeTitle(dto.getTitle());
            entity.changeContent(dto.getContent());

            repository.save(entity);
        }
    }

    private BooleanBuilder getSearch(PageRequestDTO requestDTO){

        String type = requestDTO.getType();
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QGuestBook qGuestBook = QGuestBook.guestBook;
        String Keyword = requestDTO.getKeyword();
        BooleanExpression expression = qGuestBook.gno.gt(0L);
        booleanBuilder.and(expression);
        if(type == null || type.trim().length() == 0){
            return booleanBuilder;
        }

        // 검색 조건을 작성하기
        BooleanBuilder conditionBuilder = new BooleanBuilder();
        if(type.contains("t")){
            conditionBuilder.or(qGuestBook.title.contains(Keyword));
        }
        if(type.contains("c")){
            conditionBuilder.or(qGuestBook.content.contains(Keyword));
        }
        if(type.contains("w")){
            conditionBuilder.or(qGuestBook.writer.contains(Keyword));
        }

        // 모든 조건 통합
        booleanBuilder.and(conditionBuilder);
        return booleanBuilder;
    }
}
