package com.assignment.urlshortener.repository;

import com.assignment.urlshortener.entity.UrlClick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlClickRepository extends JpaRepository<UrlClick,Long> {
    List<UrlClick> findByUrlIdOrderByAccessedAtDesc(Long urlId);

    long countByUrlId(Long urlId);

}
