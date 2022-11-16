package simpleClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.*;
import com.sun.net.httpserver.HttpServer;


import bridgeServer.Bridge;


public class Server {

	public static void main(String[] args) throws Exception {
		int port = 8080;
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		ExecutorService executor = Executors.newCachedThreadPool();
		
		Bridge bridgeServer = Bridge.getInstance();

		server.createContext("/mail", request -> {
			Runnable runnable = () -> {
				String res = "good response";
				Map<String, String> reqBody = null;

				try {
//					System.out.println("got request with path: /mail");

					String method = request.getRequestMethod();
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

					reqBody = splitQuery(buf.toString());
					System.out.println("request body:" + reqBody);
					
				} catch (Exception ex) {
					res = "an error occured: " + ex;
					System.err.println(res);
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
				try {
					bridgeServer.sendEmail(reqBody.get("to"), reqBody.get("from"), "subject", reqBody.get("body"));
				} catch (Exception e) {
					System.err.println("error during mail sending" + e);
				}

			};

			executor.execute(runnable);
		});

		server.createContext("/", request -> {
//			System.out.println("got request with root path");
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
/**
 * parsing url string. 
 * from https://stackoverflow.com/questions/13592236/parse-a-uri-string-into-name-value-collection
 * @param query
 * @return
 * @throws UnsupportedEncodingException
 */
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
