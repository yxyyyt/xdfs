package com.sciatta.xdfs.common.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by Rain on 2023/8/19<br>
 * All Rights Reserved(C) 2017 - 2023 SCIATTA <br> <p/>
 * 类型工具类
 */
@Slf4j
public class ClassUtils {

    private static final int JIT_LIMIT = 5 * 1024;

    private ClassUtils() {
    }

    /**
     * 通过类名创建实例
     *
     * @param name 类名
     * @return 实例
     */
    public static Object newInstance(String name) {
        return newInstance(forName(name));
    }

    /**
     * 通过类创建实例
     *
     * @param clazz 类
     * @return 实例
     */
    public static Object newInstance(Class<?> clazz) {
        return newInstance(clazz, null, null);
    }

    /**
     * 通过类创建实例
     *
     * @param clazz           类
     * @param parameterTypes  构造函数参数类型
     * @param parameterValues 构造函数参数值
     * @return 实例
     */
    public static Object newInstance(Class<?> clazz, Class<?>[] parameterTypes, Object[] parameterValues) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(parameterValues);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            String msg = String.format("Class %s not have a specified constructor, in parameter type is [%s] , catch exception %s",
                    clazz.getName(), Arrays.toString(parameterTypes), e.getMessage());
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * 通过类名查找类；若没有找到，遍历指定的所有包名进行查找
     *
     * @param packages  待查找的包名数组
     * @param className 类名
     * @return 类
     */
    public static Class<?> forName(String[] packages, String className) {
        try {
            return _forName(className);
        } catch (ClassNotFoundException e) {
            if (packages != null && packages.length > 0) {
                for (String pkg : packages) {
                    try {
                        return _forName(pkg + "." + className);
                    } catch (ClassNotFoundException ignore) {
                    }
                }
            }
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * 通过类名查找类
     *
     * @param className 类名
     * @return 类
     */
    public static Class<?> forName(String className) {
        try {
            return _forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * 获取原生类型的包装类型
     *
     * @param type 原生类型
     * @return 包装类型
     */
    public static Class<?> getBoxedClass(Class<?> type) {
        if (type == boolean.class) {
            return Boolean.class;
        } else if (type == char.class) {
            return Character.class;
        } else if (type == byte.class) {
            return Byte.class;
        } else if (type == short.class) {
            return Short.class;
        } else if (type == int.class) {
            return Integer.class;
        } else if (type == long.class) {
            return Long.class;
        } else if (type == float.class) {
            return Float.class;
        } else if (type == double.class) {
            return Double.class;
        } else {
            return type;
        }
    }

    /**
     * 获取包装类型
     *
     * @param v 原生类型
     * @return 包装类型
     */
    public static Boolean boxed(boolean v) {
        return Boolean.valueOf(v);
    }

    /**
     * 获取包装类型
     *
     * @param v 原生类型
     * @return 包装类型
     */
    public static Character boxed(char v) {
        return Character.valueOf(v);
    }

    /**
     * 获取包装类型
     *
     * @param v 原生类型
     * @return 包装类型
     */
    public static Byte boxed(byte v) {
        return Byte.valueOf(v);
    }

    /**
     * 获取包装类型
     *
     * @param v 原生类型
     * @return 包装类型
     */
    public static Short boxed(short v) {
        return Short.valueOf(v);
    }

    /**
     * 获取包装类型
     *
     * @param v 原生类型
     * @return 包装类型
     */
    public static Integer boxed(int v) {
        return Integer.valueOf(v);
    }

    /**
     * 获取包装类型
     *
     * @param v 原生类型
     * @return 包装类型
     */
    public static Long boxed(long v) {
        return Long.valueOf(v);
    }

    /**
     * 获取包装类型
     *
     * @param v 原生类型
     * @return 包装类型
     */
    public static Float boxed(float v) {
        return Float.valueOf(v);
    }

    /**
     * 获取包装类型
     *
     * @param v 原生类型
     * @return 包装类型
     */
    public static Double boxed(double v) {
        return Double.valueOf(v);
    }

    /**
     * 获取包装类型
     *
     * @param v 原生类型
     * @return 包装类型
     */
    public static Object boxed(Object v) {
        return v;
    }

    /**
     * 获取原生类型
     *
     * @param v 包装类型
     * @return 原生类型
     */
    public static boolean unboxed(Boolean v) {
        return v != null && v;
    }

    /**
     * 获取原生类型
     *
     * @param v 包装类型
     * @return 原生类型
     */
    public static char unboxed(Character v) {
        return v == null ? '\0' : v;
    }

    /**
     * 获取原生类型
     *
     * @param v 包装类型
     * @return 原生类型
     */
    public static byte unboxed(Byte v) {
        return v == null ? 0 : v;
    }

    /**
     * 获取原生类型
     *
     * @param v 包装类型
     * @return 原生类型
     */
    public static short unboxed(Short v) {
        return v == null ? 0 : v;
    }

    /**
     * 获取原生类型
     *
     * @param v 包装类型
     * @return 原生类型
     */
    public static int unboxed(Integer v) {
        return v == null ? 0 : v;
    }

    /**
     * 获取原生类型
     *
     * @param v 包装类型
     * @return 原生类型
     */
    public static long unboxed(Long v) {
        return v == null ? 0 : v;
    }

    /**
     * 获取原生类型
     *
     * @param v 包装类型
     * @return 原生类型
     */
    public static float unboxed(Float v) {
        return v == null ? 0 : v;
    }

    /**
     * 获取原生类型
     *
     * @param v 包装类型
     * @return 原生类型
     */
    public static double unboxed(Double v) {
        return v == null ? 0 : v;
    }

    /**
     * 获取原生类型
     *
     * @param v 包装类型
     * @return 原生类型
     */
    public static Object unboxed(Object v) {
        return v;
    }

    /**
     * 判断聚集对象是否不为空
     *
     * @param object 聚集对象
     * @return true，不为空；false，为空
     */
    public static boolean isNotEmpty(Object object) {
        return getSize(object) > 0;
    }

    /**
     * 获取聚集对象的长度，包括集合类型、Map和数组
     *
     * @param object 聚集对象
     * @return 聚集对象的长度
     */
    public static int getSize(Object object) {
        if (object == null) {
            return 0;
        }
        if (object instanceof Collection<?>) {
            return ((Collection<?>) object).size();
        } else if (object instanceof Map<?, ?>) {
            return ((Map<?, ?>) object).size();
        } else if (object.getClass().isArray()) {
            return Array.getLength(object);
        } else {
            return -1;
        }
    }

    /**
     * 将字符串转换为URI
     *
     * @param name 字符串
     * @return URI
     */
    public static URI toURI(String name) {
        try {
            return new URI(name);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 获取指定类型的泛型类型
     *
     * @param cls 指定类型
     * @return 泛型类型
     */
    public static Class<?> getGenericClass(Class<?> cls) {
        return getGenericClass(cls, 0, 0);
    }

    /**
     * 获取指定类型的泛型类型
     *
     * @param cls                    指定类型
     * @param genericInterfacesIndex 实现接口的索引
     * @return 泛型类型
     */
    public static Class<?> getGenericClass(Class<?> cls, int genericInterfacesIndex) {
        return getGenericClass(cls, genericInterfacesIndex, 0);
    }

    /**
     * 获取指定类型的泛型类型
     *
     * @param cls                      指定类型
     * @param genericInterfacesIndex   实现接口的索引
     * @param actualTypeArgumentsIndex 接口泛型类型的索引
     * @return 泛型类型
     */
    public static Class<?> getGenericClass(Class<?> cls, int genericInterfacesIndex, int actualTypeArgumentsIndex) {
        try {
            ParameterizedType parameterizedType = ((ParameterizedType) cls.getGenericInterfaces()[genericInterfacesIndex]);
            Object genericClass = parameterizedType.getActualTypeArguments()[actualTypeArgumentsIndex];
            if (genericClass instanceof ParameterizedType) {
                return (Class<?>) ((ParameterizedType) genericClass).getRawType();
            } else if (genericClass instanceof GenericArrayType) {
                return (Class<?>) ((GenericArrayType) genericClass).getGenericComponentType();
            } else if (genericClass != null) {
                return (Class<?>) genericClass;
            }
        } catch (Throwable ignored) {
        }
        if (cls.getSuperclass() != null) {
            return getGenericClass(cls.getSuperclass(), genericInterfacesIndex, actualTypeArgumentsIndex);
        } else {
            throw new IllegalArgumentException(cls.getName() + " generic type undefined!");
        }
    }

    /**
     * jdk版本是否早于5
     *
     * @param javaVersion jdk版本
     * @return true，早于5；false，不早于5
     */
    public static boolean isBeforeJava5(String javaVersion) {
        return (javaVersion == null || javaVersion.length() == 0 || "1.0".equals(javaVersion)
                || "1.1".equals(javaVersion) || "1.2".equals(javaVersion)
                || "1.3".equals(javaVersion) || "1.4".equals(javaVersion));
    }

    /**
     * jdk版本是否早于6
     *
     * @param javaVersion jdk版本
     * @return true，早于6；false，不早于6
     */
    public static boolean isBeforeJava6(String javaVersion) {
        return isBeforeJava5(javaVersion) || "1.5".equals(javaVersion);
    }

    /**
     * 检查类的字节长度，JIT限制 5k byte
     *
     * @param name     类名
     * @param bytecode 类的字节长度
     */
    public static void checkBytecode(String name, byte[] bytecode) {
        if (bytecode.length > JIT_LIMIT) {
            log.error("The template bytecode too long, may be affect the JIT compiler. template class: " + name);
        }
    }

    /**
     * 获取指定类型的获取长度的方法名
     *
     * @param cls 指定类型
     * @return 获取长度的方法名
     */
    public static String getSizeMethod(Class<?> cls) {
        try {
            return cls.getMethod("size", new Class<?>[0]).getName() + "()";
        } catch (NoSuchMethodException e) {
            try {
                return cls.getMethod("length", new Class<?>[0]).getName() + "()";
            } catch (NoSuchMethodException e2) {
                try {
                    return cls.getMethod("getSize", new Class<?>[0]).getName() + "()";
                } catch (NoSuchMethodException e3) {
                    try {
                        return cls.getMethod("getLength", new Class<?>[0]).getName() + "()";
                    } catch (NoSuchMethodException e4) {
                        return null;
                    }
                }
            }
        }
    }

    /**
     * 通过指定类型，方法名和方法的参数类型查找方法
     *
     * @param currentClass   指定类型
     * @param name           方法名
     * @param parameterTypes 方法的参数类型
     * @return 方法
     * @throws NoSuchMethodException 查不到指定方法异常
     */
    public static Method searchMethod(Class<?> currentClass, String name, Class<?>[] parameterTypes) throws NoSuchMethodException {
        if (currentClass == null) {
            throw new NoSuchMethodException("class == null");
        }
        try {
            return currentClass.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            for (Method method : currentClass.getMethods()) {
                if (method.getName().equals(name)
                        && parameterTypes.length == method.getParameterTypes().length
                        && Modifier.isPublic(method.getModifiers())) {
                    if (parameterTypes.length > 0) {
                        Class<?>[] types = method.getParameterTypes();
                        boolean match = true;
                        for (int i = 0; i < parameterTypes.length; i++) {
                            if (!types[i].isAssignableFrom(parameterTypes[i])) {
                                match = false;
                                break;
                            }
                        }
                        if (!match) {
                            continue;
                        }
                    }
                    return method;
                }
            }
            throw e;
        }
    }

    /**
     * 获取指定原生类型的初始化值的字符串代码
     *
     * @param type 指定原生类型
     * @return 初始化值的字符串代码
     */
    public static String getInitCode(Class<?> type) {
        if (byte.class.equals(type)
                || short.class.equals(type)
                || int.class.equals(type)
                || long.class.equals(type)
                || float.class.equals(type)
                || double.class.equals(type)) {
            return "0";
        } else if (char.class.equals(type)) {
            return "'\\0'";
        } else if (boolean.class.equals(type)) {
            return "false";
        } else {
            return "null";
        }
    }

    /**
     * 将Entry数组转换为一个Map
     *
     * @param entries Entry数组
     * @param <K>     key泛型
     * @param <V>     value泛型
     * @return 转换后的Map
     */
    public static <K, V> Map<K, V> toMap(Map.Entry<K, V>[] entries) {
        Map<K, V> map = new HashMap<>();
        if (entries != null && entries.length > 0) {
            for (Map.Entry<K, V> entry : entries) {
                map.put(entry.getKey(),
                        entry.getValue());
            }
        }
        return map;
    }

    /**
     * 创建指定接口数组的代理类实例
     *
     * @param invocationHandler 回调处理器
     * @param interfaces        指定接口数组
     * @return 代理类实例
     */
    public static Object newProxyInstance(InvocationHandler invocationHandler, Class<?>[] interfaces) {
        return Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                interfaces,
                invocationHandler
        );
    }

    /**
     * 指定方法是否是setter方法：<br>
     * 1. 以set开头 <br>
     * 2. 只有一个参数 <br>
     * 3. 以public作为方法修饰符
     *
     * @param method 指定方法
     * @return true，是setter方法；否则，不是setter方法
     */
    public static boolean isSetter(Method method) {
        return method.getName().startsWith("set")
                && method.getParameterTypes().length == 1
                && Modifier.isPublic(method.getModifiers());
    }

    /**
     * 获取指定setter方法的属性名
     *
     * @param method 指定setter方法
     * @return 指定setter方法的属性名
     */
    public static String getSetterProperty(Method method) {
        if (!isSetter(method)) {
            return "";
        }
        return method.getName().length() > 3 ?
                method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4) /* 取子串，当开始索引超过字符串实际长度时，返回空串 */ :
                "";
    }

    /**
     * 指定类型是否是原生类型，支持数组
     *
     * @param cls 指定类型
     * @return true，是原生类型；否则，不是原生类型
     */
    public static boolean isPrimitives(Class<?> cls) {
        while (cls.isArray()) {
            cls = cls.getComponentType();
        }
        return isPrimitive(cls);
    }

    /**
     * 指定类型是否是原生类型
     *
     * @param cls 指定类型
     * @return true，是原生类型；否则，不是原生类型
     */
    public static boolean isPrimitive(Class<?> cls) {
        return cls.isPrimitive() || cls == String.class || cls == Boolean.class || cls == Character.class
                || Number.class.isAssignableFrom(cls) || Date.class.isAssignableFrom(cls);
    }

    /**
     * 获取类加载器
     *
     * @return 类加载器
     */
    public static ClassLoader getClassLoader() {
        return getClassLoader(ClassUtils.class);
    }

    /**
     * 获取指定Class的类加载器
     *
     * @param clazz 指定Class
     * @return 指定Class的类加载器
     */
    public static ClassLoader getClassLoader(Class<?> clazz) {
        ClassLoader cl = null;

        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ignore) {
        }

        if (cl == null) {
            cl = clazz.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ignore) {
                }
            }
        }

        return cl;
    }

    // private

    /**
     * 通过类名查找类
     *
     * @param className 类名
     * @return 类
     * @throws ClassNotFoundException 类没有查找到异常
     */
    private static Class<?> _forName(String className) throws ClassNotFoundException {
        // 原生类型
        if ("boolean".equals(className))
            return boolean.class;
        if ("byte".equals(className))
            return byte.class;
        if ("char".equals(className))
            return char.class;
        if ("short".equals(className))
            return short.class;
        if ("int".equals(className))
            return int.class;
        if ("long".equals(className))
            return long.class;
        if ("float".equals(className))
            return float.class;
        if ("double".equals(className))
            return double.class;

        // 原生类型数组
        if ("boolean[]".equals(className))
            return boolean[].class;
        if ("byte[]".equals(className))
            return byte[].class;
        if ("char[]".equals(className))
            return char[].class;
        if ("short[]".equals(className))
            return short[].class;
        if ("int[]".equals(className))
            return int[].class;
        if ("long[]".equals(className))
            return long[].class;
        if ("float[]".equals(className))
            return float[].class;
        if ("double[]".equals(className))
            return double[].class;

        try {
            return loadClass(className);    // 不是原生类型，通过类名加载类
        } catch (ClassNotFoundException e) {
            // try to load from java.lang package
            if (className.indexOf('.') == -1) {
                try {
                    return loadClass("java.lang." + className);
                } catch (ClassNotFoundException e2) {
                    // ignore, let the original exception be thrown
                }
            }
            throw e;
        }
    }

    /**
     * 通过类名加载类
     *
     * @param className 类名
     * @return 类
     * @throws ClassNotFoundException 类没有查找到异常
     */
    private static Class<?> loadClass(String className) throws ClassNotFoundException {
        return Class.forName(
                className.endsWith("[]") ? "[L" + className.substring(0, className.length() - 2) + ";" : className,
                true,
                Thread.currentThread().getContextClassLoader()
        );
    }

}
