package lol;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import java.io.*;
import java.util.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TopPack1 {

	static Map<String, Integer> top = new HashMap<String, Integer>();
	static Vector<String> ID = new Vector<String>();
	static int flag = 0;
	static OkHttpClient client = new OkHttpClient();

	public static String run(String url) throws IOException {
		Request request = new Request.Builder().url(url).build();
		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}

	public static void search(String keyword) {
		try {

			JSONParser parser = new JSONParser();
			String s = run("https://api.github.com/search/repositories?q=" + keyword + "&sort=forks");
			Object obj = parser.parse(s.toString());

			JSONObject jsonObject = (JSONObject) obj;
			JSONArray items = (JSONArray) jsonObject.get("items");
			for (int i = 0; i < items.size(); i++) {

				JSONObject ob = (JSONObject) items.get(i);
				String id = (ob.get("id")).toString();
				ID.add(id);
				System.out.println("ID:" + id);

				System.out.println("Name:" + ob.get("name"));
				String o = (String) ob.get("full_name");
				int j;
				for (j = 0; j < o.length(); j++) {
					if (o.charAt(j) == '/')
						break;
				}
				o = o.substring(0, j);

				System.out.println("Owner:" + o);
				System.out.println("Fork:" + ob.get("forks"));
				System.out.println("Starcount:" + ob.get("stargazers_count"));
				System.out.println();
			}
		} catch (Exception ioe) {
		}
	}

	public static void Import(String id) {

		try {

			JSONParser parser = new JSONParser();
			String s = run("https://api.github.com/repositories/" + id);
			Object obj = parser.parse(s.toString());
			JSONObject jobj = (JSONObject) obj;

			int j;
			String name = (String) (jobj).get("name");
			String o = (String) (jobj).get("full_name");
			for (j = 0; j < o.length(); j++) {
				if (o.charAt(j) == '/')
					break;
			}
			o = o.substring(0, j);
			String s1 = run("https://api.github.com/repos/" + o + "/" + name + "/" + "contents");
			Object obj2 = parser.parse(s1.toString());
			JSONArray arr = (JSONArray) obj2;
			for (int i = 0; i < arr.size(); i++) {

				JSONObject jobj2 = (JSONObject) arr.get(i);
				if (((String) jobj2.get("name")).equals("package.json")) {
					String s2 = run((String) jobj2.get("download_url"));
					Object obj3 = parser.parse(s2.toString());
					JSONObject jobj3 = (JSONObject) obj3;
					JSONObject dep = (JSONObject) jobj3.get("dependencies");
					JSONObject devDep = (JSONObject) jobj3.get("devDependencies");

					// System.out.println("Dependencies:- ");
					for (Object key : dep.keySet()) {
						String keyStr = (String) key;

						top.put(keyStr, 1);

						if (flag == 0)
							System.out.println("key: " + keyStr);
					}
					// System.out.println("DevDependencies:- ");
					for (Object key : devDep.keySet()) {
						String keyStr = (String) key;
						if (top.containsKey(keyStr)) {
							top.replace(keyStr, top.get(keyStr) + 1);
						} else {
							top.put(keyStr, 1);
						}
						if (flag == 0)
							System.out.println("key: " + keyStr);
					}
				}
			}

		} catch (Exception mue) {
		}
	}

	public static void toppacks() {
		flag = 1;
		for (int i = 0; i < ID.size(); i++) {
			Import(ID.get(i));

		}
		top.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).limit(10)
				.forEach(System.out::println);
		flag = 0;
	}

	public static void main(String args[]) {
		Scanner sc = new Scanner(System.in);
		String keyword;
		int option;

		do {
			System.out.println("Menu");
			System.out.println("1.Search");
			System.out.println("2.Import");
			System.out.println("3.TopPacks");
			System.out.println("0.Exit");
			option = sc.nextInt();
			if (option == 1) {
				System.out.println("Enter keyword :");
				keyword = sc.next();
				search(keyword);
			} else if (option == 2) {
				System.out.println("Enter ID :");
				keyword = sc.next();
				Import(keyword);
			} else if (option == 3) {
				toppacks();
			}

		} while (option != 0);

		sc.close();
	}
}
