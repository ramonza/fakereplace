package org.fakereplace.integration.metawidget;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.fakereplace.api.ClassChangeAware;
import org.fakereplace.api.ClassChangeNotifier;
import org.fakereplace.classloading.ClassIdentifier;
import org.fakereplace.data.InstanceTracker;

public class ClassRedefinitionPlugin implements ClassChangeAware
{
   public ClassRedefinitionPlugin()
   {
      ClassChangeNotifier.add(this);
   }

   static private Method remove;

   static
   {
      try
      {
         remove = Map.class.getMethod("remove", Object.class);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   static private Field getField(Class<?> clazz, String name) throws NoSuchFieldException
   {
      if (clazz == Object.class)
         throw new NoSuchFieldException();
      try
      {
         return clazz.getDeclaredField(name);
      }
      catch (Exception e)
      {
         // TODO: handle exception
      }
      return getField(clazz.getSuperclass(), name);
   }

   public void beforeChange(Class<?>[] changed, ClassIdentifier[] added)
   {

   }

   public void notify(Class<?>[] changed, ClassIdentifier[] added)
   {
      Set<Object> data = InstanceTracker.get("org.metawidget.inspector.impl.actionstyle.BaseActionStyle");
      for (Object i : data)
      {
         clearMap(changed, i, "mActionCache");
      }
      data = InstanceTracker.get("org.metawidget.inspector.impl.propertystyle.BasePropertyStyle");
      for (Object i : data)
      {
         clearMap(changed, i, "mPropertiesCache");
      }

   }

   public static void clearMap(Class<?>[] changed, Object i, String cacheName)
   {
      try
      {
         Field f = getField(i.getClass(), cacheName);
         f.setAccessible(true);
         Object map = f.get(i);
         for (Class<?> c : changed)
         {
            remove.invoke(map, c);
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

}
