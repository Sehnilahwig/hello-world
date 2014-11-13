package com.tvmining.wifiplus.util;

import java.lang.reflect.Field;

import android.content.Context;
import android.content.res.Resources;

public class ResourceUtil {

	/**
	 * 
	 * @param name
	 *            ��Դ���
	 * @param defType
	 *            ����
	 * @return
	 */
	public static int getResId(Context mContext, String name, String defType) {
		Resources res = mContext.getResources();
		return res.getIdentifier(name, defType, mContext.getPackageName());
	}

	public static final int[] getResourceDeclareStyleableIntArray(
			Context context, String name) {
		try {
			// use reflection to access the resource class
			Field[] fields2 = Class.forName(
					context.getPackageName() + ".R$styleable").getFields();
			// browse all fields
			for (Field f : fields2) {
				// pick matching field
				if (f.getName().equals(name)) {
					// return as int array
					int[] ret = (int[]) f.get(null);
					return ret;
				}
			}
		} catch (Throwable t) {
		}

		return null;
	}

}
