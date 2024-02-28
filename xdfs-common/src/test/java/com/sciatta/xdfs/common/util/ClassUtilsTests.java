package com.sciatta.xdfs.common.util;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Created by Rain on 2023/8/19<br>
 * All Rights Reserved(C) 2017 - 2023 SCIATTA <br> <p/>
 * ClassUtilsTests
 */
public class ClassUtilsTests {

    @Test
    public void testNewInstance_DefaultConstructor() {
        IntegerData instance = (IntegerData) ClassUtils.newInstance(IntegerData.class);
        assertEquals(Integer.valueOf(0), instance.get());
    }

    @Test
    public void testNewInstance_OneParameterConstructor() {
        IntegerData instance = (IntegerData) ClassUtils.newInstance(IntegerData.class
                , new Class[]{Integer.class}, new Object[]{100});
        assertEquals(Integer.valueOf(100), instance.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewInstance_ParameterTypeIsError() {
        IntegerData instance = (IntegerData) ClassUtils.newInstance(IntegerData.class
                , new Class[]{String.class}, new Object[]{"hi"});
    }

    @Test
    public void testGetSize() {
        int[] i = new int[]{1, 2};
        assertEquals(2, ClassUtils.getSize(i));

        List<Integer> list = Arrays.asList(1, 2);
        assertEquals(2, ClassUtils.getSize(list));

        Map<Integer, Integer> map = new HashMap<>();
        map.put(1, 1);
        map.put(2, 2);
        assertEquals(2, ClassUtils.getSize(map));
    }

    @Test
    public void testGetGenericClass() {
        Class<?> genericClass = ClassUtils.getGenericClass(IntegerData.class);
        assertSame(genericClass, Integer.class);

        genericClass = ClassUtils.getGenericClass(ComplexData.class, 1, 1);
        assertSame(genericClass, String.class);
    }

    @Test
    public void testGetSetterProperty() throws NoSuchMethodException {
        Method method = ClassUtils.searchMethod(ComplexData.class, "setB", new Class[]{String.class});
        String setterProperty = ClassUtils.getSetterProperty(method);
        assertEquals("b", setterProperty);
    }

    interface Data<T> {
        T get();
    }

    interface Complex<A, B> {
        A getA();

        void setA(A a);

        B getB();

        void setB(B b);
    }

    static class IntegerData implements Data<Integer> {
        private Integer data = 0;

        public IntegerData() {

        }

        public IntegerData(Integer data) {
            this.data = data;
        }

        @Override
        public Integer get() {
            return this.data;
        }
    }

    static class ComplexData implements Data<Integer>, Complex<Integer, String> {

        @Override
        public Integer get() {
            return null;
        }

        @Override
        public Integer getA() {
            return null;
        }

        @Override
        public void setA(Integer integer) {

        }

        @Override
        public String getB() {
            return null;
        }

        @Override
        public void setB(String s) {

        }
    }

}
