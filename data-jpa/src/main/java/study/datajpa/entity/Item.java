package study.datajpa.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
//@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseTimeEntity implements Persistable<String> {
    @Id
//    @GeneratedValue
    private String id;

//    @CreatedDate
//    private LocalDateTime createdDate; //BaseTimeEntity를 상속받지 않는다면 해당 코드와 @EntityListeners(AuditingEntityListener.class)를 추가하자

    private String itemName;

    public Item(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override //오버라이드를 통해 새로운 조건으로 변경
    public boolean isNew() {
        return this.createdDate == null;
    }
}
