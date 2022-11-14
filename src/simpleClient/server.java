package simpleClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.concurrent.*;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;

//TODO move thing from main finction to other function

public class server {
	public static void main(String[] args) throws Exception {
		int port = 8080;
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		ExecutorService executor = Executors.newCachedThreadPool();

		server.createContext("/mail", request -> {
			Runnable runnable = () -> {
				String res = "good response";
				try {
					System.out.println("got request with context: /mail");

					String method = request.getRequestMethod();
					System.out.println("method: " + method);
					if (!method.equals("POST")) {
						throw new IllegalArgumentException("The server don't now how to handle" + method);
					}
					// get POST request body. from:
					// https://stackoverflow.com/questions/10393879/how-to-get-an-http-post-request-body-as-a-java-string-at-the-server-side
					InputStreamReader isr = new InputStreamReader(request.getRequestBody(), "utf-8");
					BufferedReader br = new BufferedReader(isr);

					// From now on, the right way of moving from bytes to utf-8 characters:

					int b;
					StringBuilder buf = new StringBuilder(512);
					while ((b = br.read()) != -1) {
						buf.append((char) b);
					}

					br.close();
					isr.close();

					// parsing url srting. from
					// https://stackoverflow.com/questions/13592236/parse-a-uri-string-into-name-value-collection
					Map<String, String> body;
					body = splitQuery(buf.toString());
					System.out.println(body);

					// TODO handle mail sending

				} catch (Exception ex) {
					res = "Sorry, an error occured: " + ex;
				}

				request.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
				request.getResponseHeaders().set("Content-Type", "text/plain");
				try {
					request.sendResponseHeaders(200, res.length());
					try (OutputStream os = request.getResponseBody()) {
						os.write(res.getBytes(StandardCharsets.UTF_8));
					}
				} catch (Exception ex) {
					System.out.println("Cannot send response to client");
					ex.printStackTrace();
				}
			};

			executor.execute(runnable);
		});

		server.createContext("/", request -> {
			final String input = request.getRequestURI().getQuery();
			System.out.println("[file] The input is: " + input);
			String fileName = "client.html";
			System.out.println("Got new file-request: " + fileName);
			Path path = Paths.get("client", fileName);
			String output = null;
			if (Files.exists(path)) {
				output = new String(Files.readAllBytes(path));
			} else {
				output = "File " + path + " not found!";
			}

			request.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
			request.getResponseHeaders().set("Content-Type", "text/html");
			request.sendResponseHeaders(200, 0);
			try (OutputStream os = request.getResponseBody()) {
				os.write(output.getBytes());
			}
		});
		System.out.println(MethodHandles.lookup().lookupClass().getSimpleName() + " is up.");
		System.out.println("Try http://localhost:" + port);
		server.start();
	}

	public static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
		Map<String, String> query_pairs = new LinkedHashMap<String, String>();
		String[] pairs = query.split("&");
		for (String pair : pairs) {
			int idx = pair.indexOf("=");
			query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
					URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
		}
		return query_pairs;
	}
}
