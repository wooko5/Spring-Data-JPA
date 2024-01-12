package study.datajpa.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

@EntityListeners(AuditingEntityListener.class) //해당 어노테이션이 있어야 데이터의 변경(C/U/D) 시 이벤트(@PreUpdated...)를 발생
@Getter
@MappedSuperclass
public class BaseEntity extends BaseTimeEntity {

    @CreatedBy
    @Column(updatable = false)
    String createdBy; //등록자

    @LastModifiedBy
    String lastModifiedBy; //수정자
}