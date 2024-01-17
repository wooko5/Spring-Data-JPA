package study.datajpa.repository.projection;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

//    String getUsername(); //Closed

    @Value("#{target.username + ' ' + target.age}")
    String getUsernameAndAge(); //Open
}
