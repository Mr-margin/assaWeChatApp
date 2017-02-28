package com.gistone.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapUtil {

	/*** <p>Title:MapUtil </p>* <p>Description: </p>*  * @author xuwei* @date上午11:20:57 */
	 public static <K, V extends Comparable<? super V>> Map<K, V>   
     sortByValue( Map<K, V> map )  
	 {  
	     List<Map.Entry<K, V>> list =  
	         new LinkedList<Map.Entry<K, V>>( map.entrySet() );  
	     Collections.sort( list, new Comparator<Map.Entry<K, V>>()  
	     {  
	         public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )  
	         {  
	             return ( o2.getValue() ).compareTo(o1.getValue());  
	         }  
	     } );  
	
	     Map<K, V> result = new LinkedHashMap<K, V>();  
	     for (Map.Entry<K, V> entry : list)  
	     {  
	         result.put( entry.getKey(), entry.getValue() );  
	     }  
	     return result;  
	 }
	 public static boolean isNumeric(String str){
		  for (int i = 0; i < str.length(); i++){
		   if (!Character.isDigit(str.charAt(i))){
		    return false;
		   }
		  }
		  return true;
		 }
}
