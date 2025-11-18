package com.example.demo.user.document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@Document(indexName = "users")
public class User {
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(type = FieldType.Integer)
    private Integer age;

    @Field(type = FieldType.Boolean)
    private Boolean isActive;

    @Builder
    public User(final String id,
                        final String name,
                        final Integer age,
                        final Boolean isActive
    ) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.isActive = isActive;
    }

    public UserUpdater.UserUpdaterBuilder toUpdater() {
        return UserUpdater.builder()
                .name(this.name)
                .age(this.age)
                .isActive(this.isActive);
    }

    public void update(final UserUpdater updater) {
        this.name = updater.name();
        this.age = updater.age();
        this.isActive = updater.isActive();
    }
}
