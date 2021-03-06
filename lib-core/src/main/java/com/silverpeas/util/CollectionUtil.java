/*
 * Copyright (C) 2000 - 2013 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have recieved a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "http://www.silverpeas.org/docs/core/legal/floss_exception.html"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.silverpeas.util;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author Yohann Chastagnier
 */
public class CollectionUtil {

  /**
   * Reverse the given list and returns it.
   * @param list
   * @param <T>
   * @return
   */
  public static <T> List<T> reverse(List<T> list) {
    Collections.reverse(list);
    return list;
  }

  /**
   * Checks if the given collection is not instancied or empty
   * @param <T>
   * @param collection
   * @return
   */
  public static <T> boolean isEmpty(final Collection<T> collection) {
    return !isNotEmpty(collection);
  }

  /**
   * Checks if the given collection is instancied and not empty
   * @param <T>
   * @param collection
   * @return
   */
  public static <T> boolean isNotEmpty(final Collection<T> collection) {
    return collection != null && !collection.isEmpty();
  }

  /**
   * Splits a collection into several collections. (Particularly useful for limitations of database
   * around the "in" clause)
   * @param collection
   * @return
   */
  public static <T> Collection<Collection<T>> split(final Collection<T> collection) {
    return split(collection, 500);
  }

  /**
   * Splits a collection into several collections. (Particularly useful for limitations of database
   * around the "in" clause)
   * @param collection
   * @param collectionSizeMax
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T> Collection<Collection<T>> split(final Collection<T> collection,
      final int collectionSizeMax) {
    Collection<Collection<T>> result = null;

    try {
      if (isNotEmpty(collection)) {
        if (collectionSizeMax > 0 && collection.size() > collectionSizeMax) {

          // Guessing the result size and initializing the result
          int size = (collection.size() / collectionSizeMax);
          if ((collection.size() % collectionSizeMax) != 0) {
            size++;
          }
          result = new ArrayList<Collection<T>>(size);

          // Browsing the collection
          Collection<T> curLot = null;
          for (final T element : collection) {

            // If necessary, initializing a lot
            if (curLot == null || curLot.size() >= collectionSizeMax) {
              curLot = new ArrayList<T>(collectionSizeMax);

              // Adding the new lot
              result.add(curLot);
            }

            // Adding an element into the current lot
            curLot.add(element);
          }
        } else {
          result = Collections.singletonList(collection);
        }
      }
    } catch (final Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (result == null) {
        result = new ArrayList<Collection<T>>();
      }
    }

    // Retour du resultat
    return result;
  }

  /**
   * Transforming a collection into a map
   * @param <T> collection type elements
   * @param <K> map type key
   * @param <V> map type value
   * @param collection
   * @param extractor extractor interface
   * @return a map initialized from a list by an extractor
   */
  public static <T extends Object, K extends Object, V extends Object> HashMap<K, V> listToMap(
      final Collection<T> collection, final ExtractionList<T, K, V> extractor) {
    final LinkedHashMap<K, V> result;
    if (collection == null) {
      result = null;
    } else if (collection.isEmpty()) {
      result = new LinkedHashMap<K, V>();
    } else {
      result = new LinkedHashMap<K, V>((int) (collection.size() * 0.75f));
      if (extractor instanceof ExtractionComplexList<?, ?, ?>) {
        ((ExtractionComplexList<T, K, V>) extractor).setMap(result);
      }
      for (final T toPerform : collection) {
        result.put(extractor.getKey(toPerform), extractor.getValue(toPerform));
      }
    }
    return result;
  }

  /**
   * Extracting a property.
   * @param aClass property class to extract
   * @param collection collection from that to extract
   * @param propertyName name of the property
   * @return a collection with requested elements
   */
  public static <T> Collection<T> extractFrom(final Class<T> aClass, final Collection<?> collection,
      final String propertyName) {
    return extractFrom(collection, propertyName, false, 0);
  }

  /**
   * Extracting a property.
   * @param collection collection from that to extract
   * @param propertyName name of the property
   * @return a collection with requested elements
   */
  public static <T> Collection<T> extractFrom(final Collection<?> collection,
      final String propertyName) {
    return extractFrom(collection, propertyName, false, 0);
  }

  /**
   * Extracting a property.
   * @param collection collection from that to extract
   * @param propertyName name of the property
   * @param isListOfArray indicates if the list is an array list
   * @param numberColumn column index to extract from the array
   * @return a collection with requested elements
   */
  @SuppressWarnings({"unchecked"})
  public static <T> Collection<T> extractFrom(final Collection<?> collection,
      final String propertyName, final boolean isListOfArray, final int numberColumn) {
    Set<T> result = null;
    if (collection != null) {
      result = new HashSet<T>(collection.size());
      if (!isListOfArray) {
        for (final Object object : collection) {
          result.add((T) getPropertyAsObject(object, propertyName));
        }
      } else {
        for (final Object[] myObject : (Collection<Object[]>) collection) {
          result.add((T) myObject[numberColumn]);
        }
      }
    }
    return result;
  }

  /**
   * Extracting a property from elements that each has an other property with a given value
   * @param class property class to extract
   * @param collection element collection
   * @param propertyNameToExtract name property to extract
   * @param propertyNameToCompare name value to compare
   * @param givenValueToCompare value to compare
   * @param nullValueExtracted null value extracted
   * @return a bean property collection
   */
  @SuppressWarnings({"unchecked"})
  public static <T> Collection<T> extractFrom(final Class<T> aClass, final Collection<?> collection,
      final String propertyNameToExtract, final String propertyNameToCompare,
      final Object givenValueToCompare, final boolean nullValueExtracted) {
    Set<T> result = null;
    if (collection != null) {
      result = new HashSet<T>(collection.size());
      Object valueToExtract;
      Object valueToCompare;
      for (final Object element : collection) {
        valueToCompare = getPropertyAsObject(element, propertyNameToCompare);
        if ((givenValueToCompare == null && valueToCompare == null) ||
            (givenValueToCompare != null && givenValueToCompare.equals(valueToCompare))) {
          valueToExtract = getPropertyAsObject(element, propertyNameToExtract);
          if (valueToExtract != null || nullValueExtracted) {
            result.add((T) valueToExtract);
          }
        }
      }
    }
    return result;
  }

  /**
   * Value converted from a bean property.
   * @param bean bean
   * @param propertyName
   */
  private static synchronized Object getPropertyAsObject(final Object bean,
      final String propertyName) {
    // Synchronized as PropertyEditor is not thread-safe.
    Object property = null;
    try {
      property = PropertyUtils.getProperty(bean, propertyName);
    } catch (final IllegalAccessException e) {
      throw new RuntimeException(e.getMessage(), e);
    } catch (final InvocationTargetException e) {
      throw new RuntimeException(e.getMessage(), e);
    } catch (final NoSuchMethodException e) {
      throw new RuntimeException(e.getMessage(), e);
    } catch (final NestedNullException e) {
      // In the case of a.b, with a == null, null is returned
      property = null;
    }
    if (property == null) {
      return null;
    }
    return property;
  }

  public static <T> List<T> asList(T... values) {
    return new ArrayList<T>(Arrays.asList(values));
  }

  public static <T> Set<T> asSet(T... values) {
    return new HashSet<T>(Arrays.asList(values));
  }

  /**
   * Null elements are not taking into account.
   * @see Collections#addAll(java.util.Collection, Object[])
   */
  public static <T> boolean addAllIgnoreNull(Collection<? super T> c, T... elements) {
    boolean result = false;
    for (T element : elements) {
      if (element != null) {
        result |= c.add(element);
      }
    }
    return result;
  }

  /**
   * Makes an union between both of the given lists.<br/>
   * The result contains unique values.
   * @param list1 the first list.
   * @param list2 the second list.
   * @param <T>
   * @return the union between the two lists.
   */
  public static <T> List<T> union(List<T> list1, List<T> list2) {
    Set<T> set = new LinkedHashSet<T>();
    set.addAll(list1);
    set.addAll(list2);
    return new ArrayList<T>(set);
  }

  /**
   * Makes an intersection betwwen both of the given lists.<br/>
   * The result contains unique values.
   * @param list1 the first list.
   * @param list2 the second list.
   * @param <T>
   * @return the intersection between the two lists.
   */
  public static <T> List<T> intersection(List<T> list1, List<T> list2) {
    List<T> list = new ArrayList<T>(new LinkedHashSet<T>(list1));
    Iterator<T> iterator = list.iterator();
    while(iterator.hasNext()) {
      if (!list2.contains(iterator.next())) {
        iterator.remove();
      }
    }
    return list;
  }
}
