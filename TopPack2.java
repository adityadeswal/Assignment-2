import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.util.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TopPack2 {

	static Map<String, Integer> top = new HashMap<String, Integer>();
	static Vector<String> ListofId = new Vector<String>();
	static OkHttpClient client = new OkHttpClient();
	static int flag = 0;

	public static String run(String url) throws IOException {

		Request request = new Request.Builder().url(url).build();

		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}

	public static void search(String keyword) {

		JSONParser parser = new JSONParser();
		String repos_str = null;
		try {
			repos_str = run("https://api.github.com/search/repositories?q=" + keyword + "&sort=forks&per_page=10");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Object repos_obj = null;
		try {
			repos_obj = parser.parse(repos_str);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		JSONObject repos_jobj = (JSONObject) repos_obj;
		JSONArray items = (JSONArray) repos_jobj.get("items");
		for (int i = 0; i < items.size(); i++) {

			JSONObject item = (JSONObject) items.get(i);
			String id = (item.get("id")).toString();
			ListofId.add(id);
			System.out.println("ID:" + id);

			System.out.println("Name:" + item.get("name"));
			String owner = (String) item.get("full_name");
			int j;
			for (j = 0; j < owner.length(); j++) {
				if (owner.charAt(j) == '/')
					break;
			}
			owner = owner.substring(0, j);

			System.out.println("Owner:" + owner);
			System.out.println("Fork:" + item.get("forks"));
			System.out.println("Starcount:" + item.get("stargazers_count"));
			System.out.println();
		}

	}

	public static void Import(String id) {

		try {
			JSONParser parser = new JSONParser();
			String repo_str = null;
			Object content_obj = null;
			try {
				repo_str = run("https://api.github.com/repositories/" + id);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Object repo_obj = null;
			try {
				repo_obj = parser.parse(repo_str.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			JSONObject repo_jobj = (JSONObject) repo_obj;

			try {
				String fullname = repo_jobj.get("full_name").toString();
				
				String contents = null;
				try {
					contents = run("https://api.github.com/repos/" + fullname + "/" + "contents");
				} catch (IOException e) {
					e.printStackTrace();
				}

				content_obj = parser.parse(contents.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			JSONArray listOfContents = (JSONArray) content_obj;
			for (int i = 0; i < listOfContents.size(); i++) {

				JSONObject entry = (JSONObject) listOfContents.get(i);
				if ((entry.get("name")).toString().equals("package.json")) {
					String downloadUrlContent = null;
					try {
						downloadUrlContent = run(entry.get("download_url").toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
					Object downloadUrlContent_obj = null;
					try {
						downloadUrlContent_obj = parser.parse(downloadUrlContent.toString());
					} catch (ParseException e) {
						e.printStackTrace();
					}
					JSONObject downloadUrlContent_jobj = (JSONObject) downloadUrlContent_obj;
					JSONObject dep = (JSONObject) downloadUrlContent_jobj.get("dependencies");
					JSONObject devDep = (JSONObject) downloadUrlContent_jobj.get("devDependencies");

					// System.out.println("Dependencies:- ");

					for (Object key : dep.keySet()) {
						String keyStr = key.toString();
						if (top.containsKey(keyStr)) {
							top.replace(keyStr, top.get(keyStr) + 1);
						} else {
							top.put(keyStr, 1);
						}
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
		} catch (NullPointerException e) {
		}

	}

	public static void toppacks() {
		flag = 1;
		for (int i = 0; i < ListofId.size(); i++) {
			Import(ListofId.get(i));

		}
		top.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).limit(10)
				.forEach(System.out::println);
		flag = 0;
	}

	public static void main(String args[]) {
		Scanner sc = new Scanner(System.in);
		String keyword, id;
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
				id = sc.next();
				Import(id);
			} else if (option == 3) {
				toppacks();
			}

		} while (option != 0);

		sc.close();
	}
}
