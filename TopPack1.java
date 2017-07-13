package lol;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import java.io.*;
import java.util.*;
import java.net.*;
public class TopPack1 {
	
	static Map<String,Integer> top= new HashMap<String,Integer>();
	static StringBuilder s = new StringBuilder();  
	static Vector<String> ID= new Vector<String>();
	
	public static void search(String keyword) {
	    URL url;
	    InputStream is = null;
	    BufferedReader br;
	    String line;
	    
      try {
	        url = new URL("https://api.github.com/search/repositories?q="+keyword+"&sort=forks");
	       is = url.openStream();  // throws an IOException
	       br = new BufferedReader(new InputStreamReader(is));
	      
	        while ((line=br.readLine()) != null) 
	        {	        	          
	         s.append(line);	           
	        }
	        JSONParser parser = new JSONParser();

            Object obj = parser.parse(s.toString());
            
            JSONObject jsonObject =  (JSONObject) obj;
            JSONArray items = (JSONArray) jsonObject.get("items");
           for(int i=0;i<items.size();i++){
        	   
        	   JSONObject ob = (JSONObject)items.get(i);
        	   String id=(ob.get("id")).toString();
        	   ID.add(id);	        	  
        	   System.out.println("ID:"+id);

      	  System.out.println("Name:"+ob.get("name"));
        	   String o=(String)ob.get("full_name");
        	   int j;
        	   for( j=0;j<o.length();j++)
        	   {
        		   if(o.charAt(j)=='/')break;
        	   }
        	   o=o.substring(0,j);
        	   
        	   System.out.println("Owner:"+o);
        	   System.out.println("Fork:"+ob.get("forks"));
        	   System.out.println("Starcount:"+ob.get("stargazers_count"));
        	   System.out.println();	        	       	  
           }	      	      	       
	    }  
      catch (Exception ioe) {}	            	    
	}
public static void Import (String id)
{
	URL url;
    InputStream is = null;
    BufferedReader br;
    String line;
    
  try {
       url = new URL("https://api.github.com/repositories/"+id);
       is = url.openStream();  // throws an IOException
       br = new BufferedReader(new InputStreamReader(is));
       StringBuilder sb=new StringBuilder();

       while ((line=br.readLine()) != null) {
           sb.append(line);
          
      }
       JSONParser parser = new JSONParser();
       Object obj = parser.parse(sb.toString());
        JSONObject jobj=(JSONObject)obj;
        
        
        int j;	
        String name=(String)(jobj).get("name");
        System.out.println("Name: "+name);	
        
        String o=(String)(jobj).get("full_name");
        for(j=0;j<o.length();j++){
        	if(o.charAt(j)=='/')break;
        }
        o=o.substring(0,j);
        System.out.println("Owner: "+o);
        
  URL url2=new URL("https://api.github.com/repos/"+o+"/"+name+"/"+"contents");  
  is = url2.openStream();  // throws an IOException
  br = new BufferedReader(new InputStreamReader(is));
  StringBuilder sb2= new StringBuilder();
  while((line=br.readLine())!=null)
  {
	 sb2.append(line); 
  }
   Object obj2=parser.parse(sb2.toString());
   JSONArray arr =(JSONArray)obj2;
   for(int i=0;i<arr.size();i++){
	   
	   JSONObject jobj2=(JSONObject)arr.get(i);
	   if(((String)jobj2.get("name")).equals("package.json")){
		   URL url3=new URL((String)jobj2.get("download_url"));  
		   is = url3.openStream();  // throws an IOException
		   br = new BufferedReader(new InputStreamReader(is));
		   StringBuilder sb3= new StringBuilder();   
		   while((line=br.readLine())!=null)
		   {
		 	 sb3.append(line);
		 	 
		   }
		   Object obj3=parser.parse(sb3.toString());
		   JSONObject jobj3 =(JSONObject)obj3;
		   JSONObject dep =(JSONObject)jobj3.get("dependencies");
		   JSONObject devDep =(JSONObject)jobj3.get("devDependencies");
		   
		   System.out.println("Dependencies:- "); 
		 for(Object key:dep.keySet()){
			 String keyStr = (String)key;
		      
               top.put(keyStr, 1);
		       
		        System.out.println("key: "+ keyStr  ); 
			 	 }
		 System.out.println("DevDependencies:- "); 
		 for(Object key:devDep.keySet()){
			 String keyStr = (String)key;		    
               if(top.containsKey(keyStr)){            	  
            	   top.replace(keyStr,  top.get(keyStr)+1);
               }
               else{
            	   top.put(keyStr, 1); 
               }
		        System.out.println("key: "+ keyStr  ); 
			 	 }  
		  	   }	   
   }      
     
    } catch (Exception mue) {
         mue.printStackTrace();     
    } finally {
        try {
            if (is != null) is.close();
        } 
  catch (Exception ioe) {}
                   
   }	
}	
public static void toppacks()
{
	
	for(int i=0;i<ID.size();i++)
	{
		Import(ID.get(i));
		
	}
		top.entrySet().stream()
	   .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
	   .limit(10)
	   .forEach(System.out::println);	   
}
	
public static void main(String args[]){
		Scanner sc= new Scanner(System.in);
		String keyword;
		int option;
		
do{		System.out.println("Menu");
		System.out.println("1.Search");
		System.out.println("2.Import");
		System.out.println("3.TopPacks");
		System.out.println("0.Exit");
	    option = sc.nextInt();
		if(option==1)
		{
		System.out.println("Enter keyword :");
		keyword=sc.next();
		search(keyword);
		}
		else if(option==2){
		System.out.println("Enter ID :");
		keyword=sc.next();
		Import(keyword);
		}
		else if(option==3){
		toppacks();
		}
				
}while(option!=0);
		 
	sc.close();		
	}
}
