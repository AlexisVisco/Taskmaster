package fr.aviscogl.taskmaster.command;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            commandExecutor.out = out;
            commandExecutor.name = split[0];
            commandExecutor.args = constructArrayArgs(split);
            String fullCommandAfterLabel = reconstructStringAfterLabel(split);

            for (Method method : cl.getMethods()) {
                CommandRouter[] annotations = method.getDeclaredAnnotationsByType(CommandRouter.class);
                if (annotations.length > 0) {
                    CommandRouter router = annotations[0];
                    Pattern pattern = Pattern.compile(router.regexPatternArguments());
                    Matcher matcher = pattern.matcher(fullCommandAfterLabel);
                    List<String> params = new ArrayList<>();
                    if (matcher.find()) {
                        if (!router.intoParams() && method.getParameterCount() == 0) {
                            method.invoke(commandExecutor);
                            break ;
                        }
                        else {
                            for (int i = 1; i < matcher.groupCount(); i++)
                                params.add(matcher.group(i));
                            if (method.getParameterCount() == params.size()) {
                                List<Object> parametersTyped = new ArrayList<>();
                                Parameter[] parameters = method.getParameters();
                                for (int i = 0; i < parameters.length; i++) {
                                    Parameter param = parameters[i];
                                    parametersTyped.add(toObject(param.getType(), params.get(i)));
                                }
                                method.invoke(commandExecutor, parametersTyped.toArray());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String reconstructStringAfterLabel(String[] split) {
        StringBuilder sb = new StringBuilder();
        if (split.length > 1) {
            for (int i = 1; i < split.length; i++)
                sb.append(split[i]);
            return sb.toString();
        } else return "";
    }

    private static String[] constructArrayArgs(String[] split) {
        List<String> lst = new ArrayList<>();
        for (int i = 1; i < split.length; i++) {
            lst.add(split[i]);
        }
        return (String[])lst.toArray();
    }

    private static Object toObject(Class clazz, String value) {
        if (Boolean.class == clazz) return Boolean.parseBoolean(value);
        if (Byte.class == clazz) return Byte.parseByte(value);
        if (Short.class == clazz) return Short.parseShort(value);
        if (Integer.class == clazz) return Integer.parseInt(value);
        if (Long.class == clazz) return Long.parseLong(value);
        if (Float.class == clazz) return Float.parseFloat(value);
        if (Double.class == clazz) return Double.parseDouble(value);
        return value;
    }
}
