package com.example.demo.user.repository;

import com.example.demo.user.document.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserDocumentRepository extends ElasticsearchRepository<User, String> {
}
