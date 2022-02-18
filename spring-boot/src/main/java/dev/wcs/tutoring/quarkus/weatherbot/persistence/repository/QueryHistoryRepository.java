package dev.wcs.tutoring.quarkus.weatherbot.persistence.repository;

import dev.wcs.tutoring.quarkus.weatherbot.persistence.entity.QueryHistory;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryHistoryRepository extends PagingAndSortingRepository<QueryHistory, Long> {

    List<QueryHistory> findQueryHistoryByLocationFoundIsLike(String location);
    QueryHistory findFirstByOrderByIdDesc();

}
