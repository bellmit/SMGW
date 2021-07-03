package star.sms._frame.utils;

import org.hibernate.transform.AliasToBeanResultTransformer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

public class Testtrans extends AliasToBeanResultTransformer {
	private Class resultClass;

	public Testtrans(Class resultClass) {
		super(resultClass);
		this.resultClass = resultClass;
	}

	private static final long serialVersionUID = 1L;

	public Object transformTuple(Object[] tuple, String[] aliases) {
		Object obj = null;
		try {
			obj = resultClass.newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		Method[] methods = resultClass.getMethods();// 返回这个类里面方法的集合
		Field[] fields = resultClass.getDeclaredFields();
		for (int k = 0; k < aliases.length; k++) {
			String aliase = getKey(fields, aliases[k]);
			char[] ch = aliase.toCharArray();
			ch[0] = Character.toUpperCase(ch[0]);
			String s = new String(ch);
			String[] names = new String[] { ("set" + s).intern(), ("get" + s).intern(), ("is" + s).intern(),
					("read" + s).intern() };
			Method setter = null;
			Method getter = null;
			int length = methods.length;
			for (int i = 0; i < length; ++i) {
				Method method = methods[i];
				/**
				 * 检查该方法是否为公共方法,如果非公共方法就继续
				 */
				if (!Modifier.isPublic(method.getModifiers()))
					continue;
				String methodName = method.getName();
				for (String name : names) {
					if (name.equals(methodName)) {
						if (name.startsWith("set") || name.startsWith("read"))
							setter = method;
						else if (name.startsWith("get") || name.startsWith("is"))
							getter = method;

					}
				}
			}
			if (getter != null) {
				Object[] param = buildParam(getter.getReturnType().getName(), tuple[k]);
				try {
					setter.invoke(obj, param);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return obj;
	}

	private final static Object[] buildParam(String paramType, Object value) {
		Object[] param = new Object[1];
		if (paramType.equalsIgnoreCase("java.lang.String")) {
			param[0] = (value);
		} else if (paramType.equalsIgnoreCase("int") || paramType.equalsIgnoreCase("java.lang.Integer")) {
			if (value instanceof BigDecimal) {
				param[0] = ((BigDecimal) (value)).intValue();
			}else if(value instanceof BigInteger) {
				param[0] = ((BigInteger) (value)).intValue();
			} else {
				param[0] = (Integer) (value);
			}
		} else if (paramType.equalsIgnoreCase("long") || paramType.equalsIgnoreCase("java.lang.Long")) {
			param[0] = (Long) (value);
		} else if (paramType.equalsIgnoreCase("double") || paramType.equalsIgnoreCase("java.lang.Double")) {
			param[0] = (Double) (value);
		} else if (paramType.equalsIgnoreCase("BigDecimal") || paramType.equalsIgnoreCase("java.math.BigDecimal")) {
			param[0] = (BigDecimal) (value);
		} else if (paramType.equalsIgnoreCase("float") || paramType.equalsIgnoreCase("java.lang.Float")) {
			param[0] = (Float) (value);
		} else if (paramType.equalsIgnoreCase("char") || paramType.equalsIgnoreCase("Character")) {
			param[0] = (char) (value);
		} else if (paramType.equalsIgnoreCase("timestamp") || paramType.equalsIgnoreCase("java.sql.Timestamp")) {
			param[0] = (Timestamp) (value);
		} else if (paramType.equalsIgnoreCase("date") || paramType.equalsIgnoreCase("java.util.Date")) {
			if(value!=null) {
				Timestamp ts = (Timestamp) (value);
				Date date = new Date(ts.getTime());
				param[0] = date;
			}else {
				param[0]=null;
			}
		} else {
			System.out.println(11);
		}
		return param;
	}

	private String getKey(Field[] fields, String aliase) {
		String result = aliase;
		try {
			for (Field f : fields) {
				String fieldName = f.getName();
				String temp = fieldName.toUpperCase().replace("_","");
				String temp2 = aliase.toUpperCase().replace("_","");
				if (fieldName.equals(aliase) || fieldName.equals(temp2) || temp.equals(temp2)) {
					result = fieldName;
					break;
				}
			}
		} catch (Exception e) {
		}
		return result;
	}
}
