package fr.aviscogl.taskmaster.command;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandHandler {

    private static Map<String, Class<? extends CommandExecutor>> commands = new HashMap<>();

    public static void registerCommand(Class<? extends CommandExecutor> cl) {
        try {
            Command[] annotationsByType = cl.getAnnotationsByType(Command.class);
            if (annotationsByType.length > 0) {
                Command co = annotationsByType[0];
                commands.put(co.label(), cl);
                for (String s : co.alias())
                    commands.put(s, cl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void execute(String command, PrintWriter out) {
        String[] split = command.split(" ");
        try {
            Class<? extends CommandExecutor> cl = commands.get(split[0]);
            if (cl == null)
                return;

            CommandExecutor commandExecutor = cl.newInstance();
            initCommandExecutor(out, split, commandExecutor);
            String fullCommandAfterLabel = reconstructStringAfterLabel(split);
            Iterable<? extends Method> methodsByPriority = getMethodsByPriority(cl);
            for (Method method : methodsByPriority) {
                Optional<CommandRouter> an = getFirstAnnotation(method, CommandRouter.class);
                if (an.isPresent()) {
                    CommandRouter router = an.get();
                    Pattern pattern = Pattern.compile(router.regex());
                    Matcher matcher = pattern.matcher(fullCommandAfterLabel);
                    List<String> params = new ArrayList<>();
                    if (matcher.matches()) {
                        if (!router.intoParams() && method.getParameterCount() == 0) {
                            method.invoke(commandExecutor);
                            return;
                        }
                        else if (injectParameters(commandExecutor, method, matcher, params)) return;
                    }
                }
            }
            commandExecutor.defaultMethod();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Iterable<? extends Method> getMethodsByPriority(Class<? extends CommandExecutor> cl) {
        List<Method> collect = Arrays.stream(cl.getMethods())
                .filter(a -> getFirstAnnotation(a, CommandRouter.class).isPresent())
                .collect(Collectors.toList());
        collect.sort((a, b) -> {
            Optional<CommandRouter> c1 = getFirstAnnotation(a, CommandRouter.class);
            Optional<CommandRouter> c2 = getFirstAnnotation(b, CommandRouter.class);
            if (c1.isPresent() && c2.isPresent())
                return c1.get().priority() < c2.get().priority() ? 1 : -1;
            return -1;
        });
        return collect;
    }

    private static <T extends Annotation> Optional<T> getFirstAnnotation(Method m, Class<T> cl) {
        T[] annotations = m.getDeclaredAnnotationsByType(cl);
        if (annotations.length >= 1)
            return Optional.of(annotations[0]);
        return Optional.empty();
    }

    private static void initCommandExecutor(PrintWriter out, String[] split, CommandExecutor commandExecutor) {
        commandExecutor.out = out;
        commandExecutor.name = split[0];
        commandExecutor.args = constructArrayArgs(split);
    }

    private static boolean injectParameters(CommandExecutor commandExecutor, Method method,
                                            Matcher matcher, List<String> params)
            throws IllegalAccessException, InvocationTargetException {
        for (int i = 1; i <= matcher.groupCount(); i++)
            params.add(matcher.group(i));
        if (method.getParameterCount() == params.size()) {
            List<Object> parametersTyped = new ArrayList<>();
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Parameter param = parameters[i];
                parametersTyped.add(toObject(param.getType(), params.get(i)));
            }
            method.invoke(commandExecutor, parametersTyped.toArray());
            return true;
        }
        return false;
    }

    private static String reconstructStringAfterLabel(String[] split) {
        StringBuilder sb = new StringBuilder();
        if (split.length > 1) {
            for (int i = 1; i < split.length; i++)
                sb.append(split[i]).append(i == split.length - 1 ? "" : " ");
            return sb.toString();
        }
        else return "";
    }

    private static String[] constructArrayArgs(String[] split) {
        String[] stockArr = new String[split.length - 1];
        for (int i = 1; i < split.length; i++) {
            stockArr[i - 1] = split[i];
        }
        return stockArr;
    }

    private static Object toObject(Class clazz, String value) {
        if (Boolean.class == clazz || clazz == boolean.class) return Boolean.parseBoolean(value);
        if (Byte.class == clazz || clazz == byte.class) return Byte.parseByte(value);
        if (Short.class == clazz || clazz == short.class) return Short.parseShort(value);
        if (Integer.class == clazz || clazz == int.class) return Integer.parseInt(value);
        if (Long.class == clazz || clazz == long.class) return Long.parseLong(value);
        if (Float.class == clazz || clazz == float.class) return Float.parseFloat(value);
        if (Double.class == clazz || clazz == double.class) return Double.parseDouble(value);
        if (Character.class == clazz || clazz == char.class) return value.charAt(0);
        return value;
    }
}
