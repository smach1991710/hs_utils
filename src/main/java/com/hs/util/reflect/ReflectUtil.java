package com.hs.util.reflect;

import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: songhao
 * @Date: 2020/9/14
 * @Description: 反射的工具类
 */
public class ReflectUtil {

    static Logger logger = Logger.getLogger(ReflectUtil.class);

    public ReflectUtil(){
    }

    /**
     * 获取指定类的包名
     * @param clazz
     * @return
     */
    public static String getPackage(Class<?> clazz) {
        Package pck = clazz.getPackage();
        if (null != pck) {
            return pck.getName();
        }
        return null;
    }

    /**
     * 获取继承的父类的全类名
     * @param clazz
     * @return
     */
    public static String getSuperClassName(Class<?> clazz) {
        Class<?> superClass = clazz.getSuperclass();
        if (null != superClass) {
            return superClass.getName();
        }
        return null;
    }

    /**
     * 获取全类名
     * @param clazz
     * @return
     */
    public static String getClassName(Class<?> clazz) {
        return clazz.getName();
    }

    /**
     * 获取实现的接口名
     * @param clazz
     * @return
     */
    public static List<String> getInterfaces(Class<?> clazz) {
        Class<?>[] interfaces = clazz.getInterfaces();
        int len = interfaces.length;
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < len; i++) {
            Class<?> itfc = interfaces[i];
            // 接口名
            String interfaceName = itfc.getSimpleName();
            list.add(interfaceName);
        }
        return list;
    }

    /**
     * 获取所有构造方法
     * @param clazz
     * @return
     */
    public static List<String> getConstructors(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        int len = constructors.length;

        List<String> list = new ArrayList<String>();
        for (int i = 0; i < len; i++) {
            Constructor<?> constructor = constructors[i];
            StringBuilder builder = new StringBuilder();

            // 修饰符
            String modifier = Modifier.toString(constructor.getModifiers());
            builder.append(modifier + " ");

            // 方法名（类名）
            String constructorName = clazz.getSimpleName();
            builder.append(constructorName + " (");

            // 形参列表
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            int length = parameterTypes.length;
            for (int j = 0; j < length; j++) {
                Class<?> parameterType = parameterTypes[j];

                String parameterTypeName = parameterType.getSimpleName();

                if (j < length - 1) {
                    builder.append(parameterTypeName + ", ");
                } else {
                    builder.append(parameterTypeName);
                }

            }
            builder.append(") {}");
            list.add(builder.toString());
        }
        return list;
    }

    /**
     * 根据Class类型，获取对应的实例【要求必须有无参的构造器】
     * @param clazz
     * @return
     * @throws Exception
     */
    public static <T> T getNewInstance(Class<? extends T> clazz) throws Exception {
        return clazz.newInstance();
    }

    /**
     * 根据传入的构造方法对象，以及参数，获取对应的实例
     * @param clazz
     * @param initargs
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T getNewInstance(Class<? extends T> clazz, Object... initargs) throws Exception {
        Constructor constructor = null;
        if(initargs != null){
            Class<?>[] parameterTypes = new Class<?>[initargs.length];
            for(int i = 0 ;i < initargs.length; i++){
                parameterTypes[i] = initargs[i].getClass();
            }
            constructor = getConstructor(clazz,parameterTypes);
        }else{
            return getNewInstance(clazz);
        }
        constructor.setAccessible(true);
        return (T)constructor.newInstance(initargs);
    }

    /**
     * 根据传入的类的Class对象，以及构造方法的形参的Class对象，获取对应的构造方法对象
     * @param clazz
     * @param parameterTypes
     * @return
     * @throws Exception
     */
    public static <T> Constructor<?> getConstructor(Class<? extends T> clazz, Class<?>... parameterTypes) throws Exception{
        return clazz.getDeclaredConstructor(parameterTypes);
    }

    /**
     * 反射调用方法
     * @param objclass
     * @param obj
     * @param methodName
     * @param args
     * @return
     */
    public static Object executeMethod(Class<?> objclass, Object obj, String methodName, Object... args) throws Exception {
        try {
            Method method = null;
            if(args != null){
                Class<?>[] parameterTypes = new Class<?>[args.length];
                for(int i = 0 ;i < args.length; i++){
                    parameterTypes[i] = args[i].getClass();
                }
                method = getMethod(objclass,methodName,parameterTypes);
            }else{
                method = getMethod(objclass,methodName);
            }
            method.setAccessible(true);
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 根据传入的方法名字符串，获取对应的方法
     * @param clazz
     * @param name
     * @param parameterTypes
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws Exception {
        return clazz.getDeclaredMethod(name, parameterTypes);
    }

    /**
     * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
     * @param object
     * @param fieldName
     * @return
     */
    public static Object getFieldValue(final Object object, final String fieldName) {
        Field field = getDeclaredField(object, fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
        }
        makeAccessible(field);
        Object result = null;
        try {
            result = field.get(object);
        } catch (IllegalAccessException e) {
            logger.error("不可能抛出的异常{}" + e.getMessage());
        }
        return result;
    }

    /**
     * 循环向上转型, 获取对象的DeclaredField.
     * 如向上转型到Object仍无法找到, 返回null.
     * @param object
     * @param fieldName
     * @return
     */
    protected static Field getDeclaredField(final Object object, final String fieldName) {
        for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass
                .getSuperclass()) {
            try {
                return superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                logger.error(e.getMessage(),e);
            }
        }
        return null;
    }

    /**
     * 强行设置Field可访问.
     * @param field
     */
    protected static void makeAccessible(final Field field) {
        if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
            field.setAccessible(true);
        }
    }

    /**
     * 根据传入的属性名字符串，修改对应的属性值
     * @param clazz 类的Class对象
     * @param name 属性名
     * @param obj 要修改的实例对象
     * @param value 修改后的新值
     */
    public static void setField(Class<?> clazz, String name, Object obj, Object value)
            throws Exception {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        field.set(obj, value);
    }
}
