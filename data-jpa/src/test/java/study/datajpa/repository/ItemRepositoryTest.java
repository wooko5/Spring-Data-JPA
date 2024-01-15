package study.datajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import study.datajpa.entity.Item;

@Rollback(value = false)
@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    @DisplayName("아이템 저장 테스트")
    public void save() {
        Item item = new Item("Airplane-141"); // @GeneratedValue는 persist()가 호출된 이후에 적용되므로 현재는 id값이 null임
        itemRepository.save(item);
    }
}