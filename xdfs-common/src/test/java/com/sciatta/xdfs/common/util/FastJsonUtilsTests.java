package com.sciatta.xdfs.common.util;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by Rain on 2023/8/20<br>
 * All Rights Reserved(C) 2017 - 2023 SCIATTA <br> <p/>
 * FastJsonUtilsTests
 */
public class FastJsonUtilsTests {

    @Test
    public void testFormatObjectToJsonString() {
        Person person = new Person();
        person.setAge(1);
        person.setName("dog");
        String s = FastJsonUtils.formatObjectToJsonString(person);
        assertEquals("{\"age\":1,\"name\":\"dog\"}", s);
    }

    @Test
    public void testParseJsonStringToObject() {
        Person person = FastJsonUtils.parseJsonStringToObject("{\"age\":1,\"name\":\"p\"}", Person.class);
        assertEquals(Integer.valueOf(1), person.getAge());
        assertEquals("p", person.getName());
    }

    @Test
    public void testFormatObjectToJsonStringWithNullValue() {
        Map<Object, Object> nullValueMap = new HashMap<>();
        nullValueMap.put("nv", null);

        List<Object> nullValueList = new ArrayList<>();
        nullValueList.add(null);

        Person person = new Person();
        person.setName(null);
        person.setAge(null);
        person.setMap(null);
        person.setNullValueMap(nullValueMap);
        person.setSex(null);
        person.setList(null);
        person.setNullValueList(nullValueList);

        // 序列化
        String stringWithNullValue = FastJsonUtils.formatObjectToJsonStringWithNullValue(person);
        System.out.println("JsonStringWithNullValue: " + stringWithNullValue);
        // 反序列化
        Person personWithNullValue = FastJsonUtils.parseJsonStringToObject(stringWithNullValue, Person.class);
        assertNotNull(personWithNullValue);

        // 序列化
        String string = FastJsonUtils.formatObjectToJsonString(person);
        System.out.println("JsonString: " + string);
        // 反序列化
        person = FastJsonUtils.parseJsonStringToObject(string, Person.class);
        assertNotNull(person);
    }

    @Test
    public void testReference() {
        Fruit apple = new Fruit();
        apple.setName("apple");

        Fruit orange = new Fruit();
        orange.setName("orange");

        Basket basket = new Basket();
        basket.setColor("red");
        basket.setFruits(Arrays.asList(apple, orange));

        String jsonString = FastJsonUtils.formatObjectToJsonString(basket);
        assertNotNull(jsonString);

        Basket ans = FastJsonUtils.parseJsonStringToObject(jsonString, Basket.class);
        assertEquals("red", ans.getColor());
        assertEquals(2, ans.fruits.size());

        Fruit fruit1 = ans.fruits.get(0);
        Fruit fruit2 = ans.fruits.get(1);

        if (fruit1.getName().equals("apple")) {
            assertEquals("orange", fruit2.getName());
        } else if (fruit1.getName().equals("orange")) {
            assertEquals("apple", fruit2.getName());
        } else {
            throw new RuntimeException("no reach");
        }
    }

    @Test
    public void testExtends() {
        Basket basket = new Basket();
        basket.setColor("blue");

        Fruit apple = new Apple();
        apple.setName("apple");
        Fruit orange = new Orange();
        orange.setName("Orange");

        basket.setFruits(Arrays.asList(apple, orange));

        String jsonString = FastJsonUtils.formatObjectToJsonStringWithClassName(basket);
        System.out.println(jsonString);

        Basket ans = FastJsonUtils.parseJsonStringToObject(jsonString, Basket.class);
        assertNotNull(ans);
        Fruit fruit1 = ans.getFruits().get(0);
        Fruit fruit2 = ans.getFruits().get(1);
        assertNotNull(fruit1);
        assertNotNull(fruit2);

        if (fruit1 instanceof Apple) {
            assertTrue(fruit2 instanceof Orange);
        } else if (fruit1 instanceof Orange) {
            assertTrue(fruit2 instanceof Apple);
        } else {
            throw new RuntimeException("no reach");
        }
    }

    @NoArgsConstructor
    @Data
    static class Person {
        String name;
        Integer age;
        Map<Object, Object> map;
        Map<Object, Object> nullValueMap;
        List<Object> list;
        List<Object> nullValueList;
        Boolean sex;
    }

    @NoArgsConstructor
    @Data
    static class Basket {
        String color;
        List<Fruit> fruits;
    }

    @NoArgsConstructor
    @Data
    static class Fruit {
        String name;
    }

    @NoArgsConstructor
    static class Apple extends Fruit {

    }

    @NoArgsConstructor
    static class Orange extends Fruit {

    }

}
