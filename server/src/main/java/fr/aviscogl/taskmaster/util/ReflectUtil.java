package fr.aviscogl.taskmaster.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReflectUtil {
    /**
     * Get all super classes
     *
     * @param clz
     * @return
     */
    public static Class<?>[] getAllSuperClasses(Class<?> clz) {
        List<Class<?>> list = new ArrayList<>();
        while ((clz = clz.getSuperclass()) != null) {
            list.add(clz);
        }
        return list.toArray(new Class<?>[list.size()]);
    }

    /**
     * Get all interfaces
     *
     * @param clz
     * @return
     */
    public static Class<?>[] getAllInterfaces(Class<?> clz) {
        HashSet<Class<?>> set = new HashSet<>();
        getAllInterfaces(clz, set);
        return set.toArray(new Class<?>[set.size()]);
    }

    private static void getAllInterfaces(Class<?> clz, Set<Class<?>> visited) {
        if (clz.getSuperclass() != null) {
            getAllInterfaces(clz.getSuperclass(), visited);
        }
        for (Class<?> c : clz.getInterfaces()) {
            if (visited.add(c)) {
                getAllInterfaces(c, visited);
            }
        }
    }
}
