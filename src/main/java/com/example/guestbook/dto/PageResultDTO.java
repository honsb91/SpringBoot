package com.example.guestbook.dto;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class PageResultDTO<DTO, EN> {

    // DTO 리스트
    private List<DTO> dtoList;

    // 총 페이지 번호
    private int totalPage;

    // 현재 페이지 번호
    private int page;

    // 목록 사이즈
    private int size;

    // 시작 페이지 번호, 끝 페이지 번호
    private int start, end;

    // 이전, 다음
    private boolean prev,next;

    // 페이지 번호목록
    private List<Integer> pageList;

    // ↓ 생성자는 주어진 페이지 결과를 사용하여 DTO 리스트를 생성하고, 페이징 정보를 설정합니다. ↓
    public PageResultDTO(Page<EN> result, Function<EN, DTO> fn){
        // result는 페이징된 엔티티 리스트입니다.
        // 이 리스트를 스트림으로 변환하고, 각 엔티티를 주어진 함수 fn을 사용하여 DTO로 변환합니다.
        //변환된 DTO 리스트를 dtoList에 저장합니다.
        dtoList = result.stream().map(fn).collect(Collectors.toList());

        totalPage = result.getTotalPages();

        //makePageList 메서드를 호출하여 페이징 정보를 설정합니다.
        makePageList(result.getPageable());
    }

    // ↓ 이 메서드는 페이징된 결과를 기반으로 페이지 리스트를 생성합니다. ↓
    private void makePageList(Pageable pageable){
        //현재 페이지 번호를 가져와 1을 더합니다 (0부터 시작하므로 1을 추가).
        this.page = pageable.getPageNumber() + 1; // 0부터 시작하므로 1을 추가

        // 페이지 크기를 설정합니다.
        this.size = pageable.getPageSize();

        // temp end page
        // 임시 종료 페이지 번호를 계산합니다. 현재 페이지를 10으로 나눈 후 올림을 하고 10을 곱하여 설정합니다.
        // 이는 페이지 리스트를 10개 단위로 구분하기 위한 것입니다.
        int tempEnd = (int)(Math.ceil(page/10.0))*10;

        // 시작 페이지 번호를 설정합니다. 임시 종료 페이지 번호에서 9를 뺍니다.
        start = tempEnd - 9;

        // 이전 페이지가 있는지 여부를 설정합니다.
        // 시작 페이지가 1보다 큰 경우 prev는 true가 됩니다.
        prev = start > 1;

        // 실제 종료 페이지 번호를 설정합니다.
        // 총 페이지 수가 임시 종료 페이지 번호보다 크면 임시 종료 페이지 번호를, 그렇지 않으면 총 페이지 수를 사용합니다.
        end = totalPage > tempEnd ? tempEnd: totalPage;

        // 다음 페이지가 있는지 여부를 설정합니다.
        // 총 페이지 수가 임시 종료 페이지 번호보다 크면 next는 true가 됩니다.
        next = totalPage > tempEnd;

        // 시작 페이지 번호부터 종료 페이지 번호까지의 페이지 번호 리스트를 생성하여 pageList에 저장합니다.
        pageList = IntStream.rangeClosed(start,end).boxed().collect(Collectors.toList());
    }
}
