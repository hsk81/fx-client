package ch.blackhan.representation;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

import java.util.*;
import java.lang.reflect.*;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

public class RepresentableUtil {

    public static String toJson(Object object)
    {
        StringBuilder sb = new StringBuilder("{");
        Field[] fields = RepresentableUtil.getNonPrivateFields(object.getClass());
        if (fields.length == 0) return sb.append("}").toString();

        for (Field field : fields)
        {
            if (Modifier.isProtected(field.getModifiers()))
            {
                field.setAccessible(true);
            }

            try {
                Object value = field.get(object);
                sb.append("\"");
                sb.append(field.getName());
                sb.append("\"");
                sb.append(":");

                if (value != null)
                {
                    sb.append(value instanceof Representable
                        ? ((Representable)value).toRepresentation() : value
                    );
                }
                else
                {
                    sb.append("null");
                }
            } catch (IllegalAccessException ex) {
                continue; // ignore field!
            }

            sb.append(",");
        }

        return sb.delete(sb.length() - 1, sb.length()).append("}").toString();
    }

    private static Field[] getNonPrivateFields(Class clazz)
    {
        Stack<Class> classes = new Stack<Class>();
        while (clazz != null)
        {
            classes.push(clazz); clazz = clazz.getSuperclass();
        }

        List<Field> fields = new ArrayList<Field>();
        while (!classes.empty())
        {
            for (Field field : classes.pop().getDeclaredFields())
            {
                if (!Modifier.isPrivate(field.getModifiers()))
                {
                    fields.add(field);
                }
            }
        }

        return fields.toArray(new Field[] {});
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
