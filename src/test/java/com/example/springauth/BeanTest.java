package com.example.springauth;


import com.example.springauth.beanColision.Food;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *  빈 충돌 해결법
 *  1. Bean Type(food) 로 DI 가 되지 않을 경우에 Bean Name 으로 DI 를 해준다
 *  2. Primary 사용하기
 *  3. Qualifier 사용하기

 * Qualifier vs Primary 의 우선순위
 * Qualifier 가 높다
 */
@SpringBootTest
public class BeanTest {

    //@Autowired
    //Food pizza;//Could not autowire. No beans of 'Food' type found

    //@Autowired
    //Food chicken; //등록된 빈을 명시적으로(빈이름) 을 적용해주면 된다

    @Autowired
    @Qualifier("pizza")
    Food food; //Primary 에노테이션 사용하기

    @Test
    @DisplayName("테스트")
    void test1(){
//        pizza.eat();
//        chicken.eat();
        food.eat();
    }
}
